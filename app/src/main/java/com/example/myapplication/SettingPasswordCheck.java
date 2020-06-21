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

    EditText passwordCheck;
    Intent intent;
    Handler mHandler = new Handler();
    InputMethodManager imm;
    InputMethodManager immhide;
    SharedPreferences pref_Logined;
    SharedPreferences pref_subPassword;
    SharedPreferences pref_useSubPassword;
    SharedPreferences.Editor editor_subPassword;
    SharedPreferences.Editor editor_useSubPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_password_check);


        passwordCheck = findViewById(R.id.setting_passwordCheck_password);
        intent = getIntent();
        pref_Logined = getSharedPreferences("Logined", MODE_PRIVATE);
        pref_subPassword = getSharedPreferences("subPassword", MODE_PRIVATE);
        pref_useSubPassword = getSharedPreferences("useSubPassword", MODE_PRIVATE);
        editor_subPassword = pref_subPassword.edit();
        editor_useSubPassword = pref_useSubPassword.edit();


        mHandler.postDelayed(new Runnable() {
            public void run() {
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }, 200);
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
                    // 키보드 내리기
                    immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                   if(passwordCheck.getText().toString().contentEquals(intent.getExtras().getString("Password"))){
                       setResult(RESULT_OK);
                       editor_subPassword.putString(pref_Logined.getString("ID",""),passwordCheck.getText().toString());
                       editor_subPassword.commit();

                       editor_useSubPassword.putBoolean(pref_Logined.getString("ID",""),true);
                       editor_useSubPassword.commit();
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
