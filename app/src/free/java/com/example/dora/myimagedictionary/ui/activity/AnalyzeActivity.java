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

package com.example.dora.myimagedictionary.ui.activity;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dora.myimagedictionary.GlideApp;
import com.example.dora.myimagedictionary.R;
import com.example.dora.myimagedictionary.data.Word;
import com.example.dora.myimagedictionary.data.WordsContract;
import com.example.dora.myimagedictionary.ui.adapter.TranslationAdapter;
import com.example.dora.myimagedictionary.utils.PreferenceUtils;
import com.example.dora.myimagedictionary.utils.BitmapUtils;
import com.example.dora.myimagedictionary.utils.DataUtils;
import com.example.dora.myimagedictionary.utils.LabelDetectionUtils;
import com.example.dora.myimagedictionary.utils.NetworkUtils;
import com.example.dora.myimagedictionary.utils.TranslationUtils;
import com.example.dora.myimagedictionary.widget.DictionaryWidgetProvider;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ListIterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AnalyzeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    private static final int NUMBER_OF_COLUMNS_IN_GRID = 2;
    private static final int DICTIONARY_LOADER_ID = 100;
    private static final int WORDS_LOADER_ID = 200;

    private static final int MAX_DIMENSION = 800;

    private static final String TAG = AnalyzeActivity.class.getSimpleName();

    private Context mContext;
    private TranslationAdapter mTranslationAdapter;

    private String mLanguage;
    private Uri mPhotoPath;

    private static ArrayList<Word> mWordsData;
    private static ArrayList<Word> mDictionaryData;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.ad_view) AdView mAdView;
    @BindView(R.id.select_all_checkbox) CheckBox selectAllCheckbox;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.fab) FloatingActionButton addFab;
    @BindView(R.id.image_view) ImageView imageView;
    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.words_data_frame_layout) FrameLayout wordsLayout;
    @BindView(R.id.error_linear_layout) LinearLayout errorLayout;
    @BindView(R.id.retry_button) Button retryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        PreferenceUtils.setupTheme(this);
        mLanguage = PreferenceUtils.getLanguage(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);

        ButterKnife.bind(this);

        mContext = getApplicationContext();

        setTitle(getString(R.string.new_image));

        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        mAdView.loadAd(adRequest);

        RecyclerView.LayoutManager layoutManager;
        if (getResources().getConfiguration().smallestScreenWidthDp >= 600) { //Is Tablet
            layoutManager = new GridLayoutManager(this, NUMBER_OF_COLUMNS_IN_GRID, LinearLayoutManager.VERTICAL,false);
        } else {
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        }
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        mTranslationAdapter = new TranslationAdapter(this, true);
        recyclerView.setAdapter(mTranslationAdapter);

        if (getIntent() != null) {
            if (getIntent().hasExtra("photo_path")) {
                mPhotoPath = getIntent().getParcelableExtra("photo_path");
                GlideApp.with(mContext).load(mPhotoPath).into(imageView);
            }
        }

        if (savedInstanceState == null) {
            getSupportLoaderManager().initLoader(DICTIONARY_LOADER_ID, null, this);
            getSupportLoaderManager().initLoader(WORDS_LOADER_ID, null, this);
        } else {
            mPhotoPath = savedInstanceState.getParcelable("photo_path");
            GlideApp.with(mContext).load(mPhotoPath).into(imageView);
            mWordsData = savedInstanceState.getParcelableArrayList("words_data");
            mDictionaryData = savedInstanceState.getParcelableArrayList("dictionary_data");
            mTranslationAdapter.setWordsData(mWordsData);
        }

        setSupportActionBar(mToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = (int) viewHolder.itemView.getTag();
                mWordsData.remove(position);
                mTranslationAdapter.setWordsData(mWordsData);
                DictionaryWidgetProvider.refreshWidget(mContext);
                if (mWordsData.size() == 0) {
                    imageView.setAlpha((float) 1);
                }
            }
        }).attachToRecyclerView(recyclerView);

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Word> wordsChecked = new ArrayList<>();
                for (Word word : mWordsData) {
                    if (word.getIsCheked()) {
                        wordsChecked.add(word);
                    }
                }

                wordsChecked = removeDuplicates(wordsChecked, mDictionaryData);

                int wordsAdded = 0;
                for (Word word : wordsChecked) {
                    if (DataUtils.addWordToDictionary(mContext, word) != null) {
                        wordsAdded++;
                    }
                }

                ListIterator iterator = mWordsData.listIterator();
                while (iterator.hasNext()) {
                    Word word = (Word) iterator.next();
                    if (word.getIsCheked()) {
                        iterator.remove();
                    }
                }

                if (mWordsData.size() == 0) {
                    imageView.setAlpha((float) 1);
                }

                mTranslationAdapter.setWordsData(mWordsData);
                selectAllCheckbox.setChecked(false);
                DictionaryWidgetProvider.refreshWidget(mContext);
                if (wordsAdded > 0) {
                    Toast.makeText(mContext, getResources().getString(R.string.words_added_to_dictionary), Toast.LENGTH_SHORT).show();
                }
            }
        });

        selectAllCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkbox = (CheckBox) v;
                boolean isChecked = checkbox.isChecked();
                for (Word word : mWordsData) {
                    word.setIsCheked(isChecked);
                }
                mTranslationAdapter.setWordsData(mWordsData);
            }
        });
    }

    @OnClick (R.id.retry_button)
    public void retry() {
        getSupportLoaderManager().restartLoader(WORDS_LOADER_ID, null, this);
    }

    private void showWordsDataView() {
        errorLayout.setVisibility(View.GONE);
        wordsLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        if (mWordsData.size() == 0) {
            imageView.setAlpha((float) 1);
        } else {
            imageView.setAlpha((float) 0.3);
        }
    }

    private void showErrorMessage() {
        errorLayout.setVisibility(View.VISIBLE);
        wordsLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        imageView.setAlpha((float) 1);
        progressBar.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
        wordsLayout.setVisibility(View.GONE);
    }

    private ArrayList<Word> removeDuplicates(ArrayList<Word> firstArray, ArrayList<Word> secondArray) {
        ListIterator listIterator = firstArray.listIterator();
        while (listIterator.hasNext()) {
            Word word = (Word) listIterator.next();
            for (Word secondWord : secondArray) {
                if (word.getOriginalWord().equals(secondWord.getOriginalWord())) {
                    listIterator.remove();
                }
            }
        }
        return firstArray;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("photo_path", mPhotoPath);
        outState.putParcelableArrayList("words_data", mWordsData);
        outState.putParcelableArrayList("dictionary_data", mDictionaryData);
    }

    @Override
    @NonNull
    public Loader onCreateLoader(int id, Bundle args) {
        if (id == DICTIONARY_LOADER_ID ) {
            return new CursorLoader(mContext, WordsContract.WordsEntry.CONTENT_URI, null,
                    null, null, null);
        } else if (id == WORDS_LOADER_ID ) {
            showProgressBar();
            return new AnalyzeAsyncTask(this, mLanguage, mPhotoPath);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        int id = loader.getId();
        if (id == DICTIONARY_LOADER_ID ) {
            if (data != null) {
                mDictionaryData = DataUtils.getDictionaryFromCursor(mContext, (Cursor) data);
            }
        } else if (id == WORDS_LOADER_ID ) {
            mWordsData = (ArrayList<Word>) data;
            if (data == null) {
                showErrorMessage();
            } else {
                showWordsDataView();
                mTranslationAdapter.setWordsData(mWordsData);
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    static class AnalyzeAsyncTask extends AsyncTaskLoader<ArrayList<Word>> {

        private String mLanguage;
        private Uri mPhotoPath;
        private ConnectivityManager mConnectivityManager;
        private ArrayList<Word> mData;

        public AnalyzeAsyncTask(Context context, String language, Uri photoPath) {
            super(context);
            mLanguage = language;
            mPhotoPath = photoPath;
            mConnectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        @Override
        protected void onStartLoading() {
            Log.v(TAG, "Loader has started.");
            if (mData != null) {
                deliverResult(mData);
            } else {
                forceLoad();
            }
        }

        @Override
        public ArrayList<Word> loadInBackground() {
            NetworkInfo activeNetwork = mConnectivityManager.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                Bitmap bitmap;
                try {
                    bitmap = BitmapUtils.scaleBitmapDown(MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), mPhotoPath), MAX_DIMENSION);
                    ArrayList<String> detectedWords;
                    try {
                        detectedWords = LabelDetectionUtils.LableDetectionTask(LabelDetectionUtils.prepareAnnotationRequest(bitmap));
                        ArrayList<Word> dataToReturn = new ArrayList<>();
                        for (String detectedWord : detectedWords) {
                            URL wordRequestUrl = TranslationUtils.buildUrl("eng", mLanguage, detectedWord);
                            try {
                                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(wordRequestUrl);
                                String translatedWord = TranslationUtils.getTranslatedWord(jsonResponse);
                                if (!translatedWord.equals("")) {
                                    dataToReturn.add(new Word (detectedWord, translatedWord, false));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e(TAG, "Failed to do translation request.");
                            }
                        }
                        return dataToReturn;
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to make API request.");
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Failed to create bitmap from uri.");
                    return null;
                }
            } else {
                Log.e(TAG,"Device is not connected.");
                return null;
            }
        }

        public void deliverResult(ArrayList<Word> data) {
            mData = data;
            super.deliverResult(data);
        }
    }

}
