package com.SeonWoo.ColorCheck;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    public static final int REQUEST_CODE_MAIN = 101;
    public static final int REQUEST_CODE_FIND_ID = 102;
    public static final int REQUEST_CODE_FIND_PASSWORD = 103;
    public static final int REQUEST_CODE_SIGN_UP = 104;

    InputMethodManager imm;
    EditText emailInput;
    EditText passwordInput;
    ConstraintLayout loginLayout;
    TextView findID;
    TextView findPassword;
    Button Login;
    Button SignUp;
    TextInputLayout TIL1;
    CheckBox AutoLogin;
    SharedPreferences pref_member;


    @Override
    protected void onPause() {
        super.onPause();
        Log.v("위치체크","Login_onPause");
        emailInput = findViewById(R.id.login_et_emailInput);
        passwordInput = findViewById(R.id.login_et_passwordInput);

        emailInput.setText("");
        passwordInput.setText("");
        emailInput.clearFocus();
        passwordInput.clearFocus();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("위치체크","Login_onDestroy");

    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.v("위치체크","Login_onStart");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.v("위치체크","Login_onResume");


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.v("위치체크","Login_onCreate");

        TIL1 = findViewById(R.id.textInputLayout3);

        emailInput = findViewById(R.id.login_et_emailInput);
        passwordInput = findViewById(R.id.login_et_passwordInput);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        loginLayout = findViewById(R.id.Login_layout);
        findID = findViewById(R.id.login_tv_findID);
        Login = findViewById(R.id.login_bt_loginButton);
        SignUp = findViewById(R.id.login_bt_signupButton);
        findPassword = findViewById(R.id.login_tv_findPassword);
        AutoLogin = findViewById(R.id.login_cb_AutoLogin);

        // 키보드 가리기
        loginLayout.setOnClickListener(myClickListener);
        Login.setOnClickListener(myClickListener);
        SignUp.setOnClickListener(myClickListener);

        pref_member = getSharedPreferences("Member",MODE_PRIVATE);





        // 이메일 입력 체크
        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9]+\\.[a-zA-Z.-]{2,6}$";
                String email = emailInput.getText().toString();
                Matcher matcher = Pattern.compile(emailPattern).matcher(email);
                if(email.length()==0){
                    TIL1.setError(null);
                }
                else {
                    if (!matcher.matches()) {
                        TIL1.setError("이메일 형식이 올바르지 않습니다.");
                    } else {
                        TIL1.setError(null); // null은 에러 메시지를 지워주는 기능
                    }
                }

            }
        });


        // 아이디 찾기

        findID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FindID.class);
                startActivityForResult(intent, REQUEST_CODE_FIND_ID);
            }
        });

        // 패스워드 찾기

        findPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FindPassword.class);
                startActivityForResult(intent, REQUEST_CODE_FIND_PASSWORD);
            }
        });

        // 로그인

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //null일때 처리 구현
                String idTry = emailInput.getText().toString();
                String passwordTry = passwordInput.getText().toString();
                String member_password = pref_member.getString(idTry,"");

                if(member_password.contentEquals("")){
                    Toast.makeText(getApplicationContext(),
                            "아이디를 확인해주시기 바랍니다.", Toast.LENGTH_LONG).show();
                }

                else if(member_password.contentEquals(passwordTry)){
                    Toast.makeText(getApplicationContext(),
                            "로그인 되었습니다.", Toast.LENGTH_LONG).show();

                    SharedPreferences pref2 = getSharedPreferences("Logined",MODE_PRIVATE);
                    SharedPreferences.Editor editor2 = pref2.edit();
                    editor2.putString("ID",emailInput.getText().toString());
                    editor2.commit();

                    if(AutoLogin.isChecked()){
                        SharedPreferences pref = getSharedPreferences("autoLogin",MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("ID",emailInput.getText().toString());
                        editor.commit();
                        Intent intent = new Intent(getApplicationContext(), Main.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Intent intent = new Intent(getApplicationContext(), Main.class);
                        startActivityForResult(intent, REQUEST_CODE_MAIN);
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),
                            "비밀번호를 확인해주시기 바랍니다.", Toast.LENGTH_LONG).show();
                }
            }
        });

        // 회원가입

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivityForResult(intent, REQUEST_CODE_SIGN_UP);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_CODE_FIND_ID) {

            if (resultCode == RESULT_OK) {

            }
        } else if (requestCode == REQUEST_CODE_FIND_PASSWORD) {

            if (resultCode == RESULT_OK) {

            }
        } else if (requestCode == REQUEST_CODE_MAIN) {

            if (resultCode == RESULT_OK) {
                // 로그인된 아이디 정보 없애기.
                SharedPreferences pref = getSharedPreferences("Logined",MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.remove("ID");
                editor.commit();

            }
        } else if (requestCode == REQUEST_CODE_SIGN_UP) {

            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(getApplicationContext(), Nickname.class);
                startActivity(intent);
                finish();
            }
        }

    }

    View.OnClickListener myClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideKeyboard();
//            switch (v.getId())
//            {
//                case R.id.Login_layout :
//                    break;
//
//                case R.id.Login :
//                    break;
//                case R.id.Signup :
//                    break;
//            }
        }
    };

    private void hideKeyboard() {
        imm.hideSoftInputFromWindow(emailInput.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(passwordInput.getWindowToken(), 0);
    }
}


