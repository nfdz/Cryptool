package io.github.nfdz.cryptool.presenters;

import android.os.Bundle;
import android.text.TextUtils;

import io.github.nfdz.cryptool.views.CryptoolView;
import timber.log.Timber;

public class CryptoolPresenterImpl implements CryptoolPresenter {

    private final static String MODE_KEY = "mode";

    private CryptoolView view;

    public CryptoolPresenterImpl(CryptoolView view) {
        this.view = view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            int rawMode = savedInstanceState.getInt(MODE_KEY, CryptoolView.ENCRYIPT_MODE);
            @CryptoolView.Mode int mode = rawMode == CryptoolView.ENCRYIPT_MODE ?
                    CryptoolView.ENCRYIPT_MODE : CryptoolView.DECRYIPT_MODE;
            view.setMode(mode);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(MODE_KEY, view.getMode());
    }

    @Override
    public void onPassphraseTextChanged() {
        processText();
    }

    @Override
    public void onOriginalTextChanged() {
        processText();
    }

    private void processText() {
        String pass = view.getPassphrase();
        String originalText = view.getOriginalText();

        if (TextUtils.isEmpty(pass) || TextUtils.isEmpty(originalText)) {
            view.hideLoading();
            view.setProcessedText("");
        } else {
            view.showLoading();

        }
    }

    @Override
    public void onCopyOriginalTextClick() {

    }

    @Override
    public void onPasteOriginalTextClick() {

    }

    @Override
    public void onCopyProcessedTextClick() {

    }

    @Override
    public void onReverseClick() {
        view.toggleMode();
    }
}
