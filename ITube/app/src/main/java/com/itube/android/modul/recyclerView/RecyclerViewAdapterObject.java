package com.itube.android.modul.recyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapterObject extends RecyclerView.Adapter<RecyclerViewAdapterObject.ViewHolder> {

    Context context;
    ArrayList<?> lists = new ArrayList<>();
    int layoutID;
    CustomMethod customMethod;

    public RecyclerViewAdapterObject(Context context, ArrayList<?> lists, int layoutID) {
        this.context = context;
        this.lists = lists;
        this.layoutID = layoutID;
    }

    public RecyclerViewAdapterObject(Context context, ArrayList<?> lists, int layoutID, CustomMethod customMethod) {
        this.context = context;
        this.lists = lists;
        this.layoutID = layoutID;
        this.customMethod = customMethod;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layoutID, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(this.customMethod != null) {
            this.customMethod.onBindViewHolder(holder, position, lists.get(position));
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

    public interface CustomMethod {
        public void onBindViewHolder(ViewHolder holder, int position, Object object);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View v;
        public ViewHolder(View v) {
            super(v);
            this.v = v;
        }
    }

}
