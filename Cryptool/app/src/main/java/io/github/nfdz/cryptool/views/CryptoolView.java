package io.github.nfdz.cryptool.views;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public interface CryptoolView {

    @Retention(SOURCE)
    @IntDef({ENCRYIPT_MODE, DECRYIPT_MODE})
    public @interface Mode {}
    public static final int ENCRYIPT_MODE = 0;
    public static final int DECRYIPT_MODE = 1;


    void setOriginalText(String text);
    String getOriginalText();
    void setProcessedText(String text);
    String getProcessedText();
    void setPassphraseText(String pass);
    String getPassphrase();
    void toggleMode();
    @CryptoolView.Mode int getMode();
    void setMode(@CryptoolView.Mode int mode);
    void showLoading();
    void hideLoading();
}
