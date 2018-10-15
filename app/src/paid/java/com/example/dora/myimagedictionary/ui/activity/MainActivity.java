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

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.dora.myimagedictionary.R;
import com.example.dora.myimagedictionary.ui.dialog.ChooseLanguageDialogFragment;
import com.example.dora.myimagedictionary.ui.fragment.DictionaryFragment;
import com.example.dora.myimagedictionary.ui.fragment.MainFragment;
import com.example.dora.myimagedictionary.ui.fragment.SettingsFragment;
import com.example.dora.myimagedictionary.utils.PreferenceUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private int fragmentToShow;

    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view) NavigationView mNavigationView;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        PreferenceUtils.setupTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if(savedInstanceState == null) {
            fragmentToShow = R.id.action_image;
            if (getIntent() != null) {
                if (getIntent().hasExtra("fragment_to_show")) {
                    fragmentToShow = getIntent().getIntExtra("fragment_to_show", R.id.action_image);
                }
            }
            switch (fragmentToShow) {
                case R.id.action_words:
                    getSupportFragmentManager().beginTransaction().add(R.id.fragment, new DictionaryFragment()).commit();
                    break;
                case R.id.action_settings:
                    getSupportFragmentManager().beginTransaction().add(R.id.fragment, new SettingsFragment()).commit();
                    break;
                default:
                    getSupportFragmentManager().beginTransaction().add(R.id.fragment, new MainFragment()).commit();
                    break;
            }
        } else {
            fragmentToShow = savedInstanceState.getInt("shown_fragment");
        }

        switch (fragmentToShow) {
            case R.id.action_words:
                setTitle(getString(R.string.my_words));
                break;
            case R.id.action_settings:
                setTitle(getString(R.string.settings));
                break;
            default:
                setTitle(getString(R.string.new_image));
                break;
        }

        mNavigationView.setCheckedItem(fragmentToShow);
        mNavigationView.setNavigationItemSelectedListener (
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        int id = menuItem.getItemId();
                        switch (id) {
                            case R.id.action_image:
                                if (fragmentToShow == R.id.action_image) {
                                    break;
                                } else {
                                    MainFragment mainFragment = new MainFragment();
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, mainFragment).commit();
                                    fragmentToShow = R.id.action_image;
                                    setTitle(getString(R.string.new_image));
                                    break;
                                }
                            case R.id.action_words:
                                if (fragmentToShow == R.id.action_words) {
                                    break;
                                } else {
                                    DictionaryFragment wordsFragment = new DictionaryFragment();
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, wordsFragment).commit();
                                    fragmentToShow = R.id.action_words;
                                    setTitle(getString(R.string.my_words));
                                    break;
                                }
                            case R.id.action_settings:
                                if (fragmentToShow == R.id.action_settings) {
                                    break;
                                } else {
                                    SettingsFragment settingsFragment = new SettingsFragment();
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, settingsFragment).commit();
                                    fragmentToShow = R.id.action_settings;
                                    setTitle(getString(R.string.settings));
                                    break;
                                }
                            case R.id.action_exit:
                                finish();
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                }
         );

        setSupportActionBar(mToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        setupLanguage();
    }

    private void setupLanguage() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (sharedPreferences.getBoolean(getString(R.string.pref_first_run), true)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.pref_first_run), false);
            editor.apply();
            ChooseLanguageDialogFragment dialog = new ChooseLanguageDialogFragment();
            dialog.show(getSupportFragmentManager(), "ChooseLanguageDialogFragment");
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_theme_key)) || key.equals(getString(R.string.pref_color_key)) || key.equals(getString(R.string.pref_language_key))) {
            Intent restartIntent = new Intent(this, MainActivity.class);
            restartIntent.putExtra("fragment_to_show", fragmentToShow);
            finish();
            startActivity(restartIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("shown_fragment", fragmentToShow);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
