package com.example.vo1kov.dinamycaldimersion;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by vo1kov on 14.02.15.
 */
public class NMEAMessage {

    String Source;
    String Type;
    final String[] knownSources = {"GP", "GL", "GN", "PG", "QZ", "IM"};

    //Sattelite[] Массив (номер-азимут-восхождение)
    boolean GPS;
    boolean GLONASS;
    long Date;
    double Latitude;
    double Longitude;
    double Altitude;
    double Speed;
    int Age;
    float Accuracy;




    public NMEAMessage(String nmea)
    {
        Source = nmea.substring(1,2);
     if(!Arrays.asList(knownSources).contains(Source)) saveNMEA(nmea);
        Type = nmea.substring(3,5);



        switch (Type)
        {
            case "RMC":
                fillRMC(nmea);
                break;
            default: saveNMEA(nmea);
                break;
        }


    }

    //GSV - количество спутников, (номер-азимут-восхождение)
    void fillGSV(String nmea)
    {


    }

    //GGA - дата, широта, долгота, достоверность, колво спутников, высота моря
    void fillGGA(String nmea)
    {


    }

    //GNS - время, широта, долгота, спутники GPS|спутники ГЛОНАСС, количество спутников, точность, возраст поправки
    void fillGNS(String nmea)
    {


    }

    //VTG - скорость
    void fillVTG(String nmea)
    {


    }

    //RMC - широта, долгота, достоверность, скорость, дата
    void fillRMC(String nmea)
    {


    }

    //GSA - использованные спутники, точность
    void fillGSA(String nmea)
    {


    }

    //LOR - сервисная информация
    void fillLOR(String nmea)
    {


    }






    public void saveNMEA(String nmea)
    {

        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
           // Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + "GPSTracks");
        // создаем каталог
        sdPath.mkdirs();
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, String.format("NMEA%1$tF_%1$tT.txt",new Date()));
        try {
            // открываем поток для записи
            BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
            bw.write(nmea);
            bw.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
