package com.example.myapplication;

import java.util.Iterator;
import java.util.TreeSet;

public class Rectangle {
    private TreeSet<Coordinates> CoordiSet;

    public Rectangle(TreeSet<Coordinates> coordiSet) {
        CoordiSet = coordiSet;
    }

    public Rectangle() {

    }

    public TreeSet<Coordinates> getCoordiSet() {
        return CoordiSet;
    }

    public void setCoordiSet(TreeSet<Coordinates> coordiSet) {
        CoordiSet = coordiSet;
    }

    public int getAverX() {
        int AverX = 0;
        int SumX = 0;
        Iterator<Coordinates> iter = CoordiSet.iterator();
        while (iter.hasNext()) {
            SumX += iter.next().getX();
        }
        if (CoordiSet.size() == 0) {
            return -1;
        }
        AverX = SumX / CoordiSet.size();
        return AverX;
    }

    public int getAverY() {
        int AverY = 0;
        int SumY = 0;
        Iterator<Coordinates> iter = CoordiSet.iterator();
        while (iter.hasNext()) {
            SumY += iter.next().getY();
        }
        if (CoordiSet.size() == 0) {
            return -1;
        }
        AverY = SumY / CoordiSet.size();
        return AverY;
    }

    public int getHeight() {
        int height = 0;
        int top = 0;
        int bottom = 1000;
        int y = 0;

        Iterator<Coordinates> iter = CoordiSet.iterator();
        while (iter.hasNext()) {
            y = iter.next().getY();
            if (top < y) {
                top = y;
            }
            if (bottom > y){
                bottom = y;
            }
        }

        height = top-bottom;

        return height;
    }
    public int size() {
        return CoordiSet.size();
    }
}
