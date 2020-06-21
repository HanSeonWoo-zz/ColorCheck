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

    Button button;
    EditText et;
    SharedPreferences pref_Logined;
    SharedPreferences pref_Nickname;
    SharedPreferences.Editor editor_Nickname;
    TextView tv_skip;
    ConstraintLayout nickname_layout;

    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname);

        button = findViewById(R.id.nickname_bt);
        et = findViewById(R.id.nickname_et);
        pref_Logined = getSharedPreferences("Logined", MODE_PRIVATE);
        pref_Nickname = getSharedPreferences("Nickname", MODE_PRIVATE);
        editor_Nickname = pref_Nickname.edit();
        nickname_layout=findViewById(R.id.nickname_layout);

        tv_skip = findViewById(R.id.nickname_skip);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // SKIP 클릭 시
        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("위치 체크","Nickname_SKIP");
                Intent intent = new Intent(getApplicationContext(), Main.class);
                startActivity(intent);
                finish();
            }
        });


        // 설정 버튼
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(pref_Logined.getString("ID","").contentEquals("")){
                    Log.v("값체크","Nickname : Logined정보가 안넘어 왔어");
                }
                else{
                    editor_Nickname.putString(pref_Logined.getString("ID",""), et.getText().toString());
                    editor_Nickname.commit();
                    Intent intent = new Intent(getApplicationContext(), Main.class);
                    startActivity(intent);
                    finish();
                }



            }
        });

        // 클릭 시 키보드 가리는 것
        nickname_layout.setOnClickListener(myClickListener);
        //tv_skip.setOnClickListener(myClickListener);



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
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }
}
