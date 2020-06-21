package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SettingPassword extends AppCompatActivity {

    EditText password;
    Handler mHandler = new Handler();
    InputMethodManager imm;
    InputMethodManager immhide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_password);

        password = findViewById(R.id.setting_password_password);


        mHandler.postDelayed(new Runnable() {
            public void run() {
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }, 200);
        password.requestFocus();
        immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);


        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (password.getText().toString().length() == 4) {
                    immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    Intent intent = new Intent(getApplicationContext(), SettingPasswordCheck.class);
                    intent.putExtra("Password", password.getText().toString());
                    Log.v("값 체크", password.getText().toString());
                    password.setText("");
                    Log.v("값 체크", password.getText().toString());
                    startActivityForResult(intent, 1);

                }


            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1) {
            Log.v("위치 체크", "OnResult 들어오는가?");
            if (resultCode == RESULT_OK) {
                Log.v("위치 체크", "OnResult - RESULT_OK 들어오는가?");
                Toast.makeText(getApplicationContext(),
                        "비밀번호가 설정됐습니다.", Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                finish();

            } else {
                Log.v("위치 체크", "OnResult - RESULT_CANCEL 들어오는가?");
                setResult(RESULT_CANCELED);
                Toast.makeText(getApplicationContext(),
                        "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
            }
        }

    }
}
