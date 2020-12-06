package com.example.chokipedia;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Frag3Review extends Fragment {

    private View view;
    private Frag3ReviewQuest frag3ReviewQuest;
    private Frag3ReviewSelectTag frag3ReviewSelectTag;

    public static Frag3Review newInstance(){
        return new Frag3Review();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag3_review, container, false);

        Button basicButton = view.findViewById(R.id.basic_review_button);
        Button tagButton = view.findViewById(R.id.tag_review_button);

        basicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).replaceFragment(frag3ReviewQuest.newInstance("일반"));

            }
        });

        tagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).replaceFragment(frag3ReviewSelectTag.newInstance());
            }
        });



        return view;
    }
    // ctrl+O, 검색하면 됨
    // fragment는 onCreateView로 생성하면 됨



}
