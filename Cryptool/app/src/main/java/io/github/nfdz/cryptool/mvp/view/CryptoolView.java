package io.github.nfdz.cryptool.mvp.view;

import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * This interface defines all methods of Cryptool UX.
 */
public interface CryptoolView {

    // Encrypt mode flag
    @Retention(SOURCE)
    @IntDef({Mode.ENCRYIPT_MODE, Mode.DECRYIPT_MODE})
    @interface Mode {
        int ENCRYIPT_MODE = 0;
        int DECRYIPT_MODE = 1;
    }

    void setOriginalText(String text);
    String getOriginalText();
    void setProcessedText(String text);
    String getProcessedText();
    void setPassphraseText(@Nullable String pass);
    String getPassphrase();
    void toggleMode();
    @CryptoolView.Mode int getMode();
    void setMode(@CryptoolView.Mode int mode);
    void showLoading();
    void hideLoading();
    void showError();
    void hideError();
    void setPassphraseInvisible();
    void setPassphraseVisible();
    void disablePassphraseActions();
    void enablePassphraseActions();

}
