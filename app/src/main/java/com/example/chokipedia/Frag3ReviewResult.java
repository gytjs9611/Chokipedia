package com.example.chokipedia;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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

public class Frag3ReviewResult extends Fragment {
    private static ArrayList<QuestionSet> questionSet;
    private static int totalNum;
    private static String type;

    private TextView typeTitle;
    private TextView tagTitle;
    private ConstraintLayout backButton;
    private Button againButton;

    private View view;
    private static ArrayList<String> wordList;

    private TextView scoreTextView;
    private ListView listView;
    private ArrayAdapter<String> adapter;

    private String clickData;

    private int reviewCnt;
    private int wrongCnt;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference wordRef = firebaseDatabase.getReference("dictionary").child("word_list");

   /* public Frag3ReviewResult() {
    }*/


    public static Frag3ReviewResult newInstance(String reviewType, ArrayList<QuestionSet> data, int size){
        type = reviewType;
        questionSet = data;
        totalNum = size;
        return new Frag3ReviewResult();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag3_review_result, container, false);

        // title
        typeTitle = view.findViewById(R.id.type);
        tagTitle = view.findViewById(R.id.et_input);
        // score
        scoreTextView = view.findViewById(R.id.tv_score);


        if(type.equals("일반")){  // 일반 복습
            typeTitle.setVisibility(View.VISIBLE);
            tagTitle.setVisibility(View.INVISIBLE);
        }
        else{   // 태그별 복습
            typeTitle.setVisibility(View.INVISIBLE);
            tagTitle.setVisibility(View.VISIBLE);
            tagTitle.setText("# "+type);
        }


        wordList = new ArrayList<String>();

        listView = view.findViewById(R.id.lv_wrongAnswerList);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, wordList);
        listView.setAdapter(adapter);


        // 단어 목록에 추가

        int sum = 0;
        Log.d("TAG", "size="+totalNum);

        for(int i = 0; i<=totalNum; i++){
            final String word = questionSet.get(i).getAnswer();
            Log.d("TAG", "틀린 단어 "+word);

            wordRef.child(word).child("reviewCnt").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String data = dataSnapshot.getValue().toString();
                    reviewCnt = Integer.parseInt(data);
                    Log.d("TAG", word+":reviewCnt="+reviewCnt);
                    reviewCnt++;
                    Log.d("TAG", "reviewCnt="+reviewCnt);
                    wordRef.child(word).child("reviewCnt").setValue(reviewCnt);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



            if(questionSet.get(i).getResult()) {
                sum++;
            }
            else{   // 오답일 경우 오답 리스트에 추가
                wordList.add(word);
                wordRef.child(word).child("wrongCnt").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String data = dataSnapshot.getValue().toString();
                        wrongCnt = Integer.parseInt(data);
                        wrongCnt++;
                        wordRef.child(word).child("wrongCnt").setValue(wrongCnt);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        }
        adapter.notifyDataSetChanged();
        scoreTextView.setText(sum+" / "+(totalNum+1));

        // 단어 클릭시 단어 정보 다이얼로그 호출
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickData = adapter.getItem(position);

                Intent intent = new Intent(getActivity(), ShowWordActivity.class);
                intent.putExtra("click_data", clickData);
                startActivity(intent);
            }
        });

        backButton = view.findViewById(R.id.bt_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type=="일반"){
                    ((MainActivity)getActivity()).replaceFragment(new Frag3Review().newInstance());
                }
                else{
                    ((MainActivity)getActivity()).replaceFragment(new Frag3ReviewSelectTag().newInstance());
                }
            }
        });

        againButton = view.findViewById(R.id.bt_again);
        againButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).replaceFragment(new Frag3ReviewQuest().newInstance(type));
            }
        });




        return view;
    }
}
