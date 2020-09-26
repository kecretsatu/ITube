package com.itube.android.modul.listview;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by DINAS KOPERASI on 3/24/2017.
 */
public class ListviewPreview {
    public JSONObject object;


    public ListviewPreview(){
        super();
    }

    public ListviewPreview(JSONObject object){
        this.object = object;
    }

    public static ArrayList<ListviewPreview> fromJson(JSONArray jsonObjects) {
        ArrayList<ListviewPreview> lists = new ArrayList<ListviewPreview>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                lists.add(new ListviewPreview(jsonObjects.getJSONObject(i)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lists;
    }
}