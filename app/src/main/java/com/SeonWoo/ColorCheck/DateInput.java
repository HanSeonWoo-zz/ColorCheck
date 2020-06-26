package com.SeonWoo.ColorCheck;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateInput extends AppCompatActivity {

    EditText date;
    Button bt;

    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener myDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_input);

        Log.v("위치 체크","DateInput_onCreate");

        date= findViewById(R.id.dateinput_date);
        bt = findViewById(R.id.dateinput_bt);

        // 날짜 / 데이터 픽커
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(DateInput.this, myDatePicker, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // 입력 버튼
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(date.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(), "날짜를 입력하시기 바랍니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    // 입력받은 날짜를 인텐트로 Camera.class에 넘겨준다.
                    Intent intent = new Intent();
                    intent.putExtra("date",date.getText().toString());
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        });

        myDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 E", Locale.KOREA);
                date.setText(sdf.format(myCalendar.getTime()));
            }
        };
    }
}
