package com.kirussell.tastytrucks.api.data;

import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by russellkim on 05/04/16.
 * Food Truck data
 */
public class TruckData {
    String address;
    String locationdescription;
    String applicant;
    String fooditems;
    String dayshours;
    double latitude;
    double longitude;

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    public String getTitle() {
        return applicant;
    }

    public String getFoodItems() {
        return TextUtils.join(",", fooditems.split(":"));
    }

    public String getAddress() {
        return address;
    }

    public String getLocationDescription() {
        return locationdescription;
    }

    public String getDaysHours() {
        return dayshours;
    }
}
