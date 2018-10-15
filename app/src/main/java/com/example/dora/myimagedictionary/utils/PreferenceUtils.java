package com.example.dora.myimagedictionary.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.example.dora.myimagedictionary.R;

public class PreferenceUtils {

    public static void setupTheme(Context mContext) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        final String theme = sharedPreferences.getString(mContext.getString(R.string.pref_theme_key), mContext.getString(R.string.dark_value));
        final String color = sharedPreferences.getString(mContext.getString(R.string.pref_color_key), mContext.getString(R.string.blue_value));
        if (theme.equals(mContext.getString(R.string.dark_value))) {
            if (color.equals(mContext.getString(R.string.green_value))) {
                mContext.setTheme(R.style.DarkAppThemeGreen);
            } else if (color.equals(mContext.getString(R.string.red_value))) {
                mContext.setTheme(R.style.DarkAppThemeRed);
            } else {
                mContext.setTheme(R.style.DarkAppTheme);
            }
        } else {
            if (color.equals(mContext.getString(R.string.green_value))) {
                mContext.setTheme(R.style.LightAppThemeGreen);
            } else if (color.equals(mContext.getString(R.string.red_value))) {
                mContext.setTheme(R.style.LightAppThemeRed);
            } else {
                mContext.setTheme(R.style.LightAppTheme);
            }
        }
    }

    public static String getLanguage(Context mContext) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPreferences.getString(mContext.getString(R.string.pref_language_key), mContext.getString(R.string.france_value));
    }

    public static void setLanguage(Context mContext, String language) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        sharedPreferences.edit().putString(mContext.getString(R.string.pref_language_key), language).apply();
    }

}
