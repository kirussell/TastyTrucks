package com.kirussell.tastytrucks;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kirussell.tastytrucks.api.data.TruckData;
import com.kirussell.tastytrucks.databinding.ActivityMapBinding;
import com.kirussell.tastytrucks.location.data.PlacePrediction;
import com.kirussell.tastytrucks.map.MapScreenPresenter;
import com.kirussell.tastytrucks.map.MapScreenView;

import java.util.HashMap;

import javax.inject.Inject;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, MapScreenView,
        GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

    private static final int CIRCLE_AREA_COLOR = Color.argb(30, 0, 153, 204);
    private static final int CIRCLE_STROKE_COLOR = Color.argb(90, 0, 153, 204);
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 7;
    private static final int REQUEST_INTERNET_PERMISSIONS = 8;
    private static final int REQUEST_MY_LOCATION_PERMISSIONS = 9;
    private static final int SEARCH_RADIUS_METERS = 1000;

    @Inject MapScreenPresenter presenter;
    private GoogleMap map;
    private Circle circle;
    private Marker placeMarker;
    private HashMap<Marker, TruckData> trucksMarkers = new HashMap<>();
    private Marker lastClickedMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TastyTrucksApp.from(this).getApiComponent().inject(this);
        if (checkGooglePlayServicesAvailable()) {
            onInit();
        }
    }

    private boolean checkGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int googlePlayServicesAvailable = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (googlePlayServicesAvailable != ConnectionResult.SUCCESS) {
            Dialog errorDialog = googleApiAvailability.getErrorDialog(this, googlePlayServicesAvailable, REQUEST_GOOGLE_PLAY_SERVICES);
            errorDialog.show();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_GOOGLE_PLAY_SERVICES) {
            if (checkGooglePlayServicesAvailable()) {
                onInit();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void onInit() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.INTERNET,
                    }, REQUEST_INTERNET_PERMISSIONS
            );
        }
        ActivityMapBinding activityMapBinding = DataBindingUtil.setContentView(this, R.layout.activity_map);
        activityMapBinding.setHandlers(presenter.getMapViewHandlers());
        initSearchBar(activityMapBinding);
        initMap();
    }

    private void initSearchBar(ActivityMapBinding activityMapBinding) {
        activityMapBinding.searchbar.setAdapter(presenter.getPlacesAutocompleteAdapter());
        activityMapBinding.searchbar.setThreshold(2);
        activityMapBinding.searchbar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getAdapter().getItem(position);
                if (item instanceof PlacePrediction) {
                    presenter.onPlacePredictionSelected((PlacePrediction) item);
                }
            }
        });
    }

    private void initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onAttach(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.onDetach(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }, REQUEST_MY_LOCATION_PERMISSIONS
            );
        } else {
            map.setMyLocationEnabled(true);
        }
        presenter.setInitialLocation();
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(this);
    }

    @Override
    public void moveTo(LatLng latLng) {
        if (map.getCameraPosition().zoom < 12f) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.5f));
        } else {
            map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
        updatePlaceMarkerAndCircle(latLng);
        clearTrucksMarkers();
    }

    @Override
    public void displayTrucks(TruckData[] trucks) {
        if (trucks != null) {
            for (TruckData truck : trucks) {
                trucksMarkers.put(map.addMarker(
                        new MarkerOptions()
                                .position(truck.getLatLng()).icon(presenter.getTruckMarkerIcon())
                                .title(truck.getTitle())
                                .snippet(truck.getAddress())
                ), truck);
            }
        }
    }

    private void clearTrucksMarkers() {
        for (Marker marker : trucksMarkers.keySet()) {
            marker.remove();
        }
        trucksMarkers.clear();
    }

    private void updatePlaceMarkerAndCircle(LatLng latLng) {
        if (circle != null) {
            circle.remove();
        }
        circle = map.addCircle(
                new CircleOptions()
                        .center(latLng)
                        .strokeWidth(3)
                        .strokeColor(CIRCLE_STROKE_COLOR)
                        .fillColor(CIRCLE_AREA_COLOR)
                        .radius(SEARCH_RADIUS_METERS)
        );
        if (placeMarker != null) {
            placeMarker.remove();
        }
        placeMarker = map.addMarker(new MarkerOptions().position(latLng));
    }

    @Override
    public boolean onMyLocationButtonClick() {
        presenter.onMyLocationClicked();
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_MY_LOCATION_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                map.setMyLocationEnabled(true);
            }
        } else if (requestCode == REQUEST_INTERNET_PERMISSIONS) {
            if (permissions.length == 1 &&
                    permissions[0].equals(Manifest.permission.INTERNET) &&
                    grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                finish();
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        presenter.onMapClicked(latLng);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.equals(placeMarker)) {
            presenter.onPlaceMarkerClicked();
        } else {
            TruckData truckData = trucksMarkers.get(marker);
            if (truckData != null) {
                presenter.onMarkerClicked(truckData);
            }
            lastClickedMarker = marker;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (lastClickedMarker.isInfoWindowShown()) {
            presenter.onHideMarkerInfoWindow();
            lastClickedMarker.hideInfoWindow();
        } else {
            super.onBackPressed();
        }
    }
}
