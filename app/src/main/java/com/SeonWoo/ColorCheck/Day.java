package com.SeonWoo.ColorCheck;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class Day extends AppCompatActivity {
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
    protected void onResume() {
        super.onResume();
        Log.v("위치체크", "History_onResume");
        mArrayList = getGsonPref();
        Collections.sort(mArrayList);

        mBarData = setBarData();

        barRecycler.setLayoutManager(new LinearLayoutManager(this));
        bAdapter = new BarChartAdapter(this, mBarData);
        barRecycler.setAdapter(bAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);
        pref = getSharedPreferences("1", MODE_PRIVATE);
        PickedDate = findViewById(R.id.day_et_PickedDate);
        PickedDate.setText(pref.getString("PickedDate", "2020년 06월 15일 월"));
        mBarData = setBarData();
        Gson gson = new Gson();
        Log.v("값 체크","Day_ mBarData"+mBarData.toString());
        Log.v("값 체크","Day_ mBarData size "+mBarData.size());
        Log.v("값 체크","Day_ mBarData 0_YMax "+mBarData.get(0).getYMax());

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
                new DatePickerDialog(Day.this, myDatePicker, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // 리사이클러뷰에 표시할 데이터 리스트 가져오기
        mArrayList = getGsonPref();
        Log.v("값 체크", "Day에서의 사이즈 : " + mArrayList.size());

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        barRecycler = findViewById(R.id.recycler2);
        barRecycler.setLayoutManager(new LinearLayoutManager(this));

        // 리사이클러뷰에 CustomAdapter 객체 지정.
        bAdapter = new BarChartAdapter(this, mBarData);
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
        ArrayList<BarData> barDatas = new ArrayList<>(5);

        ArrayList<BarEntry> entries_pink = new ArrayList<>();
        ArrayList<BarEntry> entries_orange = new ArrayList<>();
        ArrayList<BarEntry> entries_green = new ArrayList<>();
        ArrayList<BarEntry> entries_blue = new ArrayList<>();
        ArrayList<BarEntry> entries_purple = new ArrayList<>();
        String pickedDate = PickedDate.getText().toString();

        int Position = -10;
        for (int i = 0; i < mArrayList.size(); i++) {
            if (mArrayList.get(i).getDate().contentEquals(pickedDate)) {
                Position = i;
                break;
            }
        }
        for (int i = 0; i < 7; i++) {
            try{
                Log.v("값 체크","Position+i" + (Position+i) + "mArrayList.get(Position+i).getPink() : " + mArrayList.get(Position+i).getPink() );
                entries_pink.add(new BarEntry((float)i, Float.parseFloat(mArrayList.get(Position+i).getPink())));
                entries_orange.add(new BarEntry((float)i, Float.parseFloat(mArrayList.get(Position+i).getOrange())));
                entries_green.add(new BarEntry((float)i, Float.parseFloat(mArrayList.get(Position+i).getGreen())));
                entries_blue.add(new BarEntry((float)i, Float.parseFloat(mArrayList.get(Position+i).getBlue())));
                entries_purple.add(new BarEntry((float)i, Float.parseFloat(mArrayList.get(Position+i).getPurple())));
            }
            catch(Exception e){
                entries_pink.add(new BarEntry(i, 0));
                entries_orange.add(new BarEntry(i, 0));
                entries_green.add(new BarEntry(i, 0));
                entries_blue.add(new BarEntry(i, 0));
                entries_purple.add(new BarEntry(i, 0));
            }
        }

        BarDataSet pink = new BarDataSet(entries_pink,""); // 변수로 받아서 넣어줘도 됨
        BarDataSet orange = new BarDataSet(entries_orange, "ORANGE"); // 변수로 받아서 넣어줘도 됨
        BarDataSet green = new BarDataSet(entries_green, "GREEN"); // 변수로 받아서 넣어줘도 됨
        BarDataSet blue = new BarDataSet(entries_blue, "BLUE"); // 변수로 받아서 넣어줘도 됨
        BarDataSet purple = new BarDataSet(entries_purple, "PURPLE"); // 변수로 받아서 넣어줘도 됨
        pink.setAxisDependency(YAxis.AxisDependency.LEFT);

        pink.setColor(PINK);
        orange.setColor(ORANGE);
        green.setColor(GREEN);
        blue.setColor(BLUE);
        purple.setColor(PURPLE);

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
