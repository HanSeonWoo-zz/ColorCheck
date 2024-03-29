package com.SeonWoo.ColorCheck;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Setting extends AppCompatActivity {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    public static final int REQUEST_CODE_SETCOLOR = 101;
    public static final int REQUEST_CODE_PASSWORD = 102;
    public static final int REQUEST_CODE_ASK = 103;
    public static final int REQUEST_CODE_VERINFO = 104;
    public static final int REQUEST_CODE_LOGOUT = 105;
    public static final int REQUEST_CODE_WITHDRAWAL = 106;
    public static final int REQUEST_CODE_NICKNAME = 107;
    public static final int REQUEST_CODE_TAKEBACKUP = 108;
    public static final int REQUEST_CODE_EXCEL = 109;
    TextView setColor;
    Switch password;
    TextView ask;
    TextView VerInfo;
    TextView Logout;
    TextView Withdrawal;
    TextView Nickname;
    TextView Backup;
    TextView TakeBackup;
    File excel;
    TextView Excel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        pref = getSharedPreferences("1", MODE_PRIVATE);
        editor = pref.edit();

        // 초기화
        setColor = findViewById(R.id.setting_tv_setColor);
        password = findViewById(R.id.setting_sw_password);
        ask = findViewById(R.id.setting_tv_ask);
        VerInfo = findViewById(R.id.setting_tv_VerInfo);
        Backup = findViewById(R.id.setting_tv_backup);
        TakeBackup = findViewById(R.id.setting_tv_takebackup);
        Excel = findViewById(R.id.setting_tv_excel);

        // 로그아웃 / 회원탈퇴 기능을 빼뒀습니다.
        // 추가 가능성 있음.
//        Logout = findViewById(R.id.setting_tv_Logout);
//        Withdrawal = findViewById(R.id.setting_tv_Withdrawal);
        password.setChecked(pref.getBoolean("useSubPassword", false));
        Nickname = findViewById(R.id.setting_tv_Nickname);

        // 엑셀로 내보내기
        Excel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("위치 체크","Setting_Excel");
                try {
                    saveExcel();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        // 데이터 내보내기
        Backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("위치 체크","Setting_Backup_onClick");
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("plain/text");
                email.putExtra(Intent.EXTRA_SUBJECT, "안녕하세요. Color Check 백업 데이터입니다.");
                email.putExtra(Intent.EXTRA_TEXT, "안녕하세요. Color Check입니다. 요청하신 백업 데이터를 보내드립니다." +
                        "\n 다음 내용을 복사하여 '데이터 가져오기'에 붙여 넣으시면 됩니다. " +
                        "\n\n"+pref.getString("History",""));
                startActivity(email);

            }
        });

        // 데이터 가져오기
        TakeBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TakeBackup.class);
                startActivityForResult(intent, REQUEST_CODE_TAKEBACKUP);
            }
        });

        // 닉네임 변경
        Nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingNickname.class);
                startActivityForResult(intent, REQUEST_CODE_NICKNAME);
            }
        });

        //컬러세팅
        setColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingColor.class);
                startActivityForResult(intent, REQUEST_CODE_SETCOLOR);
            }
        });

        //비밀번호설정
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!password.isChecked()) {
                    Intent intent = new Intent(getApplicationContext(), SettingPassword.class);
                    intent.putExtra("type", "CHECK");
                    startActivityForResult(intent, REQUEST_CODE_PASSWORD);
                } else {
                    Intent intent = new Intent(getApplicationContext(), SettingPassword.class);
                    intent.putExtra("type", "SET");
                    startActivityForResult(intent, REQUEST_CODE_PASSWORD);
                }

            }
        });

        //문의하기
        ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("plain/text");
                String[] address = {"gkstjsdn10@naver.com"};
                email.putExtra(Intent.EXTRA_EMAIL, address);
                email.putExtra(Intent.EXTRA_SUBJECT, "제목");
                email.putExtra(Intent.EXTRA_TEXT, "내용");
                startActivity(email);
            }
        });
        //버전정보
        VerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), VerInfo.class);
                startActivityForResult(intent, REQUEST_CODE_VERINFO);
            }
        });


        // 우선 로그인 기능이 없기에 로그아웃과 회원탈퇴도 기능 없애두기
        // 나중에 쓸 가능성이 있어서 놔둠.

