package com.kirussell.tastytrucks;

import android.app.Application;
import android.content.Context;

import com.kirussell.tastytrucks.api.ApiModule;
import com.kirussell.tastytrucks.components.ApiComponent;
import com.kirussell.tastytrucks.components.DaggerApiComponent;

/**
 * Created by russellkim on 05/04/16.
 * Main Application class
 */
public class TastyTrucksApp extends Application {

    private ApiComponent apiComponent;

    private static final String SODAToken = "6kY24tagBFq0doFPwvdUtfV9O";

    @Override
    public void onCreate() {
        super.onCreate();
        apiComponent = DaggerApiComponent.builder()
                .apiModule(new ApiModule(SODAToken))
                .build();
    }

    public static TastyTrucksApp from(Context context) {
        return (TastyTrucksApp) context.getApplicationContext();
    }

    public ApiComponent getApiComponent() {
        return apiComponent;
    }
}
