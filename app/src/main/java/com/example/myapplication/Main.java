package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class Main extends AppCompatActivity {
    public static final int REQUEST_CODE_SETTING = 101;
    public static final int REQUEST_CODE_CAMERA = 102;
    public static final int REQUEST_CODE_HISTORY = 103;
    SharedPreferences pref_Logined;
    SharedPreferences pref_useSubPassword;
    SharedPreferences pref_Nickname;
    SharedPreferences pref_Color;

    Spinner spinner;
    LineChart linechart;
    PieChart piechart;

    TextView nick;
    TextView pink;
    TextView orange;
    TextView green;
    TextView blue;
    TextView purple;


    @Override
    protected void onResume() {
        super.onResume();

        pref_Logined = getSharedPreferences("Logined", MODE_PRIVATE);
        pref_useSubPassword = getSharedPreferences("useSubPassword", MODE_PRIVATE);
        pref_Nickname = getSharedPreferences("Nickname", MODE_PRIVATE);
        pref_Color = getSharedPreferences("Color" + pref_Logined.getString("ID", ""), MODE_PRIVATE);

        nick = findViewById(R.id.main_tv_nick);
        pink = findViewById(R.id.main_tv1);
        orange = findViewById(R.id.main_tv2);
        green = findViewById(R.id.main_tv3);
        blue = findViewById(R.id.main_tv4);
        purple = findViewById(R.id.main_tv5);

        if(pref_Nickname.getString(pref_Logined.getString("ID", ""), "").contentEquals("")){
            nick.setText("");
        }else{
            nick.setText(pref_Nickname.getString(pref_Logined.getString("ID", ""), "") + "의 Color");
        }
        pink.setText(pref_Color.getString("PINK", "자습"));
        orange.setText(pref_Color.getString("ORANGE", "수업"));
        green.setText(pref_Color.getString("GREEN", "개인업무"));
        blue.setText(pref_Color.getString("BLUE", "자기계발"));
        purple.setText(pref_Color.getString("PURPLE", "네트워킹"));



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref_Logined = getSharedPreferences("Logined", MODE_PRIVATE);
        pref_useSubPassword = getSharedPreferences("useSubPassword", MODE_PRIVATE);
        pref_Nickname = getSharedPreferences("Nickname", MODE_PRIVATE);
        pref_Color = getSharedPreferences("Color" + pref_Logined.getString("ID", ""), MODE_PRIVATE);

        // 세팅 버튼
        Button setting = findViewById(R.id.main_bt_setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Setting.class);
                startActivityForResult(intent, REQUEST_CODE_SETTING);
            }
        });
        // 촬영 버튼
        Button camera = findViewById(R.id.Main_bt_camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Camera.class);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
            }
        });
        // 기록 버튼
        Button history = findViewById(R.id.Main_bt_History);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pref_useSubPassword.getBoolean(pref_Logined.getString("ID", ""), false)) {
                    Intent intent = new Intent(getApplicationContext(), History_passwordCheck.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), History.class);
                    startActivityForResult(intent, REQUEST_CODE_HISTORY);
                }

            }
        });

        spinner = findViewById(R.id.spinner);
        linechart = findViewById(R.id.LineChart);
        piechart = findViewById(R.id.PieChart);
        piechart.setVisibility(View.INVISIBLE);

        // LineChart
        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            float val = (float) (Math.random() * 10);
            values.add(new Entry(i, val));
        }
        ArrayList<Entry> values2 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            float val = (float) (Math.random() * 10);
            values2.add(new Entry(i, val));
        }
        // 각 Line의 데이터를 저장할 Set
        LineDataSet set1;
        LineDataSet set2;
        set1 = new LineDataSet(values, "PINK");
        set2 = new LineDataSet(values2, "GREEN");

        // 모든 Line들의 데이터를 저장할 dataSets
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the data sets
        dataSets.add(set2);

        // create a data object with the data sets
        LineData linedata = new LineData(dataSets);

        // black lines and points
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);
        set2.setColor(Color.BLUE);
        set2.setCircleColor(Color.BLUE);
        // set data
        linechart.setData(linedata);


        // PieChart
        piechart.setUsePercentValues(true);
        piechart.getDescription().setEnabled(false);
        piechart.setExtraOffsets(5, 10, 5, 5);
        piechart.setDragDecelerationFrictionCoef(0.95f);
        piechart.setDrawHoleEnabled(false);
        piechart.setHoleColor(Color.WHITE);
        piechart.setTransparentCircleRadius(61f);

        // 데이터 설정
        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();
        yValues.add(new PieEntry(34f, "Japen"));
        yValues.add(new PieEntry(23f, "USA"));
        yValues.add(new PieEntry(14f, "UK"));
        yValues.add(new PieEntry(35f, "India"));
        yValues.add(new PieEntry(40f, "Russia"));
        yValues.add(new PieEntry(40f, "Korea"));

        // 오른쪽 밑 부분에 넣을 제목 역할
        Description description = new Description();
        description.setText("세계 국가"); //라벨
        description.setTextSize(15);
        piechart.setDescription(description);

        // 처음 그려질 때, 3초에 걸쳐 차르르 하고 펼쳐지는 효과
        piechart.animateY(1000, Easing.EaseInOutCubic); //애니메이션

        PieDataSet dataSet = new PieDataSet(yValues, "");

        // 거리두기
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // 템플릿 순서에 맞게 컬러 설정
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData piedata = new PieData((dataSet));
        piedata.setValueTextSize(10f);
        piedata.setValueTextColor(Color.YELLOW);

        piechart.setData(piedata);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v("값체크", "position : " + position);
                Log.v("값체크", "id : " + id);
                Log.v("값체크", "parent.getItemAtPostion : " + parent.getItemAtPosition(position));

                //직선차트

                if (position == 0) {
                    Log.v("위치체크", "onItemSelected_position==0");
                    linechart.setVisibility(View.VISIBLE);
                    piechart.setVisibility(View.INVISIBLE);

                }

                //원형차트
                else if (position == 1) {
                    linechart.setVisibility(View.INVISIBLE);
                    piechart.setVisibility(View.VISIBLE);
                    piechart.animateY(1000, Easing.EaseInOutCubic);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CAMERA) {
            if (resultCode == RESULT_OK) {

            }
        }

    }
}


