package io.github.nfdz.cryptool;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import timber.log.Timber;

public class CryptoolApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Make sure we use vector drawables
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        initLogger();
    }

    private void initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.uprootAll();
            Timber.plant(new Timber.DebugTree());
        }
    }
}
