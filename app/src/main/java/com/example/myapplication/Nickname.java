package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class Nickname extends AppCompatActivity {
    SharedPreferences pref;
    Button button;
    EditText et;
    TextView tv_skip;
    ConstraintLayout nickname_layout;

    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname);

        pref = getSharedPreferences("1", MODE_PRIVATE);
        button = findViewById(R.id.nickname_bt);
        et = findViewById(R.id.nickname_et);
        nickname_layout = findViewById(R.id.nickname_layout);
        tv_skip = findViewById(R.id.nickname_skip);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // SKIP 클릭 시
        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("위치 체크", "Nickname_SKIP");
                Intent intent = new Intent(getApplicationContext(), Main.class);
                startActivity(intent);
                finish();
            }
        });


        // 설정 버튼
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("Nickname", et.getText().toString());
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), Main.class);
                startActivity(intent);
                finish();
            }
        });

        // 클릭 시 키보드 가리는 것
        nickname_layout.setOnClickListener(myClickListener);
    }

    View.OnClickListener myClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideKeyboard();
        }
    };

    private void hideKeyboard() {
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }
}
