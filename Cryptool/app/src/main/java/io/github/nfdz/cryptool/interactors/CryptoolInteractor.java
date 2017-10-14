package io.github.nfdz.cryptool.interactors;

public interface CryptoolInteractor {

    interface Callback {
        void onResult(String processedText);
    }

    void onDestroy();

    void encrypt(String passphrase, String plainText, Callback callback);
    void decrypt(String passphrase, String encryptedText, Callback callback);
}
