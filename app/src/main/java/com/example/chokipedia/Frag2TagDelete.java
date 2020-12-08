package com.example.chokipedia;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class Frag2TagDelete extends Fragment {

    private View view;

    private Frag2TagData frag2TagData;

    private EditText editText;
    private TextView totalCnt;
    public String msg;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference searchRef = firebaseDatabase.getReference();

    private ChildEventListener mChild;
    // 데이터베이스 값을 실시간으로 갱신하기 위해 정의

    private DatabaseReference listRef, delRef, dataRef;
    private String delete_data;


    private ListView listView;
    private ArrayAdapter<String> adapter;
    // array배열 생성, listview와 연결
    List<String> Array = new ArrayList<String>();

    private String search_keyword;

    private Button deleteButton, cancelButton;

    private String click_data, flag;

    private String click_tag_data;

    private Toast deleteMsg;

    public static Frag2TagDelete newInstance(){
        Frag2TagDelete fragment = new Frag2TagDelete();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag2_tag_delete, container, false);
        //setContentView와 유사



        listView = view.findViewById(R.id.wordList);

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_multiple_choice, new ArrayList<String>());
        // 아이템 view를 선택가능하도록 만듦 : simple_list_item_multiple_choice

        listView.setAdapter(adapter);

        ConstraintLayout backButton = view.findViewById(R.id.bt_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.clearChoices();
                ((MainActivity)getActivity()).setFrag(1);
            }
        });


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
                                if (dataItem.compareTo(search_keyword) == 0) {
                                    dataItem = "# " + dataItem;
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




        listRef = firebaseDatabase.getReference("dictionary").child("tag_list");
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




        delRef = firebaseDatabase.getReference("dictionary").child("state").child("tag_delete");
        dataRef = firebaseDatabase.getReference("dictionary").child("word_list");

        delRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                flag = dataSnapshot.getValue(String.class);
                if(flag.compareTo("true")==0){
                    System.out.println("삭제하는 코드 실행");
                    SparseBooleanArray checkedItems;
                    checkedItems = listView.getCheckedItemPositions();

                    int count = adapter.getCount() ;
                    for (int i = count-1; i >= 0; i--) {
                        if (checkedItems.get(i)) {
                            // 삭제코드
                            delete_data = adapter.getItem(i);
                            delete_data = delete_data.substring(2);

                            if(flag.compareTo("true")==0){ // 삭제 후 다시 추가하는 경우를 위해
                                listRef.child(delete_data).removeValue(); // tag목록에서 삭제
                                System.out.println("ㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁ 태그명 "+delete_data);

                                dataRef.orderByChild("tag1").equalTo(delete_data).addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                        if(flag.compareTo("true")==0) // 체크해줘야함!!
                                            dataSnapshot.getRef().child("tag1").setValue("");
                                    }

                                    @Override
                                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                    }

                                    @Override
                                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                dataRef.orderByChild("tag2").equalTo(delete_data).addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                        if(flag.compareTo("true")==0)
                                            dataSnapshot.getRef().child("tag2").setValue("");
                                    }

                                    @Override
                                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                    }

                                    @Override
                                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }
                    // 모든 선택 상태 초기화.
                    listView.clearChoices();
                    adapter.notifyDataSetChanged();

//                    ((MainActivity)getActivity()).replaceFragment(frag1Basic.newInstance());
                    ((MainActivity)getActivity()).setFrag(1);
                    delRef.setValue("null");
                    deleteMsg = Toast.makeText(getActivity(), "성공적으로 삭제되었습니다.", Toast.LENGTH_SHORT);
                    deleteMsg.setGravity(Gravity.BOTTOM, Gravity.CENTER_HORIZONTAL, 200);
                    deleteMsg.show();
                }
                else if(flag.compareTo("false")==0){
                    System.out.println("아무 변화 없음");
//                    ((MainActivity)getActivity()).replaceFragment(frag1Basic.newInstance());
//                    ((MainActivity)getActivity()).setFrag(0);
                }
                else{
                    System.out.println("아무 변화 없음");
//                    ((MainActivity)getActivity()).replaceFragment(frag1Basic.newInstance());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        deleteButton = view.findViewById(R.id.delete_button);
        cancelButton = view.findViewById(R.id.cancel_button);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TagDeleteAlertDialog.class);
                startActivity(intent);
            }
        });



        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.clearChoices();
                ((MainActivity)getActivity()).setFrag(1);
            }
        });




        return view;
    }
    // ctrl+O, 검색하면 됨
    // fragment는 onCreateView로 생성하면 됨

}
