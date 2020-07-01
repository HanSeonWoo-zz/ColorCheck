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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
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

public class Static_Color extends AppCompatActivity {
    SharedPreferences pref;
    RecyclerView barRecycler;
    ArrayList<BarChart> mBarChart;
    ArrayList<BarData> mBarData;
    private ArrayList<Color> mArrayList;
    private BarChartAdapter bAdapter;
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

        mBarData = setBarData();

        barRecycler.setLayoutManager(new LinearLayoutManager(this));
        bAdapter = new BarChartAdapter(this, mBarData, pref.getString("PickedDate", "2020년 06월 15일 월"));
        barRecycler.setAdapter(bAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_color);
        pref = getSharedPreferences("1", MODE_PRIVATE);
        PickedDate = findViewById(R.id.color_et_PickedDate);
        PickedDate.setText(pref.getString("PickedDate", "2020년 06월 15일 월"));
        mBarData = setBarData();
        Gson gson = new Gson();
        Log.v("값 체크", "Day_ mBarData" + mBarData.toString());
        Log.v("값 체크", "Day_ mBarData size " + mBarData.size());
        Log.v("값 체크", "Day_ mBarData 0_YMax " + mBarData.get(0).getYMax());

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
                new DatePickerDialog(Static_Color.this, myDatePicker, myCalendar.get(Calendar.YEAR),
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
                mBarData = setBarData();
                bAdapter = new BarChartAdapter(getApplicationContext(), mBarData, PickedDate.getText().toString());
                barRecycler.setAdapter(bAdapter);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        // 리사이클러뷰에 표시할 데이터 리스트 가져오기
        mArrayList = getGsonPref();
        Log.v("값 체크", "Day에서의 사이즈 : " + mArrayList.size());

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        barRecycler = findViewById(R.id.recycler2);
        barRecycler.setLayoutManager(new LinearLayoutManager(this));

        // 리사이클러뷰에 CustomAdapter 객체 지정.
        bAdapter = new BarChartAdapter(this, mBarData, pref.getString("PickedDate", "2020년 06월 15일 월"));
        barRecycler.setAdapter(bAdapter);
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

    private ArrayList<BarData> setBarData() {
        mArrayList = getGsonPref();
        Collections.sort(mArrayList);
        ArrayList<BarData> barDatas = new ArrayList<>(5);

        ArrayList<BarEntry> entries_pink = new ArrayList<>();
        ArrayList<BarEntry> entries_orange = new ArrayList<>();
        ArrayList<BarEntry> entries_green = new ArrayList<>();
        ArrayList<BarEntry> entries_blue = new ArrayList<>();
        ArrayList<BarEntry> entries_purple = new ArrayList<>();
        String pickedDate = PickedDate.getText().toString();

        Calendar cal = Calendar.getInstance();
        // 날짜 형식
        SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일 E", Locale.KOREA);

        for (int i = 0; i < 7; i++) {
            try {
                Date date = format.parse(pickedDate);
                Log.v("값 체크","pickedDate : "+pickedDate);
                cal.setTime(date);
                cal.add(Calendar.DATE, i);
                Log.v("값 체크","캘린더 애드 : "+cal.getTime());
                Log.v("값 체크","캘린더 밀리스 : "+cal.getTimeInMillis());

                Log.v("값 체크","pickedDate+i : "+format.format(cal.getTime()));
                int Position = -1;
                for (int j = 0; j < mArrayList.size(); j++) {
                    if (mArrayList.get(j).getDate().contentEquals(format.format(cal.getTime()))) {
                        Position = j;
                        break;
                    }
                }
                if(Position==-1){
                    entries_pink.add(new BarEntry(i, 0));
                    entries_orange.add(new BarEntry(i, 0));
                    entries_green.add(new BarEntry(i, 0));
                    entries_blue.add(new BarEntry(i, 0));
                    entries_purple.add(new BarEntry(i, 0));
                }
                else{
                    entries_pink.add(new BarEntry((float) i, Float.parseFloat(mArrayList.get(Position).getPink())));
                    entries_orange.add(new BarEntry((float) i, Float.parseFloat(mArrayList.get(Position).getOrange())));
                    entries_green.add(new BarEntry((float) i, Float.parseFloat(mArrayList.get(Position).getGreen())));
                    entries_blue.add(new BarEntry((float) i, Float.parseFloat(mArrayList.get(Position).getBlue())));
                    entries_purple.add(new BarEntry((float) i, Float.parseFloat(mArrayList.get(Position).getPurple())));
                }
            } catch (ParseException e) {
                e.printStackTrace();

            }
        }

        BarDataSet pink = new BarDataSet(entries_pink, ""); // 변수로 받아서 넣어줘도 됨
        BarDataSet orange = new BarDataSet(entries_orange, ""); // 변수로 받아서 넣어줘도 됨
        BarDataSet green = new BarDataSet(entries_green, ""); // 변수로 받아서 넣어줘도 됨
        BarDataSet blue = new BarDataSet(entries_blue, ""); // 변수로 받아서 넣어줘도 됨
        BarDataSet purple = new BarDataSet(entries_purple, ""); // 변수로 받아서 넣어줘도 됨

        pink.setColor(pref.getInt("RGB_PINK",PINK));
        orange.setColor(pref.getInt("RGB_ORANGE",ORANGE));
        green.setColor(pref.getInt("RGB_GREEN",GREEN));
        blue.setColor(pref.getInt("RGB_BLUE",BLUE));
        purple.setColor(pref.getInt("RGB_PURPLE",PURPLE));

        BarData barData = new BarData();
        barData.setBarWidth(0.5f);
        barData.addDataSet(pink);
        barDatas.add(barData);

        barData = new BarData();
        barData.setBarWidth(0.5f);
        barData.addDataSet(orange);
        barDatas.add(barData);

        barData = new BarData();
        barData.setBarWidth(0.5f);
        barData.addDataSet(green);
        barDatas.add(barData);

        barData = new BarData();
        barData.setBarWidth(0.5f);
        barData.addDataSet(blue);
        barDatas.add(barData);

        barData = new BarData();
        barData.setBarWidth(0.5f);
        barData.addDataSet(purple);
        barDatas.add(barData);

        return barDatas;
    }
}
