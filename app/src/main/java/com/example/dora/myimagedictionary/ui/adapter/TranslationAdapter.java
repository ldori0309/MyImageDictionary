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

package com.example.dora.myimagedictionary.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dora.myimagedictionary.R;
import com.example.dora.myimagedictionary.data.Word;

import java.util.ArrayList;

public class TranslationAdapter extends RecyclerView.Adapter<TranslationAdapter.TranslationAdapterViewHolder> {


    private ArrayList<Word> mData;
    private final Context mContext;
    private final boolean mShowCheckbox;


    public TranslationAdapter(Context context, boolean showCheckbox) {
        mContext = context;
        mShowCheckbox = showCheckbox;
    }

    public class TranslationAdapterViewHolder extends RecyclerView.ViewHolder {
        private final TextView originalWordTextView;
        private final TextView translatedWordTextView;
        private final CheckBox checkbox;
        private final LinearLayout linearLayout;
        private final View checkboxView;

        private TranslationAdapterViewHolder(View view) {
            super(view);
            originalWordTextView = view.findViewById(R.id.word_text_view);
            translatedWordTextView = view.findViewById(R.id.translated_word_text_view);
            checkbox = view.findViewById(R.id.checkbox);
            linearLayout = view.findViewById(R.id.word_linear_layout);
            checkboxView = view.findViewById(R.id.checkbox_view);
        }
    }

    @NonNull
    @Override
    public TranslationAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        int layoutIdForListItem = R.layout.item_word;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new TranslationAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TranslationAdapterViewHolder translationAdapterViewHolder, int position) {

        String originalWord = mData.get(position).getOriginalWord();
        String translatedWord = mData.get(position).getTranslatedWord();
        boolean isChecked = mData.get(position).getIsCheked();

        translationAdapterViewHolder.itemView.setTag(position);
        translationAdapterViewHolder.checkbox.setTag(position);

        translationAdapterViewHolder.originalWordTextView.setText(originalWord);
        translationAdapterViewHolder.translatedWordTextView.setText(translatedWord);

        if (mShowCheckbox) {
            translationAdapterViewHolder.checkbox.setChecked(isChecked);
            translationAdapterViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout linearLayout = (LinearLayout) v;
                    for(int i = 0; i < linearLayout.getChildCount(); i++) {
                        View child = linearLayout.getChildAt(i);
                        if (child instanceof CheckBox) {
                            CheckBox checkbox = (CheckBox) child;
                            boolean isChecked = checkbox.isChecked();
                            int position = (int) checkbox.getTag();
                            checkbox.setChecked(!isChecked);
                            mData.get(position).setIsCheked(!isChecked);
                        }
                    }
                }
            });
            translationAdapterViewHolder.checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkbox = (CheckBox) v;
                    boolean isChecked = checkbox.isChecked();
                    int position = (int) checkbox.getTag();
                    mData.get(position).setIsCheked(isChecked);
                }
            });
        } else {
            translationAdapterViewHolder.checkbox.setVisibility(View.GONE);
            translationAdapterViewHolder.checkboxView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (null == mData) return 0;
        return mData.size();
    }

    public void setWordsData(ArrayList<Word> wordsData) {
        mData = wordsData;
        notifyDataSetChanged();
    }
}
