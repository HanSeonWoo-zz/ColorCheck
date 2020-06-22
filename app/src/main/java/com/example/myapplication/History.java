package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;

public class History extends AppCompatActivity {

    private ArrayList<Color> mArrayList;
    private CustomAdapter mAdapter;
    RecyclerView recyclerView;
    private int count = -1;


    @Override
    protected void onResume(){
        super.onResume();
        Log.v("위치체크","History_onResume");
        mArrayList = getGsonPref();
        Collections.sort(mArrayList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this)) ;
        mAdapter = new CustomAdapter(this ,mArrayList) ;
        recyclerView.setAdapter(mAdapter) ;

        //mAdapter.notifyDataSetChanged();
        Log.v("값 체크","History_onResume에서 사이즈 : "+mArrayList.size());
    }
    @Override
    protected void onStop(){
        super.onStop();
        Log.v("위치체크","History_onStop");
    }
    @Override
    protected void onPause(){
        super.onPause();
        Log.v("위치체크","History_onPause");
        try {
            setGsonPref(mArrayList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
//        Log.v("위치체크","History_onDestroy");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // 리사이클러뷰에 표시할 데이터 리스트 가져오기
        mArrayList = getGsonPref();
        Log.v("값 체크","History에서의 사이즈 : "+mArrayList.size());

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        recyclerView = findViewById(R.id.recycler1) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this)) ;

        // 리사이클러뷰에 CustomAdapter 객체 지정.
        mAdapter = new CustomAdapter(this ,mArrayList) ;
        recyclerView.setAdapter(mAdapter) ;



        Button buttonInsert = findViewById(R.id.history_add);
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            // 1. 화면 아래쪽에 있는 데이터 추가 버튼을 클릭하면
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(History.this, AddOrEdit.class);
                intent.putExtra("Type","ADD");
                startActivity(intent);
            }
        });



    }

    private ArrayList<Color> getGsonPref() {
        SharedPreferences pref = getSharedPreferences("Logined",MODE_PRIVATE);
        String id  = pref.getString("ID","");
        Log.v("값 체크","getGsonPref_로그인된 아이디 : "+id);
        SharedPreferences prefs = getSharedPreferences("History",MODE_PRIVATE);
        String json = prefs.getString(id, null);
        Gson gson = new Gson();

        ArrayList<Color> urls = new ArrayList<>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);

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

    private void setGsonPref(ArrayList<Color> classes) throws JSONException {
        SharedPreferences pref = getSharedPreferences("Logined",MODE_PRIVATE);
        String id  = pref.getString("ID","");
        Log.v("값 체크","setGsonPref_로그인된 아이디 : "+id);
        SharedPreferences prefs = getSharedPreferences("History", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        if (!classes.isEmpty()) {
            editor.putString(id, gson.toJson(classes));
            Log.v("값 체크",gson.toJson(classes));
        } else {
            editor.putString(id, null);
        }
        editor.commit();
    }

}
