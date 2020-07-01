package com.SeonWoo.ColorCheck;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeSet;

public class Camera extends AppCompatActivity {
    SharedPreferences pref;
    private static final int IMAGE_FINISH = 1;
    private static final int COLORCHECK_FINISH = 2;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_INPUT_DATE = 2;

    final String TAG = getClass().getSimpleName();

    String mCurrentPhotoPath;

    // 컬러 Hue 값 설정 // HSV
    static double PINK;
    static double ORANGE;
    static double GREEN;
    static double BLUE;
    static double PURPLE;

    final static double PINK_origin = 335;
    final static double ORANGE_origin = 23;
    final static double GREEN_origin = 113;
    final static double BLUE_origin = 199;
    final static double PURPLE_origin = 269;
    // 컬러 Saturation 값
    double SATURATION = 0.3;
    // 컬러 Value 값
    double VALUE = 0.2;
    // Hue값의 범위 설정
    int Threshold = 20;

    int color = 0;
    int curX;
    int curY;
    float[] hsv = new float[3];
    float[] hsv_pos = new float[3];
    int color_pos;

    String DateInput_date;

    ImageView imageView;
    Bitmap rotatedBitmap;
    Bitmap coloredBitmap;

    Button save;
    Button re;

    ColorCheck colorCheck = new ColorCheck();
    Thread colorcheck = new Thread(colorCheck);
    ArrayList<com.SeonWoo.ColorCheck.Color> mData;
    ArrayList<com.SeonWoo.ColorCheck.Color> sData;

    ProgressBar Bar;
    int rgb_pink=0XFFFE2E9A;
    int rgb_orange=0XFFFF8000;
    int rgb_green=0XFF1E8037;
    int rgb_blue=0XFF0000FF;
    int rgb_purple=0XFFA901DB;



    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                // 카메라 촬영 후 이미지 처리 완료 일때,
                // 버튼, 이미지는 아직 안 보이고
                // 로딩창을 띄워준다.
                case IMAGE_FINISH:
                    colorcheck.start();
                    Bar.setVisibility(View.VISIBLE);
                    save.setVisibility(View.INVISIBLE);
                    re.setVisibility(View.INVISIBLE);
                    break;

                // 이미지 처리가 완료
                // 버튼 이미지 보인다.
                // 로딩창 안 보인다.
                case COLORCHECK_FINISH:
                    imageView.setImageBitmap(coloredBitmap);
                    Bar.setVisibility(View.INVISIBLE);
                    save.setVisibility(View.VISIBLE);
                    re.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Log.v("위치체크", "OnCreate");
        pref = getSharedPreferences("1", MODE_PRIVATE);

        // 커스텀 색상이 존재하면 RGB ->HSV로 바꿔서 색상인식에 적용하기
        if(pref.getInt("RGB_PINK",0)==0){
            PINK = PINK_origin;
        }
        else{
            rgb_pink = pref.getInt("RGB_PINK",0XFFFE2E9A);
            Log.v("값체크","RGB_PINK값은? " + rgb_pink);
            float[] b = new float[3];
            Color.RGBToHSV((rgb_pink >> 16) & 0xFF, (rgb_pink >> 8) & 0xFF, (rgb_pink) & 0xFF, b);
            PINK = b[0];
        }
        if(pref.getInt("RGB_ORANGE",0)==0){
            ORANGE = ORANGE_origin;
        }
        else{
            rgb_orange = pref.getInt("RGB_ORANGE",0XFFFF8000);
            float[] b = new float[3];
            Color.RGBToHSV((rgb_orange >> 16) & 0xFF, (rgb_orange >> 8) & 0xFF, (rgb_orange) & 0xFF, b);
            ORANGE = b[0];
        }

        if(pref.getInt("RGB_GREEN",0)==0){
            GREEN = GREEN_origin;
        }
        else{
            rgb_green = pref.getInt("RGB_GREEN",0XFF1E8037);
            float[] b = new float[3];
            Color.RGBToHSV((rgb_green >> 16) & 0xFF, (rgb_green >> 8) & 0xFF, (rgb_green) & 0xFF, b);
            GREEN = b[0];
        }

        if(pref.getInt("RGB_BLUE",0)==0){
            BLUE = BLUE_origin;
        }
        else{
            rgb_blue = pref.getInt("RGB_BLUE",0XFF0000FF);
            float[] b = new float[3];
            Color.RGBToHSV((rgb_blue >> 16) & 0xFF, (rgb_blue >> 8) & 0xFF, (rgb_blue) & 0xFF, b);
            BLUE = b[0];
        }

