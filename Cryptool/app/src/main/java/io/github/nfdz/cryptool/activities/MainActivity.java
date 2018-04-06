package io.github.nfdz.cryptool.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
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
import butterknife.OnLongClick;
import io.github.nfdz.cryptool.R;
import io.github.nfdz.cryptool.mvp.presenter.CryptoolPresenter;
import io.github.nfdz.cryptool.mvp.presenter.CryptoolPresenterImpl;
import io.github.nfdz.cryptool.mvp.view.CryptoolView;
import io.github.nfdz.cryptool.services.ToolBallService;
import io.github.nfdz.cryptool.utils.BroadcastUtils;
import io.github.nfdz.cryptool.utils.ClipboardUtils;
import io.github.nfdz.cryptool.utils.OverlayPermissionHelper;
import io.github.nfdz.cryptool.utils.ViewUtils;

/**
 * Main activity class (this project has one activity only). This is a Cryptool view implementation.
 */
public class MainActivity extends AppCompatActivity implements CryptoolView, OverlayPermissionHelper.Callback {

    public static final String OPEN_TOOL_BALL_ACTION = "io.github.nfdz.cryptool.OPEN_TOOL_BALL";

    public static void start(Context context) {
        context.startActivity(new Intent(context, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @BindView(R.id.root) View rootView;
    @BindView(R.id.appbar) AppBarLayout appbar;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.et_passphrase) EditText passPhrase;
    @BindView(R.id.et_original_text) EditText originalText;
    @BindView(R.id.tv_processed_text) TextView processedText;

    @BindView(R.id.iv_passphrase_view) ImageView passPhraseView;
    @BindView(R.id.iv_passphrase_save) ImageView passPhraseSave;

    @BindView(R.id.v_original_bg) View originalBg;
    @BindView(R.id.ib_original_copy) ImageButton originalCopy;
    @BindView(R.id.ib_original_paste) ImageButton originalPaste;
    @BindView(R.id.iv_original_icon) ImageView originalIcon;
    @BindView(R.id.tv_original_label) TextView originalTextLabel;
    @BindView(R.id.ib_original_clear) ImageButton originalClear;
    @BindView(R.id.v_processed_bg) View processedBg;
    @BindView(R.id.ib_processed_copy) ImageButton processedCopy;
    @BindView(R.id.iv_processed_icon) ImageView processedIcon;
    @BindView(R.id.tv_processed_label) TextView processedTextLabel;
    @BindView(R.id.pb_processed_loading) ProgressBar loading;
    @BindView(R.id.iv_processed_error) ImageView processedError;

    private CryptoolPresenter presenter;
    private @Mode int mode;
    private OverlayPermissionHelper permissionHelper;
    private TextWatcher passPhrasseListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setToolbar();
        ViewUtils.hideHintWhenFocus(passPhrase);
        setTextListeners();
        permissionHelper = new OverlayPermissionHelper(this, this);
        presenter = new CryptoolPresenterImpl(this, this);
        presenter.onCreate();
        BroadcastUtils.sendCloseFloatingWindowsBroadcast(this);

        if (launchToolBallIntent(getIntent())) {
            permissionHelper.request();
        }
    }

    private boolean launchToolBallIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            return !TextUtils.isEmpty(action) && action.equals(OPEN_TOOL_BALL_ACTION);
        }
        return false;
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) ab.setTitle("");
    }

    private void setTextListeners() {
        passPhrasseListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* swallow */ }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.onPassphraseTextChanged();
            }
            @Override
            public void afterTextChanged(Editable s) { /* swallow */ }
        };
        passPhrase.addTextChangedListener(passPhrasseListener);
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
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
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

    @Override
    public void onBackPressed() {
        // this is a workaround because app bar does not refresh correctly
        boolean fullyExpanded = (appbar.getHeight() - appbar.getBottom()) == 0;
        if (fullyExpanded) {
            super.onBackPressed();
        } else {
            appbar.setExpanded(true, true);
        }
    }

    @OnClick(R.id.ib_original_clear)
    public void onOriginalClearClick() {
        presenter.onOriginalClearClick();
        // this is a workaround because app bar does not refresh correctly
        appbar.setExpanded(true, true);
    }

    @OnClick(R.id.fab_toggle_mode)
    public void onToggleModeClick() {
        presenter.onToggleModeClick();
        // this is a workaround because app bar does not refresh correctly
        appbar.setExpanded(true, true);
    }

    @OnClick(R.id.ib_original_copy)
    public void onCopyOriginalTextClick() {
        String text = getOriginalText();
        ClipboardUtils.copyText(this, text, mode, rootView);
    }

    @OnClick(R.id.ib_original_paste)
    public void onPasteOriginalTextClick() {
        ClipboardUtils.pasteText(this, originalText, rootView);
    }

    @OnClick(R.id.ib_processed_copy)
    public void onCopyProcessedTextClick() {
        String text = getProcessedText();
        ClipboardUtils.copyText(this, text, mode, rootView);
    }

    @OnClick(R.id.iv_passphrase_view)
    public void onViewPassphraseClick() {
        presenter.onViewPassphraseClick();
    }

    @OnClick(R.id.iv_passphrase_save)
    public void onSavePassphraseClick() {
        presenter.onSavePassphraseClick();
    }

    @OnClick(R.id.iv_passphrase_clear)
    public void onClearPassphraseClick() {
        presenter.onClearPassphraseClick();
    }

    @OnClick(R.id.iv_passphrase_icon)
    public void onPassphraseIconClick() {
        ViewUtils.showSnackbarMessage(this,
                rootView,
                getString(R.string.encryption_algorithm));
    }

    @OnLongClick(R.id.iv_passphrase_icon)
    public boolean onPassphraseIconLongClick() {
        onPassphraseIconClick();
        return true;
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
    public void showError() {
        processedError.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideError() {
        processedError.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setPassphraseInvisible() {
        passPhraseView.setImageResource(R.drawable.ic_eye);
        passPhrase.removeTextChangedListener(passPhrasseListener);
        passPhrase.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passPhrase.addTextChangedListener(passPhrasseListener);
    }

    @Override
    public void setPassphraseVisible() {
        passPhraseView.setImageResource(R.drawable.ic_transparent_eye);
        passPhrase.removeTextChangedListener(passPhrasseListener);
        passPhrase.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        passPhrase.addTextChangedListener(passPhrasseListener);
    }

    @Override
    public void disablePassphraseActions() {
        passPhrase.setEnabled(false);
        passPhraseView.setImageResource(R.drawable.ic_eye_gray);
        passPhraseSave.setImageResource(R.drawable.ic_save_gray);
        passPhraseView.setEnabled(false);
        passPhraseSave.setEnabled(false);
    }

    @Override
    public void enablePassphraseActions() {
        passPhrase.setEnabled(true);
        passPhraseView.setImageResource(R.drawable.ic_eye);
        passPhraseSave.setImageResource(R.drawable.ic_save);
        passPhraseView.setEnabled(true);
        passPhraseSave.setEnabled(true);
    }

    @Override
    public int getMode() {
        return mode;
    }

    @Override
    public void setMode(@Mode int mode) {
        if (mode == Mode.ENCRYIPT_MODE) {
            setEncryptMode();
        } else {
            setDecryptMode();
        }
    }

    @Override
    public void toggleMode() {
        if (mode == Mode.ENCRYIPT_MODE) {
            setDecryptMode();
        } else {
            setEncryptMode();
        }
    }

    private void setEncryptMode() {
        mode = Mode.ENCRYIPT_MODE;
        ViewUtils.setEncryptMode(this,
                originalBg,
                originalCopy,
                originalPaste,
                originalClear,
                originalText,
                originalTextLabel,
                originalIcon,
                processedBg,
                processedCopy,
                processedText,
                processedTextLabel,
                processedIcon);
    }

    private void setDecryptMode() {
        mode = Mode.DECRYIPT_MODE;
        ViewUtils.setDecryptMode(this,
                originalBg,
                originalCopy,
                originalPaste,
                originalClear,
                originalText,
                originalTextLabel,
                originalIcon,
                processedBg,
                processedCopy,
                processedText,
                processedTextLabel,
                processedIcon);
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
