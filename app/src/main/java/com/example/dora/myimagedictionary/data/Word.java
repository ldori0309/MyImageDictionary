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

package com.example.dora.myimagedictionary.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Word implements Parcelable {

    private int id = -1;
    private String originalWord;
    private String translatedWord;
    private boolean isChecked = false;

    public Word(String originalWord, String translatedWord, boolean isChecked) {
        this.originalWord = originalWord;
        this.translatedWord = translatedWord;
        this.isChecked = isChecked;
    }

    public Word(int id, String originalWord, String translatedWord) {
        this.id = id;
        this.originalWord = originalWord;
        this.translatedWord = translatedWord;
    }

    public int getId() { return id; }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginalWord() { return originalWord; }

    public void setOriginalWord(String originalWord) {
        this.originalWord = originalWord;
    }

    public String getTranslatedWord() {
        return translatedWord;
    }

    public void setTranslatedWord(String translatedWord) {
        this.translatedWord = translatedWord;
    }

    public boolean getIsCheked() {
        return isChecked;
    }

    public void setIsCheked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.originalWord);
        dest.writeString(this.translatedWord);
        dest.writeInt(this.isChecked ? 1 : 0);
    }

    protected Word(Parcel in) {
        this.id = in.readInt();
        this.originalWord = in.readString();
        this.translatedWord = in.readString();
        this.isChecked = in.readInt() > 0;
    }

    public static final Parcelable.Creator<Word> CREATOR = new Parcelable.Creator<Word>() {
        @Override
        public Word createFromParcel(Parcel source) {
            return new Word(source);
        }

        @Override
        public Word[] newArray(int size) {
            return new Word[size];
        }
    };
}
