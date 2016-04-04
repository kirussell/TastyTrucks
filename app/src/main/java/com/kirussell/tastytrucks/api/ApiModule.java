package com.kirussell.tastytrucks.api;

import dagger.Module;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by russellkim on 05/04/16.
 *
 */
@Module
public class ApiModule {
    
    public TrucksDataService provideTrucksDataService() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(SODAApiTrucksDataService.API_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create());
        return builder.build().create(SODAApiTrucksDataService.class);
    }
}
