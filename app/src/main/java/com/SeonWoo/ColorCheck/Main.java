package com.SeonWoo.ColorCheck;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class Main extends AppCompatActivity {
    public static final int REQUEST_CODE_SETTING = 101;
    public static final int REQUEST_CODE_CAMERA = 102;
    public static final int REQUEST_CODE_HISTORY = 103;
    SharedPreferences pref;

    int PINK = 0XFFFE2E9A;
    int ORANGE = 0XFFFF8000;
    int GREEN = 0XFF1E8037;
    int BLUE = 0XFF0000FF;
    int PURPLE = 0XFFA901DB;

    TextView userColorTitle;
    TextView userTimeTitle;
    TextView userTime;

    ArrayList<Color> mData;

    Button pink;
    Button orange;
    Button blue;
    Button green;
    Button purple;


    @Override
    protected void onResume() {
        super.onResume();
        // 컬러 현황 업데이트
        pink.setBackgroundColor(pref.getInt("RGB_PINK",PINK));
        orange.setBackgroundColor(pref.getInt("RGB_ORANGE",ORANGE));
        green.setBackgroundColor(pref.getInt("RGB_GREEN",GREEN));
        blue.setBackgroundColor(pref.getInt("RGB_BLUE",BLUE));
        purple.setBackgroundColor(pref.getInt("RGB_PURPLE",PURPLE));


        // 컬러체크한 총 시간 구하기
        mData=getGsonPref();
        double time=0;
        for(int i = 0 ; i < mData.size() ; i ++){
            time += Double.parseDouble(mData.get(i).getPink());
            time += Double.parseDouble(mData.get(i).getOrange());
            time += Double.parseDouble(mData.get(i).getGreen());
            time += Double.parseDouble(mData.get(i).getBlue());
            time += Double.parseDouble(mData.get(i).getPurple());
        }
        userTime.setText(time+"시간");

        String nickname = pref.getString("Nickname","");
        if(nickname.contentEquals("")){
            nickname="회원";
        }
        // 닉네임 + 님의 컬러
        userColorTitle.setText(nickname+"님의 컬러");

        // 닉네임 + 님의 누적 컬러시간
        userTimeTitle.setText(nickname+"님의 누적 컬러시간");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getSharedPreferences("1", MODE_PRIVATE);
        userColorTitle=findViewById(R.id.main_tv_ColorTitle);
        userTimeTitle=findViewById(R.id.main_tv_TimeTitle);
        userTime=findViewById(R.id.main_tv_Time);

        pink = findViewById(R.id.main_pink);
        orange = findViewById(R.id.main_orange);
        green = findViewById(R.id.main_green);
        blue = findViewById(R.id.main_blue);
        purple = findViewById(R.id.main_purple);



        // 6.0 마쉬멜로우 이상일 경우에는 권한 체크 후 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 카메라 권한 요청 // 외부저장소 쓰기 권한 요청 // 외부저장소 읽기 권한 요청
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }



//        // 스크린샷 버튼
//        Button screenshot = findViewById(R.id.Main_screenshot);
//        screenshot.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); //년,월,일,시간 포멧 설정
//                Date time = new Date(); //파일명 중복 방지를 위해 사용될 현재시간
//                String current_time = sdf.format(time); //String형 변수에 저장
//
//                // 현재 보여지는 차트 저장하는 스크린샷
//                if (linechart.getVisibility() == View.VISIBLE) {
//                    Request_Capture(linechart, current_time + "_LineChart");
//                } else {
//                    Request_Capture(piechart, current_time + "_PieChart");
//                }
//                Toast.makeText(getApplicationContext(), "캡처", Toast.LENGTH_SHORT).show();
//            }
//        });

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
                if (pref.getBoolean("useSubPassword", false)) {
                    Intent intent = new Intent(getApplicationContext(), History_passwordCheck.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), History.class);
                    startActivityForResult(intent, REQUEST_CODE_HISTORY);
                }

            }
        });

        // 컬러별 통계 버튼
        Button static_color = findViewById(R.id.bt_color);
        static_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Static_Color.class);
                startActivity(intent);
            }
        });

        // 날짜별 통계 버튼
        Button static_day = findViewById(R.id.bt_day);
        static_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Static_Day.class);
                startActivity(intent);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CAMERA) {
            if (resultCode == RESULT_OK) {
                // 사진 촬영 후 해당 날짜로 PickedDate 설정
//                PickedDate.setText(data.getStringExtra("date"));
            }
            // Camera.class에서 다시 찍기를 눌렀을 때
            // 다시 Camera.class로 들어가게 한다.
            else if (resultCode == RESULT_FIRST_USER) {
                Intent intent = new Intent(getApplicationContext(), Camera.class);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
            }
        }
        // History
        else if (requestCode == REQUEST_CODE_HISTORY) {
            // History에서 데이터를 삭제한 경우, 갱신을 시키기 위해 나갔다 들어오게 함.
            if (resultCode == RESULT_FIRST_USER) {
                Intent intent = new Intent(getApplicationContext(), History.class);
                startActivityForResult(intent, REQUEST_CODE_HISTORY);
            }
        }

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

    // view의 이미지를 bitmap으로 반환 / 배경이 검은색
    public Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    // view의 이미지를 bitmap으로 반환 / 배경을 지정할 수 있음
    public Bitmap getBitmapFromView(View view, int defaultColor) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(defaultColor);
        view.draw(canvas);
        return bitmap;
    }

}


