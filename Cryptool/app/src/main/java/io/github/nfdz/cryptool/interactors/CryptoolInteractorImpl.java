package io.github.nfdz.cryptool.interactors;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;

import com.scottyab.aescrypt.AESCrypt;

import io.github.nfdz.cryptool.utils.PreferencesUtils;
import io.github.nfdz.cryptool.views.CryptoolView;

public class CryptoolInteractorImpl implements CryptoolInteractor {

    private final static String ENCRYPTION_THREAD_NAME = "io.github.nfdz.cryptool.thread.encryption";

    private Context context;
    private HandlerThread handlerThread;
    private Handler handler;
    private Handler uiHandler;

    public CryptoolInteractorImpl(Context context) {
        this.context = context;
        handlerThread = new HandlerThread(ENCRYPTION_THREAD_NAME);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        uiHandler = new Handler();
    }

    @Override
    public void onDestroy(@CryptoolView.Mode int lastMode, @Nullable String lastPassphrase, @Nullable String lastOriginalText) {
        if (context != null) {
            PreferencesUtils.setLastMode(context, lastMode);
            PreferencesUtils.setLastPassphrase(context, lastPassphrase);
            PreferencesUtils.setLastOriginalText(context, lastOriginalText);
            context = null;
        }
        if (handlerThread != null) {
            try {
                handlerThread.quit();
            } catch (Exception ex) {
                // swallow
            }
            handlerThread = null;
        }
    }

    @Override
    public int getLastMode() {
        return PreferencesUtils.getLastMode(context);
    }

    @Nullable
    @Override
    public String getLastPassphrase() {
        return PreferencesUtils.getLastPassphrase(context);
    }

    @Nullable
    @Override
    public String getLastOriginalText() {
        return PreferencesUtils.getLastOriginalText(context);
    }

    @Override
    public void encrypt(String passphrase, String plainText, Callback callback) {
        handler.post(new EncryptTask(passphrase, plainText, callback));
    }

    public class EncryptTask implements Runnable {

        private Callback callback;
        private String passphrase;
        private String plainText;

        public EncryptTask(String passphrase, String plainText, Callback callback) {
            this.callback = callback;
            this.passphrase = passphrase;
            this.plainText = plainText;
        }

        @Override
        public void run() {
            try {
                final String encryptedText = AESCrypt.encrypt(passphrase, plainText);
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResult(encryptedText);
                    }
                });
            } catch (Exception e) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onError();
                    }
                });
            }
        }
    }

    @Override
    public void decrypt(String passphrase, String encryptedText, final Callback callback) {
        handler.post(new DecryptTask(passphrase, encryptedText, callback));
    }

    public class DecryptTask implements Runnable {

        private Callback callback;
        private String passphrase;
        private String encryptedText;

        public DecryptTask(String passphrase, String encryptedText, Callback callback) {
            this.callback = callback;
            this.passphrase = passphrase;
            this.encryptedText = encryptedText;
        }

        @Override
        public void run() {
            try {
                final String plainText = AESCrypt.decrypt(passphrase, encryptedText);
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResult(plainText);
                    }
                });
            } catch (Exception e) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onError();
                    }
                });
            }
        }
    }

}
