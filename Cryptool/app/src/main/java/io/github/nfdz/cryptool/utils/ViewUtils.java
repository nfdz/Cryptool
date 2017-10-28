package io.github.nfdz.cryptool.utils;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import io.github.nfdz.cryptool.R;

public class ViewUtils {

    public static void hideHintWhenFocus(final EditText editText) {
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

    public static void showSnackbarMessage(Context context, View rootView, String message) {
        Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        snackbar.show();
    }

    public static void setEncryptMode(Context context,
                                      View originalBg,
                                      ImageButton originalCopy,
                                      ImageButton originalPaste,
                                      ImageButton originalClear,
                                      EditText originalText,
                                      TextView originalTextLabel,
                                      ImageView originalIcon,
                                      View processedBg,
                                      ImageButton processedCopy,
                                      TextView processedText,
                                      TextView processedTextLabel,
                                      ImageView processedIcon) {
        // Set plain text style in original text views
        originalBg.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPlainTextBg));
        originalCopy.setBackground(ContextCompat.getDrawable(context, R.drawable.plain_text_copypaste_ripple));
        originalCopy.setImageResource(R.drawable.ic_content_copy);
        originalPaste.setBackground(ContextCompat.getDrawable(context, R.drawable.plain_text_copypaste_ripple));
        originalPaste.setImageResource(R.drawable.ic_content_paste);
        originalClear.setBackground(ContextCompat.getDrawable(context, R.drawable.plain_text_copypaste_ripple));
        originalClear.setImageResource(R.drawable.ic_clear);
        originalText.setTextColor(ContextCompat.getColor(context, R.color.colorPlainText));
        originalTextLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPlainText));
        originalTextLabel.setText(R.string.plain_text_label);
        originalIcon.setImageResource(R.drawable.ic_no_encryption);

        // Set encrypted text style in processed text views
        processedBg.setBackgroundColor(ContextCompat.getColor(context, R.color.colorEncryptedTextBg));
        processedCopy.setBackground(ContextCompat.getDrawable(context, R.drawable.encrypted_text_copypaste_ripple));
        processedCopy.setImageResource(R.drawable.ic_content_copy_light);
        processedText.setTextColor(ContextCompat.getColor(context, R.color.colorEncryptedText));
        processedTextLabel.setTextColor(ContextCompat.getColor(context, R.color.colorEncryptedText));
        processedTextLabel.setText(R.string.encrypted_text_label);
        processedIcon.setImageResource(R.drawable.ic_encryption_light);
    }


    public static void setDecryptMode(Context context,
                                      View originalBg,
                                      ImageButton originalCopy,
                                      ImageButton originalPaste,
                                      ImageButton originalClear,
                                      EditText originalText,
                                      TextView originalTextLabel,
                                      ImageView originalIcon,
                                      View processedBg,
                                      ImageButton processedCopy,
                                      TextView processedText,
                                      TextView processedTextLabel,
                                      ImageView processedIcon) {
        // Set encrypted text style in original text views
        originalBg.setBackgroundColor(ContextCompat.getColor(context, R.color.colorEncryptedTextBg));
        originalCopy.setBackground(ContextCompat.getDrawable(context, R.drawable.encrypted_text_copypaste_ripple));
        originalCopy.setImageResource(R.drawable.ic_content_copy_light);
        originalPaste.setBackground(ContextCompat.getDrawable(context, R.drawable.encrypted_text_copypaste_ripple));
        originalPaste.setImageResource(R.drawable.ic_content_paste_light);
        originalClear.setBackground(ContextCompat.getDrawable(context, R.drawable.encrypted_text_copypaste_ripple));
        originalClear.setImageResource(R.drawable.ic_clear_light);
        originalText.setTextColor(ContextCompat.getColor(context, R.color.colorEncryptedText));
        originalTextLabel.setTextColor(ContextCompat.getColor(context, R.color.colorEncryptedText));
        originalTextLabel.setText(R.string.encrypted_text_label);
        originalIcon.setImageResource(R.drawable.ic_encryption_light);

        // Set plain text style in processed text views
        processedBg.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPlainTextBg));
        processedCopy.setBackground(ContextCompat.getDrawable(context, R.drawable.plain_text_copypaste_ripple));
        processedCopy.setImageResource(R.drawable.ic_content_copy);
        processedText.setTextColor(ContextCompat.getColor(context, R.color.colorPlainText));
        processedTextLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPlainText));
        processedTextLabel.setText(R.string.plain_text_label);
        processedIcon.setImageResource(R.drawable.ic_no_encryption);
    }
}
