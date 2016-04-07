package com.kirussell.tastytrucks.location;

import android.location.Location;

import com.google.android.gms.location.LocationServices;


/**
 * Created by russellkim on 06/04/16.
 * Provider of last location
 */
public class GoogleLocationProvider implements LocationProvider {

    private final GoogleApiClientHost googleApiClientHost;
    private Location lastLocation;

    GoogleLocationProvider(GoogleApiClientHost googleApiClientHost) {
        this.googleApiClientHost = googleApiClientHost;
    }

    @Override
    public void onStart() {
        googleApiClientHost.onStart();
    }

    @Override
    public void onStop() {
        googleApiClientHost.onStop();
    }

    @Override
    public Location getLastLocation() {
        //noinspection ResourceType
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClientHost.getGoogleApiClient());
        if (location != null) {
            lastLocation = location;
        }
        return lastLocation;
    }
}
