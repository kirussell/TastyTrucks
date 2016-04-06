package com.kirussell.tastytrucks.api.data;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by russellkim on 05/04/16.
 */
public class TruckData {
    String address;
    double latitude;
    double longitude;

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }
}
