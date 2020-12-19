package com.example.chokipedia;

import android.content.Intent;
import android.media.Image;
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

public class Frag2TagDataDelete extends Fragment {

    private View view;

    private Frag2TagData frag2TagData;

    private TextView totalCnt;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    private DatabaseReference listRef, delRef;
    private String delete_data;


    private ListView listView;
    private ArrayAdapter<String> adapter;
    // array배열 생성, listview와 연결
    List<String> Array = new ArrayList<String>();


    private Button deleteButton, cancelButton;

    private String flag;

    private String click_tag_data;

    private Toast deleteMsg;

    private static int mPosition;


    public static Frag2TagDataDelete newInstance(String click_data, int position){
        Frag2TagDataDelete fragment = new Frag2TagDataDelete();
        Bundle bundle = new Bundle(1);
        bundle.putString("click_data", click_data);
        fragment.setArguments(bundle);
        mPosition = position;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag2_tagdata_delete, container, false);
        //setContentView와 유사

        click_tag_data = getArguments().getString("click_data");
        System.out.println(click_tag_data);

        Button tagDataName = view.findViewById(R.id.tagdata_name);
        tagDataName.setText(click_tag_data);

        ConstraintLayout backButton = view.findViewById(R.id.bt_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.clearChoices();
                ((MainActivity)getActivity()).replaceFragment(frag2TagData.newInstance(click_tag_data));
            }
        });



        listView = view.findViewById(R.id.wordList);

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_multiple_choice, new ArrayList<String>());
        // 아이템 view를 선택가능하도록 만듦 : simple_list_item_multiple_choice

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

            }
        });

        if(mPosition!=-1){
            listView.setItemChecked(mPosition, true);
        }
        else{
            listView.setItemChecked(mPosition, false);
        }




        delRef = firebaseDatabase.getReference("dictionary").child("state").child("tagdata_delete");


        delRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && getActivity()!=null){
                    flag = dataSnapshot.getValue().toString();
                    if(flag.compareTo("true")==0){
                        System.out.println("삭제하는 코드 실행");
                        SparseBooleanArray checkedItems;
                        checkedItems = listView.getCheckedItemPositions();

                        int count = adapter.getCount() ;
                        for (int i = count-1; i >= 0; i--) {
                            if (checkedItems.get(i)) {
                                // 삭제코드
                                delete_data = adapter.getItem(i);
                                if(flag.compareTo("true")==0){ // 삭제 후 다시 추가하는 경우를 위해
                                    listRef.child(delete_data).removeValue();
                                }
                            }
                        }
                        // 모든 선택 상태 초기화.
                        listView.clearChoices();
                        adapter.notifyDataSetChanged();

//                    ((MainActivity)getActivity()).replaceFragment(frag1Basic.newInstance());

                         delRef.setValue("null");
                         deleteMsg = Toast.makeText(getActivity(), "성공적으로 삭제되었습니다.", Toast.LENGTH_SHORT);
                         deleteMsg.setGravity(Gravity.BOTTOM, Gravity.CENTER_HORIZONTAL, 200);
                         deleteMsg.show();
                         ((MainActivity)getActivity()).replaceFragment(frag2TagData.newInstance(click_tag_data));


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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        deleteButton = view.findViewById(R.id.tagdata_delete_button);
        cancelButton = view.findViewById(R.id.tagdata_cancel_button);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyAlertDialog.class);
                intent.putExtra("isTagData", "true");
                startActivity(intent);
            }
        });



        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.clearChoices();
                ((MainActivity)getActivity()).replaceFragment(frag2TagData.newInstance(click_tag_data));
            }
        });




        return view;
    }
    // ctrl+O, 검색하면 됨
    // fragment는 onCreateView로 생성하면 됨

}
