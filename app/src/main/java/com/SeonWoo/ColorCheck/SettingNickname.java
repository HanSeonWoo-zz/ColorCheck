package com.SeonWoo.ColorCheck;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SettingNickname extends AppCompatActivity {
    SharedPreferences pref;
    Button button;
    EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_nickname);
        pref = getSharedPreferences("1",MODE_PRIVATE);
        button = findViewById(R.id.SettingNickname_bt);
        et = findViewById(R.id.SettingNickname_et);

        // 기존의 닉네임을 보여준다.
        et.setText(pref.getString("Nickname",""));

        // 버튼 클릭 시 입력한 닉네임으로 저장.
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("Nickname",et.getText().toString());
                editor.commit();
                finish();
            }
        });


    }
}
