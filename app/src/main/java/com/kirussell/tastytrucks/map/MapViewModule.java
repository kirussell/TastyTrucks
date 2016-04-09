package com.kirussell.tastytrucks.map;

import com.kirussell.tastytrucks.utils.SpanUtil;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by russellkim on 09/04/16.
 * Module for Map view
 */
@Module
public class MapViewModule {

    @Provides
    public MapViewHandlers provideMapViewHandlers() {
        return new MapViewHandlers();
    }

    @Provides
    @Singleton
    public SpanUtil provideSpanUtil() {
        return new SpanUtil();
    }
}
