package com.SeonWoo.ColorCheck;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class TakeBackup extends AppCompatActivity {
    SharedPreferences pref;
    EditText et_takebackup;
    Button bt_takebackup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_backup);

        pref = getSharedPreferences("1",MODE_PRIVATE);

        et_takebackup = findViewById(R.id.et_takebackup);
        bt_takebackup = findViewById(R.id.bt_takebackup);

        bt_takebackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String json = et_takebackup.getText().toString();
                SharedPreferences.Editor editor = pref.edit();
                Log.v("값 체크","Before take "+gson.toJson(pref.getAll()));
                Log.v("값 체크","json : "+json);

                ArrayList<Color> urls = new ArrayList<>();
                if (json != null) {
                    try {
                        JSONArray a = new JSONArray(json);
                        Log.v("값 체크","JSONArray : "+ gson.toJson(a));

                        for (int i = 0; i < a.length(); i++) {
                            Color url = gson.fromJson(a.optString(i), Color.class);
                            urls.add(url);
                        }

                        Log.v("값 체크","urls : "+ gson.toJson(urls));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.v("위치 체크","JSONArray 생성 오류");
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                }

                if (!urls.isEmpty()) {
                    editor.putString("History", gson.toJson(urls));
                } else {
                    editor.putString("History", null);
                    setResult(RESULT_CANCELED);
                    finish();
                }
                editor.commit();

                Log.v("값 체크","After take "+gson.toJson(pref.getAll()));

                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
