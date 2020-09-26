package com.itube.android.modul.spinner;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by NIGHTFURY on 5/16/2017.
 */
public class SpinnerAdapter extends ArrayAdapter<SpinnerPreview> {

    private Context context;
    private SpinnerPreview[] myObjs;
    //private ArrayList<SpinnerPreview> myObjs;

    public SpinnerAdapter(Context context, int resourceId, SpinnerPreview[] myObjs) {
        super(context, resourceId, myObjs);
        this.context = context;
        this.myObjs = myObjs;
    }

    public int getCount(){
        return myObjs.length;
    }

    public SpinnerPreview getItem(int position){
        return myObjs[position];
    }

    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView label = new TextView(context);
        label.setText(myObjs[position].getValue());
        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = new TextView(context);
        label.setText(myObjs[position].getValue());
        return label;
    }

}
