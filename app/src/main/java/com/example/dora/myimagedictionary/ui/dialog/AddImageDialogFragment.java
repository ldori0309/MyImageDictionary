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

public class AddImageDialogFragment extends DialogFragment {

    public interface AddImageDialogListener {
        void onTakePhotoClick(DialogFragment dialog);
        void onChooseFromGalleryClick(DialogFragment dialog);
    }

    private Context mContext;
    private AddImageDialogFragment.AddImageDialogListener mListener;

    public AddImageDialogFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        try {
            mListener = (AddImageDialogFragment.AddImageDialogListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getParentFragment().toString() + " must implement AddImageDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.add_picture)
                .setIcon(R.drawable.ic_image)
                .setItems(new String[]{getString(R.string.take_photo),getString(R.string.choose_from_gallery)}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            mListener.onTakePhotoClick(AddImageDialogFragment.this);
                        } else {
                            mListener.onChooseFromGalleryClick(AddImageDialogFragment.this);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel_label, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });


        return builder.create();
    }

}