package io.github.nfdz.cryptool.interactors;

import android.os.Handler;

public class DummyInteractor implements CryptoolInteractor {

    private final static int DELAY_MILLIS = 5000;

    private Handler handler = new Handler();

    @Override
    public void onDestroy() {
        // nothing to do
    }

    @Override
    public void encrypt(String passphrase, String plainText, final Callback callback) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onResult("1249081723409182740912");
            }
        }, DELAY_MILLIS);
    }

    @Override
    public void decrypt(String passphrase, String encryptedText, final Callback callback) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onResult("Hello, World!");
            }
        }, DELAY_MILLIS);
    }

}
