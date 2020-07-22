package com.example.chokipedia;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class Frag2TagData extends Fragment {
    private View view;

    private TextView tagDataName;
    private EditText editText;
    private TextView totalCnt;
    public String msg;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference searchRef = firebaseDatabase.getReference();
    private ChildEventListener mChild;
    // 데이터베이스 값을 실시간으로 갱신하기 위해 정의

    private DatabaseReference listRef, dbRef;

    private ListView listView;
    private ArrayAdapter<String> adapter; // array배열 생성, listview와 연결
    List<Object> Array = new ArrayList<Object>();

    private String search_keyword;

    private String click_tag_data;
    private String click_word_data;

    private Button addButton, deleteButton;






    public static Frag2TagData newInstance(String click_data){
        Frag2TagData fragment = new Frag2TagData();
        Bundle bundle = new Bundle(1);
        bundle.putString("click_data", click_data);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag2_tagdata, container, false);

        click_tag_data = getArguments().getString("click_data");
        System.out.println(click_tag_data);

        tagDataName = view.findViewById(R.id.tagdata_name);

        tagDataName.setText(click_tag_data);
        editText = view.findViewById(R.id.input); // 검색어입력란


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search_keyword = s.toString();
                searchRef = firebaseDatabase.getReference("dictionary").child("word_list");
                searchRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        adapter.clear();

                        if(search_keyword.length()>0) { //검색어 입력된 경우
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                String dataItem = data.getKey();
                                if (dataItem.compareTo(search_keyword) == 0) {
                                    Array.add(dataItem);
                                    adapter.add(dataItem);
                                }
                            }
                        }
                        else{ // 검색어 입력되지 않은 경우
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                String dataItem = data.getKey();
                                Array.add(dataItem);
                                adapter.add(dataItem);
                            }
                        }

                        int cnt;
                        String cntString;
                        cnt = listView.getCount();
                        cntString = "총 "+Integer.toString(cnt)+"개의 검색결과";

                        totalCnt = view.findViewById(R.id.total_count);
                        totalCnt.setText(cntString);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        addButton = view.findViewById(R.id.tagdata_add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddWordActivity.class);
                intent.putExtra("tag_data", click_tag_data);
                startActivity(intent);
            }
        });



        listView = view.findViewById(R.id.tagList);

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        listView.setAdapter(adapter);

        listRef = firebaseDatabase.getReference("dictionary").child("word_list");
        listRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot messageData : dataSnapshot.getChildren()){
                        String msg2 = messageData.getKey();
                        String tag1 = messageData.child("tag1").getValue(String.class); // getValue().toString()대신
                        String tag2 = messageData.child("tag2").getValue(String.class);
                        System.out.println(msg2+","+tag1);
                        if(click_tag_data.compareTo("# "+tag1)==0 || click_tag_data.compareTo("# "+tag2)==0){
                            Array.add(msg2);
                            adapter.add(msg2);
                        }

                    }
                    adapter.notifyDataSetChanged();
                    listView.setSelection(adapter.getCount()-1);

                    int cnt;
                    String cntString;
                    cnt = listView.getCount();
                    cntString = Integer.toString(cnt)+"개의 검색결과";

                    totalCnt = view.findViewById(R.id.total_count);
                    totalCnt.setText(cntString);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        });




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                click_data = Array.get(position).toString(); // 검색 후 아이템 클릭시, 검색전 아이템의 position 기준으로 데이터 설정됨
                click_word_data = adapter.getItem(position).toString();

                Intent intent = new Intent(getActivity(), ShowWordActivity.class);
                intent.putExtra("click_data", click_word_data);
                startActivity(intent);

//                listRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if(dataSnapshot.exists() && item_click_flag==1){
//                            for(DataSnapshot messageData : dataSnapshot.getChildren()){
//                                if(click_word_data==messageData.getKey()){
//                                    Intent intent = new Intent(getActivity(), ShowWordActivity.class);
//                                    intent.putExtra("click_data", click_word_data);
//                                    startActivity(intent);
//                                }
//                            }
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
//                    }
//                });
                // 불필요한 코드였음!!!!!!


            }
        });







        return view;

    }





}
