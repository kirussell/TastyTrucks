package com.kirussell.tastytrucks.map;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import javax.inject.Inject;

/**
 * Created by russellkim on 09/04/16.
 * Databinding handlers for map
 */
public class MapViewHandlers {
    public ObservableBoolean truckInfoShown = new ObservableBoolean(false);
    public ObservableField<CharSequence> truckInfo = new ObservableField<>();

    @Inject
    public MapViewHandlers() { }
}
