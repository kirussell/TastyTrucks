package com.kirussell.tastytrucks.api;

import com.kirussell.tastytrucks.api.data.TruckData;

import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by russellkim on 05/04/16.
 * Uses Socrata Open Data API
 * Delivers data about Foods Trucks (https://dev.socrata.com/foundry/data.sfgov.org/6a9r-agq8)
 */
interface SODAApiTrucksDataService extends TrucksDataService {

    String API_ENDPOINT = "https://data.sfgov.org";

    @Override
    @Headers("X-App-Token: 6kY24tagBFq0doFPwvdUtfV9O")
    @GET("/resource/6a9r-agq8.json?$where=within_circle(incident_location, {latitude}, {longitude}, {distance})")
    TruckData[] getTrucks(@Query("latitude")  double latitude,
                          @Query("longitude") double longitude,
                          @Query("distance")  long distanceInMeters);
}
