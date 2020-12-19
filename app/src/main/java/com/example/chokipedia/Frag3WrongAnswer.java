package com.example.chokipedia;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class Frag3WrongAnswer extends Fragment {

    private View view;

    private ListView listView;
    private WrongAnswerListAdapter adapter;
    private ArrayList<WrongAnswerListItem> wrongAnswerList = new ArrayList<WrongAnswerListItem>();


    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference wordRef = firebaseDatabase.getReference("dictionary").child("word_list");



    public static Frag3WrongAnswer newInstance() {
        return new Frag3WrongAnswer();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag3_wrong_answer,  container, false);
        listView = view.findViewById(R.id.lv_wrongAnswerList);

        ConstraintLayout backButton = view.findViewById(R.id.bt_back);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).replaceFragment(new Frag3Review().newInstance());

            }
        });


        wordRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    WrongAnswerListItem listItem = new WrongAnswerListItem();
                    int reviewCnt = Integer.parseInt(data.child("reviewCnt").getValue().toString());
                    int wrongCnt = Integer.parseInt(data.child("wrongCnt").getValue().toString());

                    // 틀린 기록이 있는 항목만 수행
                    if(wrongCnt>0){
                        float rate = ((float)wrongCnt/(float)reviewCnt)*100;

                        listItem.word = data.getKey();
                        listItem.rate = rate;
                        // String.format("%.2f", rate)+"%"
                        Log.d("WRONG_ANSWER", listItem.word + "의 오답률:"+rate);

                        wrongAnswerList.add(listItem);
                    }
                }

                // 오답률 순으로 정렬
                Collections.sort(wrongAnswerList, new ListComparator());

                // 인덱스 차례로 부여
                int cnt = wrongAnswerList.size();
                for(int i = 0; i<cnt; i++){
                    wrongAnswerList.get(i).index = i+1;
                    Log.d("WRONG_ANSWER", wrongAnswerList.get(i).index +":"+wrongAnswerList.get(i).word+" "+wrongAnswerList.get(i).rate);
                }

                // listview에 adapter 추가해줌
                adapter = new WrongAnswerListAdapter(wrongAnswerList);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String click_data = wrongAnswerList.get(position).word;
                Intent intent = new Intent(getActivity(), ShowWordActivity.class);
                intent.putExtra("click_data", click_data);
                startActivity(intent);
            }
        });


        return view;
    }


}
