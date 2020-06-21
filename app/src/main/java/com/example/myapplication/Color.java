package com.example.myapplication;

import android.util.Log;

import java.util.Iterator;
import java.util.LinkedHashSet;

public class Color implements Comparable<Color>{

    private String date;
    private String StartTime;
    private String EndTime;
    private String color;

    private String pink;
    private String orange;
    private String green;
    private String blue;
    private String purple;



    @Override
    public int compareTo(Color o) {
        return this.date.compareTo(o.date);
    }

    public Color(String date, String pink, String orange, String green, String blue, String purple) {
        this.date = date;
        this.pink = pink;
        this.orange = orange;
        this.green = green;
        this.blue = blue;
        this.purple = purple;
    }

    public String getPink() {
        return pink;
    }

    public void setPink(String pink) {
        this.pink = pink;
    }

    public String getOrange() {
        return orange;
    }

    public void setOrange(String orange) {
        this.orange = orange;
    }

    public String getGreen() {
        return green;
    }

    public void setGreen(String green) {
        this.green = green;
    }

    public String getBlue() {
        return blue;
    }

    public void setBlue(String blue) {
        this.blue = blue;
    }

    public String getPurple() {
        return purple;
    }

    public void setPurple(String purple) {
        this.purple = purple;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = Integer.toString(color);
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Color(String date, String startTime, String endTime, int color) {
        this.date = date;
        StartTime = startTime;
        EndTime = endTime;
        this.color = Integer.toString(color);
    }
    public Color(String date, String startTime, String endTime, String color) {
        this.date = date;
        StartTime = startTime;
        EndTime = endTime;
        this.color = color;
    }

    public Color(LinkedHashSet<String> stringSet) {
        Iterator<String> iterator = stringSet.iterator();

           date = iterator.next();
           StartTime = iterator.next();
           EndTime = iterator.next();
           color = iterator.next();
        Log.v("값체크",date);
        Log.v("값체크",StartTime);
        Log.v("값체크",EndTime);
        Log.v("값체크",color);
    }


}
