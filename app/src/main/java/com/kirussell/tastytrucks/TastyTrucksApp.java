package com.kirussell.tastytrucks;

import android.app.Application;
import android.content.Context;

import com.kirussell.tastytrucks.api.ApiModule;
import com.kirussell.tastytrucks.location.GoogleLocationModule;

import dagger.Component;

/**
 * Created by russellkim on 05/04/16.
 * Main Application class
 */
public class TastyTrucksApp extends Application {

    private AppComponent apiComponent;

    private static final String SODAToken = "6kY24tagBFq0doFPwvdUtfV9O";

    @Override
    public void onCreate() {
        super.onCreate();
        apiComponent = DaggerTastyTrucksApp_AppComponent.builder()
                .apiModule(new ApiModule(SODAToken))
                .googleLocationModule(new GoogleLocationModule(getApplicationContext()))
                .build();
    }

    public static TastyTrucksApp from(Context context) {
        return (TastyTrucksApp) context.getApplicationContext();
    }

    public AppComponent getApiComponent() {
        return apiComponent;
    }

    @Component(modules={ApiModule.class, GoogleLocationModule.class})
    public interface AppComponent {
        void inject(MapActivity ac);
    }
}
