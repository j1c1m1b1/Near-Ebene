package com.training.prodigious.nearebene.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.training.prodigious.nearebene.R;
import com.training.prodigious.nearebene.fragments.LocationSettingsFragment;
import com.training.prodigious.nearebene.interfaces.OnLocationChangedListener;
import com.training.prodigious.nearebene.interfaces.OnPermissionsSucceededListener;
import com.training.prodigious.nearebene.loaders.PlacesLoader;
import com.training.prodigious.nearebene.model.Place;
import com.training.prodigious.nearebene.utils.Parser;
import com.training.prodigious.nearebene.utils.Utils;

import org.json.JSONObject;

/**
 * @author Julio Mendoza on 2/3/16.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        OnLocationChangedListener, OnPermissionsSucceededListener, LoaderManager.LoaderCallbacks<JSONObject> {

    private static final int LOADER_ID = 500;
    private static final String LOCATION_KEY = "location_key";
    private static final String TAG = MapActivity.class.getSimpleName();
    private GoogleMap googleMap;
    private MapFragment mapFragment;
    private LocationSettingsFragment fragment;
    private boolean succeeded;

    private Location currentLocation;
    private CoordinatorLayout rootLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout);

        fragment = new LocationSettingsFragment();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.add(fragment, getString(R.string.locations_settings_fragment));
        transaction.commit();

        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LocationSettingsFragment.REQUEST_CHECK_SETTINGS) {
            fragment.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == LocationSettingsFragment.API_CLIENT_RESOLUTION_REQUEST) {
            fragment.onActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LocationSettingsFragment.LOCATION_PERMS_REQUEST_CODE) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        //noinspection ResourceType
        googleMap.setMyLocationEnabled(true);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed");
        if (currentLocation == null || Utils.compareLocations(currentLocation, location) > 0.5 || !succeeded) {
            if (googleMap != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
            }

            LoaderManager manager = getSupportLoaderManager();

            Bundle bundle = new Bundle();
            bundle.putParcelable(LOCATION_KEY, location);

            if (manager.hasRunningLoaders()) {
                manager.restartLoader(LOADER_ID, bundle, this).forceLoad();
            } else {
                manager.initLoader(LOADER_ID, bundle, this).forceLoad();
            }
        }
    }

    private void updateMapPlaces(Place[] places) {
        if (googleMap != null) {
            googleMap.clear();

            MarkerOptions options;

            LatLng latLng;

            for (Place place : places) {
                latLng = new LatLng(place.getLat(), place.getLng());
                options = new MarkerOptions().position(latLng).title(place.getName());

                googleMap.addMarker(options);
            }
        }
    }

    @Override
    public void onPermissionsSucceeded() {
        mapFragment.getMapAsync(this);
    }

    @Override
    public Loader<JSONObject> onCreateLoader(int id, Bundle args) {
        if (args != null && args.containsKey(LOCATION_KEY)) {
            currentLocation = args.getParcelable(LOCATION_KEY);
        }
        return new PlacesLoader(currentLocation, this);
    }

    @Override
    public void onLoadFinished(Loader<JSONObject> loader, JSONObject data) {

        try {
            succeeded = true;

            Place[] places = Parser.getPlaces(data);
            updateMapPlaces(places);
        } catch (Exception e) {
            Log.e(TAG, "Error!" + e.getMessage());
            Snackbar.make(rootLayout, R.string.error_loading, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<JSONObject> loader) {

    }
}
