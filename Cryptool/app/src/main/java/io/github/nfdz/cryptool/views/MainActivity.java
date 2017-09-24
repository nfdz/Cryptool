package io.github.nfdz.cryptool.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.nfdz.cryptool.R;
import io.github.nfdz.cryptool.presenters.CryptoolPresenter;
import io.github.nfdz.cryptool.presenters.CryptoolPresenterImpl;

public class MainActivity extends AppCompatActivity implements CryptoolView {

    @BindView(R.id.et_passphrase) EditText passPhrase;
    @BindView(R.id.et_original_text) EditText originalText;
    @BindView(R.id.tv_processed_text) TextView processedText;

    private CryptoolPresenter presenter = new CryptoolPresenterImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        hideHintWhenFocus(passPhrase);
        setTextListeners();
        presenter.onCreate(savedInstanceState);
    }

    private void hideHintWhenFocus(final EditText editText) {
        final CharSequence hint = editText.getHint();
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    editText.setHint("");
                else
                    editText.setHint(hint);
            }
        });
    }

    private void setTextListeners() {
        passPhrase.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* swallow */ }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.onPassphraseTextChanged();
            }
            @Override
            public void afterTextChanged(Editable s) { /* swallow */ }
        });
        originalText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* swallow */ }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.onOriginalTextChanged();
            }
            @Override
            public void afterTextChanged(Editable s) { /* swallow */ }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.onSaveInstanceState(outState);
    }

    @OnClick(R.id.fab_reverse)
    public void onReverseClick() {
        presenter.onReverseClick();
    }

    @OnClick(R.id.ib_original_copy)
    public void onCopyOriginalTextClick() {
        presenter.onCopyOriginalTextClick();
    }

    @OnClick(R.id.ib_original_paste)
    public void onPasteOriginalTextClick() {
        presenter.onPasteOriginalTextClick();
    }

    @OnClick(R.id.ib_processed_copy)
    public void onCopyProcessedTextClick() {
        presenter.onCopyProcessedTextClick();
    }

    @Override
    public void setOriginalText(String text) {
        originalText.setText(text);
    }

    @Override
    public String getOriginalText() {
        return originalText.getText().toString();
    }

    @Override
    public void setProcessedText(String text) {
        processedText.setText(text);
    }

    @Override
    public String getProcessedText() {
        return processedText.getText().toString();
    }

    @Override
    public void setPassphraseText(String pass) {
        passPhrase.setText(pass);
    }

    @Override
    public String getPassphrase() {
        return passPhrase.getText().toString();
    }
}
