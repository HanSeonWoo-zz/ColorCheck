package com.example.myapplication;

public class Coordinates implements Comparable<Coordinates>{
    private int x;
    private int y;

    @Override
    public boolean equals(Object object) {
        boolean sameSame = false;

        if (object != null && object instanceof Coordinates){
            if(this.x == ((Coordinates) object).getX() && this.y == ((Coordinates) object).getY()){
                sameSame = true;
            }
        }

        return sameSame;
    }


    // y 오름차순 -> x 오름차순 정렬
    @Override
    public int compareTo(Coordinates coord){
        if(this.y >coord.y){
            return 1;
        } else if (this.y == coord.y) {
            if(this.x > coord.x){
                return 1;
            }

        }
        return -1;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

