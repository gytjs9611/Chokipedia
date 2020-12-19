package com.example.chokipedia;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class Frag1Basic extends Fragment {

    private int EDIT_DELETE_CODE = 0;
    private int DELETE_WORD_CODE = 1;

    private View view;

    private Frag1Delete frag1Delete;

    private EditText editText;
    private TextView totalCnt;
    public String msg;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference searchRef = firebaseDatabase.getReference();

    private DatabaseReference listRef;


    private ListView listView;
    private ArrayAdapter<String> adapter;
    // array배열 생성, listview와 연결
    List<String> Array = new ArrayList<String>();

    private String search_keyword;

    private Button addButton, deleteButton;

    private String click_data;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==EDIT_DELETE_CODE){
            if(resultCode==EditDeleteDialog.RESULT_EDIT){   // 편집하기
                // 편집 액티비티 띄우기
            }
            else if(resultCode==EditDeleteDialog.RESULT_DELETE){    // 삭제하기
                Intent intent = new Intent(getActivity(), CheckDialog.class);
                intent.putExtra("msg", "삭제하시겠습니까?");
                startActivityForResult(intent, DELETE_WORD_CODE);
            }
            else if(resultCode==EditDeleteDialog.RESULT_CANCELED){  // 취소하기
                // do nothing
            }
        }
        else if(requestCode==DELETE_WORD_CODE){
            if(resultCode==RESULT_OK){
                listRef.child(click_data).removeValue();
                adapter.notifyDataSetChanged();

                Toast deleteMsg = Toast.makeText(getActivity(), "성공적으로 삭제되었습니다.", Toast.LENGTH_SHORT);
                deleteMsg.setGravity(Gravity.BOTTOM, Gravity.CENTER_HORIZONTAL, 200);
                deleteMsg.show();
            }
        }
    }

    public static Frag1Basic newInstance(){
        return new Frag1Basic();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag1_basic, container, false);
        //setContentView와 유사

        listRef = firebaseDatabase.getReference("dictionary").child("word_list");

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
                            Array.clear(); // 얘를 추가해야 중복으로 아이템 추가 안됨

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

        listView = view.findViewById(R.id.wordList);

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<String>());
        // 아이템 view를 선택가능하도록 만듦 : simple_list_item_multiple_choice

        listView.setAdapter(adapter);




        listRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                adapter.clear();
                Array.clear();

                for(DataSnapshot messageData : dataSnapshot.getChildren()){
                    String msg2 = messageData.getKey();
                    Array.add(msg2);
                    adapter.add(msg2);/*
                    listRef.child(msg2).child("reviewCnt").setValue(0);   // 0 으로 초기화한 데이터 추가
                    listRef.child(msg2).child("wrongCnt").setValue(0);*/
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

                Intent intent = new Intent(getActivity(), ShowWordActivity.class);
                intent.putExtra("click_data", click_data);
                startActivity(intent);


            }
        });

        // ***
        // long click 하면 편집/삭제 고르는 다이얼로그 띄우기
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                click_data = adapter.getItem(position); // 롱 클릭한 데이터 정보 저장

                Intent intent = new Intent(getActivity(), EditDeleteDialog.class);
                startActivityForResult(intent, EDIT_DELETE_CODE);
                return true;
            }
        });



        addButton = view.findViewById(R.id.add_button);
        deleteButton = view.findViewById(R.id.delete_button);


        addButton.setOnClickListener(new View.OnClickListener() { // 단어추가버튼을 누르면 단어추가하는 fragment로 교체
            @Override
            public void onClick(View v) {
//                ((MainActivity)getActivity()).replaceFragment(frag1Add.newInstance());
                Intent intent = new Intent(getActivity(), AddWordActivity.class);
                intent.putExtra("tag_data", "null");
                startActivity(intent);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).replaceFragment(frag1Delete.newInstance());
            }
        });


        return view;
    }
    // ctrl+O, 검색하면 됨
    // fragment는 onCreateView로 생성하면 됨





}
