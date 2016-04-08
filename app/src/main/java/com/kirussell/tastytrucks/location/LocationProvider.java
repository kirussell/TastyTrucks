package com.kirussell.tastytrucks.location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by russellkim on 08/04/16.
 * Provides user last location
 */
public interface LocationProvider {

    void onStart();
    void onStop();
    LatLng getLastLocation();
}
