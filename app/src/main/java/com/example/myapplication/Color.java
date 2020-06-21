package com.example.myapplication;

import android.util.Log;

import java.util.Iterator;
import java.util.LinkedHashSet;

public class Color {

    private String date;
    private String StartTime;
    private String EndTime;
    private String color;

    private double pink;
    private double orange;
    private double green;
    private double blue;
    private double purple;

    public Color(String date, double pink, double orange, double green, double blue, double purple) {
        this.date = date;
        this.pink = pink;
        this.orange = orange;
        this.green = green;
        this.blue = blue;
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
