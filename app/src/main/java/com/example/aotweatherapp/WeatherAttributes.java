package com.example.aotweatherapp;
public class WeatherAttributes{
    private int picture;
    private String date;
    private String weather;
    private String min;
    private String max;

    public WeatherAttributes(int picture, String date, String weather, String min, String max)
    {
        this.picture = picture; //must be an id
        this.date = date;
        this.weather = weather;
        this.min = min;
        this.max = max;
    }

    public int getPicture(){return picture;}
    public void setPicture(int x){this.picture = x;}
    public String getDate(){return date;}
    public void setDate(String x){this.date = x;}
    public String getWeather(){return weather;}
    public void setWeather(String x){this.weather = x;}
    public String getMin(){return min;}
    public void setMin(String x){this.min = x;}
    public String getMax(){return max;}
    public void setMax(String x){this.max = x;}

}
