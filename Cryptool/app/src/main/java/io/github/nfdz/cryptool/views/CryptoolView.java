package io.github.nfdz.cryptool.views;

public interface CryptoolView {

    void setOriginalText(String text);
    String getOriginalText();
    void setProcessedText(String text);
    String getProcessedText();
    void setPassphraseText(String pass);
    String getPassphrase();

}
