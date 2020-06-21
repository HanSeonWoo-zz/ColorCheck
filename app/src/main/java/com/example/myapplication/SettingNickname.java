package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SettingNickname extends AppCompatActivity {

    Button button;
    EditText et;
    SharedPreferences pref_Logined;
    SharedPreferences pref_Nickname;
    SharedPreferences.Editor editor_Nickname;
    String nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_nickname);

        button = findViewById(R.id.SettingNickname_bt);
        et = findViewById(R.id.SettingNickname_et);
        pref_Logined = getSharedPreferences("Logined", MODE_PRIVATE);
        pref_Nickname = getSharedPreferences("Nickname", MODE_PRIVATE);
        editor_Nickname = pref_Nickname.edit();
        nickname = pref_Nickname.getString(pref_Logined.getString("ID",""),"");


        et.setText(nickname);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(nickname.contentEquals("")){
                    Log.v("값체크","Setting_Nickname : Logined정보가 안넘어 왔어");
                }
                else{
                    editor_Nickname.putString(pref_Logined.getString("ID",""), et.getText().toString());
                    editor_Nickname.commit();
                    finish();
                }



            }
        });



    }
}
