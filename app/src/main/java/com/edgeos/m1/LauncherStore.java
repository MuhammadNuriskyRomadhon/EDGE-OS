package com.edgeos.m1;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;

final class LauncherStore {
    private static final String PREFS="edgeos_home"; private static final String ITEMS="items"; private static final String DOCK="dock";
    private final android.content.SharedPreferences p;
    LauncherStore(Context c){p=c.getSharedPreferences(PREFS,Context.MODE_PRIVATE);}
    List<String> load(String key){
        ArrayList<String> out=new ArrayList<>(); String raw=p.getString(key,"[]");
        try{JSONArray a=new JSONArray(raw); for(int i=0;i<a.length();i++) out.add(a.getString(i));}catch(Exception ignored){}
        return out;
    }
    void save(String key,List<String> list){JSONArray a=new JSONArray(); for(String s:list)a.put(s); p.edit().putString(key,a.toString()).apply();}
    List<String> items(){return load(ITEMS);} List<String> dock(){return load(DOCK);}
    void saveItems(List<String> x){save(ITEMS,x);} void saveDock(List<String> x){save(DOCK,x);}
}
