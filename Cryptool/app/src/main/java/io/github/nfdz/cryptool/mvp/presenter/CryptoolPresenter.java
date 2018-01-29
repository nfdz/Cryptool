package io.github.nfdz.cryptool.mvp.presenter;

public interface CryptoolPresenter {

    void onCreate();
    void onDestroy();

    void onPassphraseTextChanged();
    void onOriginalTextChanged();
    void onToggleModeClick();
    void onOriginalClearClick();
}
