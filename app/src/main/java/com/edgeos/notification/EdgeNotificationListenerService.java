package com.edgeos.notification;

import android.app.Notification;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import com.edgeos.dynamic.DynamicOverlayController;

/** Single notification ingress for Lock Screen, Notification Center and Dynamic UI. */
public final class EdgeNotificationListenerService extends NotificationListenerService {
    private DynamicOverlayController dynamic;
    @Override public void onCreate(){ super.onCreate(); dynamic=new DynamicOverlayController(this); }
    @Override public void onListenerConnected(){
        StatusBarNotification[] active=getActiveNotifications();
        if(active!=null) for(StatusBarNotification n:active) NotificationStore.upsert(n);
    }
    @Override public void onNotificationPosted(StatusBarNotification sbn){
        if(sbn==null || sbn.getNotification()==null || getPackageName().equals(sbn.getPackageName())) return;
        NotificationStore.upsert(sbn);
        if(dynamic!=null && Settings.canDrawOverlays(this)){
            Notification n=sbn.getNotification();
            CharSequence title=n.extras==null?null:n.extras.getCharSequence(Notification.EXTRA_TITLE);
            CharSequence text=n.extras==null?null:n.extras.getCharSequence(Notification.EXTRA_TEXT);
            dynamic.show(title==null?"Notification":title.toString(), text==null?"":text.toString());
        }
    }
    @Override public void onNotificationRemoved(StatusBarNotification sbn){ if(sbn!=null) NotificationStore.remove(sbn.getKey()); }
    @Override public void onDestroy(){ if(dynamic!=null)dynamic.destroy(); dynamic=null; super.onDestroy(); }
}
