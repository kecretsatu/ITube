package com.itube.android.modul;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.webkit.MimeTypeMap;

import com.itube.android.BuildConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


public class Initialize {

    //public static String ip_server = "http://192.168.43.107/projects/hokibrowser/";
    public static String app_name = "DISPORA";
    public static int version_code = BuildConfig.VERSION_CODE;
    public static String version_name = BuildConfig.VERSION_NAME;

    public static String ip_address_server = "http://192.168.43.107";
    public static String ip_server = ip_address_server + "/dispora";
    public static String ip_server_full = ip_address_server + "/dispora/admin";

    /*public static String ip_address_server = "http://dispora.surabaya.go.id";
    public static String ip_server = ip_address_server + "/app";
    public static String ip_server_full = "http://dispora.surabaya.go.id/app";*/

    public static String[] monthNameIDShort = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agt", "Sept", "Okt", "Nov", "Des"};
    public static String[] monthName = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};

    public static JSONObject userLogin = new JSONObject();
    public static JSONObject AllData = new JSONObject();
    public static JSONArray PostData = new JSONArray();


    public static boolean isMyServiceRunning(Context ctx, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean setStorage(Context context, String storage, String data){
        try {

            SharedPreferences.Editor editor = context.getSharedPreferences("storage", context.MODE_PRIVATE).edit();
            editor.putString(storage, data);
            editor.commit();
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static String getStorage(Context context, String storage){
        try{
            String data = "";
            SharedPreferences prefs = context.getSharedPreferences("storage", context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            if(prefs.contains(storage)){
                data = prefs.getString(storage, null);
            }

            return data;
        }
        catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public static boolean isStorageExist(Context context, String storage){
        try{
            SharedPreferences prefs = context.getSharedPreferences("storage", context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            if(prefs.contains(storage)){
                return true;
            }
            else{
                return false;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean clearStorage(Context ctx, String name){
        try{
            SharedPreferences.Editor editor = ctx.getSharedPreferences("storage", ctx.MODE_PRIVATE).edit();
            editor.remove(name);
            editor.apply();
            editor.commit();

            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }




    public static Date StringToDate(String str){
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = sdf.parse(str);

            return d;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static Date StringToDate(String str, String format){
        try{
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date d = sdf.parse(str);

            return d;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String DateToString(Date d, String format){
        try{
            SimpleDateFormat sdf = new SimpleDateFormat(format); //"yyyy-MM-dd HH:mm:ss"
            String str = (String) DateFormat.format(format, d);

            str = sdf.format(d);


            return str;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static JSONArray filterJsonArray(JSONArray data, String key, String value){
        try{
            JSONArray newData = new JSONArray();
            for(int i = 0; i < data.length(); i++){
                JSONObject obj = data.getJSONObject(i);
                if(obj.getString(key).equals(value)){
                    newData.put(obj);
                }
            }

            return newData;
        }
        catch (Exception e){
            e.printStackTrace();
            //Log.e("filterJsonArray_err:", e.getMessage());
            return  new JSONArray();
        }
    }


    public static boolean JSONArrayContainValue(JSONArray jsonArray, String value){
        boolean result = false;

        try{
            for(int i = 0; i < jsonArray.length(); i++){
                String str = jsonArray.getString(i);
                if(str.contains(value) || value.contains(str)){
                    result = true;
                    break;
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    public static int findJSONArray(JSONArray jsonArray, String item, String value){
        int position = -1;
        try{
            JSONArray json = new JSONArray();
            for(int i = 0; i < jsonArray.length(); i++){
                if(jsonArray.getJSONObject(i).getString(item).equals(value)){
                    position = i;
                    break;
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return position;
    }

    public static JSONArray removeJSONArray(JSONArray jsonArray, int position){
        try{
            JSONArray json = new JSONArray();
            for(int i = 0; i < jsonArray.length(); i++){
                if(i != position){
                    json.put(jsonArray.getJSONObject(i));
                }
            }
            return json;
        }
        catch (Exception e){
            e.printStackTrace();
            return jsonArray;
        }
    }

    public static JSONArray removeJSONArrayByValue(JSONArray jsonArray, String item, String value){
        try{
            JSONArray json = new JSONArray();
            for(int i = 0; i < jsonArray.length(); i++){
                if(!jsonArray.getJSONObject(i).getString(item).equals(value)){
                    json.put(jsonArray.getJSONObject(i));
                }
            }
            return json;
        }
        catch (Exception e){
            e.printStackTrace();
            return jsonArray;
        }
    }

    public static int getRandomNumber(){
        Random r = new Random();
        int n = r.nextInt(9 - 0) + 0;

        return n;
    }



    public static Bitmap getResizedBitmap(Bitmap bm, float currentSize, float maxSize) {

        float newWidth = 0;
        float newHeight = 0;
        int width = bm.getWidth();
        int height = bm.getHeight();

        if(currentSize > maxSize){
            //int diff = (int)currentSize / (int)maxSize;
            int diff = (int)Math.ceil(currentSize / maxSize);

            if(diff == 1){
                diff = 2;
            }

            newWidth = width / diff;
            newHeight = height / diff;
        }
        else{
            newWidth = width;
            newHeight = height;
        }

        // recreate the new Bitmap

        boolean doResize = false;

        if(width > newWidth){
            doResize = true;
        }
        else if(height > newHeight){
            doResize = true;
        }

        if(doResize){
            if(width >= height){
                newHeight = height / (width / newWidth);
            }
            else{
                newWidth = width / (height / newHeight);
            }
        }


        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();

        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

        return resizedBitmap;
    }

    public static Bitmap resizeBitmap(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static int getSizeConversion(int conversionType, int value, DisplayMetrics metrics){
        return (int)TypedValue.applyDimension(conversionType, value, metrics);
    }

    public static String getMimeType(Context context, Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    public static String getMimeType(String url) {
        String type = null;
        url = url.replace(" ", "%20");
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static String getDateStatus(Date date){
        String dayOfTheWeek = (String) DateFormat.format("EEEE", date); // Thursday
        String day          = (String) DateFormat.format("dd",   date); // 20
        String monthString  = (String) DateFormat.format("MMM",  date); // Jun
        String monthNumber  = (String) DateFormat.format("MM",   date); // 06
        String year         = (String) DateFormat.format("yyyy", date); // 2013
        String hour         = (String) DateFormat.format("HH", date); // 2013
        String minute       = (String) DateFormat.format("mm", date); // 2013
        String second       = (String) DateFormat.format("ss", date); // 2013


        Date today = new Date();

        long different = today.getTime() - date.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        if(elapsedDays == 0){
            if(elapsedHours == 0){
                if(elapsedMinutes == 0){
                    return "Baru Saja";
                }
                else{
                    return elapsedMinutes + " Menit";
                }
            }
            else if(elapsedHours > 0 && elapsedHours < 4){
                return elapsedHours + " Jam Lalu";
            }
            else{
                return hour + ":" + minute;
            }
        }
        else if(elapsedDays == 1){
            return "Kemarin " + hour + ":" + minute;
        }
        else{
            //return day + " " + monthName[Integer.parseInt(monthNumber)-1]+ " " + year;
            return day + " " + monthNameIDShort[Integer.parseInt(monthNumber)-1]+ " " + year;
        }
    }



    public static void restartApp(Context ctx, Class<?> cls){
        Intent mStartActivity = new Intent(ctx, cls);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(ctx, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

}
