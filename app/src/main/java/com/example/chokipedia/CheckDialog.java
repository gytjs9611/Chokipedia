package com.example.chokipedia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class CheckDialog extends Activity {

    private String mAlertMsg = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // title bar 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_alert_dialog);
        TextView alertMsg = findViewById(R.id.et_input);

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
