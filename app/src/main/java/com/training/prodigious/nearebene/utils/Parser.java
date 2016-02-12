package com.training.prodigious.nearebene.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.training.prodigious.nearebene.model.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Julio Mendoza on 2/10/16.
 */
public class Parser {

    private static final String RESULTS = "results";

    private static final String STATUS = "status";

    private static final String STATUS_OK = "OK";

    private static final String TAG = Parser.class.getSimpleName();

    public static Place[] getPlaces(JSONObject jsonResponse) {

        Place[] places = null;

        try {
            if (jsonResponse.getString(STATUS).equals(STATUS_OK)) {
                JSONArray results = jsonResponse.getJSONArray(RESULTS);
                Gson gson = new Gson();

                places = gson.fromJson(results.toString(), Place[].class);


            }
        } catch (JSONException e) {
            Log.e(TAG, "" + e.getMessage());
            e.printStackTrace();
        }

        return places;
    }
}
