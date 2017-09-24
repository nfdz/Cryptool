package io.github.nfdz.cryptool;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.et_passphrase) EditText mPassPhrase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        hideHintWhenFocus(mPassPhrase);

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

    @OnClick(R.id.et_original_text)
    public void onCopyOriginalTextClick() {

    }
}
