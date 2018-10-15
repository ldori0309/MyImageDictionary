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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.dora.myimagedictionary.R;

public class ChangeLanguageDialogFragment extends DialogFragment {

    public interface ChangeLanguageDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
    }

    private ChangeLanguageDialogFragment.ChangeLanguageDialogListener mListener;
    private Context mContext;

    public ChangeLanguageDialogFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        try {
            mListener = (ChangeLanguageDialogListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getParentFragment().toString() + " must implement ChangeLanguageDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.change_language_title)
                .setMessage(R.string.change_language_msg)
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(ChangeLanguageDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel_label, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }
}