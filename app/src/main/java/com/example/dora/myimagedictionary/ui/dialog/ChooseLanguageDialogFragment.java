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

package com.example.dora.myimagedictionary.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;
import android.widget.ListView;

import com.example.dora.myimagedictionary.R;

public class ChooseLanguageDialogFragment extends DialogFragment {

    private String[] mLanguageLabels;
    private Context mContext;
    private int mSelectedItem = 0;

    public ChooseLanguageDialogFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mLanguageLabels = getResources().getStringArray(R.array.language_values);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.select_language)
                .setIcon(R.drawable.ic_language)
                .setSingleChoiceItems(R.array.language_labels, mSelectedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ListView languageList = ((AlertDialog)dialog).getListView();
                        mSelectedItem = languageList.getCheckedItemPosition();
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(getString(R.string.pref_language_key), mLanguageLabels[mSelectedItem]);
                        editor.apply();
                    }
                })
                .setNegativeButton(R.string.cancel_label, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

        /*builder.setTitle(R.string.select_language)
                .setIcon(R.drawable.ic_language)
                .setSingleChoiceItems(R.array.language_labels, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(getString(R.string.pref_language_key), mLanguageLabels[i]);
                        editor.apply();
                        dialogInterface.dismiss();
                    }
                });*/

        return builder.create();

        /*AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getString(R.string.select_language))
                .setIcon(R.drawable.ic_language)
                .setItems(getResources().getStringArray(R.array.language_labels), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(getString(R.string.pref_language_key), mLanguageLabels[which]);
                        editor.apply();
                    }
                });
        return builder.create();*/
    }

}