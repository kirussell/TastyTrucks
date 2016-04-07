package com.kirussell.tastytrucks.location;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by russellkim on 06/04/16.
 *
 */
@Module
public class GoogleLocationModule {

    private final Context context;

    public GoogleLocationModule(Context ctx) {
        this.context = ctx;
    }

    @Provides
    @Singleton
    public GoogleApiClientHost provideGoogleApiClient() {
        return new GoogleApiClientHost(context);
    }

    @Provides
    public LocationProvider providesLocationProvider(GoogleApiClientHost googleApiClientHost) {
        return new GoogleLocationProvider(googleApiClientHost);
    }

    @Provides
    public PlacesProvider providePlacesProvider(GoogleApiClientHost googleApiClientHost) {
        return new GooglePlacesProvider(googleApiClientHost);
    }

}
