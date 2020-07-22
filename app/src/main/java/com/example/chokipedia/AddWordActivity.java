package com.example.chokipedia;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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

import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddWordActivity extends Activity {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference addRef, tagRef;

    private Button cancelButton;
    private Button okButton;

    private EditText word;
    private EditText mean1;
    private EditText mean2;
    private EditText ex1;
    private EditText ex2;
    private EditText tag1;
    private EditText tag2;

    String s_word, s_mean1, s_mean2, s_ex1, s_ex2, s_tag1, s_tag2;


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



        addRef = firebaseDatabase.getReference("dictionary").child("word_list");
        tagRef = firebaseDatabase.getReference("dictionary").child("tag_list");
        // 얘를 여기서 선언해야 단어 추가버튼 누를 시 오류 안나는듯??

        cancelButton = findViewById(R.id.cancel_button);
        okButton = findViewById(R.id.ok_button);

        word = findViewById(R.id.input_word);
        mean1 = findViewById(R.id.input_mean1);
        mean2 = findViewById(R.id.input_mean2);
        ex1 = findViewById(R.id.input_ex1);
        ex2 = findViewById(R.id.input_ex2);
        tag1 = findViewById(R.id.input_tag1);
        tag2 = findViewById(R.id.input_tag2);

        tag1.setText(tag_data);


        okButton.setOnClickListener(new View.OnClickListener() { // 단어 추가


            @Override
            public void onClick(View v) {
                s_word = word.getText().toString();
                s_mean1 = mean1.getText().toString();
                s_mean2 = mean2.getText().toString();
                s_ex1 = ex1.getText().toString();
                s_ex2 = ex2.getText().toString();
                s_tag1 = tag1.getText().toString();
                s_tag2 = tag2.getText().toString();


                if(s_word.length()>0){
                    addRef.child(s_word).child("meaning1").setValue(s_mean1);
                    addRef.child(s_word).child("meaning2").setValue(s_mean2);
                    addRef.child(s_word).child("example1").setValue(s_ex1);
                    addRef.child(s_word).child("example2").setValue(s_ex2);
                    addRef.child(s_word).child("tag1").setValue(s_tag1);
                    addRef.child(s_word).child("tag2").setValue(s_tag2);

                    if(s_tag1.length()>0){
                        tagRef.child(s_tag1).setValue(0);
                    }
                    if(s_tag2.length()>0){
                        tagRef.child(s_tag2).setValue(0);
                    }

                    Toast completeMsg;
                    completeMsg = Toast.makeText(getApplicationContext(), "성공적으로 추가되었습니다.", Toast.LENGTH_SHORT);
                    completeMsg.setGravity(Gravity.BOTTOM, Gravity.CENTER_HORIZONTAL, 200);
                    completeMsg.show();
                    System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX 성공적으로 추가됨!!!!!!!");
                    finish();

                }
                else{
                    // 에러메세지 출력
                    Toast errMsg;
                    errMsg = Toast.makeText(getApplicationContext(), "최소 1글자 이상 입력하세요.", Toast.LENGTH_SHORT);
                    errMsg.setGravity(Gravity.BOTTOM, Gravity.CENTER_HORIZONTAL, 200);
                    errMsg.show();
                }

            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
