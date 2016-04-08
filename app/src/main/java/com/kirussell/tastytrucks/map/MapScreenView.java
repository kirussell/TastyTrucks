package com.kirussell.tastytrucks.map;

import com.google.android.gms.maps.model.LatLng;
import com.kirussell.tastytrucks.api.data.TruckData;

/**
 * Created by russellkim on 08/04/16.
 * Map screen
 */
public interface MapScreenView {

    /**
     * 1) Moves map camera to given position
     * 2) Places marker on map
     * @param latLng position to move
     */
    void moveTo(LatLng latLng);

    void runOnUiThread(Runnable action);

    /**
     * Places markers for locations from trucks data
     * @param trucks list of trucks data
     */
    void displayTrucks(TruckData[] trucks);
}
