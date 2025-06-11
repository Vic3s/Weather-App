package com.chart.server;

public class WeatherInfoCurrent{

    int degrees;
    String rain;
    String humidity;
    String wind;
    String dow;
    String time;
    String condition;

    public WeatherInfoCurrent(){}

    public int getDegrees(){
        return degrees;
    }
    public void setDegrees(int degrees){
        this.degrees = degrees;
    }

    public String getRain(){
        return rain;
    }
    public void setRain(String rain){
        this.rain = rain;
    }

    public String getHumidity(){
        return humidity;
    }
    public void setHumidity(String humidity){
        this.humidity = humidity;
    }

    public String getWind(){
        return wind;
    }
    public void setWind(String wind){
        this.wind = wind;
    }

    public String getDow(){
        return dow;
    }
    public void setDow(String dow){
        this.dow = dow;
    }

    public String getTime(){
        return time;
    }
    public void setTime(String time){
        this.time = time;
    }

    public String getCondition(){
        return condition;
    }
    public void setCondition(String condition){
        this.condition = condition;
    }
}