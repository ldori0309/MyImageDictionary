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

package com.example.dora.myimagedictionary.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.dora.myimagedictionary.R;
import com.example.dora.myimagedictionary.ui.activity.AnalyzeActivity;
import com.example.dora.myimagedictionary.ui.dialog.AddImageDialogFragment;
import com.example.dora.myimagedictionary.utils.BitmapUtils;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

public class MainFragment extends Fragment implements AddImageDialogFragment.AddImageDialogListener {

    private static final int REQUEST_STORAGE_PERMISSION = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_GALLERY = 3;

    private static final String FILE_PROVIDER_AUTHORITY = "com.example.dora.fileprovider";

    private Context mContext;

    private Uri mPhotoURI;
    private String userChoosenTask;

    @BindView(R.id.add_image_layout) LinearLayout linearLayout;

    public MainFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        mContext = getContext();
        ButterKnife.bind(this, root);
        return root;
    }

    @OnClick(R.id.add_image_layout)
    public void addPicture() {
        DialogFragment dialog = new AddImageDialogFragment();
        dialog.show(getChildFragmentManager(), "AddImageDialogFragment");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals(getString(R.string.take_photo))) {
                        cameraIntent();
                    } else if(userChoosenTask.equals(getString(R.string.choose_from_gallery))) {
                        galleryIntent();
                    }
                } else {
                    Toast.makeText(getContext(), R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void cameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = BitmapUtils.createTempImageFile(getContext());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                mPhotoURI = FileProvider.getUriForFile(getContext(),
                        FILE_PROVIDER_AUTHORITY,
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void galleryIntent() {
        Intent uploadFromGalleryIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(uploadFromGalleryIntent,REQUEST_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                startAnalyzeActiviy(mPhotoURI);
            } else {
                BitmapUtils.deleteImageFile(getContext(), mPhotoURI);
            }
        } else if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK && null != data) {
            mPhotoURI = data.getData();
            startAnalyzeActiviy(mPhotoURI);
        }
    }

    private void startAnalyzeActiviy(Uri photoPath) {
        Intent intent = new Intent(mContext, AnalyzeActivity.class);
        intent.putExtra("photo_path", photoPath);
        startActivity(intent);
    }

    @Override
    public void onTakePhotoClick(DialogFragment dialog) {
        userChoosenTask = getResources().getString(R.string.take_photo);
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        } else {
            cameraIntent();
        }
    }

    @Override
    public void onChooseFromGalleryClick(DialogFragment dialog) {
        userChoosenTask = getResources().getString(R.string.choose_from_gallery);
        if (ContextCompat.checkSelfPermission(mContext,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        } else {
            galleryIntent();
        }
    }
}