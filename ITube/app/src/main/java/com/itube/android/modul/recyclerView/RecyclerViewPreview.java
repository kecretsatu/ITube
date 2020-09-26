package com.itube.android.modul.recyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by NIGHTFURY on 4/12/2017.
 */
public class RecyclerViewPreview {
    public JSONObject object;

    public RecyclerViewPreview(){
        super();
    }

    public RecyclerViewPreview(JSONObject object){
        this.object = object;
    }

    public static ArrayList<RecyclerViewPreview> fromJson(JSONArray jsonObjects) {
        ArrayList<RecyclerViewPreview> lists = new ArrayList<RecyclerViewPreview>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                lists.add(new RecyclerViewPreview(jsonObjects.getJSONObject(i)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lists;
    }
}
