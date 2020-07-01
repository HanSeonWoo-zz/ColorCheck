package com.SeonWoo.ColorCheck;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class CustomColor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_color);
    }


    // 이미지를 터치할 때, 터치한 위치의 색상과 같은 주변의 색상을 빨간색으로 만들어준다.
    // 처음에 색상값 확인을 위해서 만든 코드인데,
    // 나중에 유저 커스텀 색상을 설정할 때 다시 쓰기 위해서 주석 처리 보관

//        imageView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                float width_ratio;
//                float height_ratio;
//
//                // Bitmap을 복사 / 그대로 복사 / 축소 복사
//                coloredBitmap = rotatedBitmap.copy(Bitmap.Config.ARGB_8888, true);
//                coloredBitmap = Bitmap.createScaledBitmap(rotatedBitmap, rotatedBitmap.getWidth() / 4, rotatedBitmap.getHeight() / 4, true);
//
//                // 이미지뷰와 픽셀 크기 차이 비율
//                width_ratio = (float) coloredBitmap.getWidth() / (float) imageView.getWidth();
//                height_ratio = (float) coloredBitmap.getHeight() / (float) imageView.getHeight();
//
//                // User가 클릭한 위치(휴대폰에서의 X,Y좌표)를 받아와서 Bitmap 크기에 맞춘 값으로 계산
//                curX = (int) (event.getX() * width_ratio); // 실제 픽셀과의 비율을 계산
//                curY = (int) (event.getY() * height_ratio);
//
//                Log.v("값 체크", "가로비율 : " + width_ratio + " 세로비율 : " + height_ratio);
//
//                color = coloredBitmap.getPixel(curX, curY);
//                Color.RGBToHSV((color >> 16) & 0xFF, (color >> 8) & 0xFF, (color) & 0xFF, hsv);
//
//                // 클릭 지점의 주변 범위에 같은 색상값인 경우 -> 빨간색으로 변경
//                for (int j = -WIDTH; j < WIDTH; j++) {
//                    for (int i = -HEIGHT; i < HEIGHT; i++) {
//                        if (curX + i < coloredBitmap.getWidth() && curY + j < coloredBitmap.getHeight()) {
//
//                            color_pos = coloredBitmap.getPixel(curX + i, curY + j);
//                            Color.RGBToHSV((color_pos >> 16) & 0xFF, (color_pos >> 8) & 0xFF, (color_pos) & 0xFF, hsv_pos);
//
//                            if (hsv[0] - hsv_pos[0] > -Threshold && hsv[0] - hsv_pos[0] < Threshold) {
//                                coloredBitmap.setPixel(curX + i, curY + j, 0xFFFF0000);
//                                Log.v("값체크", "X : " + (curX + i) + " Y : " + (curY + j) +
//                                        " // HSV : " + hsv_pos[0] + "(" + hsv[0] + ") " +
//                                        hsv_pos[1] + "(" + hsv[1] + ") " +
//                                        hsv_pos[2] + "(" + hsv[2] + ")");
//                            }
//                        }
//                    }
//                }
//
//                imageView.setImageBitmap(coloredBitmap);
//
//                return false;
//            }
//        });
}
