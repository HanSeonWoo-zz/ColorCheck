package com.SeonWoo.ColorCheck;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;

public class History extends AppCompatActivity {
    SharedPreferences pref;
    private ArrayList<Color> mArrayList;
    private CustomAdapter mAdapter;
    RecyclerView recyclerView;
    TextView pink;
    TextView orange;
    TextView green;
    TextView blue;
    TextView purple;
    Button buttonInsert;
    Button delete;

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("위치체크", "History_onResume");
        mArrayList = getGsonPref();
        Collections.sort(mArrayList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CustomAdapter(this, mArrayList);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("위치체크", "History_onPause");
        setGsonPref(mArrayList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        pref = getSharedPreferences("1",MODE_PRIVATE);

        buttonInsert = findViewById(R.id.history_add);
        delete = findViewById(R.id.history_delete);

        pink = findViewById(R.id.main_tv1);
        orange = findViewById(R.id.main_tv2);
        green = findViewById(R.id.main_tv3);
        blue = findViewById(R.id.main_tv4);
        purple = findViewById(R.id.main_tv5);

        pink.setText(pref.getString("PINK", "자습"));
        orange.setText(pref.getString("ORANGE", "수업"));
        green.setText(pref.getString("GREEN", "개인업무"));
        blue.setText(pref.getString("BLUE", "자기계발"));
        purple.setText(pref.getString("PURPLE", "네트워킹"));

        // 추가 버튼
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            // 1. 화면 아래쪽에 있는 데이터 추가 버튼을 클릭하면
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(History.this, AddOrEdit.class);
                intent.putExtra("Type", "ADD");
                startActivity(intent);
            }
        });


        // 저장된 데이터 모두 제거
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mArrayList = new ArrayList<>();
                setGsonPref(mArrayList);
            }
        });

        // 리사이클러뷰에 표시할 데이터 리스트 가져오기
        mArrayList = getGsonPref();
        Log.v("값 체크", "History에서의 사이즈 : " + mArrayList.size());

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        recyclerView = findViewById(R.id.recycler1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 리사이클러뷰에 CustomAdapter 객체 지정.
        mAdapter = new CustomAdapter(this, mArrayList);
        recyclerView.setAdapter(mAdapter);

    }

    private ArrayList<Color> getGsonPref() {
        String json = pref.getString("History", null);
        Gson gson = new Gson();
        Log.v("값 체크","getGsonPref_json : " + json);
        ArrayList<Color> urls = new ArrayList<>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                Log.v("값 체크","getGsonPref_JSONArray");

                for (int i = 0; i < a.length(); i++) {
                    Color url = gson.fromJson(a.optString(i), Color.class);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

    private void setGsonPref(ArrayList<Color> classes) {
        SharedPreferences.Editor editor = pref.edit();
        Gson gson = new Gson();
        if (!classes.isEmpty()) {
            editor.putString("History", gson.toJson(classes));
            Log.v("값 체크", gson.toJson(classes));
        } else {
            editor.putString("History", null);
        }
        editor.commit();
    }

}
