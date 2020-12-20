package com.example.chokipedia;

import android.content.Intent;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class Frag3ReviewQuest extends Fragment {

    private int REVIEW_RESULT = 3;

    final private int OK = 0;
    final private int NEXT = 1;
    final private int RESULT = 2;

    private View view;

    private String type;
    private long totalNum;
    private int current = 0;
    private int state = OK;

    private TextView typeTitle;
    private TextView tagTitle;

    private TextView indexTextView;
    private TextView questionTextView;
    private TextView answerTextView;

    private EditText answerEditText;

    private CardView answerCard;
    private Button okButton;
    private ConstraintLayout backButton;

    private ArrayList<QuestionSet> questionSets = new ArrayList<QuestionSet>();

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference reference;


    public static Frag3ReviewQuest newInstance(String type){
        Frag3ReviewQuest fragment = new Frag3ReviewQuest();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        fragment.setArguments(bundle);

        return fragment;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REVIEW_RESULT){
            if(resultCode==RESULT_OK){  // 복습 취소 ok
                Log.d("CHECK", "ok is clicked");
                if(current>=0){
                    ((MainActivity)getActivity()).replaceFragment(new Frag3ReviewResult().newInstance(type, questionSets, current));
                }
                else{   // 데이터 없는 경우 메인으로 돌아가기(current==-1)
                    if(type=="일반"){
                        ((MainActivity)getActivity()).replaceFragment(new Frag3Review().newInstance());
                    }
                    else{
                        ((MainActivity)getActivity()).replaceFragment(new Frag3ReviewSelectTag().newInstance());
                    }
                }
            }
            else if(resultCode==RESULT_CANCELED){   // 복습 취소 cancel
                Log.d("CHECK", "cancel is clicked");
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag3_review_quest, container, false);

        type = getArguments().getString("type");
        typeTitle = view.findViewById(R.id.type);
        tagTitle = view.findViewById(R.id.et_input);

        if(type.equals("일반")){  // 일반 복습
            typeTitle.setVisibility(View.VISIBLE);
            tagTitle.setVisibility(View.INVISIBLE);
        }
        else{   // 태그별 복습
            typeTitle.setVisibility(View.INVISIBLE);
            tagTitle.setVisibility(View.VISIBLE);
            tagTitle.setText("# "+type);
        }

        reference = firebaseDatabase.getReference("dictionary").child("word_list");
        /*reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                totalNum = dataSnapshot.getChildrenCount(); // 총 단어의 개수 저장
                indexTextView.setText((current+1) + " / " + totalNum);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/


        answerCard = view.findViewById(R.id.cv_answer);
        okButton = view.findViewById(R.id.bt_ok);
        backButton = view.findViewById(R.id.bt_back);


        TextView typeTextView = view.findViewById(R.id.type);
        indexTextView = view.findViewById(R.id.tv_index);

        typeTextView.setText(type);

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





        reference = firebaseDatabase.getReference("dictionary").child("word_list");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(type.equals("일반")){  // 일반 복습
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        String question = ds.child("meaning1").getValue().toString();
                        String answer = ds.getKey();

                        QuestionSet data = new QuestionSet();
                        data.setQuestion(question);
                        data.setAnswer(answer);
                        questionSets.add(data);
                        totalNum++;
                    }

                }
                else{   // 태그별 복습
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        String tag1, tag2;
                        tag1 = ds.child("tag1").getValue().toString();
                        tag2 = ds.child("tag2").getValue().toString();

                        if(type.equals(tag1) || type.equals(tag2)){

                            String question = ds.child("meaning1").getValue().toString();
                            String answer = ds.getKey();

                            QuestionSet data = new QuestionSet();
                            data.setQuestion(question);
                            data.setAnswer(answer);
                            questionSets.add(data);
                            totalNum++;
                        }
                    }
                }
                Collections.shuffle(questionSets);  // 랜덤 순서
                if(totalNum>0){
                    TextView qMark = view.findViewById(R.id.tv_q_mark);
                    qMark.setVisibility(View.VISIBLE);
                    indexTextView.setText((current+1) + " / " + totalNum);
                    questionTextView.setText(questionSets.get(current).getQuestion());
                    answerTextView.setText(questionSets.get(current).getAnswer());
                }
                else{   // 데이터 없는 경우
                    current = -1;
                    TextView qMark = view.findViewById(R.id.tv_q_mark);
                    TextView noDataTextView = view.findViewById(R.id.tv_no_data);

                    qMark.setVisibility(View.INVISIBLE);
                    noDataTextView.setVisibility(View.VISIBLE);
                    questionTextView.setVisibility(View.INVISIBLE);

                    indexTextView.setVisibility(View.INVISIBLE);
                    backButton.setVisibility(View.VISIBLE);
                    answerEditText.setVisibility(View.INVISIBLE);
                    okButton.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CheckDialog.class);
                intent.putExtra("msg", "복습을 종료하시겠습니까?");
                startActivityForResult(intent, REVIEW_RESULT);
            }
        });
        // **





        answerCard.setVisibility(View.INVISIBLE);


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
                    ((MainActivity)getActivity()).replaceFragment(new Frag3ReviewResult().newInstance(type, questionSets, current));
                }

            }
        });




        return view;
    }
}
