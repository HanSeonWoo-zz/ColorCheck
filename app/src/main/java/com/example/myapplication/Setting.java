package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Setting extends AppCompatActivity {
    public static final int REQUEST_CODE_SETCOLOR = 101;
    public static final int REQUEST_CODE_PASSWORD = 102;
    public static final int REQUEST_CODE_ASK = 103;
    public static final int REQUEST_CODE_VERINFO = 104;
    public static final int REQUEST_CODE_LOGOUT = 105;
    public static final int REQUEST_CODE_WITHDRAWAL = 106;
    public static final int REQUEST_CODE_NICKNAME = 107;
    TextView setColor;
    Switch password;
    TextView ask;
    TextView VerInfo;
    TextView Logout;
    TextView Withdrawal;
    SharedPreferences pref_autoLogin;
    SharedPreferences pref_useSubPassword;
    SharedPreferences pref_Logined;
    SharedPreferences.Editor editor_autoLogin;
    SharedPreferences.Editor editor_useSubPassword;
    SharedPreferences.Editor editor_Logined;
    TextView Nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        pref_autoLogin = getSharedPreferences("autoLogin",MODE_PRIVATE);
        editor_autoLogin = pref_autoLogin.edit();
        pref_useSubPassword = getSharedPreferences("useSubPassword",MODE_PRIVATE);
        editor_useSubPassword = pref_useSubPassword.edit();
        pref_Logined = getSharedPreferences("Logined",MODE_PRIVATE);
        editor_Logined = pref_Logined.edit();

        // 초기화
        setColor = findViewById(R.id.setting_tv_setColor);
        password = findViewById(R.id.setting_sw_password);
        ask = findViewById(R.id.setting_tv_ask);
        VerInfo = findViewById(R.id.setting_tv_VerInfo);
        Logout = findViewById(R.id.setting_tv_Logout);
        Withdrawal = findViewById(R.id.setting_tv_Withdrawal);
        password.setChecked(pref_useSubPassword.getBoolean(pref_Logined.getString("ID",""),false));
        Nickname = findViewById(R.id.setting_tv_Nickname);

        // 닉네임 변경
        Nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingNickname.class);
                startActivityForResult(intent, REQUEST_CODE_NICKNAME);
            }
        });

        //컬러세팅
        setColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingColor.class);
                startActivityForResult(intent, REQUEST_CODE_SETCOLOR);
            }
        });

        //비밀번호설정
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!password.isChecked()){
                    password.setChecked(false);


                        editor_useSubPassword.putBoolean(pref_Logined.getString("ID",""),false);
                        editor_useSubPassword.commit();


                }
                else{
                    Intent intent = new Intent(getApplicationContext(), SettingPassword.class);
                    startActivityForResult(intent, REQUEST_CODE_PASSWORD);
                }

            }
        });

        //문의하기
        ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("plain/text");
                String[] address = {"gkstjsdn10@naver.com"};
                email.putExtra(Intent.EXTRA_EMAIL, address);
                email.putExtra(Intent.EXTRA_SUBJECT, "제목");
                email.putExtra(Intent.EXTRA_TEXT, "내용");
                startActivity(email);
            }
        });
        //버전정보
        VerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), VerInfo.class);
                startActivityForResult(intent, REQUEST_CODE_VERINFO);
            }
        });

        //로그아웃
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Setting.this);
                builder.setTitle("로그아웃");
                builder.setMessage("정말 로그아웃 하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(
                            DialogInterface dialog, int id) {
                        //"예" 버튼 클릭시 실행하는 메소드
                        Toast.makeText(getBaseContext(),"로그아웃되었습니다.", Toast.LENGTH_SHORT).show();

                        SharedPreferences pref = getSharedPreferences("autoLogin",MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.clear();
                        editor.commit();

                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("아니오",  new DialogInterface.OnClickListener() {
                    public void onClick(
                            DialogInterface dialog, int id) {
                        //"아니오" 버튼 클릭시 실행하는 메소드
                        Toast.makeText(getBaseContext(),"취소하셨습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.create().show();
            }
        });



        //회원탈퇴
        Withdrawal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), Withdrawal.class);
                    startActivityForResult(intent, REQUEST_CODE_WITHDRAWAL);
                }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_CODE_SETCOLOR) {

            if (resultCode == RESULT_OK) {

            }
        } else if (requestCode == REQUEST_CODE_PASSWORD) {

            if (resultCode == RESULT_OK) {

            }
            else{
                password.setChecked(false);
            }
        } else if (requestCode == REQUEST_CODE_ASK) {

            if (resultCode == RESULT_OK) {

            }
        } else if (requestCode == REQUEST_CODE_VERINFO) {

            if (resultCode == RESULT_OK) {

            }
        }else if (requestCode == REQUEST_CODE_LOGOUT) {

            if (resultCode == RESULT_OK) {

            }
        }else if (requestCode == REQUEST_CODE_WITHDRAWAL) {

            if (resultCode == RESULT_OK) {

            }
        }
    }
}
