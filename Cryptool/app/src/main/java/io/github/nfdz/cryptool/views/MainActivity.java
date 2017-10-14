package io.github.nfdz.cryptool.views;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.nfdz.cryptool.R;
import io.github.nfdz.cryptool.presenters.CryptoolPresenter;
import io.github.nfdz.cryptool.presenters.CryptoolPresenterImpl;
import io.github.nfdz.cryptool.services.ToolBallService;
import io.github.nfdz.cryptool.utils.OverlayPermissionHelper;

public class MainActivity extends AppCompatActivity implements CryptoolView, OverlayPermissionHelper.Callback {

    public static void start(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @BindView(R.id.root) View rootView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.et_passphrase) EditText passPhrase;
    @BindView(R.id.et_original_text) EditText originalText;
    @BindView(R.id.tv_processed_text) TextView processedText;

    @BindView(R.id.v_original_bg) View originalBg;
    @BindView(R.id.ib_original_copy) ImageButton originalCopy;
    @BindView(R.id.ib_original_paste) ImageButton originalPaste;
    @BindView(R.id.iv_original_icon) ImageView originalIcon;
    @BindView(R.id.tv_original_label) TextView originalTextLabel;
    @BindView(R.id.v_processed_bg) View processedBg;
    @BindView(R.id.ib_processed_copy) ImageButton processedCopy;
    @BindView(R.id.iv_processed_icon) ImageView processedIcon;
    @BindView(R.id.tv_processed_label) TextView processedTextLabel;
    @BindView(R.id.pb_processed_loading) ProgressBar loading;

