package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;

public class Camera extends AppCompatActivity {
    final String TAG = getClass().getSimpleName();
    final static int TAKE_PICTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;

    final static int WIDTH = 50;
    final static int HEIGHT = 50;

    // 컬러 Hue 값 설정 // HSV
    final static double PINK = 335;
    final static double ORANGE = 23;
    final static double GREEN = 113;
    final static double BLUE = 199;
    final static double PURPLE = 269;
    // 컬러 Saturation 값
    double SATURATION = 0.3;
    double SATURATION_PURPLE = 0.2;
    // 컬러 Value 값
    double VALUE = 0.2;
    // Hue값의 범위 설정
    int Threshold = 15;

    int color = 0;
    int curX;
    int curY;
    float[] hsv = new float[3];
    float[] hsv_pos = new float[3];
    int color_pos;

    ImageView imageView;
    Bitmap rotatedBitmap;
    Bitmap coloredBitmap;

    Button pink;
    Button orange;
    Button green;
    Button blue;
    Button purple;

    ProgressBar Bar;
    LoadingDialog loadingDialog;

    TextView day1;
    TextView day2;
    TextView day3;
    TextView day4;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Log.v("위치체크", "OnCreate");

        pink = findViewById(R.id.bt_pink);
        orange = findViewById(R.id.bt_orange);
        green = findViewById(R.id.bt_green);
        blue = findViewById(R.id.bt_blue);
        purple = findViewById(R.id.bt_purple);

        day1 = findViewById(R.id.day1);
        day2 = findViewById(R.id.day2);
        day3 = findViewById(R.id.day3);
        day4 = findViewById(R.id.day4);

        Bar = findViewById(R.id.progressBar);
        loadingDialog = new LoadingDialog(Camera.this);
        Handler mhandler = new Handler();

        // 색상 버튼 클릭 시, HSV값을 참조해서 비슷한 Pixel을 빨간색으로 바꿈 //
        pink.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                coloredBitmap = Bitmap.createScaledBitmap(rotatedBitmap, rotatedBitmap.getWidth() / 4, rotatedBitmap.getHeight() / 4, true);
                new MyAsyncTask().execute(PINK, SATURATION, VALUE);
                imageView.setImageBitmap(coloredBitmap);
            }
        });

        orange.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                coloredBitmap = Bitmap.createScaledBitmap(rotatedBitmap, rotatedBitmap.getWidth() / 4, rotatedBitmap.getHeight() / 4, true);
                new ColorCheck(ORANGE, SATURATION, VALUE).run();
                imageView.setImageBitmap(coloredBitmap);
            }
        });

        green.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                coloredBitmap = Bitmap.createScaledBitmap(rotatedBitmap, rotatedBitmap.getWidth() / 4, rotatedBitmap.getHeight() / 4, true);
                new ColorCheck(GREEN, SATURATION, VALUE).run();
                imageView.setImageBitmap(coloredBitmap);
            }
        });
        blue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                coloredBitmap = Bitmap.createScaledBitmap(rotatedBitmap, rotatedBitmap.getWidth() / 4, rotatedBitmap.getHeight() / 4, true);
                new ColorCheck(BLUE, SATURATION, VALUE).run();
                imageView.setImageBitmap(coloredBitmap);
            }
        });
        purple.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                coloredBitmap = Bitmap.createScaledBitmap(rotatedBitmap, rotatedBitmap.getWidth() / 4, rotatedBitmap.getHeight() / 4, true);
                new ColorCheck(PURPLE, SATURATION, VALUE).run();
                imageView.setImageBitmap(coloredBitmap);
            }
        });


        imageView = findViewById(R.id.Camera_iv_image);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float width_ratio;
                float height_ratio;

                // Bitmap을 복사
                coloredBitmap = rotatedBitmap.copy(Bitmap.Config.ARGB_8888, true);
