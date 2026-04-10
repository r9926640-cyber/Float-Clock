package com.example.clock;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int NOTIFICATION_PERMISSION_REQ_CODE = 1234;
    private static final int OVERLAY_PERMISSION_REQ_CODE = 1235;
    
    private Button toggleButton;
    private Switch amPmSwitch;
    private Switch msSwitch;
    private SeekBar sizeSeek, opacitySeek, textOpacitySeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(0xFFF5F5F5);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        setContentView(R.layout.activity_main);

        toggleButton = findViewById(R.id.btn_toggle_clock);
        amPmSwitch = findViewById(R.id.switch_am_pm);
        msSwitch = findViewById(R.id.switch_ms);
        sizeSeek = findViewById(R.id.seek_text_size);
        opacitySeek = findViewById(R.id.seek_opacity);
        textOpacitySeek = findViewById(R.id.seek_text_opacity);
        
        final SharedPreferences prefs = getSharedPreferences("ClockPrefs", MODE_PRIVATE);
        
        amPmSwitch.setChecked(prefs.getBoolean("useAmPm", false));
        msSwitch.setChecked(prefs.getBoolean("useMs", false));
        sizeSeek.setProgress(prefs.getInt("textSize", 24) - 12); 
        opacitySeek.setProgress(prefs.getInt("bgOpacity", 50));
        textOpacitySeek.setProgress(prefs.getInt("textOpacity", 100));

        amPmSwitch.setOnCheckedChangeListener((b, isChecked) -> prefs.edit().putBoolean("useAmPm", isChecked).apply());
        msSwitch.setOnCheckedChangeListener((b, isChecked) -> prefs.edit().putBoolean("useMs", isChecked).apply());

        sizeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar s, int p, boolean f) { prefs.edit().putInt("textSize", p + 12).apply(); }
            @Override public void onStartTrackingTouch(SeekBar s) {} @Override public void onStopTrackingTouch(SeekBar s) {}
        });

        opacitySeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar s, int p, boolean f) { prefs.edit().putInt("bgOpacity", p).apply(); }
            @Override public void onStartTrackingTouch(SeekBar s) {} @Override public void onStopTrackingTouch(SeekBar s) {}
        });

        textOpacitySeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar s, int p, boolean f) { prefs.edit().putInt("textOpacity", p).apply(); }
            @Override public void onStartTrackingTouch(SeekBar s) {} @Override public void onStopTrackingTouch(SeekBar s) {}
        });

        toggleButton.setOnClickListener(v -> {
            if (FloatingClockService.isRunning) stopService(new Intent(this, FloatingClockService.class));
            else checkPermissionsAndStart();
            updateButtonState();
        });
    }

    @Override protected void onResume() { super.onResume(); updateButtonState(); }

    private void updateButtonState() {
        if (FloatingClockService.isRunning) {
            toggleButton.setText("Stop Clock");
            toggleButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFF44336));
        } else {
            toggleButton.setText("Start Clock");
            toggleButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF2196F3));
        }
    }

    private void checkPermissionsAndStart() {
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQ_CODE);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), OVERLAY_PERMISSION_REQ_CODE);
            return;
        }
        Intent i = new Intent(this, FloatingClockService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(i); else startService(i);
        updateButtonState();
    }
}
