package com.example.dora.myimagedictionary.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.dora.myimagedictionary.data.Word;
import com.example.dora.myimagedictionary.data.WordsContract;
import com.example.dora.myimagedictionary.widget.DictionaryWidgetProvider;

import java.util.ArrayList;

public class DataUtils {

    public static ArrayList<Word> getDictionaryFromCursor(Context mContext, Cursor mCursor) {
        ArrayList<Word> words = new ArrayList<>();
        if (mCursor != null) {
            while (mCursor.moveToNext()) {
                int idIndex = mCursor.getColumnIndex(WordsContract.WordsEntry._ID);
                int originalWordIndex = mCursor.getColumnIndex(WordsContract.WordsEntry.COLUMN_ORIGINAL_WORD);
                int translatedWordIndex = mCursor.getColumnIndex(WordsContract.WordsEntry.COLUMN_TRANSLATED_WORD);
                int id = mCursor.getInt(idIndex);
                String originalWord = mCursor.getString(originalWordIndex);
                String translatedWord = mCursor.getString(translatedWordIndex);
                words.add(new Word (id, originalWord, translatedWord));
            }
            mCursor.close();
        }
        return words;
    }

    public static int deleteWordFromDictionary(Context mContext, int id) {
        String stringId = Integer.toString(id);
        Uri mUri = WordsContract.WordsEntry.CONTENT_URI.buildUpon().appendPath(stringId).build();
        return mContext.getContentResolver().delete(mUri, null, null);
    }

    public static void deleteDictionary(Context mContext) {
        mContext.getContentResolver().delete(WordsContract.WordsEntry.CONTENT_URI, null, null);
        DictionaryWidgetProvider.refreshWidget(mContext);
    }

    public static Uri addWordToDictionary(final Context mContext, final Word word) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WordsContract.WordsEntry.COLUMN_ORIGINAL_WORD, word.getOriginalWord());
        contentValues.put(WordsContract.WordsEntry.COLUMN_TRANSLATED_WORD, word.getTranslatedWord());
        return mContext.getContentResolver().insert(WordsContract.WordsEntry.CONTENT_URI, contentValues);
    }

}
