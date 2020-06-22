package com.example.myapplication;

public class Color implements Comparable<Color> {

    private String date;
    private String pink;
    private String orange;
    private String green;
    private String blue;
    private String purple;


    @Override
    public int compareTo(Color o) {
        //return this.date.compareTo(o.date);
        return o.date.compareTo(this.date);
    }

    public Color(String date, String pink, String orange, String green, String blue, String purple) {
        this.date = date;
        this.pink = pink;
        this.orange = orange;
        this.green = green;
        this.blue = blue;
        this.purple = purple;
    }

    public Color(String date, double pink, double orange, double green, double blue, double purple) {
        this.date = date;
        this.pink = String.format("%.1f", pink);
        this.orange = String.format("%.1f", orange);
        this.green = String.format("%.1f", green);
        this.blue = String.format("%.1f", blue);
        this.purple = String.format("%.1f", purple);
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

}
