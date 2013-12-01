package com.zst.xposed.xuimod.preference.activity;

import com.zst.xposed.xuimod.Common;
import com.zst.xposed.xuimod.R;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ChooseRandomColor extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);
        getPreferenceManager().setSharedPreferencesName(Common.RANDOM_COLOR_PREFERENCE_FILENAME);
		addPreferencesFromResource(R.xml.pref_random_color);
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
		Common.settingsChanged(this);
	}
}
