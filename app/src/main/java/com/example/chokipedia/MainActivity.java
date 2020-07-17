package com.example.chokipedia;


import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    public static Context mContext; // 다른 액티비티에서 함수 호출하기 위해 선언

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private Frag1Basic basic;
    private Frag2Tag tag;
    private Frag3Review review;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        bottomNavigationView = findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.action_basic:
                        setFrag(0);
                        break;
                    case R.id.action_tag:
                        setFrag(1);
                        break;
                    case R.id.action_review:
                        setFrag(2);
                        break;
                }
                return true;
            }
        });

        basic = new Frag1Basic();
        tag = new Frag2Tag();
        review = new Frag3Review();
        // fragment 생성


        bottomNavigationView.setSelectedItemId(R.id.action_basic); // 메뉴 클릭된 상태로 만들기
        setFrag(0); // 첫 fragment 화면 지정
    }

    public void setFragClicked(int n){
        bottomNavigationView.setSelectedItemId(n);
    }

    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment).commit();
    }


    // fragment 교체가 일어나는 실행문
    private void setFrag(int n){
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction(); // fragment 가져와서 transaction 수행
        switch(n){ // 총 3가지의 fragment가 교체됨
            case 0:
                ft.replace(R.id.main_frame, basic);
                ft.commit(); // 저장을 의미
                break;
            case 1:
                ft.replace(R.id.main_frame, tag);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.main_frame, review);
                ft.commit();
                break;
        }
    }

}