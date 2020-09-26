package com.itube.android.modul.recyclerView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.itube.android.modul.listview.ListviewPreview;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by NIGHTFURY on 4/12/2017.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static Context context;
    ArrayList<ListviewPreview> lists = new ArrayList<ListviewPreview>();
    CustomMethod customMethod;
    int layoutID;


    public interface CustomMethod {
        public void onBindViewHolder(ViewHolder holder, int position, JSONObject jsonObject);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView airlines_name;
        public View v;
        public ViewHolder(View v) {
            super(v);
            this.v = v;
        }
    }

    public RecyclerViewAdapter(Context context, ArrayList<ListviewPreview> lists, int layoutID) {
        this.context = context;
        this.lists = lists;
        this.layoutID = layoutID;
    }

    public RecyclerViewAdapter(Context context, ArrayList<ListviewPreview> lists, int layoutID, CustomMethod customMethod) {
        this.context = context;
        this.lists = lists;
        this.layoutID = layoutID;
        this.customMethod = customMethod;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layoutID, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try{
            ListviewPreview preview = lists.get(position);
            JSONObject jsonObject = preview.object;

            //holder.airlines_name.setText(jsonObject.getString("airlines_name"));
            //TextView tv = (TextView) holder.v.findViewById(R.id.airlines_name);
            //tv.setText(jsonObject.getString("airlines_name"));


            if(this.customMethod != null) {
                this.customMethod.onBindViewHolder(holder, position, jsonObject);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        try{
            return lists.size();
        }
        catch (Exception e){
            return 0;
        }
    }

    public static List<View> getAllViews(View v) {
        if (!(v instanceof ViewGroup) || ((ViewGroup) v).getChildCount() == 0) // It's a leaf
        { List<View> r = new ArrayList<View>(); r.add(v); return r; }
        else {
            List<View> list = new ArrayList<View>(); list.add(v); // If it's an internal node add itself
            int children = ((ViewGroup) v).getChildCount();
            for (int i=0;i<children;++i) {
                list.addAll(getAllViews(((ViewGroup) v).getChildAt(i)));
            }
            return list;
        }
    }
}
