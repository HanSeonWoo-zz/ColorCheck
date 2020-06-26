package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SettingPasswordCheck extends AppCompatActivity {
    SharedPreferences pref;
    EditText passwordCheck;
    Intent intent;
    Handler mHandler = new Handler();
    InputMethodManager imm;
    InputMethodManager immhide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_password_check);

        pref = getSharedPreferences("1", MODE_PRIVATE);
        passwordCheck = findViewById(R.id.setting_passwordCheck_password);
        // SettingPassword에서 입력한 비밀번호를 받아오기 위한 intent
        intent = getIntent();

        // 200ms 후에 키보드를 올린다.
        // 바로 올리려고 하니까, 화면이 아직 준비 안된 상태라서 키보드가 올라오지 않았다.
        mHandler.postDelayed(new Runnable() {
            public void run() {
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                // 키보드 올리기
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }, 200);

        // 입력창에 포커스 주기
        passwordCheck.requestFocus();
        immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

        passwordCheck.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(passwordCheck.getText().toString().length()==4){
                    // 4자리 비밀번호를 입력하면 키보드 내리기
                    immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                    // 비밀번호 일치하면 SubPassword 저장하기
                   if(passwordCheck.getText().toString().contentEquals(intent.getExtras().getString("Password"))){
                       setResult(RESULT_OK);
                       SharedPreferences.Editor editor = pref.edit();
                       editor.putString("subPassword",passwordCheck.getText().toString());
                       editor.putBoolean("useSubPassword",true);
                       editor.commit();
                       finish();
                   }
                   else{
                       setResult(RESULT_CANCELED);
                       finish();
                   }

                }


            }
        });

    }
}
