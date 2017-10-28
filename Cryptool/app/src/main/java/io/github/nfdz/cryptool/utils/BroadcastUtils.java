package io.github.nfdz.cryptool.utils;

import android.content.Context;
import android.content.Intent;

public class BroadcastUtils {

    public static final String CLOSE_FLOATING_WINDOWS_ACTION = "io.github.nfdz.cryptool.CLOSE_FLOATING_WINDOWS";

    public static void sendCloseFloatingWindowsBroadcast(Context context) {
        Intent intent = new Intent();
        intent.setAction(CLOSE_FLOATING_WINDOWS_ACTION);
        context.sendBroadcast(intent);
    }

}
