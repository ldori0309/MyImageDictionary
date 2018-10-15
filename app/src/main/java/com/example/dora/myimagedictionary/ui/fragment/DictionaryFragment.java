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

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dora.myimagedictionary.R;
import com.example.dora.myimagedictionary.data.Word;
import com.example.dora.myimagedictionary.data.WordsContract;
import com.example.dora.myimagedictionary.ui.activity.EditActivity;
import com.example.dora.myimagedictionary.ui.adapter.TranslationAdapter;
import com.example.dora.myimagedictionary.utils.DataUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DictionaryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int NUMBER_OF_COLUMNS_IN_GRID = 2;
    private static final int DICTIONARY_LOADER_ID = 100;

    private TranslationAdapter mTranslationAdapter;
    private Context mContext;

    private ArrayList<Word> mWordsData;

    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.empty_text_view) TextView mEmptyTextView;
    @BindView(R.id.fab) FloatingActionButton fab;

    public DictionaryFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(com.example.dora.myimagedictionary.R.layout.fragment_dictionary, container, false);
        mContext = getContext();
        ButterKnife.bind(this, root);

        RecyclerView.LayoutManager layoutManager;
        if (getResources().getConfiguration().smallestScreenWidthDp >= 600) { //Is Tablet
            layoutManager = new GridLayoutManager(mContext, NUMBER_OF_COLUMNS_IN_GRID, LinearLayoutManager.VERTICAL,false);
        } else {
            layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,false);
        }

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mTranslationAdapter = new TranslationAdapter(mContext, false);
        mRecyclerView.setAdapter(mTranslationAdapter);

        if (savedInstanceState == null) {
            getLoaderManager().initLoader(DICTIONARY_LOADER_ID, null, this);
        } else {
            mWordsData = savedInstanceState.getParcelableArrayList("words_data");
            mTranslationAdapter.setWordsData(mWordsData);
        }

        return root;
    }

    @OnClick (R.id.fab)
    public void startEditActivity() {
        Intent intent = new Intent(mContext, EditActivity.class);
        intent.putExtra("words", mWordsData);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(DICTIONARY_LOADER_ID,null,DictionaryFragment.this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("words_data", mWordsData);
    }

    @Override
    @NonNull
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case DICTIONARY_LOADER_ID:
                return new CursorLoader(mContext, WordsContract.WordsEntry.CONTENT_URI, null,
                        null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        switch (id) {
            case DICTIONARY_LOADER_ID:
                if (data != null) {
                    mWordsData = DataUtils.getDictionaryFromCursor(mContext, data);
                    mTranslationAdapter.setWordsData(mWordsData);
                }
                if (mWordsData.size() == 0) {
                    mEmptyTextView.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.GONE);
                } else {
                    mEmptyTextView.setVisibility(View.GONE);
                    fab.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }

}
