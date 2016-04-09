package com.kirussell.tastytrucks.api.data;

import com.google.android.gms.maps.model.LatLng;
import com.kirussell.tastytrucks.utils.MyTextUtils;

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
        if (MyTextUtils.isEmpty(fooditems)) {
            return "";
        } else {
            return MyTextUtils.join(",", fooditems.split(":"));
        }
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
