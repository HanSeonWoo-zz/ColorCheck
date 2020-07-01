package com.SeonWoo.ColorCheck;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class Static_Day extends AppCompatActivity {
    SharedPreferences pref;
    RecyclerView pieRecycler;
    ArrayList<PieData> mPieData;
    private ArrayList<Color> mArrayList;
    private PieChartAdapter pAdapter;
    EditText PickedDate;
    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener myDatePicker;

    int PINK = 0XFFFE2E9A;
    int ORANGE = 0XFFFF8000;
    int GREEN = 0XFF1E8037;
    int BLUE = 0XFF0000FF;
    int PURPLE = 0XFFA901DB;

    @Override
    protected void onPause() {
        super.onPause();
        //Pause 시 유저가 선택한 날짜 저장 -> 다시 실행 시 바로 보여주기.
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("PickedDate", PickedDate.getText().toString());
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("위치체크", "History_onResume");
        mArrayList = getGsonPref();
        Collections.sort(mArrayList);

        mPieData = setPieData();

        pieRecycler.setLayoutManager(new LinearLayoutManager(this));
        pAdapter = new PieChartAdapter(this, mPieData, pref.getString("PickedDate", "2020년 06월 15일 월"));
        pieRecycler.setAdapter(pAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_day);
        pref = getSharedPreferences("1", MODE_PRIVATE);
        PickedDate = findViewById(R.id.day_et_PickedDate);
        PickedDate.setText(pref.getString("PickedDate", "2020년 06월 15일 월"));
        mPieData = setPieData();
        Gson gson = new Gson();
        Log.v("값 체크", "Day_ mBarData" + mPieData.toString());
        Log.v("값 체크", "Day_ mBarData size " + mPieData.size());
        Log.v("값 체크", "Day_ mBarData 0_YMax " + mPieData.get(0).getYMax());

        myDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 E", Locale.KOREA);
                PickedDate.setText(sdf.format(myCalendar.getTime()));
            }
        };
        // 날짜 / 데이터 픽커
        PickedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Static_Day.this, myDatePicker, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

