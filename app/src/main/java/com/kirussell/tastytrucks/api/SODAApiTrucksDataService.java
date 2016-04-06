package com.kirussell.tastytrucks.api;

import com.kirussell.tastytrucks.api.data.TruckData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by russellkim on 05/04/16.
 * Uses Socrata Open Data API
 * Delivers data about Foods Trucks (https://dev.socrata.com/foundry/data.sfgov.org/6a9r-agq8)
 */
interface SODAApiTrucksDataService {

    String API_ENDPOINT = "https://data.sfgov.org";

    @GET("/resource/6a9r-agq8.json")
    Call<TruckData[]> getTrucks(@Query("$where")  String query);
}
