package com.example.chokipedia;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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

import static android.app.Activity.RESULT_OK;

public class Frag3ReviewSelectTag extends Fragment {

    private int TAG_REVIEW = 0;

    private View view;
    private Frag3ReviewQuest frag3ReviewQuest;

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> tagDataList = new ArrayList<String>();

    private Button startButton;
    private String selectedTag;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference tagRef = firebaseDatabase.getReference("dictionary").child("tag_list");



    public static Frag3ReviewSelectTag newInstance() {
        return new Frag3ReviewSelectTag();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==TAG_REVIEW){
            if(resultCode==RESULT_OK){  // 복습 시작
                ((MainActivity)getActivity()).replaceFragment(frag3ReviewQuest.newInstance(selectedTag));
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag3_review_select_tag,  container, false);
        listView = view.findViewById(R.id.tagList);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_single_choice, tagDataList);
        listView.setAdapter(adapter);

        startButton = view.findViewById(R.id.bt_start);
        ConstraintLayout backButton = view.findViewById(R.id.bt_back);

        startButton.setBackground(getResources().getDrawable(R.drawable.cancel_button));
        startButton.setEnabled(false);

        tagRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    tagDataList.add("# " + data.getKey());
                }
                adapter.notifyDataSetChanged(); // 얘를 해줘야 adapter 가 업데이트됨
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startButton.setBackground(getResources().getDrawable(R.drawable.ok_button));
                startButton.setEnabled(true);
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = listView.getCheckedItemPosition();
                selectedTag = listView.getItemAtPosition(position).toString().substring(2);

                Intent intent = new Intent(getActivity(), CheckDialog.class);
                intent.putExtra("msg", "복습을 시작하시겠습니까?");
                startActivityForResult(intent, TAG_REVIEW);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).replaceFragment(new Frag3Review().newInstance());

            }
        });



        return view;
    }


}
