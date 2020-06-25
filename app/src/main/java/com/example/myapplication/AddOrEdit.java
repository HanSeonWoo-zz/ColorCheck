package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddOrEdit extends AppCompatActivity {

    Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    EditText date;
    EditText pink;
    EditText orange;
    EditText green;
    EditText blue;
    EditText purple;
    ArrayList<Color> mData;
    Button bt;
    String type;
    int Pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit);
        Log.v("위치 체크","AddOrEdit_onCreate");

        Intent intent = getIntent();
        Pos = intent.getIntExtra("Position",-1);
        type = intent.getStringExtra("Type");

        date = findViewById(R.id.AD_date);
        pink = findViewById(R.id.AD_pink);
        orange = findViewById(R.id.AD_orange);
        green = findViewById(R.id.AD_green);
        blue = findViewById(R.id.AD_blue);
        purple = findViewById(R.id.AD_purple);
        bt=findViewById(R.id.AD_bt);

        mData = getGsonPref();


        if(type.contentEquals("ADD")){
            bt.setText("추가하기");
            Date currentTime = Calendar.getInstance().getTime();
            String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 E", Locale.getDefault()).format(currentTime);

//            SimpleDateFormat weekdayFormat = new SimpleDateFormat("EE", Locale.getDefault());
//            SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
//            SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
//            SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
//            String weekDay = weekdayFormat.format(currentTime);
//            String year = yearFormat.format(currentTime);
//            String month = monthFormat.format(currentTime);
//            String day = dayFormat.format(currentTime);

            date.setText(date_text);
        }
        else if(type.contentEquals("EDIT")){
            bt.setText("수정하기");
            if(mData.size()>0) {
                date.setText(mData.get(Pos).getDate());
                pink.setText(mData.get(Pos).getPink());
                orange.setText(mData.get(Pos).getOrange());
                green.setText(mData.get(Pos).getGreen());
                blue.setText(mData.get(Pos).getBlue());
                purple.setText(mData.get(Pos).getPurple());
            }
        }

        // 날짜 / 데이터 픽커
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddOrEdit.this, myDatePicker, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        // 수정 버튼 : 저장 후 Activity 종료
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Color cr = new Color(date.getText().toString(), pink.getText().toString(),
                        orange.getText().toString(), green.getText().toString(), blue.getText().toString(), purple.getText().toString());
                if(type.contentEquals("ADD")){
                    mData.add(0,cr);
                }
                else if(type.contentEquals("EDIT")){
                    mData.set(Pos, cr);
                }


                try {
                    setGsonPref(mData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });



    }

    private void updateLabel() {
        String myFormat = "yyyy년 MM월 dd일 E";    // 출력형식   2018년 12월 25일 금요일
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);

        EditText et_date = findViewById(R.id.AD_date);
        et_date.setText(sdf.format(myCalendar.getTime()));
    }
    private ArrayList<Color> getGsonPref() {
        SharedPreferences pref = getSharedPreferences("Logined",MODE_PRIVATE);
        String id = pref.getString("ID","");
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
        SharedPreferences pref = getSharedPreferences("Logined", MODE_PRIVATE);
        String id = pref.getString("ID","");
        SharedPreferences prefs = getSharedPreferences("History", MODE_PRIVATE);
        Gson gson = new Gson();
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray ar = new JSONArray();


        for (int i = 0; i < classes.size(); i++) {
            ar.put(gson.toJson(classes.get(i)));
        }

        if (!classes.isEmpty()) {
            editor.putString(id, ar.toString());
            Log.v("값 체크",ar.toString());
        } else {
            editor.putString(id, null);
        }
        editor.commit();
    }
}
