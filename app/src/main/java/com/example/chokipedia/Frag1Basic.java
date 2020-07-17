package com.example.chokipedia;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class Frag1Basic extends Fragment {

    private View view;
    private Frag1Add frag1Add;

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

    private Button addButton;

    private String click_data;


    public static Frag1Basic newInstance(){
        return new Frag1Basic();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag1_basic, container, false);
        //setContentView와 유사

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
                            cntString = Integer.toString(cnt)+"개의 검색결과";

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



//        searchBt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                msg = editText.getText().toString();
//                System.out.println(msg);
//
//                searchRef = firebaseDatabase.getReference("dictionary").child("word_list");
//                searchRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        adapter.clear();
//                        if(!TextUtils.isEmpty(msg)){ //
//                            for(DataSnapshot data : dataSnapshot.getChildren()){
//                                String dataItem = data.getKey();
//                                if(dataItem.compareTo(msg)==0){
//                                    Array.add(dataItem);
//                                    adapter.add(dataItem);
//                                }
//                            }
//                        }
//                        else{
//                            for(DataSnapshot data : dataSnapshot.getChildren()){
//                                String dataItem = data.getKey();
//                                Array.add(dataItem);
//                                adapter.add(dataItem);
//                            }
//                        }
//
//
//                        int cnt;
//                        String cntString;
//                        cnt = listView.getCount();
//                        cntString = "총 "+Integer.toString(cnt)+"개의 검색결과";
//
//                        totalCnt = view.findViewById(R.id.total_count);
//                        totalCnt.setText(cntString);
//
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//
//                InputMethodManager imm = (InputMethodManager)getContext().getSystemService(INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0); // 검색버튼 누르면 키보드 숨김
//
//
//
//
//
//            }
//        });

        listView = view.findViewById(R.id.wordList);

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        listView.setAdapter(adapter);

        listRef = firebaseDatabase.getReference("dictionary").child("word_list");
        listRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.clear();

                for(DataSnapshot messageData : dataSnapshot.getChildren()){
                    String msg2 = messageData.getKey();
                    Array.add(msg2);
                    adapter.add(msg2);
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                click_data = Array.get(position).toString(); // 검색 후 아이템 클릭시, 검색전 아이템의 position 기준으로 데이터 설정됨
                click_data = adapter.getItem(position).toString();

                listRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot messageData : dataSnapshot.getChildren()){
                            if(click_data==messageData.getKey()){
                                Intent intent = new Intent(getActivity(), ShowWordActivity.class);
                                intent.putExtra("click_data", click_data);
                                startActivity(intent);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });








        addButton = view.findViewById(R.id.add_button);


        addButton.setOnClickListener(new View.OnClickListener() { // 단어추가버튼을 누르면 단어추가하는 fragment로 교체
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).replaceFragment(frag1Add.newInstance());
            }
        });










        return view;
    }
    // ctrl+O, 검색하면 됨
    // fragment는 onCreateView로 생성하면 됨







}
