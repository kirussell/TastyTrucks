package com.kirussell.tastytrucks;

import com.google.android.gms.maps.model.LatLng;
import com.kirussell.tastytrucks.api.TestApiModule;
import com.kirussell.tastytrucks.api.data.TruckData;
import com.kirussell.tastytrucks.location.LocationProvider;
import com.kirussell.tastytrucks.location.PlacesProvider;
import com.kirussell.tastytrucks.location.TestLocationModule;
import com.kirussell.tastytrucks.map.MapScreenPresenter;
import com.kirussell.tastytrucks.map.MapScreenView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import javax.inject.Inject;

import static org.mockito.Mockito.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class MapScreenPresenterTests {

    private static final HashMap<String, String> SERVER_RESPONSES = new HashMap<>();
    private static TestApiModule testApiModule = new TestApiModule("", SERVER_RESPONSES);

    static {
        SERVER_RESPONSES.put(testApiModule.compileWhereParameter(0, 0, 1000), "[]");
    }

    @Inject MapScreenPresenter presenter;
    private MapScreenView mapScreenView;
    private LocationProvider locationApi;
    private PlacesProvider placesApi;

    @Before
    public void setup() {
        locationApi = mock(LocationProvider.class);
        placesApi = mock(PlacesProvider.class);
        presenter = DaggerTastyTrucksApp_AppComponent.builder()
                .apiModule(testApiModule)
                .googleLocationModule(new TestLocationModule(locationApi, placesApi))
                .build()
                .mapScreenPresenter();
        mapScreenView = mock(MapScreenView.class);
        presenter.onAttach(mapScreenView);
    }

    @After
    public void release() {
        presenter.onDetach(mapScreenView);
    }

    @Test
    public void requestTrucksNear() throws Exception {
        LatLng loc = new LatLng(0, 0);
        presenter.requestTrucksNear(loc);
        verify(mapScreenView, timeout(20).times(1)).displayTrucks(new TruckData[0]);
    }

    @Test
    public void myLocationClicked() throws Exception {
        verify(locationApi).onStart();
        LatLng loc = new LatLng(0, 0);
        when(locationApi.getLastLocation()).thenReturn(loc);
        presenter.onMyLocationClicked();
        verify(mapScreenView, times(1)).moveTo(loc);
        verify(mapScreenView, timeout(20).times(1)).displayTrucks(new TruckData[0]);
    }

    @Test
    public void mapClicked() {
        LatLng loc = new LatLng(0, 0);
        presenter.onMapClicked(loc);
        verify(mapScreenView, times(1)).moveTo(loc);
        verify(mapScreenView, timeout(20).times(1)).displayTrucks(new TruckData[0]);
    }
}