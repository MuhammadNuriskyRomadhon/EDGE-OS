package com.edgeos.assistive;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.edgeos.launcher.LauncherActivity;
import com.edgeos.launcher.R;

public final class AssistiveOverlayService extends Service {
    private static final String CHANNEL = "edge_assistive";
    private static final int NOTIFICATION_ID = 8101;
    private WindowManager wm;
    private BubbleView bubble;
    private WindowManager.LayoutParams params;

    @Override public void onCreate() {
        super.onCreate();
        createChannel();
        startForeground(NOTIFICATION_ID, buildNotification());
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) { stopSelf(); return; }
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        bubble = new BubbleView();
        params = new WindowManager.LayoutParams(dp(58), dp(58),
                Build.VERSION.SDK_INT >= 26 ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                android.graphics.PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        params.x = dp(12);
        params.y = 0;
        wm.addView(bubble, params);
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) { return START_STICKY; }

    @Override public void onDestroy() {
        if (wm != null && bubble != null) { try { wm.removeView(bubble); } catch (Exception ignored) {} }
        bubble = null;
        super.onDestroy();
    }

    @Override public IBinder onBind(Intent intent) { return null; }

    private Notification buildNotification() {
        Intent open = new Intent(this, AssistiveSettingsActivity.class);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= 23) flags |= PendingIntent.FLAG_IMMUTABLE;
        PendingIntent pi = PendingIntent.getActivity(this, 0, open, flags);
        Notification.Builder b = Build.VERSION.SDK_INT >= 26 ? new Notification.Builder(this, CHANNEL) : new Notification.Builder(this);
        return b.setSmallIcon(R.drawable.ic_edge_launcher).setContentTitle("EDGE Assistive Touch")
                .setContentText("Local floating control is active").setOngoing(true).setContentIntent(pi).build();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel c = new NotificationChannel(CHANNEL, "EDGE Assistive", NotificationManager.IMPORTANCE_LOW);
            c.setDescription("Persistent notification for the local assistive overlay");
            ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(c);
        }
    }

    private void openLauncher() {
        Intent i = new Intent(this, LauncherActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    private int dp(int v) { return (int)(v * getResources().getDisplayMetrics().density + .5f); }

    private final class BubbleView extends View {
        private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        private boolean expanded;
        private float downX, downY, startX, startY;
        private long downTime;

        BubbleView() { super(AssistiveOverlayService.this); p.setTypeface(Typeface.create("sans", Typeface.BOLD)); setLayerType(View.LAYER_TYPE_SOFTWARE, null); }

        @Override protected void onDraw(Canvas c) {
            super.onDraw(c);
            float cx = getWidth()/2f, cy = getHeight()/2f;
            p.setShadowLayer(dp(5), 0, dp(2), 0x55000000);
            p.setColor(0xDDF7F7FA);
            c.drawCircle(cx, cy, dp(25), p);
            p.clearShadowLayer();
            p.setColor(0xFF202124); p.setTextSize(dp(16)); p.setTextAlign(Paint.Align.CENTER);
            c.drawText(expanded ? "×" : "E", cx, cy + dp(6), p);
            if (expanded) drawMenu(c);
        }

        private void drawMenu(Canvas c) {
            // Menu is drawn around the same lightweight overlay view; no blur or continuous animation.
            float cx=getWidth()/2f, cy=getHeight()/2f, r=dp(72), s=dp(23);
            drawAction(c, cx-r, cy, "⌂");
            drawAction(c, cx+r, cy, "⚙");
            drawAction(c, cx, cy-r, "CC");
            drawAction(c, cx, cy+r, "−");
        }

        private void drawAction(Canvas c, float x, float y, String text) {
            p.setColor(0xE6FFFFFF); c.drawCircle(x,y,dp(22),p);
            p.setColor(0xFF202124); p.setTextSize(dp(14)); p.setTextAlign(Paint.Align.CENTER); c.drawText(text,x,y+dp(5),p);
        }

        @Override public boolean onTouchEvent(MotionEvent e) {
            switch(e.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    downX=e.getRawX(); downY=e.getRawY(); startX=params.x; startY=params.y; downTime=android.os.SystemClock.uptimeMillis(); return true;
                case MotionEvent.ACTION_MOVE:
                    float dx=e.getRawX()-downX, dy=e.getRawY()-downY;
                    if (Math.abs(dx)+Math.abs(dy) > dp(8)) {
                        params.x=(int)(startX-dx); params.y=(int)(startY+dy);
                        try { wm.updateViewLayout(this, params); } catch (Exception ignored) {}
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    long dt=android.os.SystemClock.uptimeMillis()-downTime;
                    float md=Math.abs(e.getRawX()-downX)+Math.abs(e.getRawY()-downY);
                    if (dt < 400 && md < dp(12)) handleTap(e.getX(),e.getY());
                    return true;
            }
            return true;
        }

        private void handleTap(float x,float y) {
            if (!expanded) { expanded=true; setLayoutExpanded(); invalidate(); return; }
            float cx=getWidth()/2f, cy=getHeight()/2f, dx=x-cx, dy=y-cy;
            if (Math.hypot(dx,dy) < dp(35)) { expanded=false; setLayoutCollapsed(); invalidate(); return; }
            if (dx < -dp(35) && Math.abs(dy)<dp(45)) openLauncher();
            else if (dx > dp(35) && Math.abs(dy)<dp(45)) startActivity(new Intent(AssistiveOverlayService.this, AssistiveSettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            else if (Math.abs(dx)<dp(55) && dy < -dp(35)) {
                startActivity(new Intent(AssistiveOverlayService.this,
                        com.edgeos.control.ControlCenterActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
            else if (Math.abs(dx)<dp(45) && dy > dp(35)) stopSelf();
            expanded=false; setLayoutCollapsed(); invalidate();
        }

        private void setLayoutExpanded() {
            params.width=dp(220); params.height=dp(220); params.x=Math.max(0,params.x-dp(160));
            try { wm.updateViewLayout(this,params); } catch(Exception ignored) {}
        }
        private void setLayoutCollapsed() {
            params.width=dp(58); params.height=dp(58); params.x=Math.max(0,params.x+dp(160));
            try { wm.updateViewLayout(this,params); } catch(Exception ignored) {}
        }
    }
}
