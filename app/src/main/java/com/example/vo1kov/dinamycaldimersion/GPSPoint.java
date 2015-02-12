/**
 * Created by vo1kov on 11.02.15.
 */
package com.example.vo1kov.dinamycaldimersion;

import android.util.Log;

import java.util.Date;

public class GPSPoint {
    double Latitude;
    double Longitude;
    double Accuracy;
    float Speed;
    int Satellites;
    long Time;
    String sTime;

    public GPSPoint(double lat, double lon, float sp,  double accuracy, long time, int sat)
    {
        this.Latitude =lat;
        this.Longitude=lon;
        this.Speed=(float)(sp*3.6);
        //this.Time = time;
        this.Time = new Date().getTime();
        this.Accuracy = accuracy;
        this.Satellites = sat;

    }

    public GPSPoint(GPSPoint p)
    {
        this.Latitude =p.Latitude;
        this.Longitude=p.Longitude;
        this.Speed=p.Speed;
        //this.Time = time;
        this.Time = new Date().getTime();
        this.Accuracy = p.Accuracy;
        this.Satellites = p.Satellites;


    }


    public boolean inMoscow()
    {
        return ((this.Latitude >36)&&(this.Latitude <34)&&(this.Longitude>54)&&(this.Longitude<52));
    }

    public void renewTime(long time)
    {
        this.Time = time;
    }

    public String toTxtString()
    {
        String s = String.format(
                "%1$.8f|%2$.8f|%3$.2f|%4$d|%5$.3f|%6$tF %6$tT\n",
                this.Latitude,
                this.Longitude,
                this.Accuracy,
                this.Satellites,
                this.Speed,
                new Date(this.Time));

        Log.d("MMV", s);
        return s;

    }


    public String ToString()
    {
        String s = String.format(
                "Широта = %1$.8f \nДолгота = %2$.8f \nТочность = %3$.8f \nСпутников = %4$d \nСкорость = %5$.3f \nДата = %6$tF %6$tT",
                this.Latitude,
                this.Longitude,
                this.Accuracy,
                this.Satellites,
                this.Speed,
                new Date(this.Time));

       // Log.d("MMV", s);
        return s;

    }

}
