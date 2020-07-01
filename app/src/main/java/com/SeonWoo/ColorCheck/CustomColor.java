package com.SeonWoo.ColorCheck;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomColor extends AppCompatActivity {
    Bitmap rotatedBitmap;
    Bitmap coloredBitmap;
    String mCurrentPhotoPath;
    private static final int IMAGE_FINISH = 1;
    private static final int COLORCHECK_FINISH = 2;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_INPUT_DATE = 2;
    ImageView image;
    ProgressBar Bar;

    int color = 0;
    int curX;
    int curY;
    float[] hsv = new float[3];
    float[] hsv_pos = new float[3];
    int color_pos;
    int RGB_last;

    Button changed;
    Button origin;
    Button change;

    int PINK = 0XFFFE2E9A;
    int ORANGE = 0XFFFF8000;
    int GREEN = 0XFF1E8037;
    int BLUE = 0XFF0000FF;
    int PURPLE = 0XFFA901DB;



    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                // 카메라 촬영 후 이미지 처리 완료 일때,
                // 버튼, 이미지는 아직 안 보이고
                // 로딩창을 띄워준다.
                case IMAGE_FINISH:
                    image.setImageBitmap(coloredBitmap);
                    Bar.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    };


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_color);

        image = findViewById(R.id.CustomColor_iv_image);
        Bar = findViewById(R.id.progressBar2);
        Bar.setVisibility(View.VISIBLE);
        changed = findViewById(R.id.button3);
        origin = findViewById(R.id.CustomColor_origin);
        change = findViewById(R.id.CustomColor_bt_change);

        Intent intent = getIntent();
        origin.setBackgroundColor(intent.getIntExtra("CurrentColor",0x00000000));


        dispatchTakePictureIntent();

        change.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent in = new Intent();
                in.putExtra("ChangedColor",RGB_last);
                setResult(RESULT_OK, in);
                finish();
            }
        });


        // 이미지를 터치할 때, 터치한 위치의 색상과 같은 주변의 색상을 빨간색으로 만들어준다.
        // 처음에 색상값 확인을 위해서 만든 코드인데,
        // 나중에 유저 커스텀 색상을 설정할 때 다시 쓰기 위해서 주석 처리 보관
        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float width_ratio;
                float height_ratio;

                // Bitmap을 복사 / 그대로 복사 / 축소 복사
                coloredBitmap = Bitmap.createScaledBitmap(rotatedBitmap, rotatedBitmap.getWidth() / 4, rotatedBitmap.getHeight() / 4, true);

                // 이미지뷰와 픽셀 크기 차이 비율
                width_ratio = (float) coloredBitmap.getWidth() / (float) image.getWidth();
                height_ratio = (float) coloredBitmap.getHeight() / (float) image.getHeight();

                // User가 클릭한 위치(휴대폰에서의 X,Y좌표)를 받아와서 Bitmap 크기에 맞춘 값으로 계산
                curX = (int) (event.getX() * width_ratio); // 실제 픽셀과의 비율을 계산
                curY = (int) (event.getY() * height_ratio);

                Log.v("값 체크", "가로비율 : " + width_ratio + " 세로비율 : " + height_ratio);

                color = coloredBitmap.getPixel(curX, curY);
                Color.RGBToHSV((color >> 16) & 0xFF, (color >> 8) & 0xFF, (color) & 0xFF, hsv);

                // 클릭 지점의 주변 범위에 같은 색상값인 경우 -> 빨간색으로 변경
                int count = 0;
                float h = 0;
                float s = 0;
                float val = 0;

                for (int j = -50; j < 50; j++) {
                    for (int i = -50; i < 50; i++) {
                        if (curX + i < coloredBitmap.getWidth() && curY + j < coloredBitmap.getHeight()) {

                            color_pos = coloredBitmap.getPixel(curX + i, curY + j);
                            Color.RGBToHSV((color_pos >> 16) & 0xFF, (color_pos >> 8) & 0xFF, (color_pos) & 0xFF, hsv_pos);

                            if (hsv[0] - hsv_pos[0] > -20 && hsv[0] - hsv_pos[0] < 20) {
                                count++;
                                h += hsv_pos[0];
                                s += hsv_pos[1];
                                val += hsv_pos[2];
                                coloredBitmap.setPixel(curX + i, curY + j, 0xFFFF0000);
                                Log.v("값체크", "X : " + (curX + i) + " Y : " + (curY + j) +
                                        " // HSV : " + hsv_pos[0] + "(" + hsv[0] + ") " +
                                        hsv_pos[1] + "(" + hsv[1] + ") " +
                                        hsv_pos[2] + "(" + hsv[2] + ")");
                            }
                        }
                    }
                }
                h=h/count;
                s=s/count;
                val=val/count;
                float[] last = {h,s,val};
                RGB_last = Color.HSVToColor(0xFF,last);
                Log.v("값 체크", "평균 HSV : " + h + " | " + s + " | " + val + "RGB_Last : " + RGB_last);
                changed.setBackgroundColor(RGB_last);


                image.setImageBitmap(coloredBitmap);

                return false;
            }
        });

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

}
