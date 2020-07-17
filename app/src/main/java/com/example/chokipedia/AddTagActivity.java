package com.example.chokipedia;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddTagActivity extends Activity {
    TextView textView;
    private FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference tagRef = fbDatabase.getReference("dictionary").child("tag_list");
    private EditText tagNameInput;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // title bar 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_addtag);
    }

    public void mOnClose(View v){
        Toast addTagMsg;
        tagNameInput = findViewById(R.id.tag_name);
        String input;

        input = tagNameInput.getText().toString();
        if(input.length()==0){
            addTagMsg = Toast.makeText(this.getApplicationContext(), "최소 1글자 이상 입력하세요.", Toast.LENGTH_SHORT);
            addTagMsg.setGravity(Gravity.BOTTOM, Gravity.CENTER_HORIZONTAL, 200);
            addTagMsg.show();
        }
        else{
            tagRef.child(input).setValue(0);
            addTagMsg = Toast.makeText(this.getApplicationContext(), "태그가 성공적으로 추가되었습니다.", Toast.LENGTH_SHORT);
            addTagMsg.setGravity(Gravity.BOTTOM, Gravity.CENTER_HORIZONTAL, 200);
            addTagMsg.show();
            finish();
        }


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
