package com.itube.android.modul.fileutils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;

import com.itube.android.BuildConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileUtils {

    static String TAG = "FileUtils";

    Context context;
    Uri uri;

    public FileUtils(){

    }

    public FileUtils(Context context, Uri uri){
        this.context = context;
        this.uri = uri;
    }

    public void setUri(Uri uri){
        this.uri = uri;
    }

    public String getPath() {
        String path = null;

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {
            if(DocumentsContract.isDocumentUri(context, uri)){
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        path = Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    path = getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else
                if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[] {split[1]};
                    path = getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
        }
        // MediaStore (and general)
        if(path == null){
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                // Return the remote address
                if (isGooglePhotosUri(uri))path = uri.getLastPathSegment();
                else path = getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                path = uri.getPath();
            }
        }

        if(path == null){
            path = getAlternativePath();
        }

        return path;
    }

    String getAlternativePath(){
        String path = uri.getPath();

        if(!path.startsWith("/storage")){
            String[] splits = path.split("/");
            path = "";
            for(int i = 0; i < splits.length; i++){
                if(splits[i].equals("storage")){
                    for(int j = i; j < splits.length; j++){
                        path += "/" + splits[j];
                    }
                    break;
                }
            }
        }



        Log.d(TAG, path);

        return path;
    }

    public String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String result = null;
        Cursor cursor = null;
        try{
            final String column = "_data";
            final String[] projection = { column };
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                if(cursor.getColumnCount() > 0){
                    int index = cursor.getColumnIndex(column);
                    result = cursor.getString(index);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (cursor != null)
                cursor.close();
        }
        return result;
    }

    public String getRealPathFromURI() {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(context, uri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }



    public static boolean isImageType(Context context, File file){
        String contentType = getMimeType(context, Uri.fromFile(file));
        return (contentType != null ? contentType.startsWith("image/") : false);
    }
    public static boolean isImageType(String url){
        String contentType = getMimeType(url);
        return (contentType != null ? contentType.startsWith("image/") : false);
    }

    public static boolean isVideoType(Context context, File file){
        String contentType = getMimeType(context, Uri.fromFile(file));
        return (contentType != null ? contentType.startsWith("video/") : false);
    }
    public static boolean isVideoType(String url){
        String contentType = getMimeType(url);
        return (contentType != null ? contentType.startsWith("video/") : false);
    }

    public static boolean isAudioType(Context context, File file){
        String contentType = getMimeType(context, Uri.fromFile(file));
        return (contentType != null ? contentType.startsWith("audio/") : false);
    }
    public static boolean isAudioType(String url){
        String contentType = getMimeType(url);
        return (contentType != null ? contentType.startsWith("audio/") : false);
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
        try{
            String type = null;
            url = url.replace(" ", "%20");
            url = url.replace("+", "%2B");
            String extension = MimeTypeMap.getFileExtensionFromUrl(url);
            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }
            return type;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String getMimeType() {
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

    public static String getExtension(String path){
        return path.substring(path.lastIndexOf("."));
    }
    public static String getExtension(File file){
        return file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
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

    public static boolean saveBitmap(File file, Bitmap bitmap){
        try {

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,50,bytes);


            if(file.exists())file.delete();

            file.createNewFile();
            FileOutputStream fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());
            fo.close();

            /*OutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            outputStream.flush();
            outputStream.close();*/

            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean saveFile(File fileOri, File fileTarget){
        try {

            if(fileTarget.exists())fileTarget.delete();

            fileTarget.createNewFile();
            FileOutputStream fo = new FileOutputStream(fileTarget);
            fo.write(FileToBytes(fileOri));
            fo.close();

            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static byte[] FileToBytes(File f) throws IOException {
        int size = (int) f.length();
        byte bytes[] = new byte[size];
        byte tmpBuff[] = new byte[size];
        FileInputStream fis= new FileInputStream(f);
        try {

            int read = fis.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }
        }  catch (IOException e){
            e.printStackTrace();
            bytes = null;
        } finally {
            fis.close();
        }

        return bytes;
    }

    public static void openFile(Context context, File file){
        String type = getMimeType(context, Uri.fromFile(file));
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID+".fileprovider", file);
            intent.setDataAndType(contentUri, type);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), type);
        }


        context.startActivity(intent);
    }

    public static void chooseFile(Context context, String mimeType){

    }


}
