package io.github.nfdz.cryptool.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import io.github.nfdz.cryptool.mvp.view.CryptoolView;
import io.github.nfdz.cryptool.services.ToolBallService;

import static android.content.Context.MODE_PRIVATE;

public class PreferencesUtils {

    private static final String PREFS_KEY = "cryptool_preferences";

    private static final String LAST_MODE_KEY = "last_mode";
    private static final @CryptoolView.Mode int LAST_MODE_DEFAULT = CryptoolView.Mode.ENCRYIPT_MODE;

    private static final String TOOLBALL_LAST_POSITION_Y_KEY = "tool_ball_last_position_y";
    private static final int TOOLBALL_LAST_POSITION_Y_DEFAULT = 0;

    private static final String TOOLBALL_LAST_GRAVITY_KEY = "tool_ball_last_gravity";
    private static final int TOOLBALL_LAST_GRAVITY_DEFAULT = ToolBallService.RIGHT_GRAVITY;

    private static final String LAST_PASSPHRASE_KEY = "last_passphrase";
    private static final String LAST_PASSPHRASE_DEFAULT = "";

    private static final String LAST_PASSPHRASE_SAVED_FLAG_KEY = "last_passphrase_saved_flag";
    private static final boolean LAST_PASSPHRASE_SAVED_FLAG_DEFAULT = false;

    private static final String LAST_PASSPHRASE_VISIBLE_FLAG_KEY = "last_passphrase_visible_flag";
    private static final boolean LAST_PASSPHRASE_VISIBLE_FLAG_DEFAULT = false;

    private static final String LAST_ORIGINAL_TEXT_KEY = "last_original_text";
    private static final String LAST_ORIGINAL_TEXT_DEFAULT = "";

    public static @CryptoolView.Mode int getLastMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        int lastModeRaw = prefs.getInt(LAST_MODE_KEY, LAST_MODE_DEFAULT);
        return lastModeRaw == CryptoolView.Mode.DECRYIPT_MODE ? CryptoolView.Mode.DECRYIPT_MODE : CryptoolView.Mode.ENCRYIPT_MODE;
    }

    public static void setLastMode(Context context, @CryptoolView.Mode int mode) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(LAST_MODE_KEY, mode);
        editor.apply();
    }

    public static int getToolBallLastPosition(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return prefs.getInt(TOOLBALL_LAST_POSITION_Y_KEY, TOOLBALL_LAST_POSITION_Y_DEFAULT);
    }

    public static void setToolBallLastPosition(Context context, int y) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(TOOLBALL_LAST_POSITION_Y_KEY, y);
        editor.apply();
    }

    public static int getToolBallLastGravity(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        int gravityRaw = prefs.getInt(TOOLBALL_LAST_GRAVITY_KEY, TOOLBALL_LAST_GRAVITY_DEFAULT);
        return gravityRaw == ToolBallService.LEFT_GRAVITY ? ToolBallService.LEFT_GRAVITY : ToolBallService.RIGHT_GRAVITY;
    }

    public static void setToolBallLastGravity(Context context, int gravity) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(TOOLBALL_LAST_GRAVITY_KEY, gravity);
        editor.apply();
    }

    public static String getLastPassphrase(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return prefs.getString(LAST_PASSPHRASE_KEY, LAST_PASSPHRASE_DEFAULT);
    }

    public static void setLastPassphrase(Context context, @Nullable String passphrase) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (TextUtils.isEmpty(passphrase)) {
            editor.remove(LAST_PASSPHRASE_KEY);
        } else {
            editor.putString(LAST_PASSPHRASE_KEY, passphrase);
        }
        editor.apply();
    }

    public static boolean isLastPassphraseSaved(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return prefs.getBoolean(LAST_PASSPHRASE_SAVED_FLAG_KEY, LAST_PASSPHRASE_SAVED_FLAG_DEFAULT);
    }

    public static void setLastPassphraseSaved(Context context, boolean isSaved) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(LAST_PASSPHRASE_SAVED_FLAG_KEY, isSaved);
        editor.apply();
    }

    public static boolean isLastPassphraseVisible(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return prefs.getBoolean(LAST_PASSPHRASE_VISIBLE_FLAG_KEY, LAST_PASSPHRASE_VISIBLE_FLAG_DEFAULT);
    }

    public static void setLastPassphraseVisible(Context context, boolean isVisible) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(LAST_PASSPHRASE_VISIBLE_FLAG_KEY, isVisible);
        editor.apply();
    }

    public static String getLastOriginalText(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return prefs.getString(LAST_ORIGINAL_TEXT_KEY, LAST_ORIGINAL_TEXT_DEFAULT);
    }

    public static void setLastOriginalText(Context context, @Nullable String originalText) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (TextUtils.isEmpty(originalText)) {
            editor.remove(LAST_ORIGINAL_TEXT_KEY);
        } else {
            editor.putString(LAST_ORIGINAL_TEXT_KEY, originalText);
        }
        editor.apply();
    }

}
