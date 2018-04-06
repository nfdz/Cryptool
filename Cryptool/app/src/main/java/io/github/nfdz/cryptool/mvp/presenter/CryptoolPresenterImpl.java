package io.github.nfdz.cryptool.mvp.presenter;

import android.content.Context;
import android.text.TextUtils;

import io.github.nfdz.cryptool.mvp.model.CryptoolModel;
import io.github.nfdz.cryptool.mvp.model.CryptoolModelImpl;
import io.github.nfdz.cryptool.mvp.view.CryptoolView;

public class CryptoolPresenterImpl implements CryptoolPresenter {

    private CryptoolView view;
    private CryptoolModel model;

    private boolean isPassphraseSaved;
    private boolean isPassphraseVisible;

    public CryptoolPresenterImpl(Context context, CryptoolView view) {
        this.view = view;
        model = new CryptoolModelImpl(context);
    }

    @Override
    public void onCreate() {
        view.setMode(model.getLastMode());
        view.setPassphraseText(model.getLastPassphrase());
        view.setOriginalText(model.getLastOriginalText());

        isPassphraseSaved = model.isLastPassphraseSaved();
        isPassphraseVisible = model.isLastPassphraseVisible();
        if (isPassphraseSaved) {
            isPassphraseVisible = false;
            view.setPassphraseInvisible();
            view.disablePassphraseActions();
        } else {
            view.enablePassphraseActions();
            if (isPassphraseVisible) {
                view.setPassphraseVisible();
            } else {
                view.setPassphraseInvisible();
            }
        }
    }

    @Override
    public void onDestroy() {
        model.onDestroy(view.getMode(),
                view.getPassphrase(),
                isPassphraseSaved,
                isPassphraseVisible,
                view.getOriginalText());
        model = null;
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
        if (model == null) return;

        String pass = view.getPassphrase();
        String originalText = view.getOriginalText();

        if (TextUtils.isEmpty(pass) || TextUtils.isEmpty(originalText)) {
            view.hideLoading();
            view.hideError();
            view.setProcessedText("");
        } else {
            view.showLoading();
            view.setProcessedText("");

            CryptoolModel.Callback callback = new CryptoolModel.Callback() {
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

            if (view.getMode() == CryptoolView.Mode.ENCRYIPT_MODE) {
                model.encrypt(pass, originalText, callback);
            } else {
                model.decrypt(pass, originalText, callback);
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

    @Override
    public void onClearPassphraseClick() {
        isPassphraseSaved = false;
        isPassphraseVisible = false;
        view.setPassphraseInvisible();
        view.setPassphraseText("");
        view.enablePassphraseActions();
    }

    @Override
    public void onViewPassphraseClick() {
        if (!isPassphraseSaved) {
            if (isPassphraseVisible) {
                isPassphraseVisible = false;
                view.setPassphraseInvisible();
            } else {
                isPassphraseVisible = true;
                view.setPassphraseVisible();
            }
        }
    }

    @Override
    public void onSavePassphraseClick() {
        if (!TextUtils.isEmpty(view.getPassphrase())) {
            isPassphraseSaved = true;
            isPassphraseVisible = false;
            view.setPassphraseInvisible();
            view.disablePassphraseActions();
        }
    }

}
