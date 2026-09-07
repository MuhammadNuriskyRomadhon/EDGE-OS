package com.edgeos.lockscreen;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.edgeos.notification.NotificationStore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** Previewable EDGE lock-screen experience; Android's secure authentication remains authoritative. */
public final class LockScreenActivity extends Activity {
    private TextView clock,date,notifications; private final Handler handler=new Handler();
    private final Runnable tick=()->{refresh();handler.postDelayed(this.tick,30000);};
    @Override protected void onCreate(Bundle b){super.onCreate(b); Window w=getWindow();w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);build();refresh();}
    private void build(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setGravity(Gravity.CENTER_HORIZONTAL);root.setPadding(dp(20),dp(54),dp(20),dp(20));
        GradientDrawable bg=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{0xFF171923,0xFF30384A,0xFF101114});bg.setCornerRadius(0);root.setBackground(bg);
        clock=tv(62,0xFFFFFFFF);date=tv(17,0xFFE5E7EF);notifications=tv(14,0xFFF3F4F7);notifications.setGravity(Gravity.CENTER);
        root.addView(clock,lp(-1,dp(82)));root.addView(date,lp(-1,dp(38)));root.addView(notifications,lp(-1,0,1));
        TextView hint=tv(12,0xFFB5B8C2);hint.setText("EDGE OS • Android security remains authoritative");root.addView(hint,lp(-1,dp(38)));setContentView(root);
    }
    private void refresh(){if(clock==null)return;Date d=new Date();clock.setText(new SimpleDateFormat("HH:mm",Locale.getDefault()).format(d));date.setText(new SimpleDateFormat("EEEE, d MMMM",Locale.getDefault()).format(d));List<NotificationStore.Item> list=NotificationStore.snapshot();StringBuilder s=new StringBuilder();int shown=0;for(int i=list.size()-1;i>=0&&shown<6;i--){NotificationStore.Item n=list.get(i);if(n.title.length()==0&&n.text.length()==0)continue;if(n.title.length()>0)s.append(n.title);if(n.text.length()>0){if(n.title.length()>0)s.append("\n");s.append(n.text);}s.append("\n\n");shown++;}notifications.setText(s.length()==0?"No notifications":s.toString().trim());}
    @Override protected void onResume(){super.onResume();refresh();handler.post(tick);}@Override protected void onPause(){handler.removeCallbacks(tick);super.onPause();}
    private TextView tv(float s,int c){TextView v=new TextView(this);v.setTextSize(s);v.setTextColor(c);v.setGravity(Gravity.CENTER);return v;}
    private LinearLayout.LayoutParams lp(int w,int h){return new LinearLayout.LayoutParams(w,h);}private LinearLayout.LayoutParams lp(int w,int h,float weight){return new LinearLayout.LayoutParams(w,h,weight);}
    private int dp(int v){return(int)(v*getResources().getDisplayMetrics().density+.5f);}
}
