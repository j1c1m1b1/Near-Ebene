package com.training.prodigious.nearebene.loaders;

import android.content.Context;
import android.location.Location;
import android.support.v4.content.AsyncTaskLoader;

import com.training.prodigious.nearebene.connection.Requests;

import org.json.JSONObject;

/**
 * @author Julio Mendoza on 2/10/16.
 */
public class PlacesLoader extends AsyncTaskLoader<JSONObject> {


    private final Location location;

    public PlacesLoader(Location location, Context context) {
        super(context);
        this.location = location;
    }

    @Override
    public JSONObject loadInBackground() {
        return Requests.searchPlacesNearby(location, getContext());
    }
}
