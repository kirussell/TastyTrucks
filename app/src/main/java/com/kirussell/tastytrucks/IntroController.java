package com.kirussell.tastytrucks;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;

import com.kirussell.garson.Garson;

/**
 * Created by russellkim on 10/04/16.
 * Shows intro tips
 */
public class IntroController {

    private static final int SEARCHBAR_TIP = 1;
    public static final String MASK = "mask";

    private final SharedPreferences prefs;
    private int mask;

    public IntroController(SharedPreferences prefs) {
        this.prefs = prefs;
        mask = prefs.getInt("mask", 0);
    }

    public void showSearchBarTip(Activity ac, View searchBar, String text) {
        if ((mask & SEARCHBAR_TIP) != SEARCHBAR_TIP) {
            Garson.in(ac)
                    .with(text, R.dimen.tip_text, R.color.white)
                    .tip(searchBar);
            mask |= SEARCHBAR_TIP;
            prefs.edit().putInt(MASK, mask).apply();
        }
    }
}
