package com.edgeos.launcher;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public final class LauncherActivity extends Activity {
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(new LauncherView(this));
    }

    @Override public void onBackPressed() {
        // A launcher should remain the home surface rather than closing on back.
    }
}
