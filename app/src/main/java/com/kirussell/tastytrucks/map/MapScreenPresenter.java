package com.kirussell.tastytrucks.map;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.kirussell.tastytrucks.R;
import com.kirussell.tastytrucks.api.TrucksDataService;
import com.kirussell.tastytrucks.api.data.TruckData;
import com.kirussell.tastytrucks.location.LocationProvider;
import com.kirussell.tastytrucks.location.PlacesProvider;
import com.kirussell.tastytrucks.location.data.PlacePrediction;
import com.kirussell.tastytrucks.utils.SpanUtil;

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

    private final TrucksDataService dataService;
    private final LocationProvider locationProvider;
    private final PlacesProvider placesProvider;
    private final PlacesAutocompleteAdapter placesAutocompleteAdapter;
    private final MapViewHandlers mapViewHandlers;
    private final SpanUtil spanUtil;
    private MapScreenView view = EMPTY_VIEW;
    private BitmapDescriptor truckMarkerIcon;
    private Executor executor = Executors.newSingleThreadExecutor();

    @Inject
    public MapScreenPresenter(TrucksDataService dataService, LocationProvider locationProvider,
                              PlacesProvider placesProvider, MapViewHandlers mapViewHandlers,
                              SpanUtil spanUtil) {
        this.dataService = dataService;
        this.locationProvider = locationProvider;
        this.placesProvider = placesProvider;
        this.placesAutocompleteAdapter = new PlacesAutocompleteAdapter(this.placesProvider);
        this.mapViewHandlers = mapViewHandlers;
        this.spanUtil = spanUtil;
    }

    public void onAttach(MapScreenView view) {
        this.view = view == null ? EMPTY_VIEW : view;
        locationProvider.onStart();
        placesProvider.onStart();
        if (truckMarkerIcon == null) {
            try {
                truckMarkerIcon = BitmapDescriptorFactory.fromResource(R.drawable.truck_marker);
            } catch(NullPointerException e) {
                Log.e("MapScreenPresenter", "Cannot create truck marker:" + e.toString());
            }
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
        moveTo(locationProvider.getLastLocation());
    }

    public void onPlacePredictionSelected(@NonNull PlacePrediction place) {
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
        if (latLng != null) {
            mapViewHandlers.truckInfoShown.set(false);
            view.moveTo(latLng);
            requestTrucksNear(latLng);
        }
    }

    public void requestTrucksNear(@NonNull LatLng latLng) {
        dataService.getTrucks(latLng.latitude, latLng.longitude, SEARCH_RADIUS_METERS).enqueue(new Callback<TruckData[]>() {
            @Override
            public void onResponse(Call<TruckData[]> call, Response<TruckData[]> response) {
                view.displayTrucks(response.body());
            }

            @Override
            public void onFailure(Call<TruckData[]> call, Throwable t) {
                Log.d("MapScreenPresenter", "Failed to get trucks data");
            }
        });
    }

    public void onMarkerClicked(@NonNull TruckData truck) {
        mapViewHandlers.truckInfoShown.set(true);
        mapViewHandlers.truckInfo.set(
                spanUtil.normal(
                        spanUtil.bold(truck.getTitle()), "\n",
                        truck.getLocationDescription(), "\n",
                        truck.getDaysHours(), "\n",
                        truck.getFoodItems()
                )
        );
    }

    public void onPlaceMarkerClicked() {
        mapViewHandlers.truckInfoShown.set(false);
    }

    public void onHideMarkerInfoWindow() {
        mapViewHandlers.truckInfoShown.set(false);
    }

    public PlacesAutocompleteAdapter getPlacesAutocompleteAdapter() {
        return placesAutocompleteAdapter;
    }

    public MapViewHandlers getMapViewHandlers() {
        return mapViewHandlers;
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
