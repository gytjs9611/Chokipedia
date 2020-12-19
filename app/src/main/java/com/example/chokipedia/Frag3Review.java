package com.example.chokipedia;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class Frag3Review extends Fragment {

    private int RESET_WRONG_COUNT_DATA = 0;
    private int BASIC_REVIEW = 1;

    private View view;
    private Frag3ReviewQuest frag3ReviewQuest;
    private Frag3ReviewSelectTag frag3ReviewSelectTag;
    private Frag3WrongAnswer frag3WrongAnswer;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference wordRef = firebaseDatabase.getReference("dictionary").child("word_list");


    public static Frag3Review newInstance(){
        return new Frag3Review();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RESET_WRONG_COUNT_DATA){
            if(resultCode==RESULT_OK){  // 초기화
                Log.d("CHECK", "ok is clicked");
                // reviewCnt, wrongCnt 0으로 초기화
                wordRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot data : dataSnapshot.getChildren()){
                            wordRef.child(data.getKey()).child("reviewCnt").setValue(0);
                            wordRef.child(data.getKey()).child("wrongCnt").setValue(0);
                        }
                        Toast deleteMsg;
                        deleteMsg = Toast.makeText(getActivity(), "오답 데이터가 초기화되었습니다.", Toast.LENGTH_SHORT);
                        deleteMsg.setGravity(Gravity.BOTTOM, Gravity.CENTER_HORIZONTAL, 200);
                        deleteMsg.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            else if(resultCode==RESULT_CANCELED){   // 초기화 취소
                Log.d("CHECK", "cancel is clicked");
            }
        }
        else if(requestCode==BASIC_REVIEW){
            if(resultCode==RESULT_OK){
                // 복습 시작
                ((MainActivity)getActivity()).replaceFragment(frag3ReviewQuest.newInstance("일반"));
            }
        }




    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag3_review, container, false);

        Button basicButton = view.findViewById(R.id.bt_review_basic);
        Button tagButton = view.findViewById(R.id.bt_review_tag);
        Button wrongAnswerButton = view.findViewById(R.id.bt_review_wrong_answer);
        Button resetButton = view.findViewById(R.id.bt_reset_data);


        /* 복습메뉴 */
        // 일반 복습
        basicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CheckDialog.class);
                intent.putExtra("msg", "복습을 시작하시겠습니까?");
                startActivityForResult(intent, BASIC_REVIEW);

            }
        });

        // 태그별 복습
        tagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).replaceFragment(frag3ReviewSelectTag.newInstance());
            }
        });



        /* 오답메뉴 */
        // 오답 모아보기
        wrongAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).replaceFragment(frag3WrongAnswer.newInstance());
            }
        });

        // 오답 데이터 삭제
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CheckDialog.class);
                intent.putExtra("msg", "오답 데이터를\n초기화하시겠습니까?");
                startActivityForResult(intent, RESET_WRONG_COUNT_DATA);
            }
        });




        return view;
    }
    // ctrl+O, 검색하면 됨
    // fragment는 onCreateView로 생성하면 됨



}
