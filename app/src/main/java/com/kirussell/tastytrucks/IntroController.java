package com.kirussell.tastytrucks;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.kirussell.garson.ClickCallbacks;
import com.kirussell.garson.Garson;

/**
 * Created by russellkim on 10/04/16.
 * Shows intro tips
 */
public class IntroController {

    private static final int SEARCHBAR_TIP = 1;
    private static final int MY_LOCATION_TIP = 2;
    public static final String MASK = "mask";

    private final SharedPreferences prefs;
    private int mask;
    private boolean searchBarTipInProgress = false;
    private Runnable deferAction;

    public IntroController(SharedPreferences prefs) {
        this.prefs = prefs;
        mask = prefs.getInt("mask", 0);
    }

    public void showSearchBarTip(Activity ac, View searchBar, String text) {
        if ((mask & SEARCHBAR_TIP) != SEARCHBAR_TIP) {
            Garson.in(ac)
                    .with(text, R.dimen.tip_text, R.color.white)
                    .callback(new DismissCallback() {
                        @Override
                        void onDismiss() {
                            searchBarTipInProgress = true;
                            if (deferAction != null) {
                                deferAction.run();
                            }
                        }
                    })
                    .tip(searchBar);
            searchBarTipInProgress = true;
            mask |= SEARCHBAR_TIP;
            prefs.edit().putInt(MASK, mask).apply();
        }
    }

    public void showMyLocationTip(final Activity ac, final View myLocation, final Drawable shadow,
                                  final String text) {
        if ((mask & MY_LOCATION_TIP) != MY_LOCATION_TIP) {
            Runnable action = new Runnable() {
                @Override
                public void run() {
                    Garson.in(ac)
                            .with(text, R.dimen.tip_text, R.color.white)
                            .withShape(shadow)
                            .tip(myLocation);
                    mask |= MY_LOCATION_TIP;
                    prefs.edit().putInt(MASK, mask).apply();
                }
            };
            if (searchBarTipInProgress) {
                deferAction = action;
            } else {
                action.run();
            }
        }
    }

    private abstract class DismissCallback implements ClickCallbacks {

        @Override
        public void onHintTextClicked(Garson garson) {
            garson.dismiss();
            onDismiss();
        }

        @Override
        public void onBackgroundClicked(Garson garson) {
            garson.dismiss();
            onDismiss();
        }

        @Override
        public void onTipViewClicked(Garson garson) {
            garson.dismiss();
            onDismiss();
        }

        abstract void onDismiss();
    }
}
