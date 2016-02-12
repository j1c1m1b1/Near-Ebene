package com.training.prodigious.nearebene.connection;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.training.prodigious.nearebene.R;
import com.training.prodigious.nearebene.interfaces.OnRequestCompleteListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Julio Mendoza on 2/10/16.
 */
public class Requests {
    private static final String SCHEME = "https";

    private static final String GM_HOST = "maps.googleapis.com";

    private static final String[] PLACES_PATH =
            new String[]{"maps", "api", "place", "nearbysearch", "json"};

    private static final String PARAM_LOCATION = "location";

    private static final String PARAM_RADIUS = "radius";

    private static final String PARAM_API_KEY = "key";

    private static final String VALUE_RADIUS = "5000";

    //FORMATS

    private static final String LAT_LNG_FORMAT_SPACED = "%f, %f";
    private static final String TAG = Requests.class.getSimpleName();


    private static OkHttpClient client = new OkHttpClient();

    public static JSONObject searchPlacesNearby(Location location, Context context) {
        String latLng = String.format(Locale.getDefault(), LAT_LNG_FORMAT_SPACED,
                location.getLatitude(), location.getLongitude());

        String apiKey = context.getString(R.string.places_server_key);

        String[] params = new String[]{PARAM_LOCATION, PARAM_RADIUS, PARAM_API_KEY};

        String[] values = new String[]{latLng, VALUE_RADIUS, apiKey};

        HttpUrl url = parseUrl(GM_HOST, PLACES_PATH, params, values);

        return callAPISync(url);

    }


    private static HttpUrl parseUrl(@NonNull String host, @NonNull String[] path,
                                    @Nullable String[] params, @Nullable String[] values) {
        HttpUrl url;

        HttpUrl.Builder urlBuilder = new HttpUrl.Builder();

        urlBuilder.scheme(SCHEME)
                .host(host);

        for (String segment : path) {
            urlBuilder.addPathSegment(segment);
        }

        urlBuilder.query("");

        if (params != null && values != null) {
            for (int i = 0; i < params.length; i++) {
                urlBuilder.addEncodedQueryParameter(params[i], values[i]);
            }
        }

        url = urlBuilder.build();

        return url;
    }

    private static JSONObject callAPISync(HttpUrl url) {
        JSONObject jsonResponse = null;
        Log.d(TAG, "" + url.toString());
        Request request = new Request.Builder().url(url).build();

        try {
            Response response = client.newCall(request).execute();
            String responseString = response.body().string();
            jsonResponse = new JSONObject(responseString);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return jsonResponse;
    }


    private static void callAPI(HttpUrl url, final OnRequestCompleteListener listener) {
        Log.d(TAG, "" + url.toString());
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error: " + e.getMessage());
                listener.onFail();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString = response.body().string();
                try {
                    JSONObject jsonResponse = new JSONObject(responseString);
                    listener.onSuccess(jsonResponse);
                } catch (JSONException e) {
                    listener.onFail();
                    e.printStackTrace();
                }
            }
        });
    }

}
