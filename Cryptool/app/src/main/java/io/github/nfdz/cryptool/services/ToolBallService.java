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

import io.github.nfdz.cryptool.R;
import io.github.nfdz.cryptool.views.MainActivity;

public class ToolBallService extends Service {

    public static void start(Context context) {
        context.startService(new Intent(context, ToolBallService.class));
    }

    private WindowManager windowManager;
    private View toolBallView;
    private WindowManager.LayoutParams params;

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
        setCloseButtonListener();
        setToolBallListener();
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

    private void setCloseButtonListener() {
        View closeButton = toolBallView.findViewById(R.id.iv_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();
            }
        });
    }

    private void setToolBallListener() {
        final View toolBallImage = toolBallView.findViewById(R.id.iv_tool_ball);
        toolBallImage.setOnTouchListener(new View.OnTouchListener() {
            private int lastAction;
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // initial position.
                        initialX = params.x;
                        initialY = params.y;
                        // touch location
                        initialTouchX = event.getRawX();
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
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(toolBallView, params);
                        lastAction = event.getAction();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (toolBallView != null) windowManager.removeView(toolBallView);
    }
}
