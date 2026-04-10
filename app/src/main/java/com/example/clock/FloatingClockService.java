package com.example.clock;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ServiceInfo;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FloatingClockService extends Service {
    public static boolean isRunning = false;
    private WindowManager windowManager;
    private View floatingView;
    private TextView clockText;
    private Handler handler;
    private Runnable updateTimeRunnable;
    
    private int curOpacity = -1, curSize = -1, curTextOpacity = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        setupNotification();

        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_clock, null);
        clockText = floatingView.findViewById(R.id.clock_text);

        int layoutFlag = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? 
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE;

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                layoutFlag, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.y = 150; 

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(floatingView, params);

        setupTouchListener(params);

        handler = new Handler(Looper.getMainLooper());
        updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = getSharedPreferences("ClockPrefs", MODE_PRIVATE);
                
                // Format logic with MS support
                boolean useAmPm = prefs.getBoolean("useAmPm", false);
                boolean useMs = prefs.getBoolean("useMs", false);
                String pattern = (useAmPm ? "hh:mm:ss" : "HH:mm:ss") + (useMs ? ".SSS" : "") + (useAmPm ? " a" : "");
                
                clockText.setText(new SimpleDateFormat(pattern, Locale.getDefault()).format(new Date()));
                
                updateVisuals(prefs);

                // Update every 30ms for smooth milliseconds, or 1000ms if just seconds
                handler.postDelayed(this, useMs ? 30 : 1000); 
            }
        };
        handler.post(updateTimeRunnable);
    }

    private void setupNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel("clock_ch", "Clock", NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(ch);
            Notification n = new Notification.Builder(this, "clock_ch").setSmallIcon(android.R.drawable.ic_menu_today).build();
            if (Build.VERSION.SDK_INT >= 34) startForeground(1, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
            else startForeground(1, n);
        }
    }

    private void setupTouchListener(WindowManager.LayoutParams p) {
        floatingView.setOnTouchListener(new View.OnTouchListener() {
            private int x, y; private float tx, ty;
            @Override public boolean onTouch(View v, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) { x = p.x; y = p.y; tx = e.getRawX(); ty = e.getRawY(); }
                else if (e.getAction() == MotionEvent.ACTION_MOVE) {
                    p.x = x + (int)(e.getRawX()-tx); p.y = y + (int)(e.getRawY()-ty);
                    windowManager.updateViewLayout(floatingView, p);
                }
                return true;
            }
        });
    }

    private void updateVisuals(SharedPreferences p) {
        int op = p.getInt("bgOpacity", 50);
        if (op != curOpacity) { curOpacity = op; floatingView.setBackgroundColor(Color.argb((int)(op/100f*255), 0,0,0)); }
        int sz = p.getInt("textSize", 24);
        if (sz != curSize) { curSize = sz; clockText.setTextSize(TypedValue.COMPLEX_UNIT_SP, sz); }
        int top = p.getInt("textOpacity", 100);
        if (top != curTextOpacity) { curTextOpacity = top; clockText.setAlpha(top/100f); }
    }

    @Override public IBinder onBind(Intent i) { return null; }
    @Override public void onDestroy() { super.onDestroy(); isRunning = false; 
        if(floatingView != null) windowManager.removeView(floatingView);
        handler.removeCallbacks(updateTimeRunnable);
    }
}
