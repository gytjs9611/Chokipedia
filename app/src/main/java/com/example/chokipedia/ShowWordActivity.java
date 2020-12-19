package com.example.chokipedia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

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


        wordRef.child(click_data).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        word.setText(dataSnapshot.getKey());

                        String strMean1 = dataSnapshot.child("meaning1").getValue().toString();
                        String strMean2 = dataSnapshot.child("meaning2").getValue().toString();
                        String strEx1 = dataSnapshot.child("example1").getValue().toString();
                        String strEx2 = dataSnapshot.child("example2").getValue().toString();
                        String strTag1 = dataSnapshot.child("tag1").getValue().toString();
                        String strTag2 = dataSnapshot.child("tag2").getValue().toString();
                        Log.d("SHOW_WORD", "data: "+strMean1+" "+strMean2 +" "+ strEx1 + " " + strEx2 +" "+strTag1+" "+strTag2);

                        // meaning
                        if(strMean1.equals("") && strMean2.equals("")){
                            Log.d("SHOW_WORD", "mean null");
                            TextView meanTitle = findViewById(R.id.mean_title);
                            meanTitle.setVisibility(View.GONE);
                        }
                        else{
                            showData(mean1, " - " , strMean1);
                            showData(mean2, " - " , strMean2);
                        }

                        //example
                        if(strEx1.equals("") && strEx2.equals("")){
                            TextView meanTitle = findViewById(R.id.ex_title);
                            meanTitle.setVisibility(View.GONE);
                        }
                        else{
                            showData(ex1, " - " , strEx1);
                            showData(ex2, " - " , strEx2);
                        }

                        // tag
                        showData(tag1, "# " , strTag1);
                        showData(tag2, "# " , strTag2);


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