//        //로그아웃
//        Logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(Setting.this);
//                builder.setTitle("로그아웃");
//                builder.setMessage("정말 로그아웃 하시겠습니까?");
//                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
//                    public void onClick(
//                            DialogInterface dialog, int id) {
//                        //"예" 버튼 클릭시 실행하는 메소드
//                        Toast.makeText(getBaseContext(),"로그아웃되었습니다.", Toast.LENGTH_SHORT).show();
//
//                        SharedPreferences pref = getSharedPreferences("autoLogin",MODE_PRIVATE);
//                        SharedPreferences.Editor editor = pref.edit();
//                        editor.clear();
//                        editor.commit();
//
//                        Intent intent = new Intent(getApplicationContext(), Login.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        startActivity(intent);
//                    }
//                });
//                builder.setNegativeButton("아니오",  new DialogInterface.OnClickListener() {
//                    public void onClick(
//                            DialogInterface dialog, int id) {
//                        //"아니오" 버튼 클릭시 실행하는 메소드
//                        Toast.makeText(getBaseContext(),"취소하셨습니다.", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                builder.create().show();
//            }
//        });
//
//
//
//        //회원탈퇴
//        Withdrawal.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(getApplicationContext(), Withdrawal.class);
//                    startActivityForResult(intent, REQUEST_CODE_WITHDRAWAL);
//                }
//        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PASSWORD) {
            if (resultCode == RESULT_OK) {
                password.setChecked(true);
            }
            // 비밀번호 해제 시, 비밀번호 확인을 요청함.
            // 비밀번호 맞으면 RESULT_FIRST_USER로 오게 됨.
            // 비밀번호 틀리게 입력 시 , RESULT_OK로 간다.
            else if (resultCode == RESULT_FIRST_USER) {
                password.setChecked(false);
                editor.putBoolean("useSubPassword", false);
                editor.commit();

            } else {
                password.setChecked(false);
            }
        }

        if (requestCode == REQUEST_CODE_TAKEBACKUP) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "가져오기 완료", Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "가져오기 실패", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQUEST_CODE_EXCEL) {
//                Toast.makeText(getApplicationContext(), "이메일이 발송됐습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveExcel() throws ParseException {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        ArrayList<Color> mItems = getGsonPref();

        Row row = sheet.createRow(0); // 새로운 행 생성
        Cell cell;

        // 1번 셀 생성과 입력
        cell = row.createCell(0);
        cell.setCellValue("DATE");

        // 2번 셀에 값 생성과 입력
        cell = row.createCell(1);
        cell.setCellValue("PINK");
        cell = row.createCell(2);
        cell.setCellValue("ORANGE");
        cell = row.createCell(3);
        cell.setCellValue("GREEN");
        cell = row.createCell(4);
        cell.setCellValue("BLUE");
        cell = row.createCell(5);
        cell.setCellValue("PURPLE");

        for(int i = 0; i < mItems.size() ; i++){ // 데이터 엑셀에 입력
            row = sheet.createRow(i+1);
            cell = row.createCell(0);
            SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일 E", Locale.KOREA);
            SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

//            cell.setCellValue(mItems.get(i).getDate());
            cell.setCellValue(format2.format(format.parse(mItems.get(i).getDate())));
            cell = row.createCell(1);
            cell.setCellValue(mItems.get(i).getPink());
            cell = row.createCell(2);
            cell.setCellValue(mItems.get(i).getOrange());
            cell = row.createCell(3);
            cell.setCellValue(mItems.get(i).getGreen());
            cell = row.createCell(4);
            cell.setCellValue(mItems.get(i).getBlue());
            cell = row.createCell(5);
            cell.setCellValue(mItems.get(i).getPurple());
        }
        String filename="ColorCheck.xls";
        File dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File xls = new File(dir, filename);

        Log.v("위치 체크",dir.toString());
        Log.v("위치 체크",xls.toString());
        //   /storage/emulated/0/Documents
        try{
            // 이 때 파일을 생성한다. 대신 아무 것도 내용이 없어서 0 B
            FileOutputStream os = new FileOutputStream(xls);
            Log.v("위치 체크","FileOutputStream _ Done");

            // FileOutputStream으로 생성된 파일에 내가 원하는 엑셀 데이터를 입력하는 과정
            workbook.write(os);
            Log.v("위치 체크","workbook.write _ Done");
        }
        catch(IOException e){
            Log.v("위치 체크","catch");
            e.printStackTrace();
        }

        Intent it = new Intent(Intent.ACTION_SEND);
        it.setType("application/excel");
        Uri uri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".fileprovider",xls);
        Log.v("값 체크","Uri에 들어가는 Authority : " + getApplicationContext().getPackageName() + ".fileprovider");
        it.putExtra(Intent.EXTRA_SUBJECT, "안녕하세요 Color Check입니다. 요청하신 엑셀 데이터 입니다.");
        it.putExtra(Intent.EXTRA_TEXT, "안녕하세요 Color Check입니다.\n" +
                "요청하신 엑셀 데이터자료 입니다. 좋은 하루 보내세요 :) ");
        it.putExtra(Intent.EXTRA_STREAM, uri);
        startActivityForResult(Intent.createChooser(it,"엑셀 공유"),REQUEST_CODE_EXCEL);

    }

    private ArrayList<Color> getGsonPref() {
        String json = pref.getString("History", null);
        Gson gson = new Gson();
        Log.v("값 체크","getGsonPref_json : " + json);
        ArrayList<Color> urls = new ArrayList<>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                Log.v("값 체크","getGsonPref_JSONArray");

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

}
