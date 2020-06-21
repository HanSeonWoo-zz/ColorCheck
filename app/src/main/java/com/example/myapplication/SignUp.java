package com.example.myapplication;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {
    InputMethodManager imm;
    EditText emailInput;
    EditText passwordInput;
    EditText passwordCheck;
    Button signup;
    ConstraintLayout signupLayout;
    TextInputLayout TIL_email;
    TextInputLayout TIL_password;
    TextInputLayout TIL_passwordcheck;
    SharedPreferences pref_member;
    ImageView emailcheck;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        signupLayout = findViewById(R.id.signup_Layout);

        TIL_email= findViewById(R.id.signup_emailInputLayout);
        TIL_password= findViewById(R.id.signup_passwordInputLayout);
        TIL_password.setCounterEnabled(true);
        TIL_password.setCounterMaxLength(12);
        TIL_passwordcheck= findViewById(R.id.signup_passwordCheckLayout);

        pref_member=getSharedPreferences("Member",MODE_PRIVATE);

        emailInput = findViewById(R.id.signup_et_emailInput);
        passwordInput = findViewById(R.id.signup_et_passwordInput);
        passwordCheck = findViewById(R.id.signup_et_passwordCheck);
        signup = findViewById(R.id.signup_bt_signup);
        emailcheck= findViewById(R.id.emailcheck);
        emailcheck.setVisibility(View.INVISIBLE);

        // 클릭 시 키보드 가리는 것
        signupLayout.setOnClickListener(myClickListener);
        signup.setOnClickListener(myClickListener);


        // 이메일 형식 확인
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
                String idTry = emailInput.getText().toString();
                String passwordTry = pref_member.getString(idTry,"");

                Matcher matcher = Pattern.compile(emailPattern).matcher(idTry);
                if(idTry.length()==0){
                    TIL_email.setError(null);
                    emailcheck.setVisibility(View.INVISIBLE);
                }
                else{
                    if (!matcher.matches()) {
                        TIL_email.setError("이메일 형식이 올바르지 않습니다.");
                        emailcheck.setVisibility(View.INVISIBLE);

                    } else {
                        if(passwordTry.contentEquals("")) {
                            TIL_email.setError(null); // null은 에러 메시지를 지워주는 기능
                            emailcheck.setVisibility(View.VISIBLE);
                        }
                        else{
                            TIL_email.setError("이미 존재하는 아이디입니다.");
                            emailcheck.setVisibility(View.INVISIBLE);
                        }
                    }
                }


            }
        });

        // 비밀번호 조건 확인
        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String pwPattern = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}$";
                String password = passwordInput.getText().toString();
                boolean error=false;
                Log.v("값체크", "에러값 :" + error);
                Matcher matcher = Pattern.compile(pwPattern).matcher(password);

                pwPattern = "(.)\\1\\1\\1";
                Matcher matcher2 = Pattern.compile(pwPattern).matcher(password);

                if(passwordInput.getText().length()==0){
                    TIL_password.setError(null);
                }
                else{
                    if(!matcher.matches()){
                        // 영문 숫자 특수문자 조합 8~12
                        TIL_password.setError("영문/숫자/특수문자 8~12자리");
                        error=true;
                    }


                    if(matcher2.find()){
                        //같은 문자 4개 이상 불가
                        TIL_password.setError("같은 문자를 반복한 비밀번호는 올바르지 않습니다.");
                        error=true;
                    }

                    //if(password.contains(userId)){

                    //}
                    if(password.contains(" ")){
                        // 공백 불가
                        TIL_password.setError("공백은 입력할 수 없습니다.");
                        error=true;
                    }

                    if(!error){
                        TIL_password.setError(null);
                    }
                }



            }
        });

        // 비밀번호 체크 확인
        passwordCheck.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(passwordCheck.getText().length()==0){
                    TIL_passwordcheck.setError(null);
                }
                else{
                    if(passwordInput.getText().toString().contentEquals(passwordCheck.getText().toString())){
                        TIL_passwordcheck.setError(null);
                    }
                    else{
                        TIL_passwordcheck.setError("비밀번호를 확인하세요.");
                    }
                }

            }
        });


        // 회원가입 버튼
        signup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(TIL_email.getError()!=null){
                    Toast.makeText(getApplicationContext(), "아이디를 확인해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                }
                else if(TIL_password.getError()!=null){
                    Toast.makeText(getApplicationContext(), "비밀번호를 확인하시기 바랍니다.", Toast.LENGTH_SHORT).show();
                }
                else if(TIL_passwordcheck.getError()!=null){
                    Toast.makeText(getApplicationContext(), "비밀번호가 서로 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent();

                    // 자동로그인 아이디 저장
                    SharedPreferences pref = getSharedPreferences("autoLogin",MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("ID",emailInput.getText().toString());
                    editor.commit();

                    // 아이디 "Member"에 저장
                    SharedPreferences pref2 = getSharedPreferences("Member",MODE_PRIVATE);
                    SharedPreferences.Editor editor2 = pref2.edit();
                    editor2.putString(emailInput.getText().toString(),passwordInput.getText().toString());
                    editor2.commit();

                    // 현재 로그인된 아이디정보 저장
                    SharedPreferences pref3 = getSharedPreferences("Logined",MODE_PRIVATE);
                    SharedPreferences.Editor editor3 = pref3.edit();
                    editor3.putString("ID",emailInput.getText().toString());
                    editor3.commit();

                    // History 기본 값 넣어보기
                    ArrayList<Color> mArrayList = new ArrayList<>();
                    mArrayList.add(new Color("20.06.01.월", "10:30", "12:00", 1));
                    mArrayList.add(new Color("20.06.01.월", "13:00", "15:00", 2));
                    mArrayList.add(new Color("20.06.01.월", "15:00", "16:00", 5));
                    mArrayList.add(new Color("20.06.01.월", "16:00", "19:00", 3));
                    mArrayList.add(new Color("20.06.01.월", "19:00", "20:00", 5));
                    mArrayList.add(new Color("20.06.01.월", "20:30", "22:00", 1));

                    mArrayList.add(new Color("20.06.02.화", "09:00", "09:30", 5));
                    mArrayList.add(new Color("20.06.02.화", "10:30", "12:00", 1));
                    mArrayList.add(new Color("20.06.02.화", "13:00", "17:00", 1));
                    mArrayList.add(new Color("20.06.02.화", "17:00", "19:00", 3));
                    mArrayList.add(new Color("20.06.02.화", "20:00", "22:00", 1));
                    mArrayList.add(new Color("20.06.02.화", "22:00", "22:30", 1));
                    mArrayList.add(new Color("20.06.02.화", "22:30", "23:00", 4));

                    mArrayList.add(new Color("20.06.03.수", "09:00", "09:30", 4));
                    mArrayList.add(new Color("20.06.03.수", "09:30", "10:00", 1));
                    mArrayList.add(new Color("20.06.03.수", "10:00", "10:30", 3));
                    mArrayList.add(new Color("20.06.03.수", "10:30", "12:00", 1));
                    mArrayList.add(new Color("20.06.03.수", "13:00", "17:00", 1));
                    mArrayList.add(new Color("20.06.03.수", "17:00", "19:00", 3));
                    mArrayList.add(new Color("20.06.03.수", "20:00", "22:00", 1));
                    mArrayList.add(new Color("20.06.03.수", "22:00", "22:30", 1));
                    mArrayList.add(new Color("20.06.03.수", "22:30", "23:00", 4));

                    mArrayList.add(new Color("20.06.04.목", "09:00", "09:30", 4));
                    mArrayList.add(new Color("20.06.04.목", "09:30", "10:00", 1));
                    mArrayList.add(new Color("20.06.04.목", "10:00", "10:30", 3));
                    mArrayList.add(new Color("20.06.04.목", "10:30", "12:00", 1));
                    mArrayList.add(new Color("20.06.04.목", "13:00", "14:00", 1));
                    mArrayList.add(new Color("20.06.04.목", "14:00", "20:00", 2));
                    mArrayList.add(new Color("20.06.04.목", "20:00", "22:00", 3));
                    mArrayList.add(new Color("20.06.04.목", "22:00", "23:00", 5));
                    mArrayList.add(new Color("20.06.04.목", "23:30", "24:00", 5));

                    mArrayList.add(new Color("20.06.05.금", "09:00", "10:00", 1));
                    mArrayList.add(new Color("20.06.05.금", "10:00", "10:30", 3));
                    mArrayList.add(new Color("20.06.05.금", "10:30", "12:30", 1));
                    mArrayList.add(new Color("20.06.05.금", "13:30", "15:30", 1));
                    mArrayList.add(new Color("20.06.05.금", "15:30", "18:00", 3));
                    mArrayList.add(new Color("20.06.05.금", "19:00", "22:30", 1));
                    mArrayList.add(new Color("20.06.05.금", "22:30", "23:30", 5));

                    mArrayList.add(new Color("20.06.06.토", "11:00", "12:00", 3));
                    mArrayList.add(new Color("20.06.06.토", "12:00", "13:00", 5));
                    mArrayList.add(new Color("20.06.06.토", "13:00", "14:00", 5));
                    mArrayList.add(new Color("20.06.06.토", "14:00", "15:00", 3));
                    mArrayList.add(new Color("20.06.06.토", "15:00", "16:00", 5));
                    mArrayList.add(new Color("20.06.06.토", "16:00", "17:00", 3));
                    mArrayList.add(new Color("20.06.06.토", "17:00", "18:00", 5));
                    mArrayList.add(new Color("20.06.06.토", "18:30", "19:30", 4));
                    mArrayList.add(new Color("20.06.06.토", "19:30", "20:30", 4));
                    mArrayList.add(new Color("20.06.06.토", "20:30", "23:00", 5));

                    mArrayList.add(new Color("20.06.07.일", "08:30", "10:30", 5));
                    mArrayList.add(new Color("20.06.07.일", "13:00", "13:30", 4));
                    mArrayList.add(new Color("20.06.07.일", "13:30", "14:00", 1));
                    mArrayList.add(new Color("20.06.07.일", "14:00", "15:00", 3));
                    mArrayList.add(new Color("20.06.07.일", "15:00", "17:00", 1));
                    mArrayList.add(new Color("20.06.07.일", "17:00", "19:00", 3));
                    mArrayList.add(new Color("20.06.07.일", "20:00", "22:00", 1));
                    mArrayList.add(new Color("20.06.07.일", "22:00", "23:30", 5));

                    try {
                        //setColorObjectPref(emailInput.getText().toString(),mArrayList);
                        setGsonPref(emailInput.getText().toString(),mArrayList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });


    }
    View.OnClickListener myClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            hideKeyboard();
        }
    };

    private void hideKeyboard()
    {
        imm.hideSoftInputFromWindow(emailInput.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(passwordInput.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(passwordCheck.getWindowToken(), 0);

    }

    private void setColorObjectPref(String id, ArrayList<Color> classes) throws JSONException {
        SharedPreferences prefs = getSharedPreferences(id, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        JSONObject ob;
        JSONArray ar = new JSONArray();


        for (int i = 0; i < classes.size(); i++) {
            ob = new JSONObject();
            ob.put("date",classes.get(i).getDate());
            ob.put("StartTime",classes.get(i).getStartTime());
            ob.put("EndTime",classes.get(i).getEndTime());
            ob.put("Color",classes.get(i).getColor());
            ar.put(ob);
        }

        if (!classes.isEmpty()) {
            editor.putString(id, ar.toString());
            Log.v("값 체크",ar.toString());
        } else {
            editor.putString(id, null);
        }
        editor.apply();
    }

    private void setGsonPref(String id, ArrayList<Color> classes) throws JSONException {
        SharedPreferences prefs = getSharedPreferences("History", MODE_PRIVATE);
        Gson gson = new Gson();
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray ar = new JSONArray();


        for (int i = 0; i < classes.size(); i++) {
            ar.put(gson.toJson(classes.get(i)));
        }

        if (!classes.isEmpty()) {
            editor.putString(id, ar.toString());
            Log.v("값 체크",ar.toString());
        } else {
            editor.putString(id, null);
        }
        editor.apply();
    }
}

