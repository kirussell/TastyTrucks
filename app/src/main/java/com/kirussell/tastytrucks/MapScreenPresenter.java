package com.kirussell.tastytrucks;

import android.location.Location;
import android.text.TextUtils;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.kirussell.tastytrucks.api.TrucksDataService;
import com.kirussell.tastytrucks.api.data.TruckData;
import com.kirussell.tastytrucks.location.LocationProvider;
import com.kirussell.tastytrucks.location.PlacesProvider;
import com.kirussell.tastytrucks.location.data.PlacePrediction;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by russellkim on 08/04/16.
 * Presenter for Map screen
 */
public class MapScreenPresenter {

    public static final LatLng UBER_HQ = new LatLng(37.7754427, -122.4203914);
    private static final int SEARCH_RADIUS_METERS = 1000;

    private TrucksDataService dataService;
    private LocationProvider locationProvider;
    private PlacesProvider placesProvider;
    private MapScreenView view = EMPTY_VIEW;
    private Executor executor = Executors.newSingleThreadExecutor();
    private PlacesAutocompleteAdapter placesAutocompleteAdapter;
    private BitmapDescriptor truckMarkerIcon;

    @Inject
    public MapScreenPresenter(TrucksDataService dataService, LocationProvider locationProvider, PlacesProvider placesProvider) {
        this.dataService = dataService;
        this.locationProvider = locationProvider;
        this.placesProvider = placesProvider;
        this.placesAutocompleteAdapter = new PlacesAutocompleteAdapter(this.placesProvider);
    }

    public void onAttach(MapScreenView view) {
        this.view = view;
        locationProvider.onStart();
        placesProvider.onStart();
        if (truckMarkerIcon == null) {
            truckMarkerIcon = BitmapDescriptorFactory.fromResource(R.drawable.truck_marker);
        }
    }

    public void onDetach(MapScreenView view) {
        this.view = EMPTY_VIEW;
        locationProvider.onStop();
        placesProvider.onStop();
    }

    public void setInitialLocation() {
        moveTo(UBER_HQ);
        requestTrucksNear(UBER_HQ);
    }

    public void onMyLocationClicked() {
        Location myLocation = locationProvider.getLastLocation();
        moveTo(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
    }

    public void onPlacePredictionSelected(PlacePrediction place) {
        final String placeId = place.getId();
        if (!TextUtils.isEmpty(placeId)) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    final Place place = placesProvider.getPlaceById(placeId);
                    if (place != null) {
                        view.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                moveTo(place.getLatLng());
                            }
                        });
                    }
                }
            });
        }
    }

    public void onMapClicked(LatLng latLng) {
        moveTo(latLng);
    }

    private void moveTo(LatLng latLng) {
        view.moveTo(latLng);
        requestTrucksNear(latLng);
    }

    public void requestTrucksNear(LatLng latLng) {
        dataService.getTrucks(latLng.latitude, latLng.longitude, SEARCH_RADIUS_METERS).enqueue(new Callback<TruckData[]>() {
            @Override
            public void onResponse(Call<TruckData[]> call, Response<TruckData[]> response) {
                view.displayTrucks(response.body());
            }

            @Override
            public void onFailure(Call<TruckData[]> call, Throwable t) {
            }
        });
    }

    public PlacesAutocompleteAdapter getPlacesAutocompleteAdapter() {
        return placesAutocompleteAdapter;
    }

    public BitmapDescriptor getTruckMarkerIcon() {
        return truckMarkerIcon;
    }

    private static final MapScreenView EMPTY_VIEW = new MapScreenView() {
        @Override
        public void moveTo(LatLng latLng) {}

        @Override
        public void runOnUiThread(Runnable action) {}

        @Override
        public void displayTrucks(TruckData[] trucks) {}
    };
}
