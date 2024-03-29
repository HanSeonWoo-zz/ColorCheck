package com.SeonWoo.ColorCheck;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

    int PINK = 0XFFFE2E9A;
    int ORANGE = 0XFFFF8000;
    int GREEN = 0XFF1E8037;
    int BLUE = 0XFF0000FF;
    int PURPLE = 0XFFA901DB;

    int[] Colorlist;

    Button box_pink;
    Button box_orange;
    Button box_green;
    Button box_blue;
    Button box_purple;


    @Override
    protected void onResume() {
        super.onResume();
        Log.v("위치체크", "History_onResume");
        mArrayList = getGsonPref();
        Collections.sort(mArrayList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CustomAdapter(this, mArrayList, Colorlist);
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

        box_pink = findViewById(R.id.box_pink);
        box_orange = findViewById(R.id.box_orange);
        box_green = findViewById(R.id.box_green);
        box_blue = findViewById(R.id.box_blue);
        box_purple = findViewById(R.id.box_purple);

        pink.setText(pref.getString("PINK", "자습"));
        orange.setText(pref.getString("ORANGE", "수업"));
        green.setText(pref.getString("GREEN", "개인업무"));
        blue.setText(pref.getString("BLUE", "자기계발"));
        purple.setText(pref.getString("PURPLE", "네트워킹"));

        Colorlist = new int[5];

        Colorlist[0] = pref.getInt("RGB_PINK",PINK);
        Colorlist[1] = pref.getInt("RGB_ORANGE",ORANGE);
        Colorlist[2] = pref.getInt("RGB_GREEN",GREEN);
        Colorlist[3] = pref.getInt("RGB_BLUE", BLUE);
        Colorlist[4] = pref.getInt("RGB_PURPLE",PURPLE);

        box_pink.setBackgroundColor(pref.getInt("RGB_PINK",PINK));
        box_orange.setBackgroundColor(pref.getInt("RGB_ORANGE",ORANGE));
        box_green.setBackgroundColor(pref.getInt("RGB_GREEN",GREEN));
        box_blue.setBackgroundColor(pref.getInt("RGB_BLUE",BLUE));
        box_purple.setBackgroundColor(pref.getInt("RGB_PURPLE",PURPLE));

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
                AlertDialog.Builder builder = new AlertDialog.Builder(History.this);

                builder.setTitle("기록 지우기").setMessage("모든 데이터를 지우시겠습니까?");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        mArrayList = new ArrayList<>();
                        setGsonPref(mArrayList);
                        setResult(RESULT_FIRST_USER);
                        Toast.makeText(getApplicationContext(), "지우기", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Toast.makeText(getApplicationContext(), "취소", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        // 리사이클러뷰에 표시할 데이터 리스트 가져오기
        mArrayList = getGsonPref();
        Log.v("값 체크", "History에서의 사이즈 : " + mArrayList.size());

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        recyclerView = findViewById(R.id.recycler1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 리사이클러뷰에 CustomAdapter 객체 지정.
        mAdapter = new CustomAdapter(this, mArrayList, Colorlist);
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
