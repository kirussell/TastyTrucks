package com.kirussell.tastytrucks.map;

import com.kirussell.tastytrucks.utils.SpanUtil;

import dagger.Provides;

/**
 * Created by russellkim on 09/04/16.
 *
 */
public class TestMapViewModule extends MapViewModule {

    private final MapViewHandlers mapViewHanlders;
    private final SpanUtil spanUtil;

    public TestMapViewModule(MapViewHandlers handlers, SpanUtil spanUtil) {
        this.mapViewHanlders = handlers;
        this.spanUtil = spanUtil;
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
}
