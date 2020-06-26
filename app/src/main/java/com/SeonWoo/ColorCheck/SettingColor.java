package com.SeonWoo.ColorCheck;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class SettingColor extends AppCompatActivity {
    SharedPreferences pref;
    Button correct;
    EditText et_pink;
    EditText et_orange;
    EditText et_green;
    EditText et_blue;
    EditText et_purple;

    InputMethodManager imm;
    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_color);

        pref = getSharedPreferences("1", MODE_PRIVATE);

        // 뷰 매칭
        correct = findViewById(R.id.color_bt_correct);
        et_pink = findViewById(R.id.text_pink);
        et_orange = findViewById(R.id.text_orange);
        et_green = findViewById(R.id.text_green);
        et_blue = findViewById(R.id.text_blue);
        et_purple = findViewById(R.id.text_purple);

        // 수정된 버전 세팅
        et_pink.setText(pref.getString("PINK","자습"));
        et_orange.setText(pref.getString("ORANGE","수업"));
        et_green.setText(pref.getString("GREEN","개인업무"));
        et_blue.setText(pref.getString("BLUE","자기계발"));
        et_purple.setText(pref.getString("PURPLE","네트워킹"));

        // 키보드 내려가게 하기 위한 셋팅
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        constraintLayout = findViewById(R.id.signup_Layout);
        constraintLayout.setOnClickListener(myClickListener);


        // 수정하기 버튼
        correct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("PINK", et_pink.getText().toString());
                editor.putString("ORANGE", et_orange.getText().toString());
                editor.putString("GREEN", et_green.getText().toString());
                editor.putString("BLUE", et_blue.getText().toString());
                editor.putString("PURPLE", et_purple.getText().toString());
                editor.commit();
                Toast.makeText(getApplicationContext(), "수정되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    // 배경 클릭 시 키보드 내려가는 기능
    View.OnClickListener myClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideKeyboard();
        }
    };

    private void hideKeyboard() {
        imm.hideSoftInputFromWindow(et_pink.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(et_orange.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(et_green.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(et_blue.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(et_purple.getWindowToken(), 0);
    }
}
