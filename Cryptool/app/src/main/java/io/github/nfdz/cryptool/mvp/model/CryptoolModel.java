package io.github.nfdz.cryptool.mvp.model;

import android.support.annotation.Nullable;

import io.github.nfdz.cryptool.mvp.view.CryptoolView;

public interface CryptoolModel {

    interface Callback {
        void onResult(String processedText);
        void onError();
    }

    void onDestroy(@CryptoolView.Mode int lastMode,
                   @Nullable String lastPassphrase,
                   boolean isLastPassphraseSaved,
                   boolean isLastPassphraseVisible,
                   @Nullable String lastOriginalText);

    @CryptoolView.Mode int getLastMode();
    @Nullable String getLastPassphrase();
    boolean isLastPassphraseSaved();
    boolean isLastPassphraseVisible();
    @Nullable String getLastOriginalText();

    void encrypt(String passphrase, String plainText, Callback callback);
    void decrypt(String passphrase, String encryptedText, Callback callback);

}
