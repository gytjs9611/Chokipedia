package com.example.chokipedia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyAlertDialog extends Activity {
    TextView textView;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference delRef = firebaseDatabase.getReference("dictionary").child("state");
    private String isTagData;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // title bar 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_alert_dialog);

        Intent intent = getIntent();
        isTagData = intent.getStringExtra("isTagData"); // basic delete인지, tagdata delete인지 구별
    }

    public void onClickOk(View v){
        if(isTagData.compareTo("true")==0){
            delRef.child("tagdata_delete").setValue("true");
        }
        else{
            delRef.child("delete").setValue("true");
        }
        finish();
    }
    public void onClickCancel(View v){
        if(isTagData.compareTo("true")==0){
            delRef.child("tagdata_delete").setValue("false");
        }
        else{
            delRef.child("tagdata_delete").setValue("false");
        }
        finish();
    }


}
