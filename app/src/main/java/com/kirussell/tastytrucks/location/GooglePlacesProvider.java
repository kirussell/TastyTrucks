package com.kirussell.tastytrucks.location;

import android.text.style.CharacterStyle;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.kirussell.tastytrucks.location.data.PlacePrediction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by russellkim on 07/04/16.
 */
public class GooglePlacesProvider implements PlacesProvider {

    private List<PlacePrediction> EMPTY_PLACES_LIST = new ArrayList<>(0);

    private final GoogleApiClientHost googleApiClientHost;

    GooglePlacesProvider(GoogleApiClientHost googleApiClientHost) {
        this.googleApiClientHost = googleApiClientHost;
    }

    @Override
    public void onStart() {
        googleApiClientHost.onStart();
    }

    @Override
    public void onStop() {
        googleApiClientHost.onStop();
    }

    /**
     * Retrieving places that fits given query. Method call is blocking
     * @param query to find suitable places
     * @param primary name text style
     * @param secondary info text style
     * @return list of places for query
     */
    @Override
    public List<PlacePrediction> getPlaces(String query, CharacterStyle primary, CharacterStyle secondary) {
        PendingResult<AutocompletePredictionBuffer> result = Places.GeoDataApi.getAutocompletePredictions(
                googleApiClientHost.getGoogleApiClient(), query,
                null, null
        );
        AutocompletePredictionBuffer autocompletePredictions = result.await(10, TimeUnit.SECONDS);
        if (!autocompletePredictions.getStatus().isSuccess()) {
            return EMPTY_PLACES_LIST;
        }
        ArrayList<AutocompletePrediction> predictions = DataBufferUtils.freezeAndClose(autocompletePredictions);
        ArrayList<PlacePrediction> places = new ArrayList<>(predictions.size());
        for (AutocompletePrediction prediction : predictions) {
            places.add(new PlacePrediction(
                    prediction.getPlaceId(),
                    prediction.getPrimaryText(primary),
                    prediction.getSecondaryText(secondary)
            ));
        }
        return places;
    }

    @Override
    public Place getPlaceById(String id) {
        PendingResult<PlaceBuffer> result = Places.GeoDataApi.getPlaceById(googleApiClientHost.getGoogleApiClient(), id);
        PlaceBuffer placeBuffer = result.await(10, TimeUnit.SECONDS);
        if (!placeBuffer.getStatus().isSuccess()) {
            return null;
        }
        ArrayList<Place> place = DataBufferUtils.freezeAndClose(placeBuffer);
        if (place.isEmpty()) {
            return null;
        }
        return place.get(0);
    }
}
