package com.edgeos.launcher;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import com.edgeos.assistive.AssistiveSettingsActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class LauncherView extends View {
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private final PackageManager pm;
    private final List<AppEntry> apps = new ArrayList<>();
    private final List<AppEntry> dock = new ArrayList<>();
    private float downX, downY;
    private long downTime;
    private int page = 0;
    private int pageCount = 1;
    private static final int COLS = 4;
    private static final int ROWS = 5;
    private static final int PER_PAGE = COLS * ROWS;

    public LauncherView(Context c) {
        super(c);
        pm = c.getPackageManager();
        setFocusable(true);
        loadApps();
    }

    private void loadApps() {
        Intent q = new Intent(Intent.ACTION_MAIN);
        q.addCategory(Intent.CATEGORY_LAUNCHER);
        List<android.content.pm.ResolveInfo> list = pm.queryIntentActivities(q, 0);
        apps.clear();
        for (android.content.pm.ResolveInfo r : list) {
            if (r.activityInfo == null) continue;
            String pkg = r.activityInfo.packageName;
            if (pkg.equals(getContext().getPackageName())) continue;
            CharSequence label = r.loadLabel(pm);
            Drawable icon = r.loadIcon(pm);
            apps.add(new AppEntry(pkg, r.activityInfo.name, label == null ? pkg : label.toString(), icon));
        }
        Collections.sort(apps, Comparator.comparing(a -> a.label.toLowerCase()));
        pageCount = Math.max(1, (apps.size() + PER_PAGE - 1) / PER_PAGE);
        dock.clear();
        addDock("com.android.dialer");
        addDock("com.android.contacts");
        addDock("com.android.mms");
        addDock("com.android.camera2");
        invalidate();
    }

    private void addDock(String packageName) {
        for (AppEntry a : apps) if (a.packageName.equals(packageName)) { if (dock.size() < 4) dock.add(a); return; }
        if (dock.size() >= 4) return;
        for (AppEntry a : apps) {
            if (dock.size() >= 4) break;
            boolean exists = false;
            for (AppEntry d : dock) if (d.packageName.equals(a.packageName)) exists = true;
            if (!exists) dock.add(a);
        }
    }

    @Override protected void onDraw(Canvas c) {
        super.onDraw(c);
        final int w = getWidth(), h = getHeight();
        // Lightweight layered background; no continuous blur or shader animation.
        c.drawColor(Color.rgb(28, 30, 34));
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.argb(35, 255, 255, 255));
        c.drawCircle(w * 0.18f, h * 0.18f, Math.min(w,h) * 0.42f, p);
        p.setColor(Color.argb(24, 80, 130, 255));
        c.drawCircle(w * 0.86f, h * 0.62f, Math.min(w,h) * 0.48f, p);

        drawTop(c, w);
        drawApps(c, w, h);
        drawDock(c, w, h);
        drawPageDots(c, w, h);
    }

    private void drawTop(Canvas c, int w) {
        p.setColor(Color.WHITE);
        p.setTextSize(dp(16)); p.setTypeface(Typeface.create("sans", Typeface.BOLD));
        p.setTextAlign(Paint.Align.LEFT);
        String time = android.text.format.DateFormat.format("HH:mm", System.currentTimeMillis()).toString();
        c.drawText(time, dp(20), dp(28), p);
        p.setTextAlign(Paint.Align.RIGHT);
        p.setTypeface(Typeface.create("sans", Typeface.NORMAL));
        c.drawText("EDGE", w - dp(20), dp(28), p);
        p.setTextAlign(Paint.Align.LEFT);
    }

    private void drawApps(Canvas c, int w, int h) {
        float top = dp(60), bottom = h - dp(150);
        float cellW = w / (float) COLS;
        float cellH = (bottom - top) / ROWS;
        int start = page * PER_PAGE;
        int end = Math.min(apps.size(), start + PER_PAGE);
        for (int i=start; i<end; i++) {
            int local = i-start, col = local % COLS, row = local / COLS;
            float cx = cellW * (col + .5f);
            float cy = top + cellH * (row + .42f);
            drawIcon(c, apps.get(i), cx, cy, dp(56));
            p.setColor(Color.WHITE); p.setTextSize(dp(11)); p.setTypeface(Typeface.create("sans", Typeface.NORMAL));
            p.setTextAlign(Paint.Align.CENTER);
            c.drawText(shortLabel(apps.get(i).label), cx, cy + dp(42), p);
        }
        p.setTextAlign(Paint.Align.LEFT);
    }

    private void drawDock(Canvas c, int w, int h) {
        float left = dp(12), right = w - dp(12), top = h - dp(126), bot = h - dp(42);
        p.setColor(Color.argb(125, 245,245,250));
        c.drawRoundRect(new RectF(left, top, right, bot), dp(28), dp(28), p);
        int n = Math.max(1, dock.size());
        for (int i=0;i<dock.size();i++) {
            float cx = left + (right-left) * ((i+.5f)/n);
            drawIcon(c, dock.get(i), cx, (top+bot)/2, dp(48));
        }
    }

    private void drawPageDots(Canvas c, int w, int h) {
        float y = h - dp(22), gap = dp(7), total = (pageCount-1)*gap;
        float start = w/2f - total/2f;
        for (int i=0;i<pageCount;i++) {
            p.setColor(i==page ? Color.WHITE : Color.argb(100,255,255,255));
            c.drawCircle(start + i*gap, y, i==page ? dp(2.6f) : dp(2), p);
        }
    }

    private void drawIcon(Canvas c, AppEntry a, float cx, float cy, float size) {
        Drawable d = a.icon;
        int l=(int)(cx-size/2), t=(int)(cy-size/2), r=(int)(cx+size/2), b=(int)(cy+size/2);
        if (d != null) { d.setBounds(l,t,r,b); d.draw(c); }
    }

    private String shortLabel(String s) { return s.length() > 14 ? s.substring(0,13) + "…" : s; }
    private float dp(float v) { return v * getResources().getDisplayMetrics().density; }

    @Override public boolean onTouchEvent(MotionEvent e) {
        switch(e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downX=e.getX(); downY=e.getY(); downTime=SystemClock.uptimeMillis(); return true;
            case MotionEvent.ACTION_UP:
                float dx=e.getX()-downX, dy=e.getY()-downY;
                long dt=SystemClock.uptimeMillis()-downTime;
                if (Math.abs(dx) > dp(55) && Math.abs(dx) > Math.abs(dy)) {
                    if (dx < 0) page=Math.min(page+1,pageCount-1); else page=Math.max(0,page-1);
                    invalidate(); return true;
                }
                if (dt >= 600 && Math.abs(dx) < dp(24) && Math.abs(dy) < dp(24) && e.getY() < dp(50)) {
                    getContext().startActivity(new Intent(getContext(), AssistiveSettingsActivity.class));
                    return true;
                }
                if (dt < 450 && Math.abs(dx) < dp(24) && Math.abs(dy) < dp(24)) {
                    launchAt(e.getX(), e.getY());
                }
                return true;
        }
        return true;
    }

    private void launchAt(float x,float y) {
        int w=getWidth(), h=getHeight();
        float top=dp(60), bottom=h-dp(150), cellW=w/(float)COLS, cellH=(bottom-top)/ROWS;
        if (y >= top && y < bottom) {
            int col=(int)(x/cellW), row=(int)((y-top)/cellH);
            if(col>=0&&col<COLS&&row>=0&&row<ROWS){
                int idx=page*PER_PAGE+row*COLS+col;
                if(idx<apps.size()) startApp(apps.get(idx));
            }
            return;
        }
        float dockTop=h-dp(126), dockBot=h-dp(42);
        if(y>=dockTop && y<=dockBot && !dock.isEmpty()){
            int slot=(int)(x/(w/(float)dock.size()));
            if(slot>=0&&slot<dock.size()) startApp(dock.get(slot));
        }
    }

    private void startApp(AppEntry a) {
        try {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            i.setClassName(a.packageName, a.activityName);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(i);
        } catch (Exception ignored) {}
    }

    private static final class AppEntry {
        final String packageName, activityName, label; final Drawable icon;
        AppEntry(String p,String a,String l,Drawable i){packageName=p;activityName=a;label=l;icon=i;}
    }
}
