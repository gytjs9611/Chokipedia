package com.example.chokipedia;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Frag1Add extends Fragment {
    private View view;

    private Frag1Basic frag1Basic;

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

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference addRef = firebaseDatabase.getReference();
    private DatabaseReference tagRef = firebaseDatabase.getReference();



    public static Frag1Add newInstance(){
        return new Frag1Add();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag1_add, container, false);

        cancelButton = view.findViewById(R.id.cancel_button);
        okButton = view.findViewById(R.id.ok_button);

        word = view.findViewById(R.id.word_text);
        mean1 = view.findViewById(R.id.mean_text1);
        mean2 = view.findViewById(R.id.mean_text2);
        ex1 = view.findViewById(R.id.ex_text1);
        ex2 = view.findViewById(R.id.ex_text2);
        tag1 = view.findViewById(R.id.tag_text1);
        tag2 = view.findViewById(R.id.tag_text2);


        cancelButton.setOnClickListener(new View.OnClickListener() { // 단어 추가 취소
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).replaceFragment(frag1Basic.newInstance());
            }
        });

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

                addRef = firebaseDatabase.getReference("dictionary").child("word_list");

                if(s_word.length()>0){
//                    addRef.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            addRef.child(s_word).child("meaning1").setValue(s_mean1);
                            addRef.child(s_word).child("meaning2").setValue(s_mean2);
                            addRef.child(s_word).child("example1").setValue(s_ex1);
                            addRef.child(s_word).child("example2").setValue(s_ex2);
                            addRef.child(s_word).child("tag1").setValue(s_tag1);
                            addRef.child(s_word).child("tag2").setValue(s_tag2);

                            if(s_tag1.length()>0){
                                int cnt;
                                tagRef = firebaseDatabase.getReference("dictionary").child("tag_list");
                                tagRef.child(s_tag1).setValue(0);
                            }
                            if(s_tag2.length()>0){
                                int cnt;
                                tagRef = firebaseDatabase.getReference("dictionary").child("tag_list");
                                tagRef.child(s_tag2).setValue(0);
                            }


//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });

                    ((MainActivity)getActivity()).replaceFragment(frag1Basic.newInstance());

                }
                else{
                    // 에러메세지 출력
                }





            }
        });



        return view;
    }
}
