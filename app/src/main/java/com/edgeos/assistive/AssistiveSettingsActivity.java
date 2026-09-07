package com.edgeos.assistive;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.edgeos.launcher.R;
import com.edgeos.lockscreen.LockScreenActivity;

public final class AssistiveSettingsActivity extends Activity {
    private TextView status;
    @Override public void onCreate(Bundle state){super.onCreate(state); setTitle("EDGE OS Settings");
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(24),dp(28),dp(24),dp(24));root.setGravity(Gravity.CENTER_HORIZONTAL);
        TextView title=new TextView(this);title.setText("EDGE OS");title.setTextSize(26);title.setTextColor(0xFF111111);root.addView(title,lp(-1,-2));
        status=new TextView(this);status.setTextSize(14);status.setPadding(0,dp(12),0,dp(16));root.addView(status,lp(-1,-2));
        add(root,"Allow display over other apps",v->openOverlaySettings());
        add(root,"Start Assistive Touch",v->startAssistive()); add(root,"Stop Assistive Touch",v->stopService(new Intent(this,AssistiveOverlayService.class)));
        add(root,"Open Control Center",v->startActivity(new Intent(this,com.edgeos.control.ControlCenterActivity.class)));
        add(root,"Open EDGE Lock Screen",v->startActivity(new Intent(this,LockScreenActivity.class)));
        add(root,"Notification access",v->startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)));
        add(root,"About Android limitations",v->status.setText("EDGE OS uses safe Android APIs. The custom lock screen is an experience layer, not a replacement for Android secure authentication. Accessibility global actions remain disabled."));
        setContentView(root);refresh(); }
    @Override protected void onResume(){super.onResume();refresh();}
    private void refresh(){boolean allowed=Build.VERSION.SDK_INT<23||Settings.canDrawOverlays(this);status.setText(allowed?"Overlay permission: allowed":"Overlay permission: required");}
    private void openOverlaySettings(){if(Build.VERSION.SDK_INT>=23){Intent i=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName()));try{startActivity(i);}catch(Exception ignored){startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));}}}
    private void startAssistive(){if(Build.VERSION.SDK_INT>=23&&!Settings.canDrawOverlays(this)){openOverlaySettings();return;}Intent i=new Intent(this,AssistiveOverlayService.class);if(Build.VERSION.SDK_INT>=26)startForegroundService(i);else startService(i);finish();}
    private void add(LinearLayout root,String text,android.view.View.OnClickListener l){Button b=new Button(this);b.setText(text);b.setAllCaps(false);b.setOnClickListener(l);root.addView(b,lp(-1,-2));}
    private LinearLayout.LayoutParams lp(int w,int h){return new LinearLayout.LayoutParams(w,h);}private int dp(int v){return(int)(v*getResources().getDisplayMetrics().density+.5f);}
}
