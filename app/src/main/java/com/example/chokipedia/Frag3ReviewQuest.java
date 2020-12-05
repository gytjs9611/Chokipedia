package com.example.chokipedia;

import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Frag3ReviewQuest extends Fragment {

    final private int OK = 0;
    final private int NEXT = 1;
    final private int RESULT = 2;

    private View view;

    private String type;
    private int totalNum;
    private int current = 0;
    private int state = OK;

    private TextView indexTextView;
    private TextView questionTextView;
    private TextView answerTextView;

    private EditText answerEditText;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference basicRef, tagRef;


    public static Frag3ReviewQuest newInstance(String type, int num){
        Frag3ReviewQuest fragment = new Frag3ReviewQuest();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        bundle.putInt("num", num);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag3_review_quest, container, false);

        type = getArguments().getString("type");
        totalNum = getArguments().getInt("num");

        TextView typeTextView = view.findViewById(R.id.type);
        indexTextView = view.findViewById(R.id.tv_index);

        typeTextView.setText(type);
        indexTextView.setText((current+1) + " / " + totalNum);

        questionTextView = view.findViewById(R.id.tv_question);
        answerTextView = view.findViewById(R.id.tv_answer);

        answerEditText = view.findViewById(R.id.et_answer);
        answerEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == event.KEYCODE_ENTER){
                    return true;
                }
                return false;
            }
        });

        questionTextView.setMovementMethod(ScrollingMovementMethod.getInstance());


//        Set<Integer> indexSet = new HashSet<>();    // HashSet 는 중복값 저장하지 않음
//        while(indexSet.size()<totalNum){
//            double index = Math.random() * totalNum;    // 0 ~ totalNum-1
//            indexSet.add((int) index);
//        }
//        final ArrayList<Integer> indexList = new ArrayList<>(indexSet);
//        Collections.sort(indexList);


        final ArrayList<QuestionSet> questionSets = new ArrayList<QuestionSet>();


        basicRef = firebaseDatabase.getReference("dictionary").child("word_list");
        basicRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(type.equals("일반")){
                    int index = 0;
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        String question = ds.child("meaning1").getValue().toString();
                        String answer = ds.getKey();

                        QuestionSet data = new QuestionSet();
                        data.setQuestion(question);
                        data.setAnswer(answer);
                        questionSets.add(data);
                    }
                    Collections.shuffle(questionSets);

                }

                questionTextView.setText(questionSets.get(current).getQuestion());
                answerTextView.setText(questionSets.get(current).getAnswer());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        final CardView answerCard = view.findViewById(R.id.cv_answer);
        answerCard.setVisibility(View.INVISIBLE);



        final Button okButton = view.findViewById(R.id.bt_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 다음 문제로 넘어감

                if(state==OK){ // 확인버튼인 상태
                    // 정답 표시
                    answerCard.setVisibility(View.VISIBLE);
                    answerEditText.setEnabled(false);

                    if(current==totalNum-1){
                        okButton.setBackgroundResource(R.drawable.close_button);
                        okButton.setText("결과보기");
                        okButton.setTextSize(18);

                        state = RESULT;
                    }
                    else{
                        // 확인 버튼 -> 다음 버튼
                        okButton.setBackgroundResource(R.drawable.next_button);
                        okButton.setText("다음");
                        okButton.setTextSize(25);

                        state = NEXT;
                    }

                    String answerRemoveSpace = answerTextView.getText().toString();
                    answerRemoveSpace = answerRemoveSpace.replaceAll(" ","");

                    String inputRemoveSpace = answerEditText.getText().toString();
                    inputRemoveSpace = inputRemoveSpace.replaceAll(" ","");

                    if(inputRemoveSpace.equals(answerRemoveSpace)){
                        questionSets.get(current).setResult(true);
                        answerCard.setCardBackgroundColor(getResources().getColor(R.color.cardNormal));
                    }
                    else{
                        questionSets.get(current).setResult(false);
                        answerCard.setCardBackgroundColor(getResources().getColor(R.color.cardWrong));
                        Log.d("TAG","틀림");
                    }

                }
                else if(state==NEXT){
                    // 정답 표시 x
                    answerCard.setVisibility(View.INVISIBLE);
                    answerEditText.setEnabled(true);
                    answerEditText.setText("");

                    // 다음 버튼 -> 확인 버튼
                    okButton.setBackgroundResource(R.drawable.ok_button);
                    okButton.setText("확인");
                    okButton.setTextSize(25);

                    state = OK;

                    current++;
                    indexTextView.setText((current+1) + " / " + totalNum);

                    questionTextView.setText(questionSets.get(current).getQuestion());
                    answerTextView.setText(questionSets.get(current).getAnswer());




                }
                else if(state==RESULT){
                    // 결과보기



                }

            }
        });




        return view;
    }
}
