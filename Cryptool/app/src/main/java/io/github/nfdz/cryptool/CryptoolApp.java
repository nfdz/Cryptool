package io.github.nfdz.cryptool;

import android.app.Application;

import timber.log.Timber;

public class CryptoolApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        initLogger();
    }

    private void initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.uprootAll();
            Timber.plant(new Timber.DebugTree());
        }
    }
}
