package com.kirussell.tastytrucks.location.data;

/**
 * Created by russellkim on 07/04/16.
 * Data object to hold predicted place
 */
public class PlacePrediction {
    String id;
    CharSequence name;
    CharSequence info;

    public PlacePrediction(String placeId, CharSequence primaryText, CharSequence secondaryText) {
        this.id = placeId;
        this.name = primaryText;
        this.info = secondaryText;
    }

    public CharSequence getName() {
        return name;
    }

    public CharSequence getInfo() {
        return info;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return name.toString();
    }
}
