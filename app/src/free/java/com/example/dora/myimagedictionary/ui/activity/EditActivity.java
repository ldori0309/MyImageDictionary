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
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.dora.myimagedictionary.R;
import com.example.dora.myimagedictionary.data.Word;
import com.example.dora.myimagedictionary.ui.adapter.TranslationAdapter;
import com.example.dora.myimagedictionary.utils.PreferenceUtils;
import com.example.dora.myimagedictionary.utils.DataUtils;
import com.example.dora.myimagedictionary.widget.DictionaryWidgetProvider;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.ListIterator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditActivity extends AppCompatActivity {

    private static final int NUMBER_OF_COLUMNS_IN_GRID = 2;

    private TranslationAdapter mTranslationAdapter;
    private Context mContext;
    private ArrayList<Word> mWordsData;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.ad_view) AdView mAdView;
    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.select_all_checkbox) CheckBox mCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        PreferenceUtils.setupTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        mContext = getApplicationContext();
        ButterKnife.bind(this);
        setTitle(getString(R.string.my_words));

        if (getIntent() != null) {
            if (getIntent().hasExtra("words")) {
                mWordsData = getIntent().getParcelableArrayListExtra("words");
            }
        }

        if(savedInstanceState != null) {
            mWordsData = savedInstanceState.getParcelableArrayList("words");
        }

        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        mAdView.loadAd(adRequest);

        RecyclerView.LayoutManager layoutManager;
        if (getResources().getConfiguration().smallestScreenWidthDp >= 600) { //Is Tablet
            layoutManager = new GridLayoutManager(mContext, NUMBER_OF_COLUMNS_IN_GRID, LinearLayoutManager.VERTICAL,false);
        } else {
            layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,false);
        }

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mTranslationAdapter = new TranslationAdapter(mContext,true);
        mRecyclerView.setAdapter(mTranslationAdapter);
        mTranslationAdapter.setWordsData(mWordsData);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = (int) viewHolder.itemView.getTag();
                int id = mWordsData.get(position).getId();
                deleteFromArrayList(id);
                DataUtils.deleteWordFromDictionary(mContext, id);
                mTranslationAdapter.setWordsData(mWordsData);
                DictionaryWidgetProvider.refreshWidget(mContext);
                Toast.makeText(mContext, getResources().getString(R.string.words_deleted_from_dictionary), Toast.LENGTH_SHORT).show();
                if (mWordsData.size() == 0) {
                    onBackPressed();
                }
            }
        }).attachToRecyclerView(mRecyclerView);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int deleted = 0;
                ArrayList<Word> copy = new ArrayList<>(mWordsData);
                for (Word word : copy) {
                    if (word.getIsCheked()) {
                        int id = word.getId();
                        deleteFromArrayList(id);
                        deleted += DataUtils.deleteWordFromDictionary(mContext, id);
                    }
                }
                if (deleted > 0) {
                    mTranslationAdapter.setWordsData(mWordsData);
                    Toast.makeText(mContext, getResources().getString(R.string.words_deleted_from_dictionary), Toast.LENGTH_SHORT).show();
                    DictionaryWidgetProvider.refreshWidget(mContext);
                }
                mCheckBox.setChecked(false);
                if (mWordsData.size() == 0) {
                    onBackPressed();
                }
            }
        });

        mCheckBox.setOnClickListener(new View.OnClickListener() {
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

        setSupportActionBar(mToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    private void deleteFromArrayList(int id) {
        ListIterator iterator = mWordsData.listIterator();
        while (iterator.hasNext()) {
            Word word = (Word) iterator.next();
            if (word.getId() == id) {
                iterator.remove();
                break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mTranslationAdapter.setWordsData(mWordsData);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("words", mWordsData);
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

}
