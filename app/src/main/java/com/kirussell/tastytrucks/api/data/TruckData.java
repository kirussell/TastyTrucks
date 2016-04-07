package com.kirussell.tastytrucks.api.data;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by russellkim on 05/04/16.
 * Food Truck data
 */
public class TruckData {
    String address;
    String applicant;
    String fooditems;
    double latitude;
    double longitude;

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    public String getTitle() {
        return applicant;
    }

    public String getInfo() {
        return address + " " + fooditems;
    }

}
