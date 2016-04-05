package com.kirussell.tastytrucks.components;

import com.kirussell.tastytrucks.MapActivity;
import com.kirussell.tastytrucks.api.ApiModule;

import dagger.Component;

/**
 * Created by russellkim on 05/04/16.
 * Component to access api
 */
@Component(modules={ApiModule.class})
public interface ApiComponent {
    void inject(MapActivity ac);
}
