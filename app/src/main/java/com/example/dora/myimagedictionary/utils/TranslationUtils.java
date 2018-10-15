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

package com.example.dora.myimagedictionary.utils;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public final class TranslationUtils {

    private static final String TAG = TranslationUtils.class.getSimpleName();

    private static final String STATUS_CODE = "status_code";

    private static final String TUC = "tuc";

    private static final String PHRASE = "phrase";

    private static final String TEXT = "text";

    private static final String BASE_URL = "https://glosbe.com/gapi/translate?";

    private static final String API_FROM = "from";

    private static final String API_DEST = "dest";

    private static final String API_PHRASE = "phrase";

    private static final String API_FORMAT = "format";

    private static final String FORMAT_JSON = "json";


    public static String getTranslatedWord(String jsonResponse)
            throws JSONException {

        JSONObject jsonObject = new JSONObject(jsonResponse);

        if (jsonObject.has(STATUS_CODE)) {
            int errorCode = jsonObject.getInt(STATUS_CODE);
            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }

        JSONArray jsonArray = jsonObject.getJSONArray(TUC);
        JSONObject index = jsonArray.getJSONObject(0);
        JSONObject phrase = index.getJSONObject(PHRASE);
        return phrase.getString(TEXT);
    }


    public static URL buildUrl(String from, String dest, String phrase) {

        // example: https://glosbe.com/gapi/translate?from=eng&dest=hun&format=json&phrase=blue&pretty=true

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(API_FROM, from)
                .appendQueryParameter(API_DEST, dest)
                .appendQueryParameter(API_FORMAT, FORMAT_JSON)
                .appendQueryParameter(API_PHRASE, phrase)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

}
