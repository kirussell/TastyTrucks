package com.kirussell.tastytrucks.api;

import com.kirussell.tastytrucks.api.data.TruckData;

import java.io.IOException;
import java.util.Locale;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by russellkim on 05/04/16.
 * Provides api access
 */
@Module
public class ApiModule {

    private final String token;

    public ApiModule(String token) {
        this.token = token;
    }

    @Provides
    public TrucksDataService provideTrucksDataService(OkHttpClient okHttpClient) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(SODAApiTrucksDataService.API_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create());
        final SODAApiTrucksDataService sodaApiTrucksDataService = builder.build().create(SODAApiTrucksDataService.class);
        return new TrucksDataService() {
            @Override
            public Call<TruckData[]> getTrucks(double latitude, double longitude, long distanceInMeters) {
                String query = String.format(
                        Locale.ENGLISH,
                        "within_circle(location, %f, %f, %d)",
                        latitude, longitude, distanceInMeters
                );
                return sodaApiTrucksDataService.getTrucks(query);
            }
        };
    }

    @Provides
    public OkHttpClient provideOkHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                return chain.proceed(
                        chain.request()
                                .newBuilder()
                                .addHeader("X-App-Token", token)
                                .build()
                );
            }
        }).build();
        return okHttpClient;
    }
}