    private CryptoolPresenter presenter;
    private @Mode int mode;
    private OverlayPermissionHelper permissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setToolbar();
        hideHintWhenFocus(passPhrase);
        setTextListeners();
        setEncryptMode();
        permissionHelper = new OverlayPermissionHelper(this, this);
        presenter = new CryptoolPresenterImpl(this);
        presenter.onCreate(savedInstanceState);
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) ab.setTitle("");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        permissionHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.open_tool_ball:
                // ensure that it has permission and handle this action in its callback
                permissionHelper.request();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.fab_toggle_mode)
    public void onToggleModeClick() {
        presenter.onToggleModeClick();
    }

    @OnClick(R.id.ib_original_copy)
    public void onCopyOriginalTextClick() {
        String text = getOriginalText();
        if (TextUtils.isEmpty(text)) {
            showMessage(getString(R.string.copy_clipboard_empty));
        } else {
            ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
            String label = getString(mode == ENCRYIPT_MODE ? R.string.plain_text_label : R.string.encrypted_text_label);
            ClipData clip = ClipData.newPlainText(label, text);
            clipboard.setPrimaryClip(clip);
            showMessage(getString(R.string.copy_clipboard_success));
        }
    }

    @OnClick(R.id.ib_original_paste)
    public void onPasteOriginalTextClick() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (!(clipboard.hasPrimaryClip())) {
            showMessage(getString(R.string.paste_clipboard_empty));
        } else if (!(clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))) {
            showMessage(getString(R.string.paste_clipboard_empty));
        } else {
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
            CharSequence pasteData = item.getText();
            if (TextUtils.isEmpty(pasteData)) {
                showMessage(getString(R.string.paste_clipboard_empty));
            } else {
                originalText.append(pasteData.toString());
            }
        }
    }

    @OnClick(R.id.ib_processed_copy)
    public void onCopyProcessedTextClick() {
        String text = getProcessedText();
        if (TextUtils.isEmpty(text)) {
            showMessage(getString(R.string.copy_clipboard_empty));
        } else {
            ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
            String label = getString(mode == ENCRYIPT_MODE ? R.string.encrypted_text_label : R.string.plain_text_label);
            ClipData clip = ClipData.newPlainText(label, text);
            clipboard.setPrimaryClip(clip);
            showMessage(getString(R.string.copy_clipboard_success));
        }
    }

    private void showMessage(String message) {
        Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        snackbar.show();
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

    @Override
    public void showLoading() {
        loading.setVisibility(View.VISIBLE);
        loading.bringToFront();
    }

    @Override
    public void hideLoading() {
        loading.setVisibility(View.GONE);
    }

    @Override
    public int getMode() {
        return mode;
    }

    @Override
    public void setMode(@Mode int mode) {
        if (mode == ENCRYIPT_MODE) {
            setEncryptMode();
        } else {
            setDecryptMode();
        }
    }

    @Override
    public void toggleMode() {
        if (mode == ENCRYIPT_MODE) {
            setDecryptMode();
        } else {
            setEncryptMode();
        }
    }

    private void setEncryptMode() {
        mode = ENCRYIPT_MODE;

        // Set plain text style in original text views
        originalBg.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPlainTextBg));
        originalCopy.setBackground(ContextCompat.getDrawable(this, R.drawable.plain_text_copypaste_ripple));
        originalCopy.setImageResource(R.drawable.ic_content_copy);
        originalPaste.setBackground(ContextCompat.getDrawable(this, R.drawable.plain_text_copypaste_ripple));
        originalPaste.setImageResource(R.drawable.ic_content_paste);
        originalText.setTextColor(ContextCompat.getColor(this, R.color.colorPlainText));
        originalTextLabel.setTextColor(ContextCompat.getColor(this, R.color.colorPlainText));
        originalTextLabel.setText(R.string.plain_text_label);
        originalIcon.setImageResource(R.drawable.ic_no_encryption);

        // Set encrypted text style in processed text views
        processedBg.setBackgroundColor(ContextCompat.getColor(this, R.color.colorEncryptedTextBg));
        processedCopy.setBackground(ContextCompat.getDrawable(this, R.drawable.encrypted_text_copypaste_ripple));
        processedCopy.setImageResource(R.drawable.ic_content_copy_light);
        processedText.setTextColor(ContextCompat.getColor(this, R.color.colorEncryptedText));
        processedTextLabel.setTextColor(ContextCompat.getColor(this, R.color.colorEncryptedText));
        processedTextLabel.setText(R.string.encrypted_text_label);
        processedIcon.setImageResource(R.drawable.ic_encryption_light);
    }

    private void setDecryptMode() {
        mode = DECRYIPT_MODE;

        // Set encrypted text style in original text views
        originalBg.setBackgroundColor(ContextCompat.getColor(this, R.color.colorEncryptedTextBg));
        originalCopy.setBackground(ContextCompat.getDrawable(this, R.drawable.encrypted_text_copypaste_ripple));
        originalCopy.setImageResource(R.drawable.ic_content_copy_light);
        originalPaste.setBackground(ContextCompat.getDrawable(this, R.drawable.encrypted_text_copypaste_ripple));
        originalPaste.setImageResource(R.drawable.ic_content_paste_light);
        originalText.setTextColor(ContextCompat.getColor(this, R.color.colorEncryptedText));
        originalTextLabel.setTextColor(ContextCompat.getColor(this, R.color.colorEncryptedText));
        originalTextLabel.setText(R.string.encrypted_text_label);
        originalIcon.setImageResource(R.drawable.ic_encryption_light);

        // Set plain text style in processed text views
        processedBg.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPlainTextBg));
        processedCopy.setBackground(ContextCompat.getDrawable(this, R.drawable.plain_text_copypaste_ripple));
        processedCopy.setImageResource(R.drawable.ic_content_copy);
        processedText.setTextColor(ContextCompat.getColor(this, R.color.colorPlainText));
        processedTextLabel.setTextColor(ContextCompat.getColor(this, R.color.colorPlainText));
        processedTextLabel.setText(R.string.plain_text_label);
        processedIcon.setImageResource(R.drawable.ic_no_encryption);
    }

    @Override
    public void onPermissionGranted() {
        ToolBallService.start(this);
        finish();
    }

    @Override
    public void onPermissionDenied() {
        Toast.makeText(this,
                "Draw over other app permission not available.",
                Toast.LENGTH_SHORT).show();
    }
}
