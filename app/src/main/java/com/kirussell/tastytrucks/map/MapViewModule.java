package com.kirussell.tastytrucks.map;

import android.content.Context;

import com.kirussell.tastytrucks.IntroController;
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

    private Context context;

    public MapViewModule(Context ctx) {
        this.context = ctx;
    }

    @Provides
    public MapViewHandlers provideMapViewHandlers() {
        return new MapViewHandlers();
    }

    @Provides
    @Singleton
    public SpanUtil provideSpanUtil() {
        return new SpanUtil();
    }

    @Provides
    @Singleton
    public IntroController provideIntroController() {
        return new IntroController(context.getSharedPreferences("Intro", Context.MODE_PRIVATE));
    }
}
