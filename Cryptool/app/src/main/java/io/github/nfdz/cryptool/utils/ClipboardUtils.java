package io.github.nfdz.cryptool.utils;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import io.github.nfdz.cryptool.R;
import io.github.nfdz.cryptool.views.CryptoolView;

public class ClipboardUtils {

    public static void copyText(Context context, String text, @CryptoolView.Mode int mode, @Nullable View rootView) {
        if (TextUtils.isEmpty(text)) {
            String msg = context.getString(R.string.copy_clipboard_empty);
            if (rootView != null) {
                ViewUtils.showSnackbarMessage(context,
                        rootView,
                        msg);
            } else {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        } else {
            ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
            String label = context.getString(mode == CryptoolView.ENCRYIPT_MODE ? R.string.plain_text_label : R.string.encrypted_text_label);
            ClipData clip = ClipData.newPlainText(label, text);
            clipboard.setPrimaryClip(clip);
            String msg = context.getString(R.string.copy_clipboard_success);
            if (rootView != null) {
                ViewUtils.showSnackbarMessage(context,
                        rootView,
                        msg);
            } else {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        }
    }

    public static void pasteText(Context context, EditText textView, @Nullable View rootView) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (!(clipboard.hasPrimaryClip())) {
            String msg = context.getString(R.string.paste_clipboard_empty);
            if (rootView != null) {
                ViewUtils.showSnackbarMessage(context,
                        rootView,
                        msg);
            } else {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        } else if (!(clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))) {
            String msg = context.getString(R.string.paste_clipboard_empty);
            if (rootView != null) {
                ViewUtils.showSnackbarMessage(context,
                        rootView,
                        msg);
            } else {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        } else {
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
            CharSequence pasteData = item.getText();
            if (TextUtils.isEmpty(pasteData)) {
                String msg = context.getString(R.string.paste_clipboard_empty);
                if (rootView != null) {
                    ViewUtils.showSnackbarMessage(context,
                            rootView,
                            msg);
                } else {
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }
            } else {
                textView.append(pasteData.toString());
            }
        }
    }
}