        if(pref.getInt("RGB_PURPLE",0)==0){
            PURPLE = PURPLE_origin;
        }
        else{
            rgb_purple = pref.getInt("RGB_PURPLE",0XFFA901DB);
            float[] b = new float[3];
            Color.RGBToHSV((rgb_purple >> 16) & 0xFF, (rgb_purple >> 8) & 0xFF, (rgb_purple) & 0xFF, b);
            PURPLE = b[0];
        }
        
        

        // 매칭 / save : 저장 버튼 / Bar : 프로그레스바 / re : 다시찍기 버튼 / imageView : 카메라 촬영한 이미지
        save = findViewById(R.id.Camera_save);
        Bar = findViewById(R.id.progressBar);
        re = findViewById(R.id.Camera_re);
        imageView = findViewById(R.id.Camera_iv_image);

        // 사진 찍기
        dispatchTakePictureIntent();


        // 다시 찍기 버튼 / Main으로 나갔다가 다시 Camera로 들어온다.
        re.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_FIRST_USER);
                finish();
            }
        });

        // 저장 버튼 // 클릭 시 데이터를 Shared Preference에 저장한다.
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 날짜의 덧셈 뺄셈이 필요해서 Calendar 씀.
                Calendar cal = Calendar.getInstance();
                // 날짜 형식
                SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일 E", Locale.KOREA);

                try {
                    // 불러온 날짜를 Date형식으로 parse
                    Date date = format.parse(DateInput_date);
                    mData = getGsonPref();

                    // sData는 카메라에서 촬영한 후의 데이터이다.
                    // 월화수 3개의 데이터인 경우 | 목금토일 4개의 데이터인 경우 2가지가 있다.
                    // DateInput_date는 첫날(월or목)의 날짜라서 반복문으로 월화수 or 목금토일의 날짜를 확인하는 작업
                    for (int i = 0; i < sData.size(); i++) {
                        cal.setTime(date);
                        cal.add(Calendar.DATE, i);
                        sData.get(i).setDate(format.format(cal.getTime()));

                        // 받아오는 날짜의 데이터가 (mData에)이미 존재하는 경우
                        // 삭제한다. (같은 날짜 중복 데이터 허용하지 않는다.)
                        for (int j = 0; j < mData.size(); j++) {
                            if (mData.get(j).getDate().contentEquals(format.format(cal.getTime()))) {
                                mData.remove(j);
                                break;
                            }
                        }

                    }

                    // 현재 카메라 촬영 데이터(sData)를 전체 데이터(mData)에 저장.
                    mData.addAll(sData);
                    setGsonPref(mData);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("PickedDate",DateInput_date);
                    Toast.makeText(getApplicationContext(), "저장 완료", Toast.LENGTH_SHORT).show();
                    finish();

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    // 권한 요청
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // 카메라 촬영 후
        // 이미지 회전 시켜서 가져오는 작업 스레드로 진행 ( 백그라운드 )
        // 회전 작업이 끝나면 컬러 처리 작업을 바로 진행할 예정
        // 유저는 날짜 입력 액티비티 창으로 바로 넘어간다.
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                new Thread_ImageProcess().start();
                Intent intent_dateinput = new Intent(getApplicationContext(), DateInput.class);
                startActivityForResult(intent_dateinput, REQUEST_INPUT_DATE);
            } else {
                finish();
            }
        }

        // 유저가 입력한 날짜를 저장한다.
        else if (requestCode == REQUEST_INPUT_DATE) {
            if (resultCode == RESULT_OK) {
                DateInput_date = intent.getExtras().getString("date");
            } else {
                finish();
            }
        }
    }


    // 이미지 회전시켜 가져오는 스레드
    public class Thread_ImageProcess extends Thread {
        @Override
        public void run() {
            // 수행할 문장
            try {
                File file = new File(mCurrentPhotoPath);
                Bitmap bitmap = MediaStore.Images.Media
                        .getBitmap(getContentResolver(), Uri.fromFile(file));
                if (bitmap != null) {

                    // Exchangeable Image File Format
                    // 교환 이미지 파일 형식 // 카메라가 촬영한 사진을 저장하기 위한 포맷이다.
                    // mCurrentPhotoPath는 String값 / 사진 파일의 주소값
                    ExifInterface ei = new ExifInterface(mCurrentPhotoPath);

                    //ExifInterface.TAG_ORIENTATION 이 사진이 회전되었는지 정보를 담고 있다 //
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);

                    rotatedBitmap = null;

                    // 여러 방향으로 찍혀도 세로모드에 보기에 적합한 사진 형태로 나오게 했다.
                    // 주석은 나중에 가로모드 적용 시 바꿔야 해서 지우지 않았다.
                    switch (orientation) {
//                        case ExifInterface.ORIENTATION_ROTATE_90:
//                            rotatedBitmap = rotateImage(bitmap, 90);
//                            break;
//
//                        case ExifInterface.ORIENTATION_ROTATE_180:
//                            rotatedBitmap = rotateImage(bitmap, 90);
//                            break;
//
//                        case ExifInterface.ORIENTATION_ROTATE_270:
//                            rotatedBitmap = rotateImage(bitmap, 90);
//                            break;

                        case ExifInterface.ORIENTATION_NORMAL:
                        default:
                            rotatedBitmap = rotateImage(bitmap, 90);
                    }

                    // 사진 픽셀이 커서 줄여서 복사
                    coloredBitmap = Bitmap.createScaledBitmap(rotatedBitmap, rotatedBitmap.getWidth() / 4, rotatedBitmap.getHeight() / 4, true);

                    // 이미지 회전 처리 끝났다고 핸들러에 메시지를 보낸다.
                    Message message = handler.obtainMessage();
                    message.what = IMAGE_FINISH;
                    handler.sendMessage(message);
                }
            } catch (Exception e) {

            }

        }
    }

    private File createImageFile() throws IOException {
        // 이미지파일의 이름을 만든다. 중복이 안되기 위해 시간값 활용
        // new Date()는 현재 날짜와 시간을 가지는 인스턴스를 반환한다.
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // 외부 저장소에 특정 형식을 저장하는 영역이 있다. //
        // DIRECTORY_PICTIURES 는 사진 // 그외에도 ARARMS / DCIM / DOWNLOADS / MUSIC / NITIFICATIONS / PODCASTS / MOVIES가 있음.
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // 파일 Path
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                // createImageFile()은 외부 저장소 DIRECTORY_PICTURES 위치에
                // 파일이름형식.jpg 인 image File을 return 한다.
                photoFile = createImageFile();
            } catch (IOException ex) {
            }

            // createImageFIle()이 잘 작동했을 때 실행.
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.SeonWoo.ColorCheck.fileprovider",
                        photoFile);

                // 촬영한 사진을 섬네일 뿐 아닌, 풀사이즈를 받기 위해서
                // MediaStore.EXTRA_OUTPUT 를 설정하고 URI를 보내준다
                // 원문 : The name of the Intent-extra used to indicate a content resolver Uri to be used to store the requested image or video.
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    // 이미지 회전 메소드
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }


    // 촬영한 이미지 색상 처리 메소드
    private class ColorCheck implements Runnable {

        @Override
        public void run() {
            int i, j;
            int count;

            // 색상 인식한 이미지의 픽셀 위치를 true로 갖는 2차원 boolean배열
            // Ex) 핑크색을 인식한 위치를 true로 갖는 bool_pink
            boolean[][] bool_pink = new boolean[coloredBitmap.getWidth()][coloredBitmap.getHeight()];
            boolean[][] bool_orange = new boolean[coloredBitmap.getWidth()][coloredBitmap.getHeight()];
            boolean[][] bool_green = new boolean[coloredBitmap.getWidth()][coloredBitmap.getHeight()];
            boolean[][] bool_blue = new boolean[coloredBitmap.getWidth()][coloredBitmap.getHeight()];
            boolean[][] bool_purple = new boolean[coloredBitmap.getWidth()][coloredBitmap.getHeight()];

            // 전체 이미지를 스캔 / y축은 상단 25%정도는 쓰지 않아서 시간 단축을 위해서 안 본다.
            for (i = 0; i < coloredBitmap.getWidth(); i++) {
                for (j = coloredBitmap.getHeight() / 4; j < coloredBitmap.getHeight(); j++) {

                    // 픽셀의 RGB -> HSV
                    color_pos = coloredBitmap.getPixel(i, j);
                    Color.RGBToHSV((color_pos >> 16) & 0xFF, (color_pos >> 8) & 0xFF, (color_pos) & 0xFF, hsv_pos);

                    // HSV 적절한 범위의 값을 찾으면 2차원 boolean 배열에 저장한다.
                    if (hsv_pos[0] - PINK > -Threshold && hsv_pos[0] - PINK < Threshold &&
                            hsv_pos[1] > SATURATION &&
                            hsv_pos[2] > VALUE) {
                        bool_pink[i][j] = true;
                    } else if (hsv_pos[0] - ORANGE > -Threshold && hsv_pos[0] - ORANGE < Threshold &&
                            hsv_pos[1] > 0.5 &&
                            hsv_pos[2] > VALUE) {
                        bool_orange[i][j] = true;
                    } else if (hsv_pos[0] - GREEN > -Threshold && hsv_pos[0] - GREEN < Threshold &&
                            hsv_pos[1] > SATURATION &&
                            hsv_pos[2] > VALUE) {
                        bool_green[i][j] = true;
                    } else if (hsv_pos[0] - BLUE > -Threshold && hsv_pos[0] - BLUE < Threshold &&
                            hsv_pos[1] > SATURATION &&
                            hsv_pos[2] > VALUE) {
                        bool_blue[i][j] = true;
                    } else if (hsv_pos[0] - PURPLE > -Threshold && hsv_pos[0] - PURPLE < Threshold &&
                            hsv_pos[1] > 0.1 &&
                            hsv_pos[2] > VALUE) {
                        bool_purple[i][j] = true;
                    }

                }
            }

            // 가로줄을 없애는 메소드
            FilterRow(bool_pink);
            FilterRow(bool_orange);
            FilterRow(bool_green);
            FilterRow(bool_blue);
            FilterRow(bool_purple);

            // 세로줄을 없애는 메소드
            FilterColumn(bool_pink);
            FilterColumn(bool_orange);
            FilterColumn(bool_green);
            FilterColumn(bool_blue);
            FilterColumn(bool_purple);

            // 2차원 boolean배열에서 유의미한 X값만 저장하려고 함.
            // 유의미하다 : 해당 X값에서 true값이 있다.
            boolean[] CheckX_pink = new boolean[coloredBitmap.getWidth()];
            boolean[] CheckX_orange = new boolean[coloredBitmap.getWidth()];
            boolean[] CheckX_green = new boolean[coloredBitmap.getWidth()];
            boolean[] CheckX_blue = new boolean[coloredBitmap.getWidth()];
            boolean[] CheckX_purple = new boolean[coloredBitmap.getWidth()];

            for (i = 0; i < coloredBitmap.getWidth(); i++) {
                for (j = coloredBitmap.getHeight() / 4; j < coloredBitmap.getHeight(); j++) {
                    if (bool_pink[i][j]) {
                        CheckX_pink[i] = true;
                    }
                    if (bool_orange[i][j]) {
                        CheckX_orange[i] = true;
                    }
                    if (bool_green[i][j]) {
                        CheckX_green[i] = true;
                    }
                    if (bool_blue[i][j]) {
                        CheckX_blue[i] = true;
                    }
                    if (bool_purple[i][j]) {
                        CheckX_purple[i] = true;
                    }

                    if (CheckX_pink[i] && CheckX_orange[i] && CheckX_green[i] && CheckX_blue[i] && CheckX_purple[i]) {
                        break;
                    }
                }

            }

            // 유의미한 X위치를 저장할 ArrayList
            ArrayList<Integer> arrayList_pink = new ArrayList<>();
            ArrayList<Integer> arrayList_orange = new ArrayList<>();
            ArrayList<Integer> arrayList_green = new ArrayList<>();
            ArrayList<Integer> arrayList_blue = new ArrayList<>();
            ArrayList<Integer> arrayList_purple = new ArrayList<>();

            for (i = 0; i < coloredBitmap.getWidth(); i++) {
                if (CheckX_pink[i]) {
                    arrayList_pink.add(i);
                }
                if (CheckX_orange[i]) {
                    arrayList_orange.add(i);
                }
                if (CheckX_green[i]) {
                    arrayList_green.add(i);
                }
                if (CheckX_blue[i]) {
                    arrayList_blue.add(i);
                }
                if (CheckX_purple[i]) {
                    arrayList_purple.add(i);
                }
            }

            // rectanglesDay : 요일 별로 나뉜 큰 사각형 덩어리의 리스트
            // Ex) rectanglesDay_pink : 월 / 화 / 수 3개의 Rectangle값을 가진다.
            // Rectangle은 좌표값을 모두 가진 TreeSet을 가지고 있다.
            ArrayList<Rectangle> rectanglesDay_pink = DayMaker(arrayList_pink, bool_pink);
            ArrayList<Rectangle> rectanglesDay_orange = DayMaker(arrayList_orange, bool_orange);
            ArrayList<Rectangle> rectanglesDay_green = DayMaker(arrayList_green, bool_green);
            ArrayList<Rectangle> rectanglesDay_blue = DayMaker(arrayList_blue, bool_blue);
            ArrayList<Rectangle> rectanglesDay_purple = DayMaker(arrayList_purple, bool_purple);

            // 요일 별 -> 하나하나의 사각형 별로 나누는 작업
            // rectangles : 하나의 COLOR CHECK 덩어리의 TreeSet
            ArrayList<Rectangle> rectangles_pink = RecMaker(rectanglesDay_pink, bool_pink);
            ArrayList<Rectangle> rectangles_orange = RecMaker(rectanglesDay_orange, bool_orange);
            ArrayList<Rectangle> rectangles_green = RecMaker(rectanglesDay_green, bool_green);
            ArrayList<Rectangle> rectangles_blue = RecMaker(rectanglesDay_blue, bool_blue);
            ArrayList<Rectangle> rectangles_purple = RecMaker(rectanglesDay_purple, bool_purple);

            // 이미지 처리
            // 색을 유저가 잘 볼 수 있게 찾는 색상을 동일하게 만들고
            // 찾지 않는 배경은 투명도를 준다.
            for (i = 0; i < coloredBitmap.getWidth(); i++) {
                for (j = 0; j < coloredBitmap.getHeight(); j++) {
                    // 찾는 Color영역의 색을 동일하게 설정한다.
                    if (bool_pink[i][j] || bool_orange[i][j] || bool_green[i][j] || bool_blue[i][j] || bool_purple[i][j]) {
                        if (bool_pink[i][j]) {
//                            coloredBitmap.setPixel(i, j, 0xFFFF3399);
                            coloredBitmap.setPixel(i, j, rgb_pink);
                        }
                        if (bool_orange[i][j]) {
//                            coloredBitmap.setPixel(i, j, 0xFFFFA500);
                            coloredBitmap.setPixel(i, j, rgb_orange);
                        }
                        if (bool_green[i][j]) {
//                            coloredBitmap.setPixel(i, j, 0xFF008000);
                            coloredBitmap.setPixel(i, j, rgb_green);
                        }
                        if (bool_blue[i][j]) {
//                            coloredBitmap.setPixel(i, j, 0xFF0000FF);
                            coloredBitmap.setPixel(i, j, rgb_blue);
                        }
                        if (bool_purple[i][j]) {
//                            coloredBitmap.setPixel(i, j, 0xFF800080);
                            coloredBitmap.setPixel(i, j, rgb_purple);
                        }
                    }
//                    else {
//                        // 찾는 COLOR영역이 아닌 부분은 ALPHA값 조절해서 투명하게 만든다.
//                        coloredBitmap.setPixel(i, j, coloredBitmap.getPixel(i, j) - 0x66000000);
//                    }
                }
            }


            // 각 색상 별 최종 데이터를 모두 합해서
            // 색상 구별없는 종합 데이터를 만드는 중입니다.
            // 요일 사이의 거리를 구해서 30분에 해당하는 픽셀을 구하기 위해서 입니다.

            // 하나의 좌표 집합을 만든다.
            TreeSet<Coordinates> CoordiSet = new TreeSet<>();
            for (i = 0; i < rectangles_pink.size(); i++) {
                CoordiSet.addAll(rectangles_pink.get(i).getCoordiSet());
            }
            for (i = 0; i < rectangles_orange.size(); i++) {
                CoordiSet.addAll(rectangles_orange.get(i).getCoordiSet());
            }
            for (i = 0; i < rectangles_green.size(); i++) {
                CoordiSet.addAll(rectangles_green.get(i).getCoordiSet());
            }
            for (i = 0; i < rectangles_blue.size(); i++) {
                CoordiSet.addAll(rectangles_blue.get(i).getCoordiSet());
            }
            for (i = 0; i < rectangles_purple.size(); i++) {
                CoordiSet.addAll(rectangles_purple.get(i).getCoordiSet());
            }

            // 하나씩 꺼내면서 2차원 boolean배열에 그린다.
            Iterator<Coordinates> iter = CoordiSet.iterator();
            boolean[][] last_bool = new boolean[coloredBitmap.getWidth()][coloredBitmap.getHeight()];
            while (iter.hasNext()) {
                Coordinates a = iter.next();
                last_bool[a.getX()][a.getY()] = true;
            }

            // 유의미한 X좌표를 뽑아낸다.
            boolean[] last_CheckX = new boolean[coloredBitmap.getWidth()];
            for (i = 0; i < coloredBitmap.getWidth(); i++) {
                count = 0;
                for (j = 0; j < coloredBitmap.getHeight(); j++) {
                    if (last_bool[i][j]) {
                        count++;
                        break;
                    }
                }
                last_CheckX[i] = count != 0;
            }

            // 유의미한 X좌표를 ArrayList에 저장한다.
            ArrayList<Integer> last_arrayList = new ArrayList<>();
            for (i = 0; i < coloredBitmap.getWidth(); i++) {
                if (last_CheckX[i]) {
                    last_arrayList.add(i);
                }
            }
            ArrayList<Rectangle> last_rectanglesDay = DayMaker(last_arrayList, last_bool);

            // 첫째날과 둘째날의 사이 거리를 저장한다.
            int distance = 165;
            try {
                distance = last_rectanglesDay.get(1).getAverX() - last_rectanglesDay.get(0).getAverX();
            } catch (Exception ignored) {
            }

            Log.v("값 체크", "last_rectanglesDay.size : " + last_rectanglesDay.size());
            for (i = 0; i < last_rectanglesDay.size(); i++) {
                Log.v("값 체크", "rectanglesDay's " + i + "번째 X위치 " + last_rectanglesDay.get(i).getAverX());
            }

            // 실제로 측정한 사이즈
            // 30분 : 3mm / 요일 사이 거리 : 31.3mm
            double THIRTY_MIN = 3;
            double DAYS_DISTANCE = 31.3;
            // 30분에 해당하는 픽셀 거리 값
            double THIRTY_PIXEL = (double) distance * THIRTY_MIN / DAYS_DISTANCE;
            Log.v("값 체크", "THIRTY_PIXEL : " + THIRTY_PIXEL);

            // 높이와 30분 픽셀 값을 토대로 계산한 시간값이 저장될 변수
            double time;

            Log.v("값 체크", "rectangles_pink size : " + rectangles_pink.size());
            for (i = 0; i < rectangles_pink.size(); i++) {
                time = Math.round((double) rectangles_pink.get(i).getHeight() / THIRTY_PIXEL) * 0.5;
                Log.v("값 체크", "rectangles_pink " + i + "'s 사이즈 : " + rectangles_pink.get(i).size() + " | 평균위치 : " + rectangles_pink.get(i).getAverX() + " " + rectangles_pink.get(i).getAverY()
                        + " | 높이 : " + rectangles_pink.get(i).getHeight() + " | 시간 : " + time);
            }

            Log.v("값 체크", "rectangles_orange size : " + rectangles_orange.size());
            for (i = 0; i < rectangles_orange.size(); i++) {
                time = Math.round((double) rectangles_orange.get(i).getHeight() / THIRTY_PIXEL) * 0.5;
                Log.v("값 체크", "rectangles_orange " + i + "'s 사이즈 : " + rectangles_orange.get(i).size() + " | 평균위치 : " + rectangles_orange.get(i).getAverX() + " " + rectangles_orange.get(i).getAverY()
                        + " | 높이 : " + rectangles_orange.get(i).getHeight() + " | 시간 : " + time);
            }

            Log.v("값 체크", "rectangles_green size : " + rectangles_green.size());
            for (i = 0; i < rectangles_green.size(); i++) {
                time = Math.round((double) rectangles_green.get(i).getHeight() / THIRTY_PIXEL) * 0.5;
                Log.v("값 체크", "rectangles_green " + i + "'s 사이즈 : " + rectangles_green.get(i).size() + " | 평균위치 : " + rectangles_green.get(i).getAverX() + " " + rectangles_green.get(i).getAverY()
                        + " | 높이 : " + rectangles_green.get(i).getHeight() + " | 시간 : " + time);
            }

            Log.v("값 체크", "rectangles_blue size : " + rectangles_blue.size());
            for (i = 0; i < rectangles_blue.size(); i++) {
                time = Math.round((double) rectangles_blue.get(i).getHeight() / THIRTY_PIXEL) * 0.5;
                Log.v("값 체크", "rectangles_blue " + i + "'s 사이즈 : " + rectangles_blue.get(i).size() + " | 평균위치 : " + rectangles_blue.get(i).getAverX() + " " + rectangles_blue.get(i).getAverY()
                        + " | 높이 : " + rectangles_blue.get(i).getHeight() + " | 시간 : " + time);
            }

            Log.v("값 체크", "rectangles_purple size : " + rectangles_purple.size());
            for (i = 0; i < rectangles_purple.size(); i++) {
                time = Math.round((double) rectangles_purple.get(i).getHeight() / THIRTY_PIXEL) * 0.5;
                Log.v("값 체크", "rectangles_purple " + i + "'s 사이즈 : " + rectangles_purple.get(i).size() + " | 평균위치 : " + rectangles_purple.get(i).getAverX() + " " + rectangles_purple.get(i).getAverY()
                        + " | 높이 : " + rectangles_purple.get(i).getHeight() + " | 시간 : " + time);
            }

            // 따로 따로 사각형 -> 요일별 색상 시간을 구하기
            // double[0] pink_day는 첫째날의 핑크 색상 시간을 의미한다.
            double[] pink_day = new double[last_rectanglesDay.size()];
            double[] orange_day = new double[last_rectanglesDay.size()];
            double[] green_day = new double[last_rectanglesDay.size()];
            double[] blue_day = new double[last_rectanglesDay.size()];
            double[] purple_day = new double[last_rectanglesDay.size()];

            for (i = 0; i < last_rectanglesDay.size(); i++) {
                // i번째 요일의 위치를 나타내는 pos
                // 그 근처에 있으면 i번째 색상 데이터에 저장한다.
                int pos = last_rectanglesDay.get(i).getAverX();
                pink_day[i] = 0;
                orange_day[i] = 0;
                green_day[i] = 0;
                blue_day[i] = 0;
                purple_day[i] = 0;

                // 핑크 사각형 모두를 확인해서 X위치가 pos 근처이면 해당 요일이라고 판단해서 저장한다.
                for (j = 0; j < rectangles_pink.size(); j++) {
                    if (rectangles_pink.get(j).getAverX() - pos > -30 && rectangles_pink.get(j).getAverX() - pos < 30) {
                        time = Math.round((double) rectangles_pink.get(j).getHeight() / THIRTY_PIXEL) * 0.5;
                        pink_day[i] += time;
                    }
                }

                for (j = 0; j < rectangles_orange.size(); j++) {
                    if (rectangles_orange.get(j).getAverX() - pos > -30 && rectangles_orange.get(j).getAverX() - pos < 30) {
                        time = Math.round((double) rectangles_orange.get(j).getHeight() / THIRTY_PIXEL) * 0.5;
                        orange_day[i] += time;
                    }
                }

                for (j = 0; j < rectangles_green.size(); j++) {
                    if (rectangles_green.get(j).getAverX() - pos > -30 && rectangles_green.get(j).getAverX() - pos < 30) {
                        time = Math.round((double) rectangles_green.get(j).getHeight() / THIRTY_PIXEL) * 0.5;
                        green_day[i] += time;
                    }
                }

                for (j = 0; j < rectangles_blue.size(); j++) {
                    if (rectangles_blue.get(j).getAverX() - pos > -30 && rectangles_blue.get(j).getAverX() - pos < 30) {
                        time = Math.round((double) rectangles_blue.get(j).getHeight() / THIRTY_PIXEL) * 0.5;
                        blue_day[i] += time;
                    }
                }

                for (j = 0; j < rectangles_purple.size(); j++) {
                    if (rectangles_purple.get(j).getAverX() - pos > -30 && rectangles_purple.get(j).getAverX() - pos < 30) {
                        time = Math.round((double) rectangles_purple.get(j).getHeight() / THIRTY_PIXEL) * 0.5;
                        purple_day[i] += time;
                    }
                }
            }
            for (i = 0; i < last_rectanglesDay.size(); i++) {
                Log.v("값체크", "Day" + i + " " + pink_day[i] + " " + orange_day[i] + " " + green_day[i] + " " + blue_day[i] + " " + purple_day[i]);
            }


            // 촬영한 후의 최종 데이터 정보를 sData에 저장한다!
            sData = new ArrayList<>();
            for (i = 0; i < last_rectanglesDay.size(); i++) {
                sData.add(new com.SeonWoo.ColorCheck.Color("", pink_day[i], orange_day[i], green_day[i], blue_day[i], purple_day[i]));
            }

            // 이미지 처리가 끝났다고 핸들러로 전달
            Message message = handler.obtainMessage();
            message.what = COLORCHECK_FINISH;
            handler.sendMessage(message);
        }

        // Rectangle Maker라는 의미
        // 요일 별로 나뉜 rectanglesDay에서
        // 하나의 사각형으로 나눠주는 작업을 한다.
        private ArrayList<Rectangle> RecMaker(ArrayList<Rectangle> rectanglesDay, boolean[][] bool) {
            ArrayList<Rectangle> rectangles = new ArrayList<>();
            TreeSet<Coordinates> corDay;
            TreeSet<Coordinates> cor = new TreeSet<>();

            int X, Y;
            int preY = 0;

            for (int i = 0; i < rectanglesDay.size(); i++) {
                corDay = rectanglesDay.get(i).getCoordiSet();
                if(i!=0 && cor.size() > 100){
                    rectangles.add(new Rectangle(cor));
                }
                cor = new TreeSet<>();
                // 좌표를 하나씩 꺼내보면서 확인
                // Coordinates 는 y축 오름차순 -> x축 오름차순 순으로 정렬되어 나옴.
                for (Coordinates c : corDay) {
                    X = c.getX();
                    Y = c.getY();
                    if (preY == 0) {
                        preY = Y;
                    }

                    // 일정 범위 이상 y값이 떨어져 있으면 다른 덩어리로 인식한다.
                    // 요일이 다른데,
                    if ((Y - preY > 5 || Y - preY < 0)) {

                        // 100보다 작은 덩어리는 잘못 인식된 부분으로 여긴다.
                        if (cor.size() > 100) {
                            rectangles.add(new Rectangle(cor));
                        } else {
                            // boolean 배열에서 제거해준다.
                            Iterator<Coordinates> iter = cor.iterator();
                            while (iter.hasNext()) {
                                Coordinates cc = iter.next();
                                int ccX = cc.getX();
                                int ccY = cc.getY();
                                bool[ccX][ccY] = false;
                            }
                        }
                        cor = new TreeSet<>();
                    }
                    cor.add(new Coordinates(X, Y));
                    preY = Y;
                }
            }
            rectangles.add(new Rectangle(cor));

            return rectangles;
        }

        private ArrayList<Rectangle> DayMaker(ArrayList<Integer> arrayList, boolean[][] bool) {
            ArrayList<Rectangle> rectanglesDay = new ArrayList<>();
            TreeSet<Coordinates> corDay = new TreeSet<>();
            // 요일 나누기
            for (int i = 0; i < arrayList.size(); i++) {
                // X 좌표가 크게 한번 바뀌면 다음 요일로 넘어간 것이다.
                if (i != 0 && arrayList.get(i) - arrayList.get(i - 1) > 20) {
                    if (corDay.size() > 100) {
                        rectanglesDay.add(new Rectangle(corDay));
                    }
                    corDay = new TreeSet<>();
                }
                for (int j = 0; j < coloredBitmap.getHeight(); j++) {
                    if (bool[arrayList.get(i)][j]) {
                        corDay.add(new Coordinates(arrayList.get(i), j));
                    }
                }
            }
            rectanglesDay.add(new Rectangle(corDay));

            return rectanglesDay;
        }

        // 세로줄이 생긴 부분을 걸러내는 메소드
        private void FilterColumn(boolean[][] bool) {
            int CHECK = 23;
            int count;

            for (int j = 0; j < coloredBitmap.getHeight(); j++) {
                for (int i = 0; i < coloredBitmap.getWidth(); i++) {
                    // 픽셀이 기존에 true인 경우만
                    if (bool[i][j]) {
                        count = 0;

                        // COLOR해당하는 위치 좌우로 CHECK개씩 확인해서 갯수를 센다.
                        for (int k = -CHECK; k < CHECK; k++) {
                            if (i + k >= 0 && i + k < coloredBitmap.getWidth()) {
                                if (bool[i + k][j]) {
                                    count++;
                                }
                            }
                        }

                        // 주변에 n개이상의 해당 COLOR가 있다면 유효한 것
                        // 아니면 원치 않는 '세로줄'에 해당하기에 없앤다.
                        if (count < 9) {
                            bool[i][j] = false;
                        }
                    }
                }
            }

        }

        // 가로줄이 생긴 부분을 걸러내는 메소드
        private void FilterRow(boolean[][] bool) {
            int CHECK = 23;
            int count;
            for (int i = 0; i < coloredBitmap.getWidth(); i++) {
                for (int j = 0; j < coloredBitmap.getHeight(); j++) {
                    //해당 COLOR인 경우만
                    if (bool[i][j]) {
                        count = 0;

                        // 해당 COLOR의 위치 위 아래로 CHECK개 만큼 확인
                        for (int k = -CHECK; k < CHECK; k++) {
                            if (j + k >= 0 && j + k < coloredBitmap.getHeight()) {
                                if (bool[i][j + k]) {
                                    count++;
                                }
                            }
                        }

                        // 해당 COLOR가 10개 미만이면 원치 않는 '가로줄'에 해당하므로 제거
                        if (count < 10) {
                            bool[i][j] = false;
                        }
                    }
                }
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
        } else {
            editor.putString("History", null);
        }
        editor.apply();
    }

}

