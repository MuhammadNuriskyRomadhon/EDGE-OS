package com.edgeos.m1;

import android.content.*; import android.content.pm.*; import java.util.*;

final class AppRepository {
    static List<AppInfo> query(Context c){
        PackageManager pm=c.getPackageManager(); Intent q=new Intent(Intent.ACTION_MAIN); q.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> ris=pm.queryIntentActivities(q,PackageManager.MATCH_ALL); ArrayList<AppInfo> out=new ArrayList<>();
        String own=c.getPackageName();
        for(ResolveInfo r:ris){ if(r.activityInfo==null || own.equals(r.activityInfo.packageName)) continue;
            String label=r.loadLabel(pm).toString(); DrawableHolder h=new DrawableHolder(r.loadIcon(pm));
            out.add(new AppInfo(r.activityInfo.packageName,r.activityInfo.name,label,h.icon)); }
        out.sort(Comparator.comparing(x->x.label.toLowerCase(Locale.ROOT))); return out;
    }
    static final class DrawableHolder { final android.graphics.drawable.Drawable icon; DrawableHolder(android.graphics.drawable.Drawable i){icon=i;} }
}
