package com.training.prodigious.nearebene.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.training.prodigious.nearebene.R;
import com.training.prodigious.nearebene.interfaces.OnLocationChangedListener;
import com.training.prodigious.nearebene.interfaces.OnPermissionsSucceededListener;

/**
 * @author Julio Mendoza on 2/5/16.
 */
public class LocationSettingsFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final int LOCATION_PERMS_REQUEST_CODE = 100;
    public static final int REQUEST_CHECK_SETTINGS = 200;
    public static final int API_CLIENT_RESOLUTION_REQUEST = 300;
    private static final String TAG = LocationSettingsFragment.class.getSimpleName();

    private LocationRequest locationRequest;
    private GoogleApiClient apiClient;

    private OnLocationChangedListener listener;

    private OnPermissionsSucceededListener onPermissionsSucceededListener;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            listener = (OnLocationChangedListener) getActivity();
            onPermissionsSucceededListener = (OnPermissionsSucceededListener) getActivity();
        } catch (Exception e) {
            //Ignore exception
        }
        requestLocationPermissions();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (apiClient != null && !apiClient.isConnected()) {
            apiClient.connect();
        } else {
            startApiClient();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (apiClient != null && apiClient.isConnected()) {
            apiClient.disconnect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (apiClient != null && apiClient.isConnected()) {
            createLocationSettingsRequest();
        } else {
            startApiClient();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (apiClient != null && apiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, this);
        }
    }

    private void requestLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            Context context = getActivity();

            Activity activity = getActivity();

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                        || ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    showExplanationDialog();
                } else {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                            LOCATION_PERMS_REQUEST_CODE);
                }
            } else {
                if (onPermissionsSucceededListener != null) {
                    onPermissionsSucceededListener.onPermissionsSucceeded();
                }
                initServices();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (onPermissionsSucceededListener != null) {
                    onPermissionsSucceededListener.onPermissionsSucceeded();
                }
                initServices();
            } else {
                getActivity().finish();
            }
        }


    }

    public void showExplanationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.location_perms_title)
                .setMessage(R.string.location_perms_message);

        builder.setPositiveButton(R.string.request_perms, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        LocationSettingsFragment.LOCATION_PERMS_REQUEST_CODE);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void initServices() {
        createLocationRequest();
        startApiClient();
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startApiClient() {
        if (apiClient == null) {
            apiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        apiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        createLocationSettingsRequest();
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (apiClient != null) {
            apiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "" + connectionResult.getErrorMessage());
        try {
            connectionResult.startResolutionForResult(getActivity(), API_CLIENT_RESOLUTION_REQUEST);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    private void createLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        final PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(apiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:

                        requestLocationUpdates();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    getActivity(),
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        getActivity().finish();
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        requestLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        getActivity().finish();
                        break;
                    default:
                        break;
                }
                break;
            case API_CLIENT_RESOLUTION_REQUEST:
                if (resultCode == Activity.RESULT_OK && apiClient != null) {
                    apiClient.connect();
                }
                break;
        }
    }

    private void requestLocationUpdates() {

        if (locationRequest == null) {
            createLocationRequest();
        }
        //noinspection ResourceType
        LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, locationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        listener.onLocationChanged(location);
    }
}
