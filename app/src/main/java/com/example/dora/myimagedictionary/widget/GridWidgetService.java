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

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.dora.myimagedictionary.R;
import com.example.dora.myimagedictionary.data.WordsContract;

public class GridWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }

    class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        final Context mContext;
        Cursor mCursor;

        public GridRemoteViewsFactory(Context applicationContext) {
            mContext = applicationContext;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            if (mCursor != null) mCursor.close();
            mCursor = mContext.getContentResolver().query(WordsContract.WordsEntry.CONTENT_URI, null,
                    null, null, null);
        }

        @Override
        public void onDestroy() {
            mCursor.close();
        }

        @Override
        public int getCount() {
            if (mCursor == null) return 0;
            return mCursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if (mCursor == null || mCursor.getCount() == 0) return null;
            mCursor.moveToPosition(position);
            String originalWord = mCursor.getString(mCursor.getColumnIndex(WordsContract.WordsEntry.COLUMN_ORIGINAL_WORD));
            String translatedWord = mCursor.getString(mCursor.getColumnIndex(WordsContract.WordsEntry.COLUMN_TRANSLATED_WORD));

            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_word);
            views.setTextViewText(R.id.word_text_view, originalWord);
            views.setTextViewText(R.id.translated_word_text_view, translatedWord);

            Intent fillInIntent = new Intent();
            views.setOnClickFillInIntent(R.id.linear_layout, fillInIntent);

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
