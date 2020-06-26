package com.SeonWoo.ColorCheck;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class FirstAuthActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_auth);

        SharedPreferences pref = getSharedPreferences("autoLogin",MODE_PRIVATE);
        if(pref.getString("ID","").length() == 0) {
            // call Login Activity

            intent = new Intent(FirstAuthActivity.this, Login.class);
            //intent = new Intent(FirstAuthActivity.this, Main.class);

            startActivity(intent);
            this.finish();
        } else {
            // Call Next Activity
            intent = new Intent(FirstAuthActivity.this, Main.class);
            //intent.putExtra("STD_NUM", SaveSharedPreference.getUserName(this));
            startActivity(intent);
            this.finish();
        }
    }
}
