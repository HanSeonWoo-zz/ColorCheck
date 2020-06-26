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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SettingPassword extends AppCompatActivity {
    SharedPreferences pref;
    EditText password;
    Handler mHandler = new Handler();
    InputMethodManager imm;
    InputMethodManager immhide;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_password);

        pref = getSharedPreferences("1", MODE_PRIVATE);

        password = findViewById(R.id.setting_password_password);

        Intent intent = getIntent();
        // 비밀번호 확인 : CHECK / 비밀번호 설정 : SET
        type = intent.getExtras().getString("type");

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
                // 4자리 비밀번호를 입력받으면, SettingPasswordCheck 액티비티로 넘어간다.
                if (password.getText().toString().length() == 4) {
                    //키보드 가리기
                    immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                    if(type.contentEquals("SET")){
                        // 인텐트 부르기
                        Intent intent = new Intent(getApplicationContext(), SettingPasswordCheck.class);
                        // 인텐트의 Password에 입력받은 값을 저장
                        intent.putExtra("Password", password.getText().toString());
                        // EditText의 입력값을 지워줌으로써 마지막 순간에 입력받아졌다는 느낌을 줌.
                        password.setText("");
                        startActivityForResult(intent, 1);
                    }
                    else if(type.contentEquals("CHECK")){
                        // 입력한 비밀번호가 일치할 때
                        if(pref.getString("subPassword","").contentEquals(password.getText().toString())){
                            setResult(RESULT_FIRST_USER);
                            finish();
                        }
                        // 비밀번호가 틀리다.
                        else{
                            Toast.makeText(getApplicationContext(), "비밀번호 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check했을 시 값이 동일한 경우 설정 하고 종료.
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(),
                        "비밀번호가 설정됐습니다.", Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                finish();

            }

            // Check했을 시 값이 다른 경우 다시 비밀번호를 입력 받는다.
            else {
                setResult(RESULT_CANCELED);
                Toast.makeText(getApplicationContext(),
                        "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
            }
        }

    }
}
