package com.kirussell.tastytrucks.location;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;


/**
 * Created by russellkim on 06/04/16.
 * Provider of last location
 */
public class GoogleLocationProvider implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final Context context;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;

    GoogleLocationProvider(Context ctx) {
        this.context = ctx;
    }

    public void onStart() {
        getGoogleApiClient().connect();
    }

    public void onStop() {
        getGoogleApiClient().disconnect();
    }

    private GoogleApiClient getGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        return googleApiClient;
    }

    public Location getLastLocation() {
        //noinspection ResourceType
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(getGoogleApiClient());
        return lastLocation;
    }

    @Override
    public void onConnected(Bundle bundle) {
        //empty
    }

    @Override
    public void onConnectionSuspended(int i) {
        //empty
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //empty
    }
}
