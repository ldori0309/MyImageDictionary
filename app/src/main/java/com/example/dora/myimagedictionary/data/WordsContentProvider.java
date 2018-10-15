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

package com.example.dora.myimagedictionary.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import static com.example.dora.myimagedictionary.data.WordsContract.WordsEntry.TABLE_NAME;

public class WordsContentProvider extends ContentProvider {

    private static final int WORDS = 100;
    private static final int WORD_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(WordsContract.AUTHORITY, WordsContract.PATH_WORDS, WORDS);
        uriMatcher.addURI(WordsContract.AUTHORITY, WordsContract.PATH_WORDS + "/#", WORD_WITH_ID);
        return uriMatcher;
    }

    private WordsDbHelper mWordsDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mWordsDbHelper = new WordsDbHelper(context);
        return true;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mWordsDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case WORDS:
                long id = db.insert(TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(WordsContract.WordsEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = mWordsDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case WORDS:
                retCursor =  db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mWordsDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int wordsDeleted;
        switch (match) {
            case WORD_WITH_ID:
                String id = uri.getPathSegments().get(1);
                wordsDeleted = db.delete(TABLE_NAME, "_id=?", new String[]{id});
                break;
            case WORDS:
                wordsDeleted = db.delete(TABLE_NAME,null,null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (wordsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return wordsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase db = mWordsDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int wordsUpdated;
        switch (match) {
            case WORD_WITH_ID:
                String id = uri.getPathSegments().get(1);
                wordsUpdated = db.update(TABLE_NAME, values, "_id=?", new String[]{id});
                break;
            case WORDS:
                wordsUpdated = db.update(TABLE_NAME, values, null,null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (wordsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return wordsUpdated;
    }


    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not supported");
    }

}