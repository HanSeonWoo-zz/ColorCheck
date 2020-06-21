package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Withdrawal extends AppCompatActivity {

    EditText password;
    Button Withdrawal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal);

        Withdrawal = findViewById(R.id.withdrawal_bt_withdrawal);
        password = findViewById(R.id.withdrawal_et_password);

        Withdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id;

                SharedPreferences pref_Logined = getSharedPreferences("Logined",MODE_PRIVATE);
                id=pref_Logined.getString("ID", "");

                SharedPreferences pref_Member = getSharedPreferences("Member",MODE_PRIVATE);
                SharedPreferences pref_autoLogin = getSharedPreferences("autoLogin",MODE_PRIVATE);
                SharedPreferences pref_useSubPassword = getSharedPreferences("useSubPassword",MODE_PRIVATE);
                SharedPreferences pref_Color = getSharedPreferences("Color"+pref_Logined.getString("ID",""),MODE_PRIVATE);

                // 비밀번호 체크
                if(password.getText().toString().contentEquals(pref_Member.getString(id,""))){


                    // 로그인된 정보 없애기
                    SharedPreferences.Editor editor = pref_Logined.edit();
                    editor.remove("ID");
                    editor.commit();

                    // Member에서 제거
                    SharedPreferences.Editor editor2 = pref_Member.edit();
                    editor2.remove(id);
                    editor2.commit();

                    // autoLogin에서 제거
                    SharedPreferences.Editor editor3 = pref_autoLogin.edit();
                    editor3.remove("ID");
                    editor3.commit();

                    // useSubPassword 제거
                    SharedPreferences.Editor editor4 = pref_useSubPassword.edit();
                    editor4.remove(id);
                    editor4.commit();

                    // Color 제거
                    SharedPreferences.Editor editor5 = pref_Color.edit();
                    editor5.clear();
                    editor5.commit();


                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "회원탈퇴", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }







            }
        });
    }
}
