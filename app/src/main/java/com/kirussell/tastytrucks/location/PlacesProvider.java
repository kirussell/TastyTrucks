package com.kirussell.tastytrucks.location;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.style.CharacterStyle;

import com.google.android.gms.location.places.Place;
import com.kirussell.tastytrucks.location.data.PlacePrediction;

import java.util.List;

/**
 * Created by russellkim on 08/04/16.
 */
public interface PlacesProvider {

    void onStart();

    void onStop();

    /**
     * Retrieving places that fits given query. Method call is blocking
     * @param query to find suitable places
     * @param primary name text style
     * @param secondary info text style
     * @return list of places for query
     */
    @NonNull
    @WorkerThread
    List<PlacePrediction> getPlaces(String query, CharacterStyle primary, CharacterStyle secondary);

    @Nullable
    @WorkerThread
    public Place getPlaceById(String id);
}
