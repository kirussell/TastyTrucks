package com.kirussell.tastytrucks;

import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.kirussell.tastytrucks.api.TestApiModule;
import com.kirussell.tastytrucks.api.data.TruckData;
import com.kirussell.tastytrucks.location.LocationProvider;
import com.kirussell.tastytrucks.location.PlacesProvider;
import com.kirussell.tastytrucks.location.TestLocationModule;
import com.kirussell.tastytrucks.map.MapScreenPresenter;
import com.kirussell.tastytrucks.map.MapScreenView;
import com.kirussell.tastytrucks.map.MapViewHandlers;
import com.kirussell.tastytrucks.map.TestMapViewModule;
import com.kirussell.tastytrucks.utils.DummySpanUtil;
import com.kirussell.tastytrucks.utils.SpanUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import javax.inject.Inject;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class MapScreenPresenterTests {

    private static final HashMap<String, String> SERVER_RESPONSES = new HashMap<>();
    private static TestApiModule testApiModule = new TestApiModule("", SERVER_RESPONSES);

    static {
        SERVER_RESPONSES.put(testApiModule.compileWhereParameter(0, 0, 1000), "[]");
    }

    private static TruckData createTruckData(String address, String locationDescription,
                                             String applicant, String foodItems,
                                             String dayHours) {
        String template = "{" +
                "\"address\":\"%s\"," +
                "\"locationdescription\":\"%s\"," +
                "\"applicant\":\"%s\"," +
                "\"fooditems\":\"%s\"," +
                "\"dayshours\":\"%s\"" +
                "}";
        return new Gson().fromJson(
                String.format(template, address, locationDescription, applicant, foodItems, dayHours),
                TruckData.class
        );
    }

    private static TruckData createTruckData() {
        String address = "GOUGH ST";
        String locationDescription = "GOUGH ST to OCTABIA ST (300-399)";
        String applicant = "Casey's Pizza LLC";
        String foodItems = "Bacon Kale: Drinks";
        String dayHours = "Su:12PM-8PM; Fr:3PM-8PM";
        return createTruckData(
                address, locationDescription, applicant, foodItems, dayHours
        );
    }

    @Inject MapScreenPresenter presenter;
    private MapScreenView mapScreenView;
    private LocationProvider locationApi;
    private PlacesProvider placesApi;
    private MapViewHandlers mapViewHandlers;
    private SpanUtil spanUtil;
    private IntroController introlController;

    @Before
    public void setup() {
        locationApi = mock(LocationProvider.class);
        placesApi = mock(PlacesProvider.class);
        mapViewHandlers = new MapViewHandlers();
        spanUtil = new DummySpanUtil();
        introlController = new IntroController(mock(SharedPreferences.class));
        presenter = DaggerTastyTrucksApp_AppComponent.builder()
                .apiModule(testApiModule)
                .googleLocationModule(new TestLocationModule(locationApi, placesApi))
                .mapViewModule(new TestMapViewModule(mapViewHandlers, spanUtil, introlController))
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

    @Test
    public void markerClicked() {
        String address = "GOUGH ST";
        String locationDescription = "GOUGH ST to OCTABIA ST (300-399)";
        String applicant = "Casey's Pizza LLC";
        String foodItems = "Bacon Kale: Drinks";
        String dayHours = "Su:12PM-8PM; Fr:3PM-8PM";
        TruckData truckData = createTruckData(
                address, locationDescription, applicant, foodItems, dayHours
        );
        presenter.onMarkerClicked(truckData);
        assertTrue(mapViewHandlers.truckInfoShown.get());
        String text = mapViewHandlers.truckInfo.get().toString();
        assertTrue(text.contains(address));
        assertTrue(text.contains(locationDescription));
        assertTrue(text.contains(applicant));
        for(String item : foodItems.split(":")) {
            assertTrue(text.contains(item));
        }
        assertTrue(text.contains(dayHours));

    }

    @Test
    public void placeMarkerClicked() {
        presenter.onMarkerClicked(createTruckData());
        assertTrue(mapViewHandlers.truckInfoShown.get());
        presenter.onPlaceMarkerClicked();
        assertFalse(mapViewHandlers.truckInfoShown.get());
    }

    @Test
    public void hideMarkerInfoWindow() {
        presenter.onMarkerClicked(createTruckData());
        assertTrue(mapViewHandlers.truckInfoShown.get());
        presenter.onHideMarkerInfoWindow();
        assertFalse(mapViewHandlers.truckInfoShown.get());
    }

    @Test
    public void checkNpes() {
        presenter.onMapClicked(null);
        presenter.onMarkerClicked(null);
        presenter.onPlacePredictionSelected(null);
    }
}