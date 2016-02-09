package com.training.prodigious.nearebene.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.training.prodigious.nearebene.R;
import com.training.prodigious.nearebene.fragments.LocationSettingsFragment;
import com.training.prodigious.nearebene.interfaces.OnLocationChangedListener;
import com.training.prodigious.nearebene.interfaces.OnPermissionsSucceededListener;

/**
 * @author Julio Mendoza on 2/3/16.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        OnLocationChangedListener, OnPermissionsSucceededListener {

    private GoogleMap googleMap;
    private MapFragment mapFragment;
    private LocationSettingsFragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        fragment = new LocationSettingsFragment();
        transaction.add(fragment, getString(R.string.locations_settings_fragment));
        transaction.commit();

        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LocationSettingsFragment.REQUEST_CHECK_SETTINGS) {
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
        if (googleMap != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 14, 0f, 0f)));
        }
    }

    @Override
    public void onPermissionsSucceeded() {
        mapFragment.getMapAsync(this);
    }
}
