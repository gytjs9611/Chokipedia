package com.example.chokipedia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddTagActivity extends Activity {

    private int TAG_EDIT_CODE = 0;

    private FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference tagRef = fbDatabase.getReference("dictionary").child("tag_list");
    private DatabaseReference wordRef = fbDatabase.getReference("dictionary").child("word_list");
    private EditText tagNameInput;
    private TextView title;

    private String mode;
    private String prevTag;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // title bar 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_addtag);

        Intent intent = getIntent();
        mode = intent.getStringExtra("mode");
        prevTag = intent.getStringExtra("tag");

        tagNameInput = findViewById(R.id.et_input);
        title = findViewById(R.id.tv_title);
        
        String msg = null;
        if(mode.equals("add")){
            msg = "추가할 태그명을 입력하세요";
        }
        else if(mode.equals("edit")){
            msg = "변경할 태그명을 입력하세요";
            title.setText("태그 편집");
            tagNameInput.setText(prevTag);
        }

        final String hintMsg = msg;
        tagNameInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    tagNameInput.setHint("");
                }
                else{
                    tagNameInput.setHint(hintMsg);
                }
            }
        });


    }

    public void mOnClose(View v){
        final String input;
        input = tagNameInput.getText().toString();

        if(input.length()==0){
            showToast("최소 1글자 이상 입력하세요.");
        }
        else{
            tagRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Boolean isExists = dataSnapshot.hasChild(input);
                    if(isExists){
                        showToast("이미 존재하는 태그입니다.");
                    }
                    else if(mode.equals("add")){
                        tagRef.child(input).setValue(0);
                        showToast("태그가 성공적으로 추가되었습니다.");
                        finish();
                    }
                    else if(mode.equals("edit")){
                        tagRef.child(prevTag).removeValue();    // 기존 태그 삭제
                        tagRef.child(input).setValue(0);        // 새로운 태그 추가

                        wordRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot data : dataSnapshot.getChildren()){
                                    String word = data.getKey();
                                    String tag1 = data.child("tag1").getValue(String.class);
                                    String tag2 = data.child("tag2").getValue(String.class);

                                    if(tag1.equals(prevTag)){   // 기존 태그로 저장된 태그명을 변경한 태그명으로 업데이트
                                        wordRef.child(word).child("tag1").setValue(input);
                                    }
                                    if(tag2.equals(prevTag)){
                                        wordRef.child(word).child("tag2").setValue(input);
                                    }
                                }
                                showToast("변경사항이 저장되었습니다.");
                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }



    }

    private void showToast(String msg){
        Toast addTagMsg;
        addTagMsg = Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_SHORT);
        addTagMsg.setGravity(Gravity.BOTTOM, Gravity.CENTER_HORIZONTAL, 200);
        addTagMsg.show();
    }


//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        // 바깥 레이어 클릭 시 닫히지 않게
//        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
//            return false;
//        }
//        return true;
//    }

//    @Override
//    public void onBackPressed() {
//        // android backbutton 막기
//        super.onBackPressed();
//        return;
//    }
}
