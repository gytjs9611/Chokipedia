package com.example.chokipedia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShowWordActivity extends Activity {
    TextView textView;
    private FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference wordRef = fbDatabase.getReference("dictionary").child("word_list");
    private EditText tagNameInput;
    private String click_data;

    private TextView word, mean1, mean2, ex1, ex2;
    private Button tag1, tag2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // title bar 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_showword);

        Intent intent = getIntent();
        click_data = intent.getStringExtra("click_data");



        word = findViewById(R.id.show_word);
        mean1 = findViewById(R.id.show_mean1);
        mean2 = findViewById(R.id.show_mean2);
        ex1 = findViewById(R.id.show_ex1);
        ex2 = findViewById(R.id.show_ex2);

        tag1 = findViewById(R.id.show_tag1);
        tag2 = findViewById(R.id.show_tag2);



        wordRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if(click_data.compareTo(data.getKey())==0){

                        word.setText(data.getKey());
                        showData(mean1, "1. " , data.child("meaning1").getValue().toString());
                        showData(mean2, "2. " , data.child("meaning2").getValue().toString());
                        showData(ex1, "예) " , data.child("example1").getValue().toString());
                        showData(ex2, "예) " , data.child("example2").getValue().toString());
                        showData(tag1, "# " , data.child("tag1").getValue().toString());
                        showData(tag2, "# " , data.child("tag2").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    public void showData(TextView textView, String append, String data){
        if(data.length()==0){
            textView.setVisibility(View.GONE);
        }
        else{
            textView.setText(append + data);
        }
    }

    public void onClickTag1(View v){
        BottomNavigationView bottomNavigationView;
        String tag = tag1.getText().toString();
        Frag2TagData frag2TagData = new Frag2TagData();
        ((MainActivity)MainActivity.mContext).setFragClicked(R.id.action_tag);
        ((MainActivity)MainActivity.mContext).replaceFragment(frag2TagData.newInstance(tag));
        finish();
    }

    public void onClickTag2(View v){
        // edit word page
        String tag = tag2.getText().toString();
        Frag2TagData frag2TagData = new Frag2TagData();
        ((MainActivity)MainActivity.mContext).setFragClicked(R.id.action_tag);
        ((MainActivity)MainActivity.mContext).replaceFragment(frag2TagData.newInstance(tag));
        finish();
    }


//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        // 바깥 레이어 클릭 시 닫히지 않게
//        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
//            return false;
//        }
//        return true;
//    }



    // back키 막으려면 아래 코드 추가
//    @Override
//    public void onBackPressed() {
//        return;
//    }
}
