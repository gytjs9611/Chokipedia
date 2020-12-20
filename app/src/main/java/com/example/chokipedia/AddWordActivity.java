package com.example.chokipedia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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

public class AddWordActivity extends Activity {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference wordRef, tagRef;

    private Button cancelButton;
    private Button okButton;

    private EditText wordEt;
    private EditText mean1Et;
    private EditText mean2Et;
    private EditText ex1Et;
    private EditText ex2Et;
    private EditText tag1Et;
    private EditText tag2Et;

    String s_word, s_mean1, s_mean2, s_ex1, s_ex2, s_tag1, s_tag2;

    private String mode;
    private String target_word;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // title bar 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_addword);


        String tag_data = getIntent().getStringExtra("tag_data");
        if(tag_data.compareTo("null")==0) {
            tag_data ="";
        }
        else{
            tag_data = tag_data.substring(2);
        }



        wordRef = firebaseDatabase.getReference("dictionary").child("word_list");
        tagRef = firebaseDatabase.getReference("dictionary").child("tag_list");
        // 얘를 여기서 선언해야 단어 추가버튼 누를 시 오류 안나는듯??

        cancelButton = findViewById(R.id.cancel_button);
        okButton = findViewById(R.id.ok_button);

        // textView 선언
        wordEt = findViewById(R.id.input_word);
        mean1Et = findViewById(R.id.input_mean1);
        mean2Et = findViewById(R.id.input_mean2);
        ex1Et = findViewById(R.id.input_ex1);
        ex2Et = findViewById(R.id.input_ex2);
        tag1Et = findViewById(R.id.input_tag1);
        tag2Et = findViewById(R.id.input_tag2);

        Intent intent = getIntent();
        mode = intent.getStringExtra("mode");
        if(mode.equals("edit")){
            TextView title = findViewById(R.id.tv_title);
            title.setText("단어편집 : ");
            target_word = intent.getStringExtra("word");    // 편집할 단어명 불러옴

            // 기존 저장되어있는 데이터 텍스트뷰에 입력
            wordEt.setText(target_word);
            wordRef.child(target_word).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String data_mean1 = dataSnapshot.child("meaning1").getValue(String.class);
                    String data_mean2 = dataSnapshot.child("meaning2").getValue(String.class);
                    String data_ex1 = dataSnapshot.child("example1").getValue(String.class);
                    String data_ex2 = dataSnapshot.child("example2").getValue(String.class);
                    String data_tag1 = dataSnapshot.child("tag1").getValue(String.class);
                    String data_tag2 = dataSnapshot.child("tag2").getValue(String.class);

                    mean1Et.setText(data_mean1);
                    mean2Et.setText(data_mean2);
                    ex1Et.setText(data_ex1);
                    ex2Et.setText(data_ex2);
                    tag1Et.setText(data_tag1);
                    tag2Et.setText(data_tag2);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else if(mode.equals("add")){
            // tag 별 저장에서 추가할때 textView 에 tag 정보 입력
            tag1Et.setText(tag_data);
        }



        okButton.setOnClickListener(new View.OnClickListener() { // 단어 추가

            @Override
            public void onClick(View v) {
                // EditText 에 적힌 데이터 불러옴
                s_word = wordEt.getText().toString();
                s_mean1 = mean1Et.getText().toString();
                s_mean2 = mean2Et.getText().toString();
                s_ex1 = ex1Et.getText().toString();
                s_ex2 = ex2Et.getText().toString();
                s_tag1 = tag1Et.getText().toString();
                s_tag2 = tag2Et.getText().toString();


                if(s_word.length()>0){  // 단어명 입력된 경우
                    if(s_mean1.equals("") && s_mean2.equals("")){   // 의미 입력 안한 경우
                        showToast("의미를 입력하세요.");
                    }
                    else{
                        writeDataIfNotExists();
                    }
                }
                else{   // 단어명 입력되지 않은 경우
                    showToast("단어명을 입력하세요.");
                }

            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });


    }

    private void writeDataToFirebase(Boolean isNewWord){
        wordRef.child(s_word).child("meaning1").setValue(s_mean1);
        wordRef.child(s_word).child("meaning2").setValue(s_mean2);
        wordRef.child(s_word).child("example1").setValue(s_ex1);
        wordRef.child(s_word).child("example2").setValue(s_ex2);

        // 태그1,2 똑같이 적으면 하나만 저장되도록
        if(s_tag1.equals(s_tag2)){
            wordRef.child(s_word).child("tag1").setValue(s_tag1);
            wordRef.child(s_word).child("tag2").setValue("");
        }
        else{
            wordRef.child(s_word).child("tag1").setValue(s_tag1);
            wordRef.child(s_word).child("tag2").setValue(s_tag2);
        }

        if(!s_tag1.equals("")){
            tagRef.child(s_tag1).setValue(0);
        }
        if(!s_tag2.equals("")){
            tagRef.child(s_tag2).setValue(0);
        }

        // 새 단어라면 복습 데이터 0으로 초기화
        if(isNewWord){
            wordRef.child(s_word).child("wrongCnt").setValue(0);
            wordRef.child(s_word).child("reviewCnt").setValue(0);
        }
    }

    private void showToast(String msg){
        Toast completeMsg = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        completeMsg.setGravity(Gravity.BOTTOM, Gravity.CENTER_HORIZONTAL, 200);
        completeMsg.show();
    }

    private void writeDataIfNotExists(){
        String toastMsg ="";
        if(mode.equals("edit")){
            toastMsg = "변경사항이 저장되었습니다.";
        }
        else if(mode.equals("add")){
            toastMsg = "단어가 성공적으로 추가되었습니다.";
        }

        final String finalToastMsg = toastMsg;

        wordRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean isExists = dataSnapshot.hasChild(s_word);

                if(mode.equals("edit") && s_word.equals(target_word)){    // 단어명은 그대로 두고 편집
                    writeDataToFirebase(false);
                    showToast(finalToastMsg);
                    setResult(RESULT_OK);
                    finish();
                }
                else if(isExists){
                    showToast("이미 저장되어있는 단어입니다.");
                }
                else{
                    if(mode.equals("edit")){
                        wordRef.child(target_word).removeValue();    // 기존 단어 삭제
                    }
                    writeDataToFirebase(true);
                    showToast(finalToastMsg);
                    setResult(RESULT_OK);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 바깥 레이어 클릭 시 닫히지 않게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }




}
