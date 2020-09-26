package com.itube.android.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.itube.android.R;
import com.itube.android.control.CustomPlayerView;

import org.json.JSONObject;

public class PlayerActivity extends AppCompatActivity {

    CustomPlayerView player;
    JSONObject param;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        player = (CustomPlayerView)findViewById(R.id.player);



        playVideo();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadVideo();
            }
        }, 1000);
    }

    void playVideo(){
        try{
            param = new JSONObject(getIntent().getStringExtra("param"));
            player.setAutoHide(true);
            player.setAutoSize(true);
            player.setUseController(true);
            player.initLocal(param.getString("path"));
            player.play();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    void loadVideo(){
        FragmentManager fragMan = getSupportFragmentManager();
        FragmentTransaction fragTransaction = fragMan.beginTransaction();

        Fragment myFrag = new VideoList(true, player);
        fragTransaction.add(((LinearLayout)findViewById(R.id.container)).getId(), myFrag , "videoList");
        fragTransaction.commit();
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
    public void onBackPressed() {
        if(player.getHasVideoSource())player.stop();
        finish();
    }

    void pausePlayer(){
        player.pause();
    }
}
