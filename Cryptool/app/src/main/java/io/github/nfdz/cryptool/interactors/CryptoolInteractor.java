package io.github.nfdz.cryptool.interactors;

import android.support.annotation.Nullable;

import io.github.nfdz.cryptool.views.CryptoolView;

public interface CryptoolInteractor {

    interface Callback {
        void onResult(String processedText);
        void onError();
    }

    void onDestroy(@CryptoolView.Mode int lastMode, @Nullable String lastPassphrase, @Nullable String lastOriginalText);

    @CryptoolView.Mode int getLastMode();
    @Nullable String getLastPassphrase();
    @Nullable String getLastOriginalText();

    void encrypt(String passphrase, String plainText, Callback callback);
    void decrypt(String passphrase, String encryptedText, Callback callback);
}
