package io.github.nfdz.cryptool.presenters;

import android.os.Bundle;
import android.text.TextUtils;

import io.github.nfdz.cryptool.interactors.CryptoolInteractor;
import io.github.nfdz.cryptool.interactors.DummyInteractor;
import io.github.nfdz.cryptool.views.CryptoolView;

public class CryptoolPresenterImpl implements CryptoolPresenter {

    private final static String MODE_KEY = "mode";

    private CryptoolView view;
    private CryptoolInteractor interactor;

    public CryptoolPresenterImpl(CryptoolView view) {
        this.view = view;
        interactor = new DummyInteractor();
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
    public void onDestroy() {
        view = null;
        interactor.onDestroy();
        interactor = null;
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
            view.setProcessedText("");

            CryptoolInteractor.Callback callback = new CryptoolInteractor.Callback() {
                @Override
                public void onResult(String processedText) {
                    if (view != null) {
                        view.hideLoading();
                        view.setProcessedText(processedText);
                    }
                }
            };

            if (view.getMode() == CryptoolView.ENCRYIPT_MODE) {
                interactor.encrypt(pass, originalText, callback);
            } else {
                interactor.decrypt(pass, originalText, callback);
            }
        }
    }

    @Override
    public void onToggleModeClick() {
        view.toggleMode();
    }
}
