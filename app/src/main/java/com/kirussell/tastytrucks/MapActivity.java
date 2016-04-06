package com.kirussell.tastytrucks;

import android.Manifest;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kirussell.tastytrucks.api.TrucksDataService;
import com.kirussell.tastytrucks.api.data.TruckData;
import com.kirussell.tastytrucks.databinding.ActivityMapBinding;
import com.kirussell.tastytrucks.location.GoogleLocationProvider;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int CIRCLE_AREA_COLOR = Color.argb(30, 0, 153, 204);
    private static final int REQUEST_MY_LOCATION_PERMISSIONS = 9;
    private static final int SEARCH_RADIUS_METERS = 1000;

    private GoogleMap mMap;
    @Inject
    public TrucksDataService dataService;
    @Inject
    public GoogleLocationProvider locationProvider;
    private Circle circle;
    private Marker placeMarker;
    private List<Marker> trucksMarkers = new ArrayList<>();
    private BitmapDescriptor truckMarkerIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TastyTrucksApp.from(this).getApiComponent().inject(this);
        ActivityMapBinding activityMapBinding = DataBindingUtil.setContentView(this, R.layout.activity_map);
        initToolbar(activityMapBinding);
        initMap();
        truckMarkerIcon = BitmapDescriptorFactory.fromResource(R.drawable.truck_marker);
    }

    private void initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initToolbar(ActivityMapBinding activityMapBinding) {
        setSupportActionBar(activityMapBinding.toolbar);
        activityMapBinding.toolbar.setTitle("");
    }

    @Override
    protected void onStart() {
        locationProvider.onStart();
        super.onStart();
    }

    @Override
    protected void onStop() {
        locationProvider.onStop();
        super.onStop();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }, REQUEST_MY_LOCATION_PERMISSIONS
            );
        } else {
            mMap.setMyLocationEnabled(true);
        }
        LatLng sf = new LatLng(37.7754427, -122.4203914);
        moveTo(sf);
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Location myLocation = locationProvider.getLastLocation();
                moveTo(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
                return false;
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                moveTo(latLng);
            }
        });
    }

    private void moveTo(LatLng sf) {
        if (mMap.getCameraPosition().zoom < 12f) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sf, 14f));
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLng(sf));
        }
        updatePlaceMarkerAndCircle(sf);
        requestTrucksNear(sf.latitude, sf.longitude);
    }

    private void requestTrucksNear(double latitude, double longitude) {
        dataService.getTrucks(latitude, longitude, SEARCH_RADIUS_METERS).enqueue(new Callback<TruckData[]>() {
            @Override
            public void onResponse(Call<TruckData[]> call, Response<TruckData[]> response) {
                displayTrucks(response.body());
            }

            @Override
            public void onFailure(Call<TruckData[]> call, Throwable t) {
            }
        });
    }

    private void displayTrucks(TruckData[] trucks) {
        for (Marker marker : trucksMarkers) {
            marker.remove();
        }
        trucksMarkers.clear();
        if (trucks != null) {
            for (TruckData truck : trucks) {
                trucksMarkers.add(mMap.addMarker(new MarkerOptions().position(truck.getLatLng()).icon(truckMarkerIcon)));
            }
        }
    }

    private void updatePlaceMarkerAndCircle(LatLng latLng) {
        if (circle != null) {
            circle.remove();
        }
        circle = mMap.addCircle(
                new CircleOptions()
                        .center(latLng)
                        .strokeWidth(0)
                        .fillColor(CIRCLE_AREA_COLOR)
                        .radius(SEARCH_RADIUS_METERS)
        );
        if (placeMarker != null) {
            placeMarker.remove();
        }
        placeMarker = mMap.addMarker(new MarkerOptions().position(latLng));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_MY_LOCATION_PERMISSIONS) {
            if (permissions.length == 2 &&
                    permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    permissions[1].equals(Manifest.permission.ACCESS_COARSE_LOCATION) &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //noinspection ResourceType
                mMap.setMyLocationEnabled(true);
            }
        }
    }
}
