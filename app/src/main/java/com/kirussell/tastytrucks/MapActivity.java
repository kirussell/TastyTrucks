package com.kirussell.tastytrucks;

import android.Manifest;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;

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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, MapScreenView,
        GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMapClickListener {

    private static final int CIRCLE_AREA_COLOR = Color.argb(30, 0, 153, 204);
    private static final int REQUEST_INTERNET_PERMISSIONS = 8;
    private static final int REQUEST_MY_LOCATION_PERMISSIONS = 9;
    private static final int SEARCH_RADIUS_METERS = 1000;

    @Inject MapScreenPresenter presenter;
    private GoogleMap mMap;
    private Circle circle;
    private Marker placeMarker;
    private List<Marker> trucksMarkers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.INTERNET,
                    }, REQUEST_INTERNET_PERMISSIONS
            );
        }
        TastyTrucksApp.from(this).getApiComponent().inject(this);
        ActivityMapBinding activityMapBinding = DataBindingUtil.setContentView(this, R.layout.activity_map);
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
        presenter.setInitialLocation();
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMapClickListener(this);
    }

    @Override
    public void moveTo(LatLng latLng) {
        if (mMap.getCameraPosition().zoom < 12f) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.5f));
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
        updatePlaceMarkerAndCircle(latLng);
    }

    @Override
    public void displayTrucks(TruckData[] trucks) {
        for (Marker marker : trucksMarkers) {
            marker.remove();
        }
        trucksMarkers.clear();
        if (trucks != null) {
            for (TruckData truck : trucks) {
                trucksMarkers.add(mMap.addMarker(
                        new MarkerOptions()
                                .position(truck.getLatLng()).icon(presenter.getTruckMarkerIcon())
                                .title(truck.getTitle())
                                .snippet(truck.getInfo())
                ));
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
    public boolean onMyLocationButtonClick() {
        presenter.onMyLocationClicked();
        return false;
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

    public interface Handlers {
    }
}
