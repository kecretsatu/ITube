package com.itube.android.views;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestFutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.itube.android.MainActivity;
import com.itube.android.R;
import com.itube.android.control.CustomPlayerView;
import com.itube.android.filepicker.MyDirectory;
import com.itube.android.modul.Initialize;
import com.itube.android.modul.recyclerView.RecyclerViewAdapterObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoList extends Fragment {

    String TAG = "VideoList";
    View view;
    CustomPlayerView player;

    boolean onPlayer = false;
    String path = null; //MyDirectory.videoDirectory;

    public VideoList(){

    }

    public VideoList(boolean onPlayer, CustomPlayerView player){
        this.onPlayer = onPlayer;
        this.player = player;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        try{
            view = inflater.inflate(R.layout.videolist, container, false);
            recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
            initializeRecyclerView();
            if(Initialize.isStorageExist(view.getContext(), "path")){
                path = Initialize.getStorage(view.getContext(), "path");
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getListFiles();
                }
            }, 500);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }

    public void setPath(String path){
        this.path = path;
    }

    public void getListFiles(){
        try{
            data.clear();
            if(path == null)return;
            File directory = new File(path);

            File[] files = directory.listFiles();

            /*if (files != null && files.length > 1) {
                Arrays.sort(files, new Comparator<File>() {
                    @Override
                    public int compare(File object1, File object2) {
                        return (int) ((object1.lastModified() > object2.lastModified()) ? object1.lastModified(): object2.lastModified());
                    }
                });
            }*/

            Log.d("Files", "Size: "+ files.length);
            for (int i = 0; i < files.length; i++)
            {
                //Log.d("Files", "FileName:" + files[i].getName());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", files[i].getName());
                jsonObject.put("path", files[i].getAbsolutePath());
                jsonObject.put("modified", Initialize.DateToString(new Date(files[i].lastModified()), "dd MMMM yyyy HH:mm:ss"));

                Log.d(TAG, jsonObject.toString());

                data.add(jsonObject);
            }
            mAdapter.notifyDataSetChanged();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    RecyclerView recyclerView;
    RecyclerViewAdapterObject mAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<JSONObject> data = new ArrayList<>();

    void initializeRecyclerView(){
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(null);
        recyclerView.setNestedScrollingEnabled(false);


        RecyclerViewAdapterObject.CustomMethod cm = new RecyclerViewAdapterObject.CustomMethod() {
            @Override
            public void onBindViewHolder(RecyclerViewAdapterObject.ViewHolder holder, final int position, Object object) {

                try{
                    LinearLayout linearLayout = (LinearLayout)holder.v;
                    final JSONObject jsonObject = (JSONObject)object;

                    String fname = jsonObject.getString("name");
                    int pos = fname.lastIndexOf(".");
                    if (pos > 0) {
                        fname = fname.substring(0, pos);
                    }


                    String detail = jsonObject.getString("modified") + " " + String.valueOf(getRandom(100, 999)) + "K views";

                    TextView fileNameTxt = (TextView)linearLayout.findViewById(R.id.fileNameTxt);
                    TextView fileDetailTxt = (TextView)linearLayout.findViewById(R.id.fileDetailTxt);
                    final ImageView thumbnail = (ImageView)linearLayout.findViewById(R.id.thumbnail);
                    final CircleImageView circleImage = (CircleImageView) linearLayout.findViewById(R.id.circleImage);

                    fileNameTxt.setText(fname);
                    fileDetailTxt.setText(detail);

                    File file = new File(jsonObject.getString("path"));
                    Glide.with(view.getContext()).load(file).addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            circleImage.setImageDrawable(resource);
                            return false;
                        }
                    }).into(thumbnail);

                    Log.d(TAG, jsonObject.getString("name"));

                    linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try{
                                //((MainActivity)getActivity()).playVideo(jsonObject);
                                //layoutManager.scrollToPosition(position);
                                if(!onPlayer){
                                    Intent intent = new Intent(view.getContext(), PlayerActivity.class);
                                    intent.putExtra("param", jsonObject.toString());
                                    startActivity(intent);
                                }
                                else{
                                    if(player.getHasVideoSource()){
                                        player.stop();
                                        player.initView();
                                    }
                                    player.setAutoHide(true);
                                    player.setAutoSize(true);
                                    player.setUseController(true);
                                    player.initLocal(jsonObject.getString("path"));
                                    player.play();
                                }
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });

                }
                catch (Exception e){
                    e.printStackTrace();
                }
                /*Uri uri = (Uri)object;
                LinearLayout linearLayout = (LinearLayout)holder.v;
                ImageView imageView = (ImageView)linearLayout.findViewById(R.id.imageView);

                Glide.with(getBaseContext()).load(uri).into(imageView);*/
            }
        };
        mAdapter = new RecyclerViewAdapterObject(view.getContext(), data, (onPlayer?R.layout.videolistview2:R.layout.videolistview), cm);
        //layoutManager = new GridLayoutManager(getBaseContext(), 3);
        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    public int getRandom(int min, int max){
        return new Random().nextInt((max - min) + 1) + min;
    }

}
