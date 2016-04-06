package com.kirussell.tastytrucks.location;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * Created by russellkim on 06/04/16.
 *
 */
@Module
public class GoogleLocationModule {

    private final Context context;

    public GoogleLocationModule(Context ctx) {
        this.context = ctx;
    }

    @Provides
    public GoogleLocationProvider providesLocationProvider() {
        return new GoogleLocationProvider(context);
    }
}