//         PickedDate 변경 시, 차트 업데이트
        PickedDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable arg0) {
                // 데이터 업데이트
                mPieData = setPieData();
                pAdapter = new PieChartAdapter(getApplicationContext(), mPieData, pref.getString("PickedDate", "2020년 06월 15일 월"));
                pieRecycler.setAdapter(pAdapter);

                // VISIBLE을 껐다가 켜주면 업데이트가 반영됨.
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        // 리사이클러뷰에 표시할 데이터 리스트 가져오기
        mArrayList = getGsonPref();
        Log.v("값 체크", "Day에서의 사이즈 : " + mArrayList.size());

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        pieRecycler = findViewById(R.id.recycler3);
        pieRecycler.setLayoutManager(new LinearLayoutManager(this));

        // 리사이클러뷰에 CustomAdapter 객체 지정.
        pAdapter = new PieChartAdapter(this, mPieData, pref.getString("PickedDate", "2020년 06월 15일 월"));
        pieRecycler.setAdapter(pAdapter);
    }

    private ArrayList<Color> getGsonPref() {
        String json = pref.getString("History", "");
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

    private ArrayList<PieData> setPieData() {
        mArrayList = getGsonPref();
        Collections.sort(mArrayList);
        ArrayList<PieData> pieDatas = new ArrayList<>(3);
        String pickedDate = PickedDate.getText().toString();

        ArrayList<PieEntry> entries_day = new ArrayList<>();
        ArrayList<PieEntry> entries_week = new ArrayList<>();
        ArrayList<PieEntry> entries_total = new ArrayList<>();

        // 데이터 설정
        float pink_day = 0;
        float orange_day = 0;
        float green_day = 0;
        float blue_day = 0;
        float purple_day = 0;

        float pink_week = 0;
        float orange_week = 0;
        float green_week = 0;
        float blue_week = 0;
        float purple_week = 0;

        float pink_total = 0;
        float orange_total = 0;
        float green_total = 0;
        float blue_total = 0;
        float purple_total = 0;

        Calendar cal = Calendar.getInstance();
        // 날짜 형식
        SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일 E", Locale.KOREA);

        for (int i = 0; i < 7; i++) {
            try {
                Date date = format.parse(pickedDate);
                cal.setTime(date);
                cal.add(Calendar.DATE, i);
                int Position = -1;
                for (int j = 0; j < mArrayList.size(); j++) {
                    if (mArrayList.get(j).getDate().contentEquals(format.format(cal.getTime()))) {
                        Position = j;
                        break;
                    }
                }
                if (Position == -1) {
                } else {
                    if (i == 0) {
                        pink_day += Float.parseFloat(mArrayList.get(Position).getPink());
                        orange_day += Float.parseFloat(mArrayList.get(Position).getOrange());
                        green_day += Float.parseFloat(mArrayList.get(Position).getGreen());
                        blue_day += Float.parseFloat(mArrayList.get(Position).getBlue());
                        purple_day += Float.parseFloat(mArrayList.get(Position).getPurple());
                    }
                    pink_week += Float.parseFloat(mArrayList.get(Position).getPink());
                    orange_week += Float.parseFloat(mArrayList.get(Position).getOrange());
                    green_week += Float.parseFloat(mArrayList.get(Position).getGreen());
                    blue_week += Float.parseFloat(mArrayList.get(Position).getBlue());
                    purple_week += Float.parseFloat(mArrayList.get(Position).getPurple());
                }
            } catch (ParseException e) {
                e.printStackTrace();

            }
        }

        for (int i = 0; i < mArrayList.size(); i++) {
            pink_total += Float.parseFloat(mArrayList.get(i).getPink());
            orange_total += Float.parseFloat(mArrayList.get(i).getOrange());
            green_total += Float.parseFloat(mArrayList.get(i).getGreen());
            blue_total += Float.parseFloat(mArrayList.get(i).getBlue());
            purple_total += Float.parseFloat(mArrayList.get(i).getPurple());
        }

        // 색상 의미 불러오기 / 없을 시 기본 설정
        entries_day.add(new PieEntry(pink_day, pref.getString("PINK", "자습")));
        entries_day.add(new PieEntry(orange_day, pref.getString("ORANGE", "수업")));
        entries_day.add(new PieEntry(green_day, pref.getString("GREEN", "개인업무")));
        entries_day.add(new PieEntry(blue_day, pref.getString("BLUE", "자기계발")));
        entries_day.add(new PieEntry(purple_day, pref.getString("PURPLE", "네트워킹")));

        entries_week.add(new PieEntry(pink_week, pref.getString("PINK", "자습")));
        entries_week.add(new PieEntry(orange_week, pref.getString("ORANGE", "수업")));
        entries_week.add(new PieEntry(green_week, pref.getString("GREEN", "개인업무")));
        entries_week.add(new PieEntry(blue_week, pref.getString("BLUE", "자기계발")));
        entries_week.add(new PieEntry(purple_week, pref.getString("PURPLE", "네트워킹")));

        entries_total.add(new PieEntry(pink_total, pref.getString("PINK", "자습")));
        entries_total.add(new PieEntry(orange_total, pref.getString("ORANGE", "수업")));
        entries_total.add(new PieEntry(green_total, pref.getString("GREEN", "개인업무")));
        entries_total.add(new PieEntry(blue_total, pref.getString("BLUE", "자기계발")));
        entries_total.add(new PieEntry(purple_total, pref.getString("PURPLE", "네트워킹")));


        PieDataSet dataSet_day = new PieDataSet(entries_day, "");
        PieDataSet dataSet_week = new PieDataSet(entries_week, "");
        PieDataSet dataSet_total = new PieDataSet(entries_total, "");

        // 거리두기
        dataSet_day.setSliceSpace(3f);
        dataSet_day.setSelectionShift(5f);

        dataSet_week.setSliceSpace(3f);
        dataSet_week.setSelectionShift(5f);

        dataSet_total.setSliceSpace(3f);
        dataSet_total.setSelectionShift(5f);

        // 컬러 설정
        dataSet_day.setColors(PINK, ORANGE, GREEN, BLUE, PURPLE);
        dataSet_week.setColors(PINK, ORANGE, GREEN, BLUE, PURPLE);
        dataSet_total.setColors(PINK, ORANGE, GREEN, BLUE, PURPLE);


        PieData piedata_day = new PieData(dataSet_day);
        PieData piedata_week = new PieData(dataSet_week);
        PieData piedata_total = new PieData(dataSet_total);
        piedata_day.setValueTextSize(10f);
        piedata_day.setValueTextColor(android.graphics.Color.YELLOW);
        piedata_week.setValueTextSize(10f);
        piedata_week.setValueTextColor(android.graphics.Color.YELLOW);
        piedata_total.setValueTextSize(10f);
        piedata_total.setValueTextColor(android.graphics.Color.YELLOW);

        pieDatas.add(piedata_day);
        pieDatas.add(piedata_week);
        pieDatas.add(piedata_total);

        return pieDatas;
    }


//
}

