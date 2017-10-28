package io.github.nfdz.cryptool.presenters;

public interface CryptoolPresenter {

    void onCreate();
    void onDestroy();

    void onPassphraseTextChanged();
    void onOriginalTextChanged();
    void onToggleModeClick();
    void onOriginalClearClick();
}
