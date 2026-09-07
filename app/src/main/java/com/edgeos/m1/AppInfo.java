package com.edgeos.m1;

import android.graphics.drawable.Drawable;

final class AppInfo {
    final String packageName; final String activityName; final String label; final Drawable icon;
    AppInfo(String p, String a, String l, Drawable i){ packageName=p; activityName=a; label=l; icon=i; }
}
