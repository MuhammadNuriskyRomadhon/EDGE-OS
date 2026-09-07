package com.edgeos.m1;

import android.app.*; import android.os.*; import android.content.*; import android.content.pm.PackageManager; import android.graphics.Color; import android.graphics.drawable.GradientDrawable; import android.view.*; import android.widget.*; import java.util.*;

public class HomeActivity extends Activity {
    LinearLayout root, grid, dock; LauncherStore store; List<AppInfo> apps; BroadcastReceiver packageReceiver;
    int page=0; static final int COLS=4, ROWS=5, PER_PAGE=20;
    @Override public void onCreate(Bundle b){super.onCreate(b); getWindow().setStatusBarColor(Color.TRANSPARENT); getWindow().setNavigationBarColor(Color.BLACK); store=new LauncherStore(this); build(); registerPackageReceiver();}
    void build(){
        root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(10),dp(28),dp(10),dp(12));
        root.setBackgroundColor(Color.rgb(18,19,23)); setContentView(root); render();
        root.setOnTouchListener(new View.OnTouchListener(){float x; public boolean onTouch(View v,android.view.MotionEvent e){if(e.getAction()==0)x=e.getX(); if(e.getAction()==1){float dx=e.getX()-x;if(Math.abs(dx)>dp(70)){if(dx<0) page++; else page--; render(); return true;}} return true;}});
    }
    void render(){
        apps=AppRepository.query(this); List<String> saved=store.items(); LinkedHashMap<String,AppInfo> byKey=new LinkedHashMap<>(); for(AppInfo a:apps)byKey.put(key(a),a);
        ArrayList<AppInfo> ordered=new ArrayList<>(); for(String k:saved){AppInfo a=byKey.remove(k); if(a!=null)ordered.add(a);} ordered.addAll(byKey.values());
        ArrayList<AppInfo> dockApps=new ArrayList<>(); for(String k:store.dock()){AppInfo a=find(ordered,k); if(a!=null)dockApps.add(a);} if(dockApps.isEmpty()) for(int i=0;i<Math.min(4,ordered.size());i++)dockApps.add(ordered.get(i));
        if(page<0)page=0; int pages=Math.max(1,(ordered.size()+PER_PAGE-1)/PER_PAGE); if(page>=pages)page=pages-1;
        root.removeAllViews(); TextView title=new TextView(this); title.setText("EDGEOS"); title.setTextColor(Color.WHITE); title.setTextSize(28); title.setTypeface(null, android.graphics.Typeface.BOLD); title.setGravity(Gravity.CENTER); root.addView(title,new LinearLayout.LayoutParams(-1,dp(48)));
        grid=new LinearLayout(this); grid.setOrientation(LinearLayout.VERTICAL); grid.setGravity(Gravity.CENTER); root.addView(grid,new LinearLayout.LayoutParams(-1,0,1));
        int start=page*PER_PAGE; for(int r=0;r<ROWS;r++){LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER);grid.addView(row,new LinearLayout.LayoutParams(-1,0,1)); for(int c=0;c<COLS;c++){int idx=start+r*COLS+c; if(idx<ordered.size())row.addView(iconView(ordered.get(idx)),new LinearLayout.LayoutParams(0,-1,1)); else row.addView(new Space(this),new LinearLayout.LayoutParams(0,-1,1));}}
        TextView dots=new TextView(this); dots.setText(makeDots(pages)); dots.setTextColor(Color.LTGRAY); dots.setGravity(Gravity.CENTER); root.addView(dots,new LinearLayout.LayoutParams(-1,dp(24)));
        dock=new LinearLayout(this);dock.setGravity(Gravity.CENTER);dock.setPadding(dp(6),dp(6),dp(6),dp(6));GradientDrawable bg=new GradientDrawable();bg.setColor(0x332F3138);bg.setCornerRadius(dp(28));dock.setBackground(bg);root.addView(dock,new LinearLayout.LayoutParams(-1,dp(78)));for(AppInfo a:dockApps)dock.addView(iconView(a),new LinearLayout.LayoutParams(0,-1,1));
    }
    View iconView(AppInfo a){LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setGravity(Gravity.CENTER);ImageView iv=new ImageView(this);iv.setImageDrawable(a.icon);iv.setScaleType(ImageView.ScaleType.FIT_CENTER);box.addView(iv,new LinearLayout.LayoutParams(-1,0,1));TextView t=new TextView(this);t.setText(a.label);t.setTextColor(Color.WHITE);t.setTextSize(11);t.setSingleLine(true);t.setEllipsize(android.text.TextUtils.TruncateAt.END);t.setGravity(Gravity.CENTER);box.addView(t,new LinearLayout.LayoutParams(-1,dp(18)));box.setOnClickListener(v->launch(a));return box;}
    void launch(AppInfo a){try{Intent i=new Intent(Intent.ACTION_MAIN);i.setClassName(a.packageName,a.activityName);i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);startActivity(i);}catch(Exception e){Toast.makeText(this,"Tidak bisa membuka "+a.label,Toast.LENGTH_SHORT).show();}}
    String key(AppInfo a){return a.packageName+"/"+a.activityName;} AppInfo find(List<AppInfo> l,String k){for(AppInfo a:l)if(key(a).equals(k))return a;return null;} String makeDots(int n){StringBuilder s=new StringBuilder();for(int i=0;i<n;i++)s.append(i==page?'●':'○');return s.toString();} int dp(int v){return Math.round(v*getResources().getDisplayMetrics().density);}
    void registerPackageReceiver(){packageReceiver=new BroadcastReceiver(){public void onReceive(Context c,Intent i){render();}};IntentFilter f=new IntentFilter();f.addAction(Intent.ACTION_PACKAGE_ADDED);f.addAction(Intent.ACTION_PACKAGE_REMOVED);f.addAction(Intent.ACTION_PACKAGE_CHANGED);f.addDataScheme("package");if(Build.VERSION.SDK_INT>=33)registerReceiver(packageReceiver,f,Context.RECEIVER_EXPORTED);else registerReceiver(packageReceiver,f);}
    @Override protected void onDestroy(){if(packageReceiver!=null)try{unregisterReceiver(packageReceiver);}catch(Exception ignored){}super.onDestroy();}
}
