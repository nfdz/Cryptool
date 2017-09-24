package io.github.nfdz.cryptool.presenters;

import android.os.Bundle;

public interface CryptoolPresenter {

    void onCreate(Bundle savedInstanceState);
    void onSaveInstanceState(Bundle outState);

    void onPassphraseTextChanged();
    void onOriginalTextChanged();

    void onCopyOriginalTextClick();
    void onPasteOriginalTextClick();
    void onCopyProcessedTextClick();
    void onReverseClick();

}
