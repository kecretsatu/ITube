package com.itube.android.modul.spinner;

import android.content.Context;
import android.widget.ArrayAdapter;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NIGHTFURY on 5/16/2017.
 */
public class SpinnerPreview {

    private String display;
    private String value;

    public SpinnerPreview(String value, String display){
        this.display = display;
        this.value = value;
    }

    public void setValue(String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }

    public void setDisplay(String display){
        this.display = display;
    }

    public String getDisplay(){
        return this.display;
    }

    @Override
    public String toString() {
        return display;
    }

    public static ArrayAdapter<SpinnerPreview> buildSpinnerData(Context ctx, final JSONArray resource, String valueKey, String displayKey, String hint, String tag){
        List<SpinnerPreview> spResource = new ArrayList<SpinnerPreview>();
        ArrayAdapter<SpinnerPreview> spAdapter = new ArrayAdapter<SpinnerPreview>(ctx, android.R.layout.simple_spinner_item, spResource);

        try{

            for(int i = 0; i < resource.length(); i++){
                String value = resource.getJSONObject(i).getString(valueKey);
                String display = resource.getJSONObject(i).getString(displayKey);
                spResource.add(new SpinnerPreview(value, display));
            }

            spAdapter = new ArrayAdapter<SpinnerPreview>(ctx, android.R.layout.simple_spinner_item, spResource);
            spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            return spAdapter;
        }
        catch (Exception e){
            e.printStackTrace();
            return spAdapter;
        }
    }

}
