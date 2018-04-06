package io.github.nfdz.cryptool.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
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
import io.github.nfdz.cryptool.utils.BroadcastUtils;
import io.github.nfdz.cryptool.utils.ClipboardUtils;
import io.github.nfdz.cryptool.utils.ViewUtils;
import io.github.nfdz.cryptool.mvp.view.CryptoolView;
import io.github.nfdz.cryptool.activities.MainActivity;

/**
 * This foreground service is an implementation of main CryptoolView because has the same functionality
 * but in another way (floating tool). It uses the same presenter.
 */
public class FloatingToolService extends Service implements CryptoolView {

    public static void start(Context context) {
        context.startService(new Intent(context, FloatingToolService.class));
    }

    private WindowManager windowManager;
    private View toolView;
    private WindowManager.LayoutParams params;
    private CloseBroadcastReceiver receiver;

    private CryptoolPresenter presenter;
    private @CryptoolView.Mode int mode;
    private TextWatcher passPhrasseListener;

    @BindView(R.id.iv_background) ImageView toolBackground;
    @BindView(R.id.container) View container;

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

    public FloatingToolService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        toolView = LayoutInflater.from(this).inflate(R.layout.floating_tool, null);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        params = setupParams();
        windowManager.addView(toolView, params);
        ButterKnife.bind(this, toolView);
        setListeners();
        presenter = new CryptoolPresenterImpl(this, this);
        setBroadcastReceiver();
        playEnterAnimation(new Runnable() {
            @Override
            public void run() {
                presenter.onCreate();
            }
        });
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        presenter.onDestroy();
        if (toolView != null) windowManager.removeView(toolView);
        super.onDestroy();
    }

    private void setBroadcastReceiver() {
        receiver = new CloseBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastUtils.CLOSE_FLOATING_WINDOWS_ACTION);
        registerReceiver(receiver, filter);
    }


    private WindowManager.LayoutParams setupParams() {
        WindowManager.LayoutParams params;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    PixelFormat.TRANSLUCENT);
        }
        params.gravity = Gravity.CENTER;
        return params;
    }


    private void setListeners() {
        ViewUtils.hideHintWhenFocus(passPhrase);
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
        toolView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playExitAnimation(new Runnable() {
                    @Override
                    public void run() {
                        ToolBallService.start(FloatingToolService.this);
                        stopSelf();
                    }
                });
            }
        });
    }

    private void playEnterAnimation(final Runnable callback) {

        final int fromXscale = 0;
        final int toXscale = 1;
        final int fromYscale = 0;
        final int toYscale = 1;

        Animation scale = new ScaleAnimation(fromXscale,
                toXscale, fromYscale,
                toYscale, Animation.
                RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        scale.setDuration(300); // 0.3s

        scale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                container.setVisibility(View.VISIBLE);
                callback.run();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        toolBackground.startAnimation(scale);
    }

    private void playExitAnimation(final Runnable runnable) {

        originalText.setVisibility(View.GONE);
        processedText.setVisibility(View.GONE);
        passPhrase.setVisibility(View.GONE);

        final int fromXscale = 1;
        final int toXscale = 0;
        final int fromYscale = 1;
        final int toYscale = 0;

        Animation scale = new ScaleAnimation(fromXscale,
                toXscale, fromYscale,
                toYscale, Animation.
                RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        scale.setDuration(300); // 0.3s

        scale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                container.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                toolBackground.setVisibility(View.INVISIBLE);
                runnable.run();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        toolBackground.startAnimation(scale);
    }

    @OnClick(R.id.tv_logo)
    void onLogoClick() {
        playExitAnimation(new Runnable() {
            @Override
            public void run() {
                stopSelf();
                MainActivity.start(FloatingToolService.this);
            }
        });
    }

    @OnClick(R.id.ib_original_clear)
    void onOriginalClearClick() {
        presenter.onOriginalClearClick();
    }

    @OnClick(R.id.iv_toggle_mode)
    void onToggleModeClick() {
        presenter.onToggleModeClick();
    }

    @OnClick(R.id.ib_original_copy)
    void onCopyOriginalTextClick() {
        String text = getOriginalText();
        ClipboardUtils.copyText(this, text, mode, null);
    }

    @OnClick(R.id.ib_original_paste)
    void onPasteOriginalTextClick() {
        ClipboardUtils.pasteText(this, originalText, null);
    }

    @OnClick(R.id.ib_processed_copy)
    void onCopyProcessedTextClick() {
        String text = getProcessedText();
        ClipboardUtils.copyText(this, text, mode, null);
    }

    @OnClick(R.id.iv_collapse_view)
    void onCollapseButtonClick() {
        playExitAnimation(new Runnable() {
            @Override
            public void run() {
                ToolBallService.start(FloatingToolService.this);
                stopSelf();
            }
        });
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
        Toast.makeText(this, getString(R.string.encryption_algorithm), Toast.LENGTH_LONG).show();
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
    public void toggleMode() {
        if (mode == Mode.ENCRYIPT_MODE) {
            setDecryptMode();
        } else {
            setEncryptMode();
        }
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
        passPhrase.removeTextChangedListener(passPhrasseListener);
        passPhraseView.setImageResource(R.drawable.ic_eye);
        passPhrase.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passPhrase.addTextChangedListener(passPhrasseListener);
    }

    @Override
    public void setPassphraseVisible() {
        passPhrase.removeTextChangedListener(passPhrasseListener);
        passPhraseView.setImageResource(R.drawable.ic_transparent_eye);
        passPhrase.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
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

    private class CloseBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopSelf();
        }
    }
}
