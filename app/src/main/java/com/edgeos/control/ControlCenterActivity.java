package com.edgeos.control;

import android.app.Activity;
import android.graphics.Typeface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public final class ControlCenterActivity extends Activity {
    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + .5f); }

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(242,242,247));
        getWindow().setNavigationBarColor(Color.rgb(242,242,247));
        buildUi();
    }

    private void buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(18), dp(18), dp(18));
        root.setBackgroundColor(Color.rgb(242,242,247));

        TextView title = new TextView(this);
        title.setText("EDGE Control Center");
        title.setTextSize(25);
        title.setTextColor(Color.rgb(25,25,28));
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setTypeface(null, Typeface.BOLD);
        root.addView(title, new LinearLayout.LayoutParams(-1, dp(56)));

        TextView note = new TextView(this);
        note.setText("Local controls and safe Android settings shortcuts");
        note.setTextSize(13);
        note.setTextColor(Color.rgb(90,90,95));
        root.addView(note, new LinearLayout.LayoutParams(-1, dp(42)));

        LinearLayout grid = new LinearLayout(this);
        grid.setOrientation(LinearLayout.VERTICAL);
        addRow(grid, new String[]{"Wi-Fi","Bluetooth","Airplane"});
        addRow(grid, new String[]{"Display","Sound","Settings"});
        root.addView(grid, new LinearLayout.LayoutParams(-1, dp(116)));

        TextView volume = new TextView(this);
        volume.setText("Volume");
        volume.setTextSize(16);
        volume.setTextColor(Color.DKGRAY);
        volume.setPadding(0, dp(18), 0, 0);
        root.addView(volume, new LinearLayout.LayoutParams(-1, dp(44)));

        SeekBar volumeBar = new SeekBar(this);
        AudioManager am=(AudioManager)getSystemService(AUDIO_SERVICE);
        final int max=am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volumeBar.setMax(max);
        volumeBar.setProgress(am.getStreamVolume(AudioManager.STREAM_MUSIC));
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar b,int p,boolean fromUser){ if(fromUser) am.setStreamVolume(AudioManager.STREAM_MUSIC,p,0); }
            public void onStartTrackingTouch(SeekBar b){}
            public void onStopTrackingTouch(SeekBar b){}
        });
        root.addView(volumeBar, new LinearLayout.LayoutParams(-1, dp(48)));

        Button close = new Button(this);
        close.setText("Close");
        close.setOnClickListener(v -> finish());
        LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,dp(52));
        cp.topMargin=dp(20);
        root.addView(close,cp);
        setContentView(root);
    }

    private void addRow(LinearLayout parent, String[] labels) {
        LinearLayout row=new LinearLayout(this);
        row.setGravity(Gravity.CENTER);
        for(String label:labels) {
            Button b=new Button(this);
            b.setText(label);
            b.setAllCaps(false);
            b.setTextSize(12);
            b.setOnClickListener(v -> openSafeSetting(label));
            LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(52),1);
            p.setMargins(dp(4),dp(4),dp(4),dp(4));
            row.addView(b,p);
        }
        parent.addView(row,new LinearLayout.LayoutParams(-1,dp(58)));
    }

    private void openSafeSetting(String label) {
        Intent i;
        if ("Wi-Fi".equals(label)) i=new Intent(Settings.ACTION_WIFI_SETTINGS);
        else if ("Bluetooth".equals(label)) i=new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        else if ("Airplane".equals(label)) i=new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
        else if ("Display".equals(label)) i=new Intent(Settings.ACTION_DISPLAY_SETTINGS);
        else if ("Sound".equals(label)) i=new Intent(Settings.ACTION_SOUND_SETTINGS);
        else i=new Intent(Settings.ACTION_SETTINGS);
        try { startActivity(i); } catch (Exception ignored) {}
    }
}
