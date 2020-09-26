package com.itube.android.modul.listview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by DINAS KOPERASI on 3/24/2017.
 */
public class ListviewAdapter extends ArrayAdapter<ListviewPreview> {

    private static Context context;
    CustomMethod customMethod;
    int layoutID;


    public interface CustomMethod {
        public void getView(View view, JSONObject jsonObject);
    }

    public ListviewAdapter(Context context, ArrayList<ListviewPreview> lists, int layoutID) {
        super(context, 0, lists);
        this.context = context;
        this.layoutID = layoutID;
    }

    public ListviewAdapter(Context context, ArrayList<ListviewPreview> lists, int layoutID, CustomMethod customMethod) {
        super(context, 0, lists);
        this.context = context;
        this.layoutID = layoutID;
        this.customMethod = customMethod;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position

        final ViewGroup thisParent = parent;
        final int thisPosition = position;

        ListviewPreview preview = getItem(position);
        JSONObject jsonObject = preview.object;

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(layoutID, parent, false);
        }

        try{
            ViewGroup viewParent = ((ViewGroup)convertView);
            List<View> allViews = getAllViews(viewParent);

            for(int i = 0; i < allViews.size(); i++){
                boolean isBinding = false;

                View objectView = allViews.get(i);
                String tag = "";

                if(objectView.getTag() != null){
                    tag = objectView.getTag().toString();
                    if(!tag.equals("")){
                        isBinding = true;
                    }
                }

                if(isBinding){
                    String[] objectTags = tag.split("!");

                    String datacolumn="", datatype="", dataformat="", datadefault="", datakey="";

                    for(int a = 0; a < objectTags.length; a++){
                        String[] p = objectTags[a].split(":");
                        if(p[0].equals("datacolumn")){datacolumn = p[1].replace("[;]", ":");}
                        else if(p[0].equals("datatype")){datatype = p[1].replace("[;]", ":");}
                        else if(p[0].equals("dataformat")){dataformat = p[1].replace("[;]", ":");}
                        else if(p[0].equals("datadefault")){datadefault = p[1].replace("[;]", ":");}
                        else if(p[0].equals("datakey")){datakey = p[1].replace("[;]", ":");}
                    }

                    String value = "";

                    if(datatype.equals("string")){
                        value = jsonObject.getString(datacolumn);
                        if(value.equals("")){
                            value = datadefault;
                        }
                    }
                    else if(datatype.equals("number")){
                        float v = jsonObject.getInt(datacolumn);

                        if(!dataformat.equals("none")){
                            DecimalFormat formatter = new DecimalFormat(dataformat);
                            value = formatter.format(v);
                        }
                        else {
                            value = String.valueOf(v);
                        }
                    }
                    else if(datatype.equals("decimal")){
                        double v = jsonObject.getDouble(datacolumn);

                        if(!dataformat.equals("none")){
                            DecimalFormat formatter = new DecimalFormat(dataformat);
                            value = formatter.format(v);
                        }
                        else {
                            value = String.valueOf(v);
                        }
                    }
                    else if(datatype.equals("integer")){
                        int v = jsonObject.getInt(datacolumn);
                        if(v == 0){
                            //value = datadefault;
                        }
                        value = String.valueOf(v);
                    }
                    else if(datatype.equals("date")){
                        String v = jsonObject.getString(datacolumn);

                        if(!dataformat.equals("none")){

                            SimpleDateFormat sdf = new SimpleDateFormat(dataformat);
                            Date d = sdf.parse(v);

                            sdf = new SimpleDateFormat(dataformat);
                            value = sdf.format(d);
                        }
                        else {
                            value = String.valueOf(v);
                        }
                    }
                    else if(datatype.equals("images")){
                        value = jsonObject.getString(datacolumn);
                        if(value.equals("")){
                            value = datadefault;
                        }
                    }

                    if(objectView instanceof TextView){
                        TextView object = (TextView)objectView;
                        object.setText(value);
                    }
                    else if(objectView instanceof ImageView){
                        ImageView object = (ImageView)objectView;
                        object.setVisibility(View.GONE);

                        if(!value.equals("")){
                            JSONObject imageURLs = new JSONObject(value);

                            if(imageURLs.has(datakey)){
                                object.setVisibility(View.VISIBLE);
                                String imageURL = imageURLs.getJSONObject(datakey).getString("url");

                                //new ImageLoadTask(context, object, imageURL, true, "", false).execute();
                            }
                        }
                        //object.setText(value);
                    }
                }
            }

            if(this.customMethod != null) {
                this.customMethod.getView(convertView, jsonObject);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        // Return the completed view to render on screen
        return convertView;
    }

    private List<View> getAllViews(View v) {
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