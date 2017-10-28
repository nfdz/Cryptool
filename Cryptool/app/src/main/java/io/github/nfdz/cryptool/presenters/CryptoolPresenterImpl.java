package io.github.nfdz.cryptool.presenters;

import android.content.Context;
import android.text.TextUtils;

import io.github.nfdz.cryptool.interactors.CryptoolInteractor;
import io.github.nfdz.cryptool.interactors.CryptoolInteractorImpl;
import io.github.nfdz.cryptool.views.CryptoolView;

public class CryptoolPresenterImpl implements CryptoolPresenter {

    private CryptoolView view;
    private CryptoolInteractor interactor;

    public CryptoolPresenterImpl(Context context, CryptoolView view) {
        this.view = view;
        interactor = new CryptoolInteractorImpl(context);
    }

    @Override
    public void onCreate() {
        view.setMode(interactor.getLastMode());
        view.setPassphraseText(interactor.getLastPassphrase());
        view.setOriginalText(interactor.getLastOriginalText());
    }

    @Override
    public void onDestroy() {
        interactor.onDestroy(view.getMode(), view.getPassphrase(), view.getOriginalText());
        interactor = null;
        view = null;
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
        if (view == null) return;
        if (interactor == null) return;

        String pass = view.getPassphrase();
        String originalText = view.getOriginalText();

        if (TextUtils.isEmpty(pass) || TextUtils.isEmpty(originalText)) {
            view.hideLoading();
            view.hideError();
            view.setProcessedText("");
        } else {
            view.showLoading();
            view.setProcessedText("");

            CryptoolInteractor.Callback callback = new CryptoolInteractor.Callback() {
                @Override
                public void onResult(String processedText) {
                    if (view != null) {
                        view.hideLoading();
                        view.hideError();
                        view.setProcessedText(processedText);
                    }
                }
                @Override
                public void onError() {
                    if (view != null) {
                        view.hideLoading();
                        view.showError();
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
        processText();
    }

    @Override
    public void onOriginalClearClick() {
        view.setOriginalText("");
    }
}
