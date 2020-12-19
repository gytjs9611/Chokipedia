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

public class EditDeleteDialog extends Activity {

    final static int RESULT_EDIT = 0;
    final static int RESULT_DELETE = 1;
    final static int RESULT_CANCELED = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // title bar 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_delete_dialog);
    }

    public void onClickEdit(View v){
        setResult(this.RESULT_EDIT);
        finish();
    }

    public void onClickDelete(View v){
        setResult(this.RESULT_DELETE);
        finish();
    }

    public void onClickCancel(View v){
        setResult(this.RESULT_CANCELED);
        finish();
    }


}
