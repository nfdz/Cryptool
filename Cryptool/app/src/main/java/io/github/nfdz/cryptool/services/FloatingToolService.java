package io.github.nfdz.cryptool.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.nfdz.cryptool.R;

public class FloatingToolService extends Service {

    public static void start(Context context) {
        context.startService(new Intent(context, FloatingToolService.class));
    }

    private WindowManager windowManager;
    private View toolView;
    private WindowManager.LayoutParams params;

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
    }

    private WindowManager.LayoutParams setupParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER;

        return params;
    }

    @OnClick(R.id.iv_collapse_view)
    void onCollapseButtonClick() {
        ToolBallService.start(this);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (toolView != null) windowManager.removeView(toolView);
    }
}
