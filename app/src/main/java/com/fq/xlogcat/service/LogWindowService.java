package com.fq.xlogcat.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fq.xlogcat.R;
import com.fq.xlogcat.logcat.VirtualTerminal;

/**
 * Created by fanqi on 2019-07-05.
 * Description:
 */
public class LogWindowService extends Service {

    private static final String TAG = LogWindowService.class.getCanonicalName();

    private ConstraintLayout toucherLayout;
    private WindowManager.LayoutParams params;
    private WindowManager windowManager;

    private int statusBarHeight = -1;

    private ImageView touchBtn;
    private ImageView closeBtn, clearBtn, toBottomBtn;
    private TextView tvLog;
    private ScrollView scrollView;

    private float lastX;
    private float lastY;
    private float nowX;
    private float nowY;
    private float tranX;
    private float tranY;

    private Handler handler;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createFloatView();
        new VirtualTerminal(this).execute();
        handler = new Handler();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createFloatView() {
        params = new WindowManager.LayoutParams();
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        //设置效果为背景透明.
        params.format = PixelFormat.RGBA_8888;
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        //设置窗口初始停靠位置.
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.x = 0;
        params.y = 0;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局.
        toucherLayout = (ConstraintLayout) inflater.inflate(R.layout.overlay_log_view, null);
        touchBtn = toucherLayout.findViewById(R.id.touch_btn);
        closeBtn = toucherLayout.findViewById(R.id.close);
        tvLog = toucherLayout.findViewById(R.id.tv_log);
        clearBtn = toucherLayout.findViewById(R.id.clear);
        toBottomBtn = toucherLayout.findViewById(R.id.to_bottom);
        scrollView = toucherLayout.findViewById(R.id.scroView);


        //设置悬浮窗口长宽数据.
        Log.d(TAG, "window-->width:" + display.getWidth());
        Log.d(TAG, "toucherlayout-->width:" + toucherLayout.getWidth());
        Log.d(TAG, "toucherlayout-->height:" + toucherLayout.getHeight());
        params.width = display.getWidth() / 3 * 2;
        params.height = display.getHeight() / 2;

        //添加toucherlayout
        windowManager.addView(toucherLayout, params);

        Log.d(TAG, "toucherlayout-->left:" + toucherLayout.getLeft());
        Log.d(TAG, "toucherlayout-->right:" + toucherLayout.getRight());
        Log.d(TAG, "toucherlayout-->top:" + toucherLayout.getTop());
        Log.d(TAG, "toucherlayout-->bottom:" + toucherLayout.getBottom());

        //主动计算出当前View的宽高信息.
        toucherLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        //用于检测状态栏高度.
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        closeBtn.setOnClickListener(new View.OnClickListener() {

            long[] hints = new long[2];

            @Override
            public void onClick(View v) {

                System.arraycopy(hints, 1, hints, 0, hints.length - 1);
                hints[hints.length - 1] = SystemClock.uptimeMillis();
                if (SystemClock.uptimeMillis() - hints[0] >= 700) {
                    Toast.makeText(LogWindowService.this, "连续点击两次以退出", Toast.LENGTH_SHORT).show();
                } else {
                    stopSelf();
                }
            }
        });

        touchBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean ret = false;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 获取按下时的X，Y坐标
                        lastX = event.getRawX();
                        lastY = event.getRawY();
                        ret = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 获取移动时的X，Y坐标
                        nowX = event.getRawX();
                        nowY = event.getRawY();
                        // 计算XY坐标偏移量
                        tranX = nowX - lastX;
                        tranY = nowY - lastY;
                        // 移动悬浮窗
                        params.x += tranX;
                        params.y += tranY;
                        //更新悬浮窗位置
                        windowManager.updateViewLayout(toucherLayout, params);
                        //记录当前坐标作为下一次计算的上一次移动的位置坐标
                        lastX = nowX;
                        lastY = nowY;
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    default:
                        break;
                }
                return ret;

            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvLog.setText("");
            }
        });

        toBottomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });
    }


    public void refreshLog(String linLog) {
        tvLog.append(linLog + "\n");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        if (toucherLayout != null) {
            windowManager.removeView(toucherLayout);
        }
        super.onDestroy();
    }


}
