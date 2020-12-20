package com.example.chokipedia;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
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

public class Frag2Tag extends Fragment {

    private int EDIT_DELETE_CODE = 0;
    private int DELETE_TAG_CODE = 1;

    private View view;
    private Frag2TagData frag2TagData;

    private EditText editText;
    private TextView totalCnt;
    public String msg;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference searchRef = firebaseDatabase.getReference();
    // 데이터베이스 값을 실시간으로 갱신하기 위해 정의

    private DatabaseReference listRef, dataRef;

    private ListView listView;
    private ArrayAdapter<String> adapter; // array배열 생성, listview와 연결
    List<Object> Array = new ArrayList<Object>();

    private String search_keyword;

    private String click_data;

    private Button addButton, deleteButton;

    private Toast deleteMsg;


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==EDIT_DELETE_CODE){  // 롱클릭
            if(resultCode==EditDeleteDialog.RESULT_EDIT){
                // 편집 액티비티 띄우기
                Intent intent = new Intent(getActivity(), AddTagActivity.class);
                intent.putExtra("tag", click_data);
                intent.putExtra("mode", "edit");
                startActivity(intent);
            }
            else if(resultCode==EditDeleteDialog.RESULT_DELETE){
                Intent intent = new Intent(getActivity(), CheckDialog.class);
                intent.putExtra("msg", "태그 정보가 모두 삭제됩니다.\n정말 삭제하시겠습니까?");
                startActivityForResult(intent, DELETE_TAG_CODE);
            }
            else if(resultCode==EditDeleteDialog.RESULT_CANCELED){
                // do nothing
            }
        }
        else if(requestCode==DELETE_TAG_CODE){  // 태그 삭제 확인
            Log.d("TAG_DELETE_CHECK", "result data = "+resultCode);

            if(resultCode==RESULT_OK){
                // 태그 정보 모두 삭제
                listRef.child(click_data).removeValue(); // tag 목록에서 삭제

                // 태그 목록에서 삭제한 태그 정보 tag1, tag2에 있으면 지워줌
                dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot data : dataSnapshot.getChildren()){
                            String word = data.getKey();
                            String tag1 = data.child("tag1").getValue(String.class);
                            String tag2 = data.child("tag2").getValue(String.class);

                            if(tag1.equals(click_data)){
                                dataRef.child(word).child("tag1").setValue("");
                            }
                            if(tag2.equals(click_data)){
                                dataRef.child(word).child("tag2").setValue("");
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                adapter.notifyDataSetChanged();

                deleteMsg = Toast.makeText(getActivity(), "성공적으로 삭제되었습니다.", Toast.LENGTH_SHORT);
                deleteMsg.setGravity(Gravity.BOTTOM, Gravity.CENTER_HORIZONTAL, 200);
                deleteMsg.show();
            }
        }

    }

    public static Frag2Tag newInstance(){
        return new Frag2Tag();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag2_tag, container, false);
        listRef = firebaseDatabase.getReference("dictionary").child("tag_list");
        dataRef = firebaseDatabase.getReference("dictionary").child("word_list");

        // 태그 복구용
       /* dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    String tag1 = data.child("tag1").getValue(String.class);
                    String tag2 = data.child("tag2").getValue(String.class);

                    if(!tag1.equals("")){
                        listRef.child(tag1).setValue(0);
                    }
                    if(!tag2.equals("")){
                        listRef.child(tag2).setValue(0);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/




        editText = view.findViewById(R.id.input); // 검색어입력란

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search_keyword = s.toString();
                searchRef = firebaseDatabase.getReference("dictionary").child("tag_list");
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
                                String dataItem = "# " + data.getKey();
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


        listView = view.findViewById(R.id.tagList);

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<String>());
        listView.setAdapter(adapter);

        listRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.clear();

                for(DataSnapshot messageData : dataSnapshot.getChildren()){
                    String msg2 = "# " + messageData.getKey();
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
                click_data = adapter.getItem(position);
                ((MainActivity)getActivity()).replaceFragment(frag2TagData.newInstance(click_data));
            }
        });



        // ***
        // long click 하면 편집/삭제 고르는 다이얼로그 띄우기
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                click_data = adapter.getItem(position);
                click_data = click_data.substring(2);
                Intent intent = new Intent(getActivity(), EditDeleteDialog.class);
                startActivityForResult(intent, EDIT_DELETE_CODE);
                return true;
            }
        });



        addButton = view.findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() { // 단어추가버튼을 누르면 단어추가하는 fragment로 교체
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddTagActivity.class);
                intent.putExtra("tag", "");
                intent.putExtra("mode", "add");
                startActivity(intent);
            }
        });


        deleteButton = view.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).replaceFragment(new Frag2TagDelete().newInstance());
            }
        });










        return view;
    }
    // ctrl+O, 검색하면 됨
    // fragment는 onCreateView로 생성하면 됨



}
