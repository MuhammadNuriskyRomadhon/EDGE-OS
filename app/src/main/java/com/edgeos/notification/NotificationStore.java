package com.edgeos.notification;

import android.app.Notification;
import android.service.notification.StatusBarNotification;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class NotificationStore {
    private static final int MAX = 40;
    private static final LinkedHashMap<String, Item> ITEMS = new LinkedHashMap<String, Item>() {
        @Override protected boolean removeEldestEntry(Map.Entry<String, Item> eldest) { return size() > MAX; }
    };
    private NotificationStore() {}

    public static synchronized void upsert(StatusBarNotification sbn) {
        Notification n = sbn.getNotification();
        CharSequence title = n.extras.getCharSequence(Notification.EXTRA_TITLE);
        CharSequence text = n.extras.getCharSequence(Notification.EXTRA_TEXT);
        ITEMS.put(sbn.getKey(), new Item(sbn.getPackageName(),
                title == null ? "" : title.toString(), text == null ? "" : text.toString(),
                System.currentTimeMillis()));
    }
    public static synchronized void remove(String key) { ITEMS.remove(key); }
    public static synchronized List<Item> snapshot() { return new ArrayList<>(ITEMS.values()); }

    public static final class Item {
        public final String packageName, title, text; public final long time;
        Item(String p, String t, String x, long tm) { packageName=p; title=t; text=x; time=tm; }
    }
}
