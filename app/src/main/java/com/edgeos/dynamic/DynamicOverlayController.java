package com.edgeos.dynamic;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Event-driven Dynamic Island renderer. No polling and no persistent service. */
public final class DynamicOverlayController {
    private final Context context;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private WindowManager wm;
    private View panel;
    private Runnable hideRunnable;
    private int dp(float v) { return (int)(v * context.getResources().getDisplayMetrics().density + .5f); }
    public DynamicOverlayController(Context c) { context = c.getApplicationContext(); }

    public void show(String title, String text) {
        if (!Settings.canDrawOverlays(context)) return;
        removeImmediate();
        if (wm == null) wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        LinearLayout box = new LinearLayout(context);
        box.setOrientation(LinearLayout.VERTICAL); box.setGravity(Gravity.CENTER_VERTICAL);
        box.setPadding(dp(18), dp(7), dp(18), dp(7));
        GradientDrawable bg = new GradientDrawable(); bg.setColor(0xF018191D); bg.setCornerRadius(dp(26)); box.setBackground(bg);
        box.setAlpha(0f); box.setScaleX(.88f); box.setScaleY(.88f);
        TextView a = textView(title == null || title.isEmpty() ? "Notification" : title, 13, true);
        TextView b = textView(text == null ? "" : text, 11, false);
        box.addView(a, new LinearLayout.LayoutParams(-1, dp(22)));
        if (text != null && !text.isEmpty()) box.addView(b, new LinearLayout.LayoutParams(-1, dp(20)));
        int type = android.os.Build.VERSION.SDK_INT >= 26 ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(dp(300), dp(text != null && !text.isEmpty() ? 58 : 42), type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, android.graphics.PixelFormat.TRANSLUCENT);
        lp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL; lp.y = dp(14);
        try { wm.addView(box, lp); panel = box; } catch (Exception e) { panel = null; return; }
        box.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(170).start();
        hideRunnable = this::hideAnimated; handler.postDelayed(hideRunnable, 3000);
    }
    private TextView textView(String s, int size, boolean bold) {
        TextView v = new TextView(context); v.setText(s); v.setTextColor(Color.WHITE); v.setTextSize(size);
        v.setTypeface(Typeface.DEFAULT, bold ? Typeface.BOLD : Typeface.NORMAL); v.setSingleLine(true);
        v.setEllipsize(android.text.TextUtils.TruncateAt.END); return v;
    }
    private void hideAnimated() {
        final View v = panel; if (v == null) return;
        v.animate().alpha(0f).scaleX(.94f).scaleY(.94f).setDuration(140).setListener(new AnimatorListenerAdapter(){
            @Override public void onAnimationEnd(Animator animation){ removeView(v); }
        }).start();
    }
    private void removeView(View v) { if (wm != null) try { wm.removeView(v); } catch(Exception ignored) {} if(panel==v)panel=null; }
    private void removeImmediate(){ if(hideRunnable!=null)handler.removeCallbacks(hideRunnable); View v=panel; panel=null; if(wm!=null&&v!=null)try{wm.removeView(v);}catch(Exception ignored){} }
    public void destroy(){ handler.removeCallbacksAndMessages(null); removeImmediate(); }
}
