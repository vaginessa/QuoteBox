package fr.klemek.quotetube.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.util.Calendar;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.Duration;

import fr.klemek.quotetube.MainActivity;

/**
 * Created by klemek on 16/03/17.
 */

public abstract class Utils {

    public static void debugLog(Object o,String msg){
        debugLog(o.getClass(),msg,0);
    }

    public static void debugLog(Class c,String msg,int c2){
        if(Constants.DEBUG)
            Log.d(Constants.LOG_ID+":"+c.getSimpleName(),"\t"+msg);
    }

    public static String JSONgetString(JSONObject obj,String path) throws JSONException {
        String[] names = path.split("\\.");
        if(path.length()==1){
            return obj.getString(path);
        }else{
            JSONObject obj2 = obj.getJSONObject(names[0]);
            for(int i = 1; i < names.length-1; i++){
                obj2 = obj2.getJSONObject(names[i]);
            }
            return obj2.getString(names[names.length-1]);
        }

    }

    public static String formatDuration(long millis){
        return formatDuration(millis, false);
    }

    public static String formatDuration(long millis, boolean showMillis) {
        long seconds = millis/1000;
        if(seconds/3600>0){
            if(showMillis) {
                return String.format(
                        "%d:%02d:%02d.%03d",
                        seconds / 3600,
                        (seconds % 3600) / 60,
                        seconds % 60,
                        millis%1000);
            }else{
                return String.format(
                        "%d:%02d:%02d",
                        seconds / 3600,
                        (seconds % 3600) / 60,
                        seconds % 60);
            }
        }else{
            if(showMillis) {
                return String.format(
                        "%02d:%02d.%03d",
                        (seconds % 3600) / 60,
                        seconds % 60,
                        millis%1000);
            }else {
                return String.format(
                        "%02d:%02d",
                        seconds / 60,
                        seconds % 60);
            }
        }
    }

    public static String formatDurationInSeconds(long millis){
        long seconds = millis/1000;
        return String.format(
                "%02d.%03d",
                seconds,
                millis%1000);
    }



    public static long getDuration(String sduration) {
        String time = sduration.substring(2);
        long duration = 0L;
        Object[][] indexs = new Object[][]{{"H", 3600}, {"M", 60}, {"S", 1}};
        for(int i = 0; i < indexs.length; i++) {
            int index = time.indexOf((String) indexs[i][0]);
            if(index != -1) {
                String value = time.substring(0, index);
                duration += Integer.parseInt(value) * (int) indexs[i][1] * 1000;
                time = time.substring(value.length() + 1);
            }
        }
        return duration;
    }


}