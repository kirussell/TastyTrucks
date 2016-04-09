package com.kirussell.tastytrucks.map;

import android.content.SharedPreferences;

import com.kirussell.tastytrucks.IntroController;
import com.kirussell.tastytrucks.utils.SpanUtil;

import dagger.Provides;

/**
 * Created by russellkim on 09/04/16.
 *
 */
public class TestMapViewModule extends MapViewModule {

    private final MapViewHandlers mapViewHanlders;
    private final SpanUtil spanUtil;
    private IntroController introController;

    public TestMapViewModule(MapViewHandlers handlers, SpanUtil spanUtil,
                             IntroController introController) {
        super(null);
        this.mapViewHanlders = handlers;
        this.spanUtil = spanUtil;
        this.introController = introController;
    }

    @Provides
    @Override
    public MapViewHandlers provideMapViewHandlers() {
        return mapViewHanlders;
    }

    @Provides
    @Override
    public SpanUtil provideSpanUtil() {
        return spanUtil;
    }

    @Override
    public IntroController provideIntroController() {
        return introController;
    }
}
