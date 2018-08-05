package com.sofialopes.android.bakingapp;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by Sofia on 5/3/2018.
 */

public class MyCustomAppClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());
    }
}
