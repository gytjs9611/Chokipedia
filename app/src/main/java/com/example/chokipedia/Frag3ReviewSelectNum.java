package com.example.chokipedia;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Frag3ReviewSelectNum extends Fragment {

    private View view;
    private Frag3ReviewQuest frag3ReviewQuest;
    private String type;
    private long totalNum = 0;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference basicRef, tagRef;


    public static Frag3ReviewSelectNum newInstance(String type){
        Frag3ReviewSelectNum fragment = new Frag3ReviewSelectNum();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag3_review_select_num, container, false);

        type = getArguments().getString("type");    // 일반 or 태그별

        TextView typeTextView = view.findViewById(R.id.type);
        typeTextView.setText(type);

        Button goButton = view.findViewById(R.id.bt_go);
        final EditText number = view.findViewById(R.id.et_quest_num);


        basicRef = firebaseDatabase.getReference("dictionary").child("word_list");
        basicRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                totalNum = dataSnapshot.getChildrenCount(); // 총 단어의 개수 저장
                number.setHint("최대 "+totalNum+" 까지");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(number.getText().length()!=0){
                    int num = Integer.parseInt(number.getText().toString());
                    // 총 단어 수와 비교해서 유효값일 경우만 다음 프래그먼트로 넘어가게 설정하기
                    if(num<=totalNum && num>0){
                        if(type.equals("일반")){
                            ((MainActivity)getActivity()).replaceFragment(frag3ReviewQuest.newInstance("일반", num));
                        }
                        else if(type.equals("태그별")){
//                        ((MainActivity)getActivity()).replaceFragment(frag3ReviewQuest.newInstance("일반", num));
                        }
                    }
                    else{
                        String msg = "입력 가능한 문제 수는 0~" + totalNum + "입니다.";
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getContext(), "문제 수를 입력하세요", Toast.LENGTH_SHORT).show();
                }


            }
        });


        return view;
    }



}
