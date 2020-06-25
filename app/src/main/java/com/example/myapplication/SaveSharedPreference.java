package com.example.myapplication;

/*
///이건 어때
    ID
useSubPassword - true/false
subPassword - 1234
Nickname - 선우
PINK
ORANGE
GREEN
BLUE
PURPLE
History
PickedDate - 2020년 06월 22일 월

    Logined
    "ID" 로그인된 아이디를 보여줌.

    autoLogin
    "ID"(String) : 자동로그인할 아이디

    useSubPassword
    ID - true / false
    subPassword(boolean) : History접근 시 필요한 서브비밀번호를 쓰는지 안 쓰는지

    Member
    ID(String) - Password(String)

    subPassword
    ID(String) - 설정한 subPassword가 나옴

    Nickname
    ID(String) - 설정한 닉네임이 나옴

    Color + ID
    PINK
    ORANGE
    GREEN
    BLUE
    PURPLE

    History
    ID - 스트링화된 History정보



 */


import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class SaveSharedPreference extends AppCompatActivity {

    static final String PREF_USER_NAME = "username";

//    static SharedPreferences getSharedPreferences(Context ctx) {
//        return PreferenceManager.getDefaultSharedPreferences(ctx);
//    }
//
//    // 계정 정보 저장
//    public static void setUserName(Context ctx, String userName) {
//        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
//        editor.putString(PREF_USER_NAME, userName);
//        editor.commit();
//    }
//
//    // 저장된 정보 가져오기
//    public static String getUserName(Context ctx) {
//        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
//    }
//
//    // 로그아웃
//    public static void clearUserName(Context ctx) {
//        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
//        editor.clear();
//        editor.commit();
//    }

    private ArrayList<Color> getGsonPref(String id) {
        SharedPreferences prefs = getSharedPreferences("History",MODE_PRIVATE);
        String json = prefs.getString(id, null);
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

}