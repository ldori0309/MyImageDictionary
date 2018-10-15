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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WordsDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "wordsDb.db";

    private static final int VERSION = 1;

    WordsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE "  + WordsContract.WordsEntry.TABLE_NAME + " (" +
                WordsContract.WordsEntry._ID + " INTEGER PRIMARY KEY, " +
                WordsContract.WordsEntry.COLUMN_ORIGINAL_WORD + " TEXT NOT NULL, " +
                WordsContract.WordsEntry.COLUMN_TRANSLATED_WORD + " TEXT NOT NULL);";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WordsContract.WordsEntry.TABLE_NAME);
        onCreate(db);
    }
}
