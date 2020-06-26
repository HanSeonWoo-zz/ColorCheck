package com.SeonWoo.ColorCheck;

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
    SharedPreferences pref;

    Calendar myCalendar = Calendar.getInstance();

    //데이터 피커 다이얼로그 설정
    DatePickerDialog.OnDateSetListener myDatePicker;

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
        Log.v("위치 체크", "AddOrEdit_onCreate");

        pref = getSharedPreferences("1", MODE_PRIVATE);

        // Pos : 리사이클러뷰에서의 위치
        // type : ADD / EDIT
        Intent intent = getIntent();
        Pos = intent.getIntExtra("Position", -1);
        type = intent.getStringExtra("Type");

        date = findViewById(R.id.AD_date);
        pink = findViewById(R.id.AD_pink);
        orange = findViewById(R.id.AD_orange);
        green = findViewById(R.id.AD_green);
        blue = findViewById(R.id.AD_blue);
        purple = findViewById(R.id.AD_purple);
        bt = findViewById(R.id.AD_bt);

        mData = getGsonPref();


        if (type.contentEquals("ADD")) {
            bt.setText("추가하기");
            Date currentTime = Calendar.getInstance().getTime();
            String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 E", Locale.getDefault()).format(currentTime);
            date.setText(date_text);
        } else if (type.contentEquals("EDIT")) {
            bt.setText("수정하기");
            if (mData.size() > 0) {
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

                // 추가하거나 편집하려는 날짜가 있으면 기존 데이터 삭제
                for(int i = 0 ; i < mData.size() ; i ++){
                    if(mData.get(i).getDate().contentEquals(cr.getDate())){
                        mData.remove(i);
                        break;
                    }
                }
                if (type.contentEquals("ADD")) {
                    mData.add(0, cr);

                } else if (type.contentEquals("EDIT")) {
                    mData.set(Pos, cr);

                }

                setGsonPref(mData);
                finish();
            }
        });

        myDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                // 데이터 선택 시 업데이트.
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 E", Locale.KOREA);
                date.setText(sdf.format(myCalendar.getTime()));
            }
        };


    }

    private ArrayList<com.SeonWoo.ColorCheck.Color> getGsonPref() {
        String json = pref.getString("History", "");
        Gson gson = new Gson();

        ArrayList<com.SeonWoo.ColorCheck.Color> urls = new ArrayList<>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);

                for (int i = 0; i < a.length(); i++) {
                    com.SeonWoo.ColorCheck.Color url = gson.fromJson(a.optString(i), com.SeonWoo.ColorCheck.Color.class);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

    private void setGsonPref(ArrayList<com.SeonWoo.ColorCheck.Color> classes) {
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
