package com.kirussell.tastytrucks;

import android.app.Application;
import android.content.Context;

import com.kirussell.tastytrucks.api.ApiModule;
import com.kirussell.tastytrucks.location.GoogleLocationModule;
import com.kirussell.tastytrucks.map.MapScreenPresenter;
import com.kirussell.tastytrucks.map.MapViewHandlers;
import com.kirussell.tastytrucks.map.MapViewModule;

import javax.inject.Singleton;

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
                .mapViewModule(new MapViewModule())
                .build();
    }

    public static TastyTrucksApp from(Context context) {
        return (TastyTrucksApp) context.getApplicationContext();
    }

    public AppComponent getApiComponent() {
        return apiComponent;
    }

    @Singleton
    @Component(modules={ApiModule.class, GoogleLocationModule.class, MapViewModule.class})
    public interface AppComponent {
        MapScreenPresenter mapScreenPresenter();
        void inject(MapActivity ac);
    }
}
