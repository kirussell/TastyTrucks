package com.kirussell.tastytrucks.api;

import com.kirussell.tastytrucks.api.data.TruckData;

import retrofit2.Call;

/**
 * Created by russellkim on 05/04/16.
 */
public interface TrucksDataService {

    /**
     * Trucks in circle area with center in [latitude, longitude] and radius in meters
     * @param latitude center of circle area
     * @param longitude center of circle area
     * @param distanceInMeters radius of circle area
     * @return trucks data within area
     */
    Call<TruckData[]> getTrucks(double latitude, double longitude, long distanceInMeters);
}
