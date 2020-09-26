package com.itube.android.filepicker;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import java.io.File;

public class MyDirectory {

    public static String app_name = "ITube";
    public static String rootDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static String parentDirectory = rootDirectory + File.separator + app_name;
    public static String mediaDirectory = parentDirectory + File.separator + "Media";
    public static String imageDirectory = mediaDirectory + File.separator + "Images";
    public static String videoDirectory = mediaDirectory + File.separator + "Video";



}
