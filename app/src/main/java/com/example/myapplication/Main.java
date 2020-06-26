package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Main extends AppCompatActivity {
    public static final int REQUEST_CODE_SETTING = 101;
    public static final int REQUEST_CODE_CAMERA = 102;
    public static final int REQUEST_CODE_HISTORY = 103;
    SharedPreferences pref_Logined = getSharedPreferences("Logined",MODE_PRIVATE);
    String ID;
    SharedPreferences pref_ID;
    SharedPreferences pref_useSubPassword;
    SharedPreferences pref_Nickname;
    SharedPreferences pref_Color;

    Spinner spinner;
    LineChart linechart;
    PieChart piechart;

    int PINK = 0XFFFE2E9A;
    int ORANGE = 0XFFFF8000;
    int GREEN = 0XFF1E8037;
    int BLUE = 0XFF0000FF;
    int PURPLE = 0XFFA901DB;

    EditText PickedDate;

    Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener myDatePicker;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = pref_ID.edit();
        editor.putString("PickedDate", PickedDate.getText().toString());
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SetLineChart();
        SetPieChart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref_Logined = getSharedPreferences("Logined", MODE_PRIVATE);
        ID = pref_Logined.getString("ID", "");
        pref_useSubPassword = getSharedPreferences("useSubPassword", MODE_PRIVATE);
        pref_Nickname = getSharedPreferences("Nickname", MODE_PRIVATE);
        pref_Color = getSharedPreferences("Color" + pref_Logined.getString("ID", ""), MODE_PRIVATE);
        pref_ID = getSharedPreferences(ID, MODE_PRIVATE);

        PickedDate = findViewById(R.id.Main_et_PickedDate);
        PickedDate.setText(pref_ID.getString("PickedDate", "2020년 06월 15일 월"));

        myDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        // PickedDate 변경 시, 차트 업데이트
        PickedDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                SetLineChart();
                SetPieChart();
                if (linechart.getVisibility() == View.INVISIBLE) {
                    linechart.setVisibility(View.VISIBLE);
                    linechart.setVisibility(View.INVISIBLE);
                    piechart.setVisibility(View.INVISIBLE);
                    piechart.setVisibility(View.VISIBLE);
                } else {
                    linechart.setVisibility(View.INVISIBLE);
                    linechart.setVisibility(View.VISIBLE);
                    piechart.setVisibility(View.VISIBLE);
                    piechart.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        // 스크린샷 버튼
        Button screenshot = findViewById(R.id.Main_screenshot);
        screenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("위치 체크", "Main_screenshot_onClick");
                ConstraintLayout Layout = findViewById(R.id.Main_Layout);
                LineChart linechartView = findViewById(R.id.LineChart); //캡쳐할 영역의 레이아웃
                PieChart piechartView = findViewById(R.id.PieChart);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); //년,월,일,시간 포멧 설정
                Date time = new Date(); //파일명 중복 방지를 위해 사용될 현재시간
                String current_time = sdf.format(time); //String형 변수에 저장
                Request_Capture(linechartView, current_time + "_LineChart"); //지정한 Layout 영역 사진첩 저장 요청
                Request_Capture(piechartView, current_time + "_PieChart"); //지정한 Layout 영역 사진첩 저장 요청
                Request_Capture(Layout, current_time + "_Layout"); //지정한 Layout 영역 사진첩 저장 요청
                Toast.makeText(getApplicationContext(), "캡처", Toast.LENGTH_SHORT).show();
            }
        });


        // 날짜 / 데이터 픽커
        PickedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Main.this, myDatePicker, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

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


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //직선차트
                if (position == 0) {
                    linechart.setVisibility(View.VISIBLE);
                    piechart.setVisibility(View.INVISIBLE);
                }

                //원형차트
                else if (position == 1) {
                    linechart.setVisibility(View.INVISIBLE);
                    piechart.setVisibility(View.VISIBLE);
                    // 애니메이션 효과, 펼쳐지는 느낌
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
            // Camera.class에서 다시 찍기를 눌렀을 때
            // 다시 Camera.class로 들어가게 한다.
            else if (resultCode == RESULT_FIRST_USER) {
                Intent intent = new Intent(getApplicationContext(), Camera.class);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
            }
        }

    }

    private ArrayList<com.example.myapplication.Color> getGsonPref() {
        SharedPreferences pref = getSharedPreferences("Logined", MODE_PRIVATE);
        String id = pref.getString("ID", "");
        Log.v("값 체크", "getGsonPref_로그인된 아이디 : " + id);
        SharedPreferences prefs = getSharedPreferences("History", MODE_PRIVATE);
        String json = prefs.getString(id, null);
        Gson gson = new Gson();

        ArrayList<com.example.myapplication.Color> urls = new ArrayList<>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);

                for (int i = 0; i < a.length(); i++) {
                    com.example.myapplication.Color url = gson.fromJson(a.optString(i), com.example.myapplication.Color.class);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

    private void setGsonPref(ArrayList<com.example.myapplication.Color> classes) throws JSONException {
        SharedPreferences pref = getSharedPreferences("Logined", MODE_PRIVATE);
        String id = pref.getString("ID", "");
        Log.v("값 체크", "setGsonPref_로그인된 아이디 : " + id);
        SharedPreferences prefs = getSharedPreferences("History", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        if (!classes.isEmpty()) {
            editor.putString(id, gson.toJson(classes));
            Log.v("값 체크", gson.toJson(classes));
        } else {
            editor.putString(id, null);
        }
        editor.commit();
    }

    private void updateLabel() {
        String myFormat = "yyyy년 MM월 dd일 E";    // 출력형식   2018년 12월 25일 금
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);

        EditText et_date = findViewById(R.id.Main_et_PickedDate);
        et_date.setText(sdf.format(myCalendar.getTime()));
    }

    private void SetPieChart() {

        ArrayList<com.example.myapplication.Color> mData = getGsonPref();
        Collections.sort(mData);

        String pickedDate = PickedDate.getText().toString();

        // 데이터 위치 찾기
        int Position = -10;
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).getDate().contentEquals(pickedDate)) {
                Position = i;
                break;
            }
        }

        // PieChart
        piechart.getDescription().setEnabled(false);
        piechart.setExtraOffsets(5, 10, 5, 5);
        piechart.setDragDecelerationFrictionCoef(0.95f);
        piechart.setDrawHoleEnabled(false);
        piechart.setHoleColor(android.graphics.Color.WHITE);
        piechart.setTransparentCircleRadius(61f);

        // 데이터 설정
        ArrayList<PieEntry> yValues = new ArrayList<>();

        float pink = 0;
        float orange = 0;
        float green = 0;
        float blue = 0;
        float purple = 0;

        for (int i = 0; i < 7; i++) {
            try {
                pink += Float.parseFloat(mData.get(Position - i).getPink());
                orange += Float.parseFloat(mData.get(Position - i).getOrange());
                green += Float.parseFloat(mData.get(Position - i).getGreen());
                blue += Float.parseFloat(mData.get(Position - i).getBlue());
                purple += Float.parseFloat(mData.get(Position - i).getPurple());
            } catch (Exception e) {
            }
        }

        yValues.add(new PieEntry(pink, pref_Color.getString("PINK", "자습")));
        yValues.add(new PieEntry(orange, pref_Color.getString("ORANGE", "수업")));
        yValues.add(new PieEntry(green, pref_Color.getString("GREEN", "개인업무")));
        yValues.add(new PieEntry(blue, pref_Color.getString("BLUE", "자기계발")));
        yValues.add(new PieEntry(purple, pref_Color.getString("PURPLE", "네트워킹")));

        // 오른쪽 밑 부분에 넣을 제목 역할
        Description description_pie = new Description();
        if (pref_Nickname.getString(pref_Logined.getString("ID", ""), "").contentEquals("")) {
            description_pie.setText("");
        } else {
            description_pie.setText(pref_Nickname.getString(pref_Logined.getString("ID", ""), ""));
        }
        description_pie.setTextSize(15);
        piechart.setDescription(description_pie);

        // 처음 그려질 때, 1초에 걸쳐 차르르 하고 펼쳐지는 효과
        piechart.animateY(1000, Easing.EaseInOutCubic); //애니메이션

        PieDataSet dataSet = new PieDataSet(yValues, "");

        // 거리두기
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // 컬러 설정
        dataSet.setColors(PINK, ORANGE, GREEN, BLUE, PURPLE);

        PieData piedata = new PieData((dataSet));
        piedata.setValueTextSize(10f);
        piedata.setValueTextColor(android.graphics.Color.YELLOW);

        piechart.setData(piedata);
    }

    private void SetLineChart() {
        // LineChart
        ArrayList<Entry> values_pink = new ArrayList<>();
        ArrayList<Entry> values_orange = new ArrayList<>();
        ArrayList<Entry> values_green = new ArrayList<>();
        ArrayList<Entry> values_blue = new ArrayList<>();
        ArrayList<Entry> values_purple = new ArrayList<>();

        ArrayList<com.example.myapplication.Color> mData = getGsonPref();
        Collections.sort(mData);


        String pickedDate = PickedDate.getText().toString();
        int Position = -10;
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).getDate().contentEquals(pickedDate)) {
                Position = i;
                break;
            }
        }

        for (int i = 0; i < 7; i++) {
            try {
                values_pink.add(new Entry(i, Float.parseFloat(mData.get(Position - i).getPink())));
                values_orange.add(new Entry(i, Float.parseFloat(mData.get(Position - i).getOrange())));
                values_green.add(new Entry(i, Float.parseFloat(mData.get(Position - i).getGreen())));
                values_blue.add(new Entry(i, Float.parseFloat(mData.get(Position - i).getBlue())));
                values_purple.add(new Entry(i, Float.parseFloat(mData.get(Position - i).getPurple())));

            } catch (Exception e) {
                values_pink.add(new Entry(i, 0));
                values_orange.add(new Entry(i, 0));
                values_green.add(new Entry(i, 0));
                values_blue.add(new Entry(i, 0));
                values_purple.add(new Entry(i, 0));
            }
        }

        // 선택한 날의 요일 확인해서 요일 셋팅
        final HashMap<Integer, String> numMap = new HashMap<>();
        if (Character.toString(pickedDate.charAt(14)).contentEquals("월")) {
            numMap.put(0, "월");
            numMap.put(1, "화");
            numMap.put(2, "수");
            numMap.put(3, "목");
            numMap.put(4, "금");
            numMap.put(5, "토");
            numMap.put(6, "일");
        } else if (Character.toString(pickedDate.charAt(14)).contentEquals("화")) {
            numMap.put(6, "월");
            numMap.put(0, "화");
            numMap.put(1, "수");
            numMap.put(2, "목");
            numMap.put(3, "금");
            numMap.put(4, "토");
            numMap.put(5, "일");
        } else if (Character.toString(pickedDate.charAt(14)).contentEquals("수")) {
            numMap.put(5, "월");
            numMap.put(6, "화");
            numMap.put(0, "수");
            numMap.put(1, "목");
            numMap.put(2, "금");
            numMap.put(3, "토");
            numMap.put(4, "일");
        } else if (Character.toString(pickedDate.charAt(14)).contentEquals("목")) {
            numMap.put(4, "월");
            numMap.put(5, "화");
            numMap.put(6, "수");
            numMap.put(0, "목");
            numMap.put(1, "금");
            numMap.put(2, "토");
            numMap.put(3, "일");
        } else if (Character.toString(pickedDate.charAt(14)).contentEquals("금")) {
            numMap.put(3, "월");
            numMap.put(4, "화");
            numMap.put(5, "수");
            numMap.put(6, "목");
            numMap.put(0, "금");
            numMap.put(1, "토");
            numMap.put(2, "일");
        } else if (Character.toString(pickedDate.charAt(14)).contentEquals("토")) {
            numMap.put(2, "월");
            numMap.put(3, "화");
            numMap.put(4, "수");
            numMap.put(5, "목");
            numMap.put(6, "금");
            numMap.put(0, "토");
            numMap.put(1, "일");
        } else if (Character.toString(pickedDate.charAt(14)).contentEquals("일")) {
            numMap.put(1, "월");
            numMap.put(2, "화");
            numMap.put(3, "수");
            numMap.put(4, "목");
            numMap.put(5, "금");
            numMap.put(6, "토");
            numMap.put(0, "일");
        }


        XAxis xAxis = linechart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return numMap.get((int) value);
            }
        });

        // 오른쪽 밑 부분에 넣을 제목 역할
        Description description = new Description();
        if (pref_Nickname.getString(pref_Logined.getString("ID", ""), "").contentEquals("")) {
            description.setText("");
        } else {
            description.setText(pref_Nickname.getString(pref_Logined.getString("ID", ""), ""));
        }
        description.setTextSize(15);
        linechart.setDescription(description);

        // 각 Line의 데이터를 저장할 Set
        LineDataSet set_pink = new LineDataSet(values_pink, pref_Color.getString("PINK", "자습"));
        LineDataSet set_orange = new LineDataSet(values_orange, pref_Color.getString("ORANGE", "수업"));
        LineDataSet set_green = new LineDataSet(values_green, pref_Color.getString("GREEN", "개인업무"));
        LineDataSet set_blue = new LineDataSet(values_blue, pref_Color.getString("BLUE", "자기계발"));
        LineDataSet set_purple = new LineDataSet(values_purple, pref_Color.getString("PURPLE", "네트워킹"));

        // 모든 Line들의 데이터를 저장할 dataSets
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set_pink); // add the data sets
        dataSets.add(set_orange);
        dataSets.add(set_green);
        dataSets.add(set_blue);
        dataSets.add(set_purple);

        // create a data object with the data sets
        LineData linedata = new LineData(dataSets);

        // black lines and points
        set_pink.setColor(PINK);
        set_pink.setCircleColor(PINK);
        set_orange.setColor(ORANGE);
        set_orange.setCircleColor(ORANGE);
        set_green.setColor(GREEN);
        set_green.setCircleColor(GREEN);
        set_blue.setColor(BLUE);
        set_blue.setCircleColor(BLUE);
        set_purple.setColor(PURPLE);
        set_purple.setCircleColor(PURPLE);
        // set data
        linechart.setData(linedata);
    }

    public void Request_Capture(View view, String title) {
        if (view == null) { //Null Point Exception ERROR 방지
            System.out.println("::::ERROR:::: view == NULL");
            return;
        }

        /* 캡쳐 파일 저장 */
        // 배경 : 흰색 / view의 이미지를 저장
        Bitmap bitmap = getBitmapFromView(view, 0xFFFFFFFF);
        FileOutputStream fos;

        /* 저장할 폴더 Setting */
        File uploadFolder = Environment.getExternalStoragePublicDirectory("/DCIM/Camera/"); //저장 경로 (File Type형 변수)


        if (!uploadFolder.exists()) { //만약 경로에 폴더가 없다면
            uploadFolder.mkdir(); //폴더 생성
        }

        /* 파일 저장 */
        String Str_Path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/"; //저장 경로 (String Type 변수)

        try {
            fos = new FileOutputStream(Str_Path + title + ".jpg"); // 경로 + 제목 + .jpg로 FileOutputStream Setting
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }

        MediaScanner ms = MediaScanner.newInstance(getApplicationContext());
        try {
            ms.mediaScanning(Str_Path + title + ".jpg");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("::::ERROR:::: " + e);
        }

    }//End Function

    public Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public Bitmap getBitmapFromView(View view, int defaultColor) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(defaultColor);
        view.draw(canvas);
        return bitmap;
    }
}


