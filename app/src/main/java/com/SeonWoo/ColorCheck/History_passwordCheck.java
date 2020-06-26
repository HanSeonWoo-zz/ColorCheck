package com.SeonWoo.ColorCheck;

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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class History_passwordCheck extends AppCompatActivity {
    SharedPreferences pref;
    EditText et;

    Handler mHandler = new Handler();
    InputMethodManager imm;
    InputMethodManager immhide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_password_check);

        pref = getSharedPreferences("1", MODE_PRIVATE);

        et = findViewById(R.id.history_password_et);

        // 키보드 올리기 (바로 올리면 안 떠서 딜레이를 주고 올림)
        mHandler.postDelayed(new Runnable() {
            public void run() {
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }, 200);

        // 포커스 주기
        et.requestFocus();

        //키보드 내리기 할 때 필요한 거
        immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et.getText().toString().length() == 4) {
                    //키보드 내리기
                    immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    if (et.getText().toString().contentEquals(pref.getString("subPassword", ""))) {
                        Intent intent = new Intent(getApplicationContext(), History.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
