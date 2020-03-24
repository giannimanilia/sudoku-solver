package com.gmaniliapp.sudokusolver.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

public class PreferenceManager {

    private SharedPreferences instance;

    public PreferenceManager(Context context, String name) {
        instance = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public boolean putString(String key, String value) {
        SharedPreferences.Editor editor = instance.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public String getString(String key, String default_value) {
        return instance.getString(key, default_value);
    }

    public boolean putInt(String key, int value) {
        SharedPreferences.Editor editor = instance.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    public int getInt(String key, int default_value) {
        try {
            return Integer.parseInt(instance.getString(key, default_value + ""));
        } catch (Exception e) {
            return instance.getInt(key, default_value);
        }
    }

    public void putBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = instance.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return instance.getBoolean(key, defValue);
    }

    public void putDate(String key, Date value) {
        long time = value.getTime();
        SharedPreferences.Editor editor = instance.edit();
        editor.putLong(key, time);
        editor.apply();
    }

    public Date getDate(String key) {
        try {
            long value = instance.getLong(key, -1);
            if (value != -1) {
                return new Date(value);
            } else
                return null;
        } catch (Exception e) {
            return null;
        }
    }
}
