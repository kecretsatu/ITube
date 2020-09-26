package com.itube.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;

import android.Manifest;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.itube.android.control.CustomPlayerView;
import com.itube.android.filepicker.MyDirectory;
import com.itube.android.modul.Initialize;
import com.itube.android.modul.fileutils.FileUtils;
import com.itube.android.permission.MyPermission;
import com.itube.android.views.VideoList;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;
import net.rdrei.android.dirchooser.DirectoryChooserConfig;
import net.rdrei.android.dirchooser.DirectoryChooserFragment;

import org.json.JSONObject;

import java.io.File;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements MenuItem.OnMenuItemClickListener {

    String TAG = "MainActivity";

    private final String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    LinearLayout container, playerContainer;
    CustomPlayerView player;
    MaterialToolbar toolbar;

    DirectoryChooserConfig config;
    DirectoryChooserFragment mDialog;
    FileUtils fileUtils;

    TextView titleTxt;

    Fragment myFrag;
    FragmentTransaction fragTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleTxt = (TextView) findViewById(R.id.titleTxt);
        toolbar = (MaterialToolbar)findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main);
        for(int i = 0; i < toolbar.getMenu().size(); i++){
            toolbar.getMenu().getItem(i).setOnMenuItemClickListener(this);
        }

        playerContainer = (LinearLayout)findViewById(R.id.playerContainer);
        container = (LinearLayout)findViewById(R.id.container);
        player = (CustomPlayerView)findViewById(R.id.player);

        fileUtils = new FileUtils(this, null);
        config = DirectoryChooserConfig.builder()
                .newDirectoryName("Nama Folder")
                .allowNewDirectoryNameModification(true)
                .initialDirectory("/")
                .build();
        mDialog = DirectoryChooserFragment.newInstance(config);
        mDialog.setDirectoryChooserListener(new DirectoryChooserFragment.OnFragmentInteractionListener() {
            @Override
            public void onSelectDirectory(@NonNull String path) {
                Log.d(TAG, path);

                VideoList videoList = ((VideoList)myFrag);
                Initialize.setStorage(getBaseContext(), "path", path);
                videoList.setPath(path);
                videoList.getListFiles();

                mDialog.dismiss();
            }

            @Override
            public void onCancelChooser() {
                mDialog.dismiss();
            }
        });

        setTitle();

        checkPermission();
    }

    void checkPermission(){
        MyPermission myPermission = new MyPermission(this);
        if(!myPermission.hasPermissions(PERMISSIONS)){
            Log.d(TAG, "Permission Not Granted");

            myPermission.showPermission(MyPermission.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
        else{
            //buildDirectory();
            loadVideo();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MyPermission.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                checkPermission();
            }
            else{
                Toast.makeText(getBaseContext(), "Semua perizinan harus disetujui", Toast.LENGTH_LONG).show();
                //beres();
            }
        }
    }

    void buildDirectory(){
        String[] folders = {MyDirectory.parentDirectory, MyDirectory.mediaDirectory, MyDirectory.imageDirectory, MyDirectory.videoDirectory, };

        for(int i = 0; i < folders.length; i++){
            String path = folders[i];
            File file = new File(path);
            if(!file.exists()){
                if(file.mkdir()){
                    Log.d(TAG, "Create Folder: " + path);
                }
                else {
                    Log.d(TAG, "Create Folder Failed: " + path);
                }
            }
        }

        //getListFiles();

        loadVideo();


    }

    void loadVideo(){
        FragmentManager fragMan = getSupportFragmentManager();
        fragTransaction = fragMan.beginTransaction();

        myFrag = new VideoList();
        fragTransaction.add(container.getId(), myFrag , "videoList");
        fragTransaction.commit();
    }

    public void playVideo(JSONObject jsonObject){
        try{
            playerContainer.setVisibility(View.VISIBLE);
            String fname = jsonObject.getString("name");
            int pos = fname.lastIndexOf(".");
            if (pos > 0) {
                fname = fname.substring(0, pos);
            }


            String detail = jsonObject.getString("modified") + " " + String.valueOf(getRandom(100, 999)) + "K views";

            TextView fileNameTxt = (TextView)findViewById(R.id.fileNameTxt2);
            TextView fileDetailTxt = (TextView)findViewById(R.id.fileDetailTxt2);
            final CircleImageView circleImage = (CircleImageView) findViewById(R.id.circleImage2);

            File file = new File(jsonObject.getString("path"));
            Glide.with(getBaseContext()).load(file).into(circleImage);

            fileNameTxt.setText(fname);
            fileDetailTxt.setText(detail);

            String path = jsonObject.getString("path");
            if(player.getHasVideoSource()){
                player.stop();
                player.initView();
            }
            player.setAutoHide(true);
            player.setAutoSize(true);
            player.setUseController(true);
            player.initLocal(path);
            player.play();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getRandom(int min, int max){
        return new Random().nextInt((max - min) + 1) + min;
    }

    @Override
    public void onBackPressed() {
        if(player.getIsPlaying()){
            player.stop();
            playerContainer.setVisibility(View.GONE);
        }
        else{
            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(player.getHasVideoSource())player.play();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(player.getHasVideoSource())player.stop();
    }

    @Override
    public void onPause() {
        super.onPause();

        if(player.getHasVideoSource())player.pause();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.menu_choose_directory){
            pickDirectory();
        }
        else if(id == R.id.menu_choose_directory_ext){
            pickDirectorySDCard();
        }
        else if(id == R.id.menu_change_title){
            changeTitle();
        }
        return false;
    }

    int PICK_DIRECTORY = 100;
    void pickDirectory(){
        mDialog.show(getFragmentManager(), null);

// REQUEST_DIRECTORY is a constant integer to identify the request, e.g. 0
        //startActivityForResult(chooserIntent, PICK_DIRECTORY);
    }


    void pickDirectorySDCard() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, PICK_DIRECTORY);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == PICK_DIRECTORY) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                //fileUtils.setUri(uri);

                String path = getPathFromURI(uri);
                Log.d(TAG, path);
            }
        }
        //TODO handle your request here
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getPathFromURI(Uri uri){
        DocumentFile documentFile = DocumentFile.fromTreeUri(this, uri);
        DocumentFile[] fileList = documentFile.listFiles();
        Log.d(TAG,fileList.toString() );
        int jj=1;

        for (DocumentFile docfile : fileList) {

            Log.d(TAG, "File: " + docfile.getUri() + "\n");
        }
        return "";
    }

    public String getRealPathFromURI (Uri contentUri) {
        String path = null;
        String[] proj = { MediaStore.MediaColumns.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            path = cursor.getString(column_index);
        }
        cursor.close();
        return path;
    }

    public void changeTitle(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ubah Judul");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        //input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setHint("ITube");
        if(Initialize.isStorageExist(getBaseContext(),"title")){
            input.setText(Initialize.getStorage(getBaseContext(), "title"));
        }

        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //m_Text = input.getText().toString();
                Initialize.setStorage(getBaseContext(), "title", input.getText().toString());
                setTitle();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    void setTitle(){
        String title = "ITube";
        if(Initialize.isStorageExist(getBaseContext(),"title")){
            title = Initialize.getStorage(getBaseContext(), "title");
        }
        titleTxt.setText(title);
    }

}
