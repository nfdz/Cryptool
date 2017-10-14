package io.github.nfdz.cryptool.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

public class OverlayPermissionHelper {

    private static final int CODE_PERMISSION_REQUEST = 2839;

    public static interface Callback {
        void onPermissionGranted();
        void onPermissionDenied();
    }

    private final Callback callback;
    private final Activity activity;

    public OverlayPermissionHelper(Activity activity, Callback callback) {
        this.callback = callback;
        this.activity = activity;
    }

    public void request() {
        if (!hasPermission()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity.getPackageName()));
            activity.startActivityForResult(intent, CODE_PERMISSION_REQUEST);
        } else {
            callback.onPermissionGranted();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_PERMISSION_REQUEST) {
            if (hasPermission()) {
                callback.onPermissionGranted();
            } else {
                callback.onPermissionDenied();
            }
        }
    }

    private boolean hasPermission() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(activity);
    }
}
