package com.SeonWoo.ColorCheck;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Intent intent = new Intent(getApplicationContext(), FirstAuthActivity.class);
        startActivity(intent);
        finish();

    }


}
