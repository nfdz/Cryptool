package io.github.nfdz.cryptool.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.nfdz.cryptool.R;
import io.github.nfdz.cryptool.utils.BroadcastUtils;
import io.github.nfdz.cryptool.utils.PreferencesUtils;

public class ToolBallService extends Service {

    public static void start(Context context) {
        context.startService(new Intent(context, ToolBallService.class));
    }

    public static int RIGHT_GRAVITY = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
    public static int LEFT_GRAVITY = Gravity.CENTER_VERTICAL | Gravity.LEFT;

    private WindowManager windowManager;
    private View toolBallView;
    private WindowManager.LayoutParams params;
    private float touchXThreshold;
    private BroadcastReceiver receiver;

    @BindView(R.id.iv_tool_ball) View toolBallImage;
    @BindView(R.id.iv_close) View close;

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
        touchXThreshold = getXThreshold();
        params = setupParams();
        windowManager.addView(toolBallView, params);
        ButterKnife.bind(this, toolBallView);
        toolBallImage.setOnTouchListener(new TouchListener());
        setBroadcastReceiver();
        playEnterAnimation();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        if (toolBallView != null) windowManager.removeView(toolBallView);
        super.onDestroy();
    }

    private void setBroadcastReceiver() {
        receiver = new CloseBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastUtils.CLOSE_FLOATING_WINDOWS_ACTION);
        registerReceiver(receiver, filter);
    }

    private float getXThreshold() {
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return width/2f;
    }

    private WindowManager.LayoutParams setupParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = PreferencesUtils.getToolBallLastGravity(this);
        params.y = PreferencesUtils.getToolBallLastPosition(this);

        return params;
    }

    private void playEnterAnimation() {
        final int fromXscale = 0;
        final int toXscale = 1;
        final int fromYscale = 0;
        final int toYscale = 1;

        Animation ballScale = new ScaleAnimation(fromXscale,
                toXscale, fromYscale,
                toYscale, Animation.
                RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        ballScale.setDuration(200); // 0.2s

        final Animation closeScale = new ScaleAnimation(fromXscale,
                toXscale,
                fromYscale,
                toYscale,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        closeScale.setDuration(150); // 0.15s

        ballScale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                toolBallImage.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                close.setVisibility(View.VISIBLE);
                close.startAnimation(closeScale);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        toolBallImage.startAnimation(ballScale);
    }

    private void playExitAnimation(final Runnable callback) {
        final int fromXscale = 1;
        final int toXscale = 0;
        final int fromYscale = 1;
        final int toYscale = 0;

        final Animation ballScale = new ScaleAnimation(fromXscale,
                toXscale, fromYscale,
                toYscale, Animation.
                RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        ballScale.setDuration(100); // 0.1s

        Animation closeScale = new ScaleAnimation(fromXscale,
                toXscale,
                fromYscale,
                toYscale,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        closeScale.setDuration(100); // 0.1s

        ballScale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                toolBallImage.setVisibility(View.INVISIBLE);
                callback.run();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        closeScale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                close.setVisibility(View.INVISIBLE);
                toolBallImage.startAnimation(ballScale);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        close.startAnimation(closeScale);
    }

    @OnClick(R.id.iv_close)
    void onCloseClick() {
        PreferencesUtils.setToolBallLastPosition(ToolBallService.this, params.y);
        PreferencesUtils.setToolBallLastGravity(ToolBallService.this, params.gravity);
        playExitAnimation(new Runnable() {
            @Override
            public void run() {
                stopSelf();
            }
        });
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
                        PreferencesUtils.setToolBallLastPosition(ToolBallService.this, params.y);
                        PreferencesUtils.setToolBallLastGravity(ToolBallService.this, params.gravity);
                        playExitAnimation(new Runnable() {
                            @Override
                            public void run() {
                                FloatingToolService.start(ToolBallService.this);
                                stopSelf();
                            }
                        });
                    }
                    lastAction = event.getAction();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (event.getRawX() >= touchXThreshold && params.gravity == LEFT_GRAVITY) {
                        params.gravity = RIGHT_GRAVITY;
                    } else if (event.getRawX() < touchXThreshold && params.gravity == RIGHT_GRAVITY) {
                        params.gravity = LEFT_GRAVITY;
                    }
                    params.y = initialY + (int) (event.getRawY() - initialTouchY);
                    windowManager.updateViewLayout(toolBallView, params);
                    lastAction = event.getAction();
                    return true;
            }
            return false;
        }
    }

    private class CloseBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            onCloseClick();
        }
    }
}
