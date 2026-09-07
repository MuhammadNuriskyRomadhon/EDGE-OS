package com.edgeos.theme;

import android.content.Context;
import android.content.res.Configuration;

/** Lightweight theme state. Visuals use native drawing; no blur engine or external assets. */
public final class EdgeTheme {
    private EdgeTheme() {}
    public static boolean isDark(Context context) {
        return (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }
    public static int background(Context c) { return isDark(c) ? 0xFF111216 : 0xFFF5F5F7; }
    public static int foreground(Context c) { return isDark(c) ? 0xFFF5F5F7 : 0xFF15161A; }
    public static int secondary(Context c) { return isDark(c) ? 0xFFB9BBC4 : 0xFF5F616B; }
    public static int panel(Context c) { return isDark(c) ? 0xE61D1F25 : 0xE6FFFFFF; }
}
