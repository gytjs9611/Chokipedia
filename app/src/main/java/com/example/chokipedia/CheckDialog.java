package com.example.chokipedia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import java.text.BreakIterator;

public class CheckDialog extends Activity {

    private String mAlertMsg = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // title bar 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_alert_dialog);
        TextView alertMsg = findViewById(R.id.tv_msg);

        Intent intent = getIntent();
        mAlertMsg = intent.getStringExtra("msg");
        alertMsg.setText(mAlertMsg);
    }


    public void onClickOk(View v){
        setResult(RESULT_OK);
        finish();
    }
    public void onClickCancel(View v){
        setResult(RESULT_CANCELED);
        finish();
    }


}
