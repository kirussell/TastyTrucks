package com.kirussell.tastytrucks.location;

import dagger.Module;

/**
 * Created by russellkim on 08/04/16.
 *
 */
@Module
public class TestLocationModule extends GoogleLocationModule {

    private final LocationProvider locationProvider;
    private final PlacesProvider placesProvider;

    public TestLocationModule(LocationProvider locationProvider, PlacesProvider placesProvider) {
        super(null);
        this.locationProvider = locationProvider;
        this.placesProvider = placesProvider;
    }

    @Override
    public LocationProvider providesLocationProvider(GoogleApiClientHost googleApiClientHost) {
        return locationProvider;
    }

    @Override
    public PlacesProvider providePlacesProvider(GoogleApiClientHost googleApiClientHost) {
        return placesProvider;
    }
}
