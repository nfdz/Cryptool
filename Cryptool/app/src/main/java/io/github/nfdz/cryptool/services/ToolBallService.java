package io.github.nfdz.cryptool.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.nfdz.cryptool.R;

public class ToolBallService extends Service {

    public static void start(Context context) {
        context.startService(new Intent(context, ToolBallService.class));
    }

    private WindowManager windowManager;
    private View toolBallView;
    private WindowManager.LayoutParams params;

    @BindView(R.id.iv_tool_ball) View toolBallImage;

    public ToolBallService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        toolBallView = LayoutInflater.from(this).inflate(R.layout.tool_ball, null);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        params = setupParams();
        windowManager.addView(toolBallView, params);
        ButterKnife.bind(this, toolBallView);
        toolBallImage.setOnTouchListener(new TouchListener());
    }

    private WindowManager.LayoutParams setupParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;

        return params;
    }

    @OnClick(R.id.iv_close)
    void onCloseClick() {
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (toolBallView != null) windowManager.removeView(toolBallView);
    }

    private class TouchListener implements View.OnTouchListener {

        private int lastAction;
        private int initialY;
        private float initialTouchY;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // initial position.
                    initialY = params.y;
                    // touch location
                    initialTouchY = event.getRawY();
                    lastAction = event.getAction();
                    return true;
                case MotionEvent.ACTION_UP:
                    if (lastAction == MotionEvent.ACTION_DOWN) {
                        FloatingToolService.start(ToolBallService.this);
                        stopSelf();
                    }
                    lastAction = event.getAction();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    params.y = initialY + (int) (event.getRawY() - initialTouchY);
                    windowManager.updateViewLayout(toolBallView, params);
                    lastAction = event.getAction();
                    return true;
            }
            return false;
        }
    }
}
