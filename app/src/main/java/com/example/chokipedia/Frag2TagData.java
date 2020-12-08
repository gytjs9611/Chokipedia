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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
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

    private DatabaseReference listRef, delRef;

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

        ConstraintLayout backButton = view.findViewById(R.id.bt_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).replaceFragment(new Frag2Tag().newInstance());
            }
        });


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
                                String itemForCheck = dataItem.toLowerCase();
                                itemForCheck = itemForCheck.replaceAll(" ","");
                                search_keyword = search_keyword.replaceAll(" ", "");

                                if (itemForCheck.contains(search_keyword.toLowerCase())) {
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


        delRef = firebaseDatabase.getReference("dictionary").child("state").child("tagdata_delete");

        deleteButton = view.findViewById(R.id.tagdata_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Frag2TagDataDelete frag2TagDataDelete = new Frag2TagDataDelete();
                delRef.setValue("null");
                ((MainActivity)getActivity()).replaceFragment(frag2TagDataDelete.newInstance(click_tag_data));
            }
        });



        listView = view.findViewById(R.id.tagList);

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<String>());
        listView.setAdapter(adapter);

        listRef = firebaseDatabase.getReference("dictionary").child("word_list");
        listRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.clear();
                if(dataSnapshot.exists()){ // 웬만하면 체크 꼭 해주기!!! - 후에 다른 함수에서 문제 발생 시 이 부분 추가해보기!
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

            }
        });







        return view;

    }





}
