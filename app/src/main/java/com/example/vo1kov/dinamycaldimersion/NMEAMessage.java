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
        Source = nmea.substring(1,3);
     if(!Arrays.asList(knownSources).contains(Source)) saveNMEA(nmea);
        Type = nmea.substring(3,6);



        switch (Type)
        {
            case "GSV":
                fillGSV(nmea);
                break;
            case "GGA":
                fillGGA(nmea);
                break;
            case "GNS":
                fillGNS(nmea);
                break;
            case "VTG":
                fillVTG(nmea);
                break;
            case "GSA":
                fillGSA(nmea);
                break;
            case "LOR":
                fillLOR(nmea);
                break;
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

    /*
    $GPRMC,123519,A,4807.038,N,01131.000,,022.4,084.4,230394,003.1,W*6A

    Where:
    RMC          Recommended Minimum sentence C
    123519       Fix taken at 12:35:19 UTC
    A            Status A=active or V=Void.
    4807.038,N   Latitude 48 deg 07.038' N
            01131.000,E  Longitude 11 deg 31.000' E
            022.4        Speed over the ground in knots
    084.4        Track angle in degrees True
    230394       Date - 23rd of March 1994
            003.1,W      Magnetic Variation
    *6A          The checksum data, always begins with *
    */
    //RMC - широта, долгота, достоверность, скорость, дата
    void fillRMC(String nmea)
    {
        String[] NMEA = nmea.split(",");
        this.Date = Long.parseLong(NMEA[1]);

    }

    //GSA - использованные спутники, точность
    /*
    $GPGSA,A,3,04,05,,09,12,,,24,,,,,2.5,1.3,2.1*39

    Where:
    GSA      Satellite status
    A        Auto selection of 2D or 3D fix (M = manual)
    3        3D fix - values include: 1 = no fix
    2 = 2D fix
    3 = 3D fix
    04,05... PRNs of satellites used for fix (space for 12)
    2.5      PDOP (dilution of precision)
    1.3      Horizontal dilution of precision (HDOP)
    2.1      Vertical dilution of precision (VDOP)
    *39      the checksum data, always begins with *
    */
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
