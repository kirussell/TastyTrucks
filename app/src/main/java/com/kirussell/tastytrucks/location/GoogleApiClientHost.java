package com.kirussell.tastytrucks.location;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

/**
 * Created by russellkim on 07/04/16.
 * Handling GoogleApiClient obj to provide it to other services
 */
class GoogleApiClientHost implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final Context context;
    private GoogleApiClient googleApiClient;
    private boolean isConnectCalled = false;

    GoogleApiClientHost(Context ctx) {
        this.context = ctx;
    }

    GoogleApiClient getGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .build();
        }
        return googleApiClient;
    }

    public void onStart() {
        GoogleApiClient googleApiClient = getGoogleApiClient();
        if (!isConnectCalled && googleApiClient != null && !googleApiClient.isConnected()) {
            googleApiClient.connect();
            isConnectCalled = true;
        }
    }

    public void onStop() {
        GoogleApiClient googleApiClient = getGoogleApiClient();
        if (isConnectCalled && googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
            isConnectCalled = false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("GoogleApiClient", "connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("GoogleApiClient", "suspended" + i);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("GoogleApiClient", "failed with msg:" + connectionResult.getErrorMessage());
    }
}
