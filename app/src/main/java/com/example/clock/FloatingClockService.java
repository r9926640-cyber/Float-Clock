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
    
    private int currentOpacity = -1;
    private int currentTextSize = -1;
    private int currentTextOpacity = -1; // NEW

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "clock_channel", "Clock Service", NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            Notification notification = new Notification.Builder(this, "clock_channel")
                    .setContentTitle("Floating Clock")
                    .setContentText("Running...")
                    .setSmallIcon(android.R.drawable.ic_menu_today)
                    .build();

            if (Build.VERSION.SDK_INT >= 34) {
                startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
            } else {
                startForeground(1, notification);
            }
        } else {
            Notification notification = new Notification.Builder(this)
                    .setContentTitle("Floating Clock")
                    .setContentText("Running...")
                    .setSmallIcon(android.R.drawable.ic_menu_today)
                    .build();
            startForeground(1, notification);
        }

        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_clock, null);
        clockText = floatingView.findViewById(R.id.clock_text);

        int layoutFlag = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? 
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : 
                WindowManager.LayoutParams.TYPE_PHONE;

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutFlag,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.y = 150; 

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(floatingView, params);

        floatingView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(floatingView, params);
                        return true;
                }
                return false;
            }
        });

        handler = new Handler(Looper.getMainLooper());
        updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = getSharedPreferences("ClockPrefs", MODE_PRIVATE);
                
                // 1. Time Update
                boolean useAmPm = prefs.getBoolean("useAmPm", false);
                String formatPattern = useAmPm ? "hh:mm:ss a" : "HH:mm:ss";
                SimpleDateFormat sdf = new SimpleDateFormat(formatPattern, Locale.getDefault());
                clockText.setText(sdf.format(new Date()));
                
                // 2. Background Opacity Update
                int prefOpacity = prefs.getInt("bgOpacity", 50);
                if (prefOpacity != currentOpacity) {
                    currentOpacity = prefOpacity;
                    int alpha = (int) ((currentOpacity / 100f) * 255);
                    floatingView.setBackgroundColor(Color.argb(alpha, 0, 0, 0));
                }
                
                // 3. Text Size Update
                int prefSize = prefs.getInt("textSize", 24);
                if (prefSize != currentTextSize) {
                    currentTextSize = prefSize;
                    clockText.setTextSize(TypedValue.COMPLEX_UNIT_SP, currentTextSize);
                }

                // 4. Text Transparency Update
                int prefTextOpacity = prefs.getInt("textOpacity", 100);
                if (prefTextOpacity != currentTextOpacity) {
                    currentTextOpacity = prefTextOpacity;
                    // setAlpha takes a float from 0.0 to 1.0 (0 is invisible, 1 is fully visible)
                    clockText.setAlpha(currentTextOpacity / 100f);
                }

                handler.postDelayed(this, 1000); 
            }
        };
        handler.post(updateTimeRunnable);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; 
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        if (floatingView != null) windowManager.removeView(floatingView);
        if (handler != null) handler.removeCallbacks(updateTimeRunnable);
    }
}
