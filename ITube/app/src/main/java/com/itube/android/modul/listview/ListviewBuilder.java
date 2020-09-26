package com.itube.android.modul.listview;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by NIGHTFURY on 3/23/2017.
 */

public class ListviewBuilder implements ListView.OnItemClickListener, ListView.OnScrollListener {
    private Context context;

    public ListviewAdapter listviewAdapter;
    ListviewAdapter.CustomMethod customMethod;

    public ArrayList<ListviewPreview> listviewPreview;
    ListView listView;
    View footer;
    FrameLayout footerViewContainer;

    JSONArray dataListView = new JSONArray();

    int layoutID;

    public ListviewBuilder(){

    }

    public ListviewBuilder(Context context, ListView listView, int layoutID){
        this.context = context;
        this.listView= listView;
        this.layoutID = layoutID;
    }

    public ListviewBuilder(Context context, ListView listView, int layoutID, MyInterface myInterface){
        this.context = context;
        this.listView= listView;
        this.layoutID = layoutID;
        this.myInterface = myInterface;
    }

    public ListviewBuilder(Context context, ListView listView, int layoutID, MyInterface myInterface, ListviewAdapter.CustomMethod customMethod){
        this.context = context;
        this.listView= listView;
        this.layoutID = layoutID;
        this.customMethod = customMethod;
        this.myInterface = myInterface;
    }

    public ListviewBuilder(Context context, ListView listView, int layoutID, ListviewAdapter.CustomMethod customMethod){
        this.context = context;
        this.listView= listView;
        this.layoutID = layoutID;
    }

    public void build(){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //footer = inflater.inflate(R.layout.footer_listview_loading, null);
        footerViewContainer = new FrameLayout(context);

        listView.addFooterView(footerViewContainer);
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(this);

        dataListView = new JSONArray();
        listviewPreview = new ArrayList<ListviewPreview>();

        if(this.customMethod != null) {
            listviewAdapter = new ListviewAdapter(context, listviewPreview, layoutID, customMethod);
        }
        else {
            listviewAdapter = new ListviewAdapter(context, listviewPreview, layoutID);
        }


        listView.setAdapter(listviewAdapter);
    }

    public void add(JSONArray json){
        try{
            for(int i = 0; i < json.length(); i++){
                dataListView.put(json.getJSONObject(i));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void set(JSONArray dataListView){
        this.dataListView = dataListView;
    }

    public void clear(){
        listviewAdapter.clear();
    }

    public void refresh(){
        listviewPreview = ListviewPreview.fromJson(dataListView);
        listviewAdapter.addAll(listviewPreview);
        listviewAdapter.notifyDataSetChanged();

        isFirstTime = false;

        Log.e("DataToListview", "Data: " + dataListView.toString());
    }

    public JSONObject getItem(int position){
        try{
            JSONObject item = dataListView.getJSONObject(position);
            return item;
        }
        catch (Exception e){
            e.printStackTrace();
            return new JSONObject();
        }
    }

    public JSONArray getItemx(int position){
        try{
            JSONArray item = dataListView.getJSONArray(position);
            return item;
        }
        catch (Exception e){
            e.printStackTrace();
            return new JSONArray();
        }
    }


    //region Interface Scrol Method
    public MyInterface myInterface;

    public interface MyInterface {
        public void requestOlderPost(JSONObject lastData);
    }

    //endregion


    //region ListView OnScroll

    boolean isFirstTime = true;
    boolean is_footer_shown = false;
    boolean isLast = false;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        try{
            if(myInterface == null) {
                return;
            }
            if(isFirstTime){
                return;
            }
            if(isLast){
                return;
            }

            if (listView.getLastVisiblePosition() == listView.getAdapter().getCount() - 1
                    && listView.getChildAt(listView.getChildCount() - 1).getBottom() <= listView.getHeight()) {

                if(listView.getChildCount() > 0){
                    if(!is_footer_shown){
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                footerViewContainer.addView(footer);
                                listView.setSelection(listView.getCount() - 1);

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        try{
                                            if(myInterface != null) {
                                                myInterface.requestOlderPost(dataListView.getJSONObject(dataListView.length()-1));
                                            }
                                        }
                                        catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                }, 300);
                            }
                        }, 100);
                        is_footer_shown = true;
                    }
                }

                //Toast.makeText(getContext(), "Last Item", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //endregion

    //region Interface OnItemClick Method
    OnClickInterface onClickInterface;

    public interface OnClickInterface {
        public void onItemClickListener(AdapterView<?> parent, View view, int position, long id);
    }
    public void setOnClickIterface(OnClickInterface onClickInterface){
        this.onClickInterface = onClickInterface;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(onClickInterface != null){
            onClickInterface.onItemClickListener(parent, view, position, id);
        }
    }
    //endregion
}
