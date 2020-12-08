package com.example.chokipedia;


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

public class Frag3ReviewSelectTag extends Fragment {

    private View view;
    private Frag3ReviewQuest frag3ReviewQuest;

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> tagDataList = new ArrayList<String>();

    private Button startButton;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference tagRef = firebaseDatabase.getReference("dictionary").child("tag_list");



    public static Frag3ReviewSelectTag newInstance() {
        return new Frag3ReviewSelectTag();
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
                String selectedTag = listView.getItemAtPosition(position).toString().substring(2);
                ((MainActivity)getActivity()).replaceFragment(frag3ReviewQuest.newInstance(selectedTag));
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
