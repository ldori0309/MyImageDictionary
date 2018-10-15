/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.dora.myimagedictionary.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.widget.RemoteViews;

import com.example.dora.myimagedictionary.R;
import com.example.dora.myimagedictionary.data.WordsContract;
import com.example.dora.myimagedictionary.ui.activity.MainActivity;

public class DictionaryWidgetProvider extends AppWidgetProvider {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, Cursor cursor, int appWidgetId) {
        appWidgetManager.updateAppWidget(appWidgetId, getGridRemoteView(context));
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_grid_view);
        Cursor cursor =  context.getContentResolver().query(WordsContract.WordsEntry.CONTENT_URI, null,
                null, null, null);
        updateWidgets(context, appWidgetManager, cursor ,appWidgetIds);
    }

    public static void updateWidgets(Context context, AppWidgetManager appWidgetManager, Cursor cursor, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, cursor, appWidgetId);
        }
    }

    public static RemoteViews getGridRemoteView(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_dictionary);
        Intent intent = new Intent(context, GridWidgetService.class);
        views.setRemoteAdapter(R.id.widget_grid_view, intent);
        views.setEmptyView(R.id.widget_grid_view, R.id.empty_view);

        Intent dictionaryIntent = new Intent(context, MainActivity.class);
        dictionaryIntent.putExtra("fragment_to_show", R.id.action_words);
        PendingIntent dictionaryPendingIntent = PendingIntent.getActivity(context, 0, dictionaryIntent, 0);
        views.setPendingIntentTemplate(R.id.widget_grid_view, dictionaryPendingIntent);

        Intent emptyViewIntent = new Intent(context, MainActivity.class);
        PendingIntent emptyViewPendingIntent = PendingIntent.getActivity(context, 0, emptyViewIntent, 0);
        views.setPendingIntentTemplate(R.id.empty_view, emptyViewPendingIntent);

        return views;
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // Perform any action when one or more AppWidget instances have been deleted
    }

    @Override
    public void onEnabled(Context context) {
        // Perform any action when an AppWidget for this provider is instantiated
    }

    @Override
    public void onDisabled(Context context) {
        // Perform any action when the last AppWidget instance for this provider is deleted
    }

    public static void refreshWidget(Context mContext) {
        Intent intent = new Intent(mContext, DictionaryWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(mContext, DictionaryWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        mContext.sendBroadcast(intent);
    }

}

