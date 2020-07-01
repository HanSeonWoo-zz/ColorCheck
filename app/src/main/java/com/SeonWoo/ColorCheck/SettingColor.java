package com.SeonWoo.ColorCheck;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class SettingColor extends AppCompatActivity {

    public static final int REQUEST_CODE_PINK = 101;
    public static final int REQUEST_CODE_ORANGE = 102;
    public static final int REQUEST_CODE_GREEN = 103;
    public static final int REQUEST_CODE_BLUE = 104;
    public static final int REQUEST_CODE_PURPLE = 105;
    int PINK = 0XFFFE2E9A;
    int ORANGE = 0XFFFF8000;
    int GREEN = 0XFF1E8037;
    int BLUE = 0XFF0000FF;
    int PURPLE = 0XFFA901DB;


    SharedPreferences pref;
    Button correct;
    EditText et_pink;
    EditText et_orange;
    EditText et_green;
    EditText et_blue;
    EditText et_purple;

    Button pink;
    Button orange;
    Button green;
    Button blue;
    Button purple;

    Button reset;

    InputMethodManager imm;
    ConstraintLayout constraintLayout;

    @Override
    protected void onResume() {
        super.onResume();
        // 컬러 현황 업데이트
        pink.setBackgroundColor(pref.getInt("RGB_PINK",PINK));
        orange.setBackgroundColor(pref.getInt("RGB_ORANGE",ORANGE));
        green.setBackgroundColor(pref.getInt("RGB_GREEN",GREEN));
        blue.setBackgroundColor(pref.getInt("RGB_BLUE",BLUE));
        purple.setBackgroundColor(pref.getInt("RGB_PURPLE",PURPLE));

    }

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

        pink = findViewById(R.id.pink);
        orange = findViewById(R.id.orange);
        green = findViewById(R.id.green);
        blue = findViewById(R.id.blue);
        purple = findViewById(R.id.purple);

        reset = findViewById(R.id.ColorReset);

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

        // 리셋 버튼
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = pref.edit();
                editor.remove("PINK");
                editor.remove("ORANGE");
                editor.remove("GREEN");
                editor.remove("BLUE");
                editor.remove("PURPLE");
                editor.remove("RGB_PINK");
                editor.remove("RGB_ORANGE");
                editor.remove("RGB_GREEN");
                editor.remove("RGB_BLUE");
                editor.remove("RGB_PURPLE");
                editor.commit();
                Toast.makeText(getApplicationContext(), "컬러 정보가 리셋되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

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


        pink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CustomColor.class);
                intent.putExtra("CurrentColor",pref.getInt("RGB_PINK",PINK));
                startActivityForResult(intent, REQUEST_CODE_PINK);
            }
        });
        orange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CustomColor.class);
                intent.putExtra("CurrentColor",pref.getInt("RGB_ORANGE",ORANGE));
                startActivityForResult(intent, REQUEST_CODE_ORANGE);
            }
        });
        green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CustomColor.class);
                intent.putExtra("CurrentColor",pref.getInt("RGB_GREEN",GREEN));
                startActivityForResult(intent, REQUEST_CODE_GREEN);
            }
        });
        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CustomColor.class);
                intent.putExtra("CurrentColor",pref.getInt("RGB_BLUE",BLUE));
                startActivityForResult(intent, REQUEST_CODE_BLUE);
            }
        });
        purple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CustomColor.class);
                intent.putExtra("CurrentColor",pref.getInt("RGB_PURPLE",PURPLE));
                startActivityForResult(intent, REQUEST_CODE_PURPLE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SharedPreferences.Editor editor = pref.edit();

        if (requestCode == REQUEST_CODE_PINK) {
            if (resultCode == RESULT_OK) {
                editor.putInt("RGB_PINK",data.getIntExtra("ChangedColor",PINK));
            }
        }
        else if (requestCode == REQUEST_CODE_ORANGE) {
            if (resultCode == RESULT_OK) {
                editor.putInt("RGB_ORANGE",data.getIntExtra("ChangedColor",ORANGE));
            }
        }
        else if (requestCode == REQUEST_CODE_GREEN) {
            if (resultCode == RESULT_OK) {
                editor.putInt("RGB_GREEN",data.getIntExtra("ChangedColor",GREEN));
            }
        }
        else if (requestCode == REQUEST_CODE_BLUE) {
            if (resultCode == RESULT_OK) {
                editor.putInt("RGB_BLUE",data.getIntExtra("ChangedColor",BLUE));
            }
        }
        else if (requestCode == REQUEST_CODE_PURPLE) {
            if (resultCode == RESULT_OK) {
                editor.putInt("RGB_PURPLE",data.getIntExtra("ChangedColor",PURPLE));
            }
        }

        editor.commit();

    }
}
