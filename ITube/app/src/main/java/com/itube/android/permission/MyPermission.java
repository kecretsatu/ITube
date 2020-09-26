package com.itube.android.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class MyPermission {

    String TAG = "MyPermission";

    public static int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 500;
    public static int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 600;
    public static int MY_PERMISSIONS_REQUEST_GALLERY= 800;

    Context context;
    Activity activity;
    String[] permissions;


    public MyPermission(){

    }

    public MyPermission(Context context){
        this.context = context;
    }

    public MyPermission(Activity activity){
        this.activity = activity;
    }

    public boolean hasPermissions(String... permissions) {
        this.permissions = permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(activity, permission)
                        != PackageManager.PERMISSION_GRANTED) {

                    Log.d(TAG, "Permission not granted: " + permission);
                    return false;
                }
            }
        }
        return true;
    }

    public void showPermission(int requestCode){
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

}