//                Log.v("값 체크", "픽셀의 Width : " + rotatedBitmap.getWidth());
//                Log.v("값 체크", "픽셀의 Height : " + rotatedBitmap.getHeight());
//                Log.v("값 체크", "이미지뷰의 Width : " + imageView.getWidth());
//                Log.v("값 체크", "이미지뷰의 Height : " + imageView.getHeight());
                // 세로 모드
                if (coloredBitmap.getWidth() < coloredBitmap.getHeight()) {
                    width_ratio = (float) coloredBitmap.getWidth() / (float) imageView.getWidth();
                    height_ratio = (float) coloredBitmap.getHeight() / (float) imageView.getHeight();
                    Log.v("값 체크", " 세로 모드");
                }
                // 가로 모드
                else {
                    width_ratio = (float) coloredBitmap.getWidth() / (float) imageView.getWidth();
                    height_ratio = (float) coloredBitmap.getHeight() / (float) imageView.getHeight();
                    Log.v("값 체크", " 가로 모드");
                }

                // User가 클릭한 위치(휴대폰에서의 X,Y좌표)를 받아와서 Bitmap 크기에 맞춘 값으로 계산
                curX = (int) (event.getX() * width_ratio); // 실제 픽셀과의 비율을 계산
                curY = (int) (event.getY() * height_ratio);

                Log.v("값 체크", "가로비율 : " + width_ratio + " 세로비율 : " + height_ratio);

                color = coloredBitmap.getPixel(curX, curY);
                Color.RGBToHSV((color >> 16) & 0xFF, (color >> 8) & 0xFF, (color) & 0xFF, hsv);

                // 클릭 지점의 주변 범위에 같은 색상값인 경우 -> 빨간색으로 변경
                for (int j = -WIDTH; j < WIDTH; j++) {
                    for (int i = -HEIGHT; i < HEIGHT; i++) {
                        if (curX + i < coloredBitmap.getWidth() && curY + j < coloredBitmap.getHeight()) {

                            color_pos = coloredBitmap.getPixel(curX + i, curY + j);
                            Color.RGBToHSV((color_pos >> 16) & 0xFF, (color_pos >> 8) & 0xFF, (color_pos) & 0xFF, hsv_pos);

                            if (hsv[0] - hsv_pos[0] > -Threshold && hsv[0] - hsv_pos[0] < Threshold) {
                                coloredBitmap.setPixel(curX + i, curY + j, 0xFFFF0000);
                                Log.v("값체크", "X : " + (curX + i) + " Y : " + (curY + j) +
                                        " // HSV : " + hsv_pos[0] + "(" + hsv[0] + ") " +
                                        hsv_pos[1] + "(" + hsv[1] + ") " +
                                        hsv_pos[2] + "(" + hsv[2] + ")");
//                                Log.v("값체크", "RGB : " + ((color_pos >> 16) & 0xFF) + "(" + ((color >> 16) & 0xFF) + ") " +
//                                        ((color_pos >> 8) & 0xFF) + "(" + ((color >> 8) & 0xFF) + ") " +
//                                        ((color_pos) & 0xFF) + "(" + ((color) & 0xFF) + ")" +
//                                        " // HSV : " + hsv_pos[0] + "(" + hsv[0] + ") " +
//                                        hsv_pos[1] + "(" + hsv[1] + ") " +
//                                        hsv_pos[2] + "(" + hsv[2] + ")");
                            }
                        }
                    }
                }

                imageView.setImageBitmap(coloredBitmap);

                return false;
            }
        });


        // 6.0 마쉬멜로우 이상일 경우에는 권한 체크 후 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "권한 설정 완료");
            } else {
                Log.d(TAG, "권한 설정 요청");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        dispatchTakePictureIntent();


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

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        int action = event.getAction();
//        curX = (int) event.getX();
//        curY = (int) event.getY();
//
//        return super.onTouchEvent(event);
//    }

    // 카메라로 촬영한 영상을 가져오는 부분
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        try {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO: {
                    if (resultCode == RESULT_OK) {
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
                            // 회전된 것을 다시 돌려온다.
                            switch (orientation) {

                                case ExifInterface.ORIENTATION_ROTATE_90:
                                    rotatedBitmap = rotateImage(bitmap, 90);
                                    break;

                                case ExifInterface.ORIENTATION_ROTATE_180:
                                    rotatedBitmap = rotateImage(bitmap, 180);
                                    break;

                                case ExifInterface.ORIENTATION_ROTATE_270:
                                    rotatedBitmap = rotateImage(bitmap, 270);
                                    break;

                                case ExifInterface.ORIENTATION_NORMAL:
                                default:
                                    rotatedBitmap = bitmap;
                            }
                            imageView.setImageBitmap(rotatedBitmap);
                        }
                    }
                    break;
                }
            }

        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        // new Date()는 현재 날짜와 시간을 가지는 인스턴스를 반환한다.
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // 외부 저장소 중에 특정 데이터를 저장하는 영역 //
        // DIRECTORY_PICTIURES 는 사진 // 그외에도 ARARMS / DCIM / DOWNLOADS / MUSIC / NITIFICATIONS / PODCASTS / MOVIES가 있음.
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                // createImageFile()은 외부 저장소 DIRECTORY_PICTURES 위치에
                // 파일이름형식.jpg 인 image File을 return 한다.

                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.myapplication.fileprovider",
                        photoFile);

                // 촬영한 사진을 섬네일 뿐 아닌, 풀사이즈를 받기 위해서
                // MediaStore.EXTRA_OUTPUT 를 설정하고 URI를 보내준다
                // The name of the Intent-extra used to indicate a content resolver Uri to be used to store the requested image or video.
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }


    private class ColorCheck implements Runnable {

        private double hue;
        private double saturation;
        private double value;

        public ColorCheck(double hue, double saturation, double value) {
            this.hue = hue;
            this.saturation = saturation;
            this.value = value;
        }

        @Override
        public void run() {
            int i, j = 0;
            int count = 0;
            int CUT_Y = 30;



            boolean[][] bool_pink = new boolean[coloredBitmap.getWidth()][coloredBitmap.getHeight()];
            boolean[][] bool_orange = new boolean[coloredBitmap.getWidth()][coloredBitmap.getHeight()];
            boolean[][] bool_green = new boolean[coloredBitmap.getWidth()][coloredBitmap.getHeight()];
            boolean[][] bool_blue = new boolean[coloredBitmap.getWidth()][coloredBitmap.getHeight()];
            boolean[][] bool_purple = new boolean[coloredBitmap.getWidth()][coloredBitmap.getHeight()];

            for (i = 0; i < coloredBitmap.getWidth(); i++) {
                for (j = coloredBitmap.getHeight() / 4; j < coloredBitmap.getHeight(); j++) {

                    // 픽셀의 RGB -> HSV
                    color_pos = coloredBitmap.getPixel(i, j);
                    Color.RGBToHSV((color_pos >> 16) & 0xFF, (color_pos >> 8) & 0xFF, (color_pos) & 0xFF, hsv_pos);

                    // 찾는 COLOR의 조건이면 해당 픽셀을 빨간색으로 바꾼다.
                    // 2차원 boolean 배열에 저장한다.
                    if (hsv_pos[0] - PINK > -Threshold && hsv_pos[0] - PINK < Threshold &&
                            hsv_pos[1] > SATURATION &&
                            hsv_pos[2] > VALUE) {
                        coloredBitmap.setPixel(i, j, 0xFFFF0000);
                        bool_pink[i][j] = true;
                    } else if (hsv_pos[0] - ORANGE > -Threshold && hsv_pos[0] - ORANGE < Threshold &&
                            hsv_pos[1] > SATURATION &&
                            hsv_pos[2] > VALUE) {
                        coloredBitmap.setPixel(i, j, 0xFFFF0000);
                        bool_orange[i][j] = true;
                    } else if (hsv_pos[0] - GREEN > -Threshold && hsv_pos[0] - GREEN < Threshold &&
                            hsv_pos[1] > SATURATION &&
                            hsv_pos[2] > VALUE) {
                        coloredBitmap.setPixel(i, j, 0xFFFF0000);
                        bool_green[i][j] = true;
                    } else if (hsv_pos[0] - BLUE > -Threshold && hsv_pos[0] - BLUE < Threshold &&
                            hsv_pos[1] > SATURATION &&
                            hsv_pos[2] > VALUE) {
                        coloredBitmap.setPixel(i, j, 0xFFFF0000);
                        bool_blue[i][j] = true;
                    } else if (hsv_pos[0] - PURPLE > -Threshold && hsv_pos[0] - PURPLE < Threshold &&
                            hsv_pos[1] > SATURATION &&
                            hsv_pos[2] > VALUE) {
                        coloredBitmap.setPixel(i, j, 0xFFFF0000);
                        bool_purple[i][j] = true;
                    }

                }
            }

            FilterRow(bool_pink);
            FilterRow(bool_orange);
            FilterRow(bool_green);
            FilterRow(bool_blue);
            FilterRow(bool_purple);

            FilterColumn(bool_pink);
            FilterColumn(bool_orange);
            FilterColumn(bool_green);
            FilterColumn(bool_blue);
            FilterColumn(bool_purple);


            boolean[][] bool = new boolean[coloredBitmap.getWidth()][coloredBitmap.getHeight()];
            boolean[] CheckX = new boolean[coloredBitmap.getWidth()];
            for (i = 0; i < coloredBitmap.getWidth(); i++) {
                count = 0;
                for (j = 0; j < coloredBitmap.getHeight(); j++) {
                    if(bool_pink[i][j] || bool_orange[i][j] || bool_green[i][j] || bool_blue[i][j] || bool_purple[i][j]){
                        count++;
                        bool[i][j]=true;
                    }
                }
                CheckX[i]= count!=0;
            }

            // 2차원 boolean배열에서 y축이 모두 false인 부분이면 arrayList에서 빼려고 함.
            // CheckX : 해당하는 X값의 모든 Y값이 원하는 COLOR가 아닌 경우 false
            boolean[] CheckX_pink = new boolean[coloredBitmap.getWidth()];
            boolean[] CheckX_orange = new boolean[coloredBitmap.getWidth()];
            boolean[] CheckX_green = new boolean[coloredBitmap.getWidth()];
            boolean[] CheckX_blue = new boolean[coloredBitmap.getWidth()];
            boolean[] CheckX_purple = new boolean[coloredBitmap.getWidth()];

//            else {
//                int point = coloredBitmap.getPixel(i, j);
//                int a = (point >> 24) & 0xFF;
//                a = a / 2;
//                int r = (point >> 16) & 0xFF;
//                int g = (point >> 8) & 0xFF;
//                int b = (point) & 0xFF;
//                point = Color.argb(a, r, g, b);
//                coloredBitmap.setPixel(i, j, point);
//            }
            
            for (i = 0; i < coloredBitmap.getWidth(); i++) {
                int count_pink = 0;
                int count_orange = 0;
                int count_green = 0;
                int count_blue = 0;
                int count_purple = 0;

                for (j = 0; j < coloredBitmap.getHeight(); j++) {
                    if (bool_pink[i][j]) {
                        coloredBitmap.setPixel(i, j, 0xFFFF3399);
                        count_pink++;
                    }
                    if (bool_orange[i][j]) {
                        coloredBitmap.setPixel(i, j, 0xFFFFA500);
                        count_orange++;
                    }
                    if (bool_green[i][j]) {
                        coloredBitmap.setPixel(i, j, 0xFF008000);
                        count_green++;
                    }
                    if (bool_blue[i][j]) {
                        coloredBitmap.setPixel(i, j, 0xFF0000FF);
                        count_blue++;
                    }
                    if (bool_purple[i][j]) {
                        coloredBitmap.setPixel(i, j, 0xFF800080);
                        count_purple++;
                    }
                }
                CheckX_pink[i]= count_pink!=0;
                CheckX_orange[i]= count_orange!=0;
                CheckX_green[i]= count_green!=0;
                CheckX_blue[i]= count_blue!=0;
                CheckX_purple[i]= count_purple!=0;

            }

            ArrayList<Integer> arrayList = new ArrayList<>();
            ArrayList<Integer> arrayList_pink = new ArrayList<>();
            ArrayList<Integer> arrayList_orange = new ArrayList<>();
            ArrayList<Integer> arrayList_green = new ArrayList<>();
            ArrayList<Integer> arrayList_blue = new ArrayList<>();
            ArrayList<Integer> arrayList_purple = new ArrayList<>();

            // CheckX 를 토대로 arrayList를 만들어 준다.
            for (i = 0; i < coloredBitmap.getWidth(); i++) {
                if (CheckX[i]) {
                    arrayList.add(i);
                }
                if(CheckX_pink[i]){
                    arrayList_pink.add(i);
                }
                if(CheckX_orange[i]){
                    arrayList_orange.add(i);
                }
                if(CheckX_green[i]){
                    arrayList_green.add(i);
                }
                if(CheckX_blue[i]){
                    arrayList_blue.add(i);
                }
                if(CheckX_purple[i]){
                    arrayList_purple.add(i);
                }
            }

            // rectanglesDay : 요일 별로 나뉜 큰 사각형 덩어리의 리스트
            ArrayList<Rectangle> rectanglesDay = DayMaker(arrayList, bool);
            ArrayList<Rectangle> rectanglesDay_pink = DayMaker(arrayList_pink, bool_pink);
            ArrayList<Rectangle> rectanglesDay_orange = DayMaker(arrayList_orange, bool_orange);
            ArrayList<Rectangle> rectanglesDay_green = DayMaker(arrayList_green, bool_green);
            ArrayList<Rectangle> rectanglesDay_blue = DayMaker(arrayList_blue, bool_blue);
            ArrayList<Rectangle> rectanglesDay_purple = DayMaker(arrayList_purple, bool_purple);
            
//            Log.v("값 체크", "rectanglesDay size : " + rectanglesDay.size());
//            for (i = 0; i < rectanglesDay.size(); i++) {
//                Log.v("값 체크", "rectanglesDay " + i + "'s 평균X위치: " + rectanglesDay.get(i).getAverX());
//            }


            // 각 사각형 별로 나누는 작업
            // rectangles : 하나의 COLOR CHECK 덩어리의 리스트
            ArrayList<Rectangle> rectangles_pink = RecMaker(rectanglesDay_pink);
            ArrayList<Rectangle> rectangles_orange = RecMaker(rectanglesDay_orange);
            ArrayList<Rectangle> rectangles_green = RecMaker(rectanglesDay_green);
            ArrayList<Rectangle> rectangles_blue = RecMaker(rectanglesDay_blue);
            ArrayList<Rectangle> rectangles_purple = RecMaker(rectanglesDay_purple);


            TreeSet<Coordinates> CoordiSet= new TreeSet<>();
            for(i = 0 ; i < rectangles_pink.size() ; i ++){
                CoordiSet.addAll(rectangles_pink.get(i).getCoordiSet());
            }
            for(i = 0 ; i < rectangles_orange.size() ; i ++){
                CoordiSet.addAll(rectangles_orange.get(i).getCoordiSet());
            }
            for(i = 0 ; i < rectangles_green.size() ; i ++){
                CoordiSet.addAll(rectangles_green.get(i).getCoordiSet());
            }
            for(i = 0 ; i < rectangles_blue.size() ; i ++){
                CoordiSet.addAll(rectangles_blue.get(i).getCoordiSet());
            }
            for(i = 0 ; i < rectangles_purple.size() ; i ++){
                CoordiSet.addAll(rectangles_purple.get(i).getCoordiSet());
            }
            Gson gson = new Gson();
            Iterator<Coordinates> iter = CoordiSet.iterator();
            boolean[][] last_bool=new boolean[coloredBitmap.getWidth()][coloredBitmap.getHeight()];
            while(iter.hasNext()){
                Coordinates a = iter.next();
                last_bool[a.getX()][a.getY()]=true;
            }

            boolean[] last_CheckX = new boolean[coloredBitmap.getWidth()];
            for(i=0 ; i < coloredBitmap.getWidth() ; i ++){
                count=0;
                for(j=0 ; j <coloredBitmap.getHeight();j++){
                    if(last_bool[i][j]) {
                        count++;
                        break;
                    }
                }
                last_CheckX[i] = count!=0;
            }
            ArrayList<Integer> last_arrayList = new ArrayList<>();
            for (i = 0; i < coloredBitmap.getWidth(); i++) {
                if (last_CheckX[i]) {
                    last_arrayList.add(i);
                }
            }
            ArrayList<Rectangle> last_rectanglesDay = DayMaker(last_arrayList, last_bool);

            int distance = 15;
            try{
                distance = last_rectanglesDay.get(1).getAverX()-last_rectanglesDay.get(0).getAverX();
            }catch (Exception e){

            }
            Log.v("값 체크", "rectanglesDay.size : " + last_rectanglesDay.size());
            Log.v("값 체크", "rectanglesDay: " + gson.toJson(last_rectanglesDay));
            for(i = 0 ; i <last_rectanglesDay.size() ; i ++){
                Log.v("값 체크", "rectanglesDay's " + i +"번째 X위치 " +  last_rectanglesDay.get(i).getAverX());
            }
            double THIRTY_MIN = 3;
            double DAYS_DISTANCE = 31.3;
            // 30분에 해당하는 픽셀 거리 값
            double THIRTY_PIXEL = (double) distance * THIRTY_MIN / DAYS_DISTANCE;
            Log.v("값 체크", "THIRTY_PIXEL : " + THIRTY_PIXEL);
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
            double[] pink_day = new double[last_rectanglesDay.size()];
            double[] orange_day = new double[last_rectanglesDay.size()];
            double[] green_day = new double[last_rectanglesDay.size()];
            double[] blue_day = new double[last_rectanglesDay.size()];
            double[] purple_day = new double[last_rectanglesDay.size()];

            for(i = 0 ; i < last_rectanglesDay.size() ; i ++){
                int pos = last_rectanglesDay.get(i).getAverX();
                pink_day[i]=0;
                orange_day[i]=0;
                green_day[i]=0;
                blue_day[i]=0;
                purple_day[i]=0;

                for(j=0 ; j < rectangles_pink.size() ; j++){
                    if(rectangles_pink.get(j).getAverX()-pos > -30 && rectangles_pink.get(j).getAverX()-pos < 30){
                        time = Math.round((double) rectangles_pink.get(j).getHeight() / THIRTY_PIXEL) * 0.5;
                        pink_day[i]+=time;
                    }
                }

                for(j=0 ; j < rectangles_orange.size() ; j++){
                    if(rectangles_orange.get(j).getAverX()-pos > -30 && rectangles_orange.get(j).getAverX()-pos < 30){
                        time = Math.round((double) rectangles_orange.get(j).getHeight() / THIRTY_PIXEL) * 0.5;
                        orange_day[i]+=time;
                    }
                }

                for(j=0 ; j < rectangles_green.size() ; j++){
                    if(rectangles_green.get(j).getAverX()-pos > -30 && rectangles_green.get(j).getAverX()-pos < 30){
                        time = Math.round((double) rectangles_green.get(j).getHeight() / THIRTY_PIXEL) * 0.5;
                        green_day[i]+=time;
                    }
                }

                for(j=0 ; j < rectangles_blue.size() ; j++){
                    if(rectangles_blue.get(j).getAverX()-pos > -30 && rectangles_blue.get(j).getAverX()-pos < 30){
                        time = Math.round((double) rectangles_blue.get(j).getHeight() / THIRTY_PIXEL) * 0.5;
                        blue_day[i]+=time;
                    }
                }

                for(j=0 ; j < rectangles_purple.size() ; j++){
                    if(rectangles_purple.get(j).getAverX()-pos > -30 && rectangles_purple.get(j).getAverX()-pos < 30){
                        time = Math.round((double) rectangles_purple.get(j).getHeight() / THIRTY_PIXEL) * 0.5;
                        purple_day[i]+=time;
                    }
                }
            }
            for(i =0 ; i < last_rectanglesDay.size() ; i ++){
                Log.v("값체크","Day"+i+" "+pink_day[i]+" "+orange_day[i]+" "+green_day[i]+" "+blue_day[i]+" "+purple_day[i]);
            }


        }

        private ArrayList<Rectangle> RecMaker(ArrayList<Rectangle> rectanglesDay) {
            ArrayList<Rectangle> rectangles = new ArrayList<>();
            TreeSet<Coordinates> corDay;
            TreeSet<Coordinates> cor = new TreeSet<>();
            
            int X, Y = 0;
            int preY = 0;
            
            for (int i = 0; i < rectanglesDay.size(); i++) {
                corDay = rectanglesDay.get(i).getCoordiSet();
                Iterator<Coordinates> iter = corDay.iterator();

                // 좌표를 하나씩 꺼내보면서 확인
                // Coordinates 는 y축 오름차순 -> x축 오름차순 순으로 정렬되어 나옴.
                while (iter.hasNext()) {
                    Coordinates c = iter.next();
                    X = c.getX();
                    Y = c.getY();
                    if (preY == 0) {
                        preY = Y;
                    }

                    // 일정 범위 이상 y값이 떨어져 있으면 다른 덩어리로 인식한다.
                    if (Y - preY > 5 || Y - preY < 0) {
                        //Log.v("값 체크", "언제 오는 거지? " + X + " " + Y);

                        // 100보다 작은 덩어리는 잘못 인식된 부분으로 여긴다.
                        if (cor.size() > 100) {
                            rectangles.add(new Rectangle(cor));
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
                    if(corDay.size()>100) {
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
            int count = 0;

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
            int count = 0;
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

    private ArrayList<com.example.myapplication.Color> getGsonPref() {
        SharedPreferences pref = getSharedPreferences("Logined",MODE_PRIVATE);
        String id  = pref.getString("ID","");
        Log.v("값 체크","getGsonPref_로그인된 아이디 : "+id);
        SharedPreferences prefs = getSharedPreferences("History",MODE_PRIVATE);
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
        SharedPreferences pref = getSharedPreferences("Logined",MODE_PRIVATE);
        String id  = pref.getString("ID","");
        Log.v("값 체크","setGsonPref_로그인된 아이디 : "+id);
        SharedPreferences prefs = getSharedPreferences("History", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        if (!classes.isEmpty()) {
            editor.putString(id, gson.toJson(classes));
            Log.v("값 체크",gson.toJson(classes));
        } else {
            editor.putString(id, null);
        }
        editor.commit();
    }


    // AsyncTask <doInBackground, onProgressUpdate, onPostExecute>
    // 각각 해당 위치에서 필요한 데이터 타입이다.
    public class MyAsyncTask extends AsyncTask<Double, Integer, ArrayList<Double>> {
        public MyAsyncTask() {
        }

        @Override
        protected ArrayList<Double> doInBackground(Double... COLOR) {
            int i, j = 0;
            int count = 0;
            int CUT_Y = 30;

            ArrayList<Integer> arrayList = new ArrayList<>();
            boolean[][] bool = new boolean[coloredBitmap.getWidth()][coloredBitmap.getHeight()];

            for (i = 0; i < coloredBitmap.getWidth(); i++) {
                publishProgress((int) ((double) i / (double) coloredBitmap.getWidth() * (double) 100));
                for (j = coloredBitmap.getHeight() / 4; j < coloredBitmap.getHeight(); j++) {

                    // X축 위치 표시를 위해서 넣어둠
                    if (j == coloredBitmap.getHeight() / 4 && (i % 100 == 0 || i % 100 == 1 || i % 100 == 2 || i % 100 == 3 || i % 100 == 4)) {
                        coloredBitmap.setPixel(i, j, 0xFF000000);
                    }

                    // 해당 위치의 픽셀 RGB -> HSV로 변환
                    color_pos = coloredBitmap.getPixel(i, j);
                    Color.RGBToHSV((color_pos >> 16) & 0xFF, (color_pos >> 8) & 0xFF, (color_pos) & 0xFF, hsv_pos);

                    // 적절한 COLOR 값에 맞는 지 체크
                    if (hsv_pos[0] - COLOR[0] > -Threshold && hsv_pos[0] - COLOR[0] < Threshold &&
                            hsv_pos[1] > COLOR[1] &&
                            hsv_pos[2] > COLOR[2]) {
                        count++;
                    } else {
                        count = 0;
                    }

                    // 일정 범위만큼 연속해서 적절한 COLOR값이 있다면 해당 X좌표를 arrayList에 저장
                    if (count > CUT_Y) {
                        arrayList.add(i);
                        break;
                    }

                }
            }

            // 찾고 있는 COLOR값들의 X값을 저장한 arrayList
            Log.v("값 체크", "arrayList : " + arrayList.size());
            Log.v("값 체크", arrayList.toString());


            // arrayList에 저장된 X위치에서 찾는 COLOR값의 위치를 빨간색 바꿔 화면에 표시해줌.
            for (i = 0; i < arrayList.size(); i++) {
                for (j = coloredBitmap.getHeight() / 4; j < coloredBitmap.getHeight(); j++) {

                    // 픽셀의 RGB -> HSV
                    color_pos = coloredBitmap.getPixel(arrayList.get(i), j);
                    Color.RGBToHSV((color_pos >> 16) & 0xFF, (color_pos >> 8) & 0xFF, (color_pos) & 0xFF, hsv_pos);

                    // 찾는 COLOR의 조건이면 해당 픽셀을 빨간색으로 바꾼다.
                    // 2차원 boolean 배열에 저장한다.
                    if (hsv_pos[0] - COLOR[0] > -Threshold && hsv_pos[0] - COLOR[0] < Threshold &&
                            hsv_pos[1] > COLOR[1] &&
                            hsv_pos[2] > COLOR[2]) {
                        coloredBitmap.setPixel(arrayList.get(i), j, 0xFFFF0000);
                        bool[arrayList.get(i)][j] = true;
                    }
                }
            }

            // 위 코드까지는 세로 기준의 판단이기에 세로줄이 생긴 경우가 있다. 원치 않는 경우라 빼줘야 한다.
            // y축을 고정하고 x축으로 움직이면서 일정 범위 이상 원하는 COLOR를 찾는 경우만 유효.
            // 나머지 2차원 Boolean 값은 false로 바꾼다.
            int CHECK = 20;
            for (j = 0; j < coloredBitmap.getHeight(); j++) {
                for (i = 0; i < arrayList.size(); i++) {
                    // 픽셀이 기존에 true인 경우만
                    if (bool[arrayList.get(i)][j]) {
                        count = 0;

                        // COLOR해당하는 위치 좌우로 CHECK개씩 확인해서 갯수를 센다.
                        for (int k = -CHECK; k < CHECK; k++) {
                            if (arrayList.get(i) + k >= 0 && arrayList.get(i) + k < coloredBitmap.getWidth()) {
                                if (bool[arrayList.get(i) + k][j]) {
                                    count++;
                                }
                            }
                        }

                        // 주변에 10개의 해당 COLOR가 있다면 유효한 것
                        // 아니면 원치 않는 '세로줄'에 해당하기에 없앤다.
                        if (count < 10) {
                            bool[arrayList.get(i)][j] = false;
                        }
                    }
                }
            }

            // 위의 코드가 '세로줄'을 없앤 것이라면,
            // 지금 코드는 '가로줄'을 없애는 것이다.

            CHECK = 20;
            for (i = 0; i < arrayList.size(); i++) {
                for (j = 0; j < coloredBitmap.getHeight(); j++) {
                    //해당 COLOR인 경우만
                    if (bool[arrayList.get(i)][j]) {
                        count = 0;

                        // 해당 COLOR의 위치 위 아래로 CHECK개 만큼 확인
                        for (int k = -CHECK; k < CHECK; k++) {
                            if (j + k >= 0 && j + k < coloredBitmap.getHeight()) {
                                if (bool[arrayList.get(i)][j + k]) {
                                    count++;
                                }
                            }
                        }

                        // 해당 COLOR가 10개 미만이면 원치 않는 '가로줄'에 해당하므로 제거
                        if (count < 10) {
                            bool[arrayList.get(i)][j] = false;
                        }
                    }
                }
            }
            // 2차원 boolean배열에서 y축이 모두 false인 부분이면 arrayList에서 빼려고 함.
            // CheckX : 해당하는 X값의 모든 Y값이 원하는 COLOR가 아닌 경우 false
            boolean[] CheckX = new boolean[coloredBitmap.getWidth()];
            for (i = 0; i < coloredBitmap.getWidth(); i++) {
                count = 0;
                for (j = 0; j < coloredBitmap.getHeight(); j++) {
                    if (bool[i][j]) {
                        coloredBitmap.setPixel(i, j, 0xFF000000);
                        count++;
                    }
                }
                CheckX[i] = count != 0;
//                if (count == 0) {
//                    CheckX[i] = false;
//                } else {
//                    CheckX[i] = true;
//                }


            }

            // CheckX 를 토대로 arrayList에서 필요없는 X좌표를 없애준다.
            for (i = 0; i < arrayList.size(); i++) {
                if (!CheckX[arrayList.get(i)]) {
                    arrayList.remove(i);
                    Log.v("값 체크", "사라진 i : " + i + " 현재 Size : " + arrayList.size());
                    i--;
                }
            }


            // rectanglesDay : 요일 별로 나뉜 큰 사각형 덩어리
            // corDay : 요일별로 나뉜 덩어리의 좌표 정보 Set
            ArrayList<Rectangle> rectanglesDay = new ArrayList<>();
            TreeSet<Coordinates> corDay = new TreeSet<>();
            int preY;

            // 요일 나누기
            for (i = 0; i < arrayList.size(); i++) {
                // X 좌표가 크게 한번 바뀌면 다음 요일로 넘어간 것이다.
                if (i != 0 && arrayList.get(i) - arrayList.get(i - 1) > 20) {
                    //Log.v("값 체크", "언제 오는 거지? " + arrayList.get(i) + " i-1 : " + arrayList.get(i - 1));
                    rectanglesDay.add(new Rectangle(corDay));
                    corDay = new TreeSet<>();
                }
                for (j = 0; j < coloredBitmap.getHeight(); j++) {
                    if (bool[arrayList.get(i)][j]) {
                        corDay.add(new Coordinates(arrayList.get(i), j));
                    }
                }
            }
            rectanglesDay.add(new Rectangle(corDay));


            Log.v("값 체크", "rectanglesDay size : " + rectanglesDay.size());
            for (i = 0; i < rectanglesDay.size(); i++) {
                Log.v("값 체크", "rectanglesDay " + i + "'s 평균X위치: " + rectanglesDay.get(i).getAverX());
            }
            int distance = rectanglesDay.get(1).getAverX() - rectanglesDay.get(0).getAverX();
            Log.v("값체크", "요일 사이 Distance : " + distance);


            // 각 사각형 별로 나누는 작업
            // rectangles : 하나의 COLOR CHECK 덩어리
            // cor : 컬러체크 덩어리의 좌표 Set
            ArrayList<Rectangle> rectangles = new ArrayList<>();
            TreeSet<Coordinates> cor = new TreeSet<>();
            int X, Y = 0;
            preY = 0;
            for (i = 0; i < rectanglesDay.size(); i++) {
                corDay = rectanglesDay.get(i).getCoordiSet();
                Iterator<Coordinates> iter = corDay.iterator();

                // 좌표를 하나씩 꺼내보면서 확인
                // Coordinates 는 y축 오름차순 -> x축 오름차순 순으로 정렬되어 나옴.
                while (iter.hasNext()) {
                    Coordinates c = iter.next();
                    X = c.getX();
                    Y = c.getY();
                    //Log.v("값체크","x : "+X + "y : "+Y);
                    if (preY == 0) {
                        preY = Y;
                    }

                    // 일정 범위 이상 y값이 떨어져 있으면 다른 덩어리로 인식한다.
                    if (Y - preY > 5 || Y - preY < 0) {
                        //Log.v("값 체크", "언제 오는 거지? " + X + " " + Y);

                        // 100보다 작은 덩어리는 잘못 인식된 부분으로 여긴다.
                        if (cor.size() > 100) {
                            rectangles.add(new Rectangle(cor));
                        }
                        cor = new TreeSet<>();
                    }
                    cor.add(new Coordinates(X, Y));
                    preY = Y;
                }
            }
            rectangles.add(new Rectangle(cor));

            // 요일 사이 실제 거리 31.3mm
            // 30분을 의미하는 실제 거리 3mm
            double THIRTY_MIN = 3;
            double DAYS_DISTANCE = 31.3;
            // 30분에 해당하는 픽셀 거리 값
            double THIRTY_PIXEL = (double) distance * THIRTY_MIN / DAYS_DISTANCE;

            int MON_MIN = 200;
            int MON_MAX = 300;
            int TUE_MIN = 360;
            int TUE_MAX = 460;
            int WED_MIN = 510;
            int WED_MAX = 610;
            int THU_MIN = 50;
            int THU_MAX = 150;
            int FRI_MIN = 210;
            int FRI_MAX = 310;
            int SAT_MIN = 380;
            int SAT_MAX = 480;
            int SUN_MIN = 540;
            int SUN_MAX = 640;


            double Day1 = 0;
            double Day2 = 0;
            double Day3 = 0;
            double Day4 = 0;
            ArrayList<Double> ColorTime = new ArrayList<>();

            if ((rectanglesDay.get(0).getAverX() > 50 && rectanglesDay.get(0).getAverX() < 150)) {
                ColorTime.add(0.0);
                ColorTime.add(0.0);
                ColorTime.add(0.0);
                ColorTime.add(0.0);
            } else {
                ColorTime.add(0.0);
                ColorTime.add(0.0);
                ColorTime.add(0.0);
            }

            double time;

            Log.v("값 체크", "rectangles size : " + rectangles.size());
            for (i = 0; i < rectangles.size(); i++) {
                time = Math.round((double) rectangles.get(i).getHeight() / THIRTY_PIXEL) * 0.5;
                Log.v("값 체크", "rectangles " + i + "'s 사이즈 : " + rectangles.get(i).size() + " | 평균위치 : " + rectangles.get(i).getAverX() + " " + rectangles.get(i).getAverY()
                        + " | 높이 : " + rectangles.get(i).getHeight() + " | 시간 : " + time);
                //목금토일
                if ((rectanglesDay.get(0).getAverX() > 50 && rectanglesDay.get(0).getAverX() < 150)) {
                    if (rectangles.get(i).getAverX() > THU_MIN && rectangles.get(i).getAverX() < THU_MAX) {
                        Day1 += time;
                        ColorTime.set(0, Day1);
                    } else if (rectangles.get(i).getAverX() > FRI_MIN && rectangles.get(i).getAverX() < FRI_MAX) {
                        Day2 += time;
                        ColorTime.set(1, Day2);
                    } else if (rectangles.get(i).getAverX() > SAT_MIN && rectangles.get(i).getAverX() < SAT_MAX) {
                        Day3 += time;
                        ColorTime.set(2, Day3);
                    } else if (rectangles.get(i).getAverX() > SUN_MIN && rectangles.get(i).getAverX() < SUN_MAX) {
                        Day4 += time;
                        ColorTime.set(3, Day4);
                    }
                }
                //월화수
                else {
                    if (rectangles.get(i).getAverX() > MON_MIN && rectangles.get(i).getAverX() < MON_MAX) {
                        Day1 += time;
                        ColorTime.set(0, Day1);
                    } else if (rectangles.get(i).getAverX() > TUE_MIN && rectangles.get(i).getAverX() < TUE_MAX) {
                        Day2 += time;
                        ColorTime.set(1, Day2);
                    } else if (rectangles.get(i).getAverX() > WED_MIN && rectangles.get(i).getAverX() < WED_MAX) {
                        Day3 += time;
                        ColorTime.set(2, Day3);
                    }
                }
            }

            return ColorTime;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v("위치 체크", "AsyncTask_onPreExecute");
            Bar.setVisibility(View.VISIBLE);
        }

        // doInBackground 에서 publishProgress(values); 라고 설정하면
        // 그 값이 바뀔 때 마다 values값을 받아와서 onProgressUpdate가 업데이트 시켜준다.
        @Override
        protected void onProgressUpdate(Integer... values) {
//            Log.v("위치 체크", "AsyncTask_onProgressUpdate");
//            Log.v("값 체크", " " + values[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<Double> ColorTime) {
            super.onPostExecute(ColorTime);
            Log.v("위치 체크", "AsyncTask_onPostExecute");
            Bar.setVisibility(View.INVISIBLE);


            Log.v("값 체크", "ColorTime size : " + ColorTime.size());

            if (ColorTime.size() == 3) {
                Log.v("위치 체크", "AsyncTask_onPostExecute_Color size 3");
                day1.setVisibility(View.VISIBLE);
                day2.setVisibility(View.VISIBLE);
                day3.setVisibility(View.VISIBLE);
                day1.setText("월요일\n" + ColorTime.get(0) + "시간");
                day2.setText("화요일\n" + ColorTime.get(1) + "시간");
                day3.setText("수요일\n" + ColorTime.get(2) + "시간");
            } else if (ColorTime.size() == 4) {
                Log.v("위치 체크", "AsyncTask_onPostExecute_Color size 4");
                day1.setVisibility(View.VISIBLE);
                day2.setVisibility(View.VISIBLE);
                day3.setVisibility(View.VISIBLE);
                day4.setVisibility(View.VISIBLE);
                day1.setText("목요일\n" + ColorTime.get(0) + "시간");
                day2.setText("금요일\n" + ColorTime.get(1) + "시간");
                day3.setText("토요일\n" + ColorTime.get(2) + "시간");
                day4.setText("일요일\n" + ColorTime.get(3) + "시간");
            }
        }
    }
}

