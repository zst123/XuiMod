/*
 * Copyright (C) 2013 XuiMod
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zst.xposed.xuimod;

import java.io.DataOutputStream;

import com.zst.xposed.xuimod.mods.SecondsClockMod;
import com.zst.xposed.xuimod.preference.activity.AnimControlPreference;
import com.zst.xposed.xuimod.preference.activity.ListViewBlacklist;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

@SuppressLint("WorldReadableFiles")
@SuppressWarnings("deprecation")
public class SettingActivity extends PreferenceActivity implements
		OnPreferenceClickListener, OnSharedPreferenceChangeListener {
	public static final String TAG = SettingActivity.class.getSimpleName();
	public static final String FIRST_KEY = "first";
	public static final String PREFERENCE_FILE = Common.MY_PACKAGE_NAME
			+ "_preferences";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences prefs = getSharedPreferences(PREFERENCE_FILE,
				MODE_WORLD_READABLE);
		// make a new preference so "MODE WORLD READABLE" works
		if (!prefs.contains(FIRST_KEY)) {
			prefs.edit().putBoolean(FIRST_KEY, false).commit();
		}

		fixStringInt(prefs, Common.KEY_VOLUME_ALPHA,
				Common.DEFAULT_VOLUME_ALPHA);
		fixStringInt(prefs, Common.KEY_BATTERYBAR_HEIGHT,
				Common.DEFAULT_BATTERYBAR_HEIGHT);

		addPreferencesFromResource(R.xml.pref_setting);
		findPreference("batterybar_restart").setOnPreferenceClickListener(this);
		findPreference("seconds_restart").setOnPreferenceClickListener(this);
		findPreference("listview_testing").setOnPreferenceClickListener(this);
		findPreference(Common.KEY_LISTVIEW_BLACKLIST).setOnPreferenceClickListener(this);

		Preference animation_control = findPreference(Common.KEY_ANIMATION_CONTROLS_PREF_SCREEN);
		boolean sdk18 = Build.VERSION.SDK_INT >= 18;
		String summary = getResources().getString(R.string.anim_controls_main_summary);
		if (!sdk18) { /* if not Android 4.3, use unsupported summary text */
			summary = String.format(getResources().getString
					(R.string.version_unsupported), "4.3", Build.VERSION.RELEASE);
		}
		animation_control.setSummary(summary);
		animation_control.setEnabled(sdk18);
		animation_control.setOnPreferenceClickListener(this);
  
		
		prefs.registerOnSharedPreferenceChangeListener(this);
		Log.i(TAG, "onCreate");
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String key) {
		Common.settingsChanged(this);
	}
	
	@Override
	public boolean onPreferenceClick(Preference p) {
		if (p.getKey().equals("seconds_restart")
				|| p.getKey().equals("batterybar_restart")) {
			SecondsClockMod.thix = null;
			SecondsClockMod.enabled = false;
			SecondsClockMod.stopForever = false;
			dialog_killSystemUI();
			return true;
		}
		if (p.getKey().equals("listview_testing")) {
			listViewTester();
			return true;
		}
		if (p.getKey().equals(Common.KEY_LISTVIEW_BLACKLIST)) {
			Intent i = new Intent(this, ListViewBlacklist.class);
			startActivity(i);
		}
		if (p.getKey().equals(Common.KEY_ANIMATION_CONTROLS_PREF_SCREEN)) {
			Intent i = new Intent(this, AnimControlPreference.class);
			startActivity(i);
		}
		return false;
	}

	private void dialog_killSystemUI() {
		new AlertDialog.Builder(this)
				.setMessage(R.string.systemui_restart_dialog)
				.setPositiveButton("YES",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								killPackage("com.android.systemui");
							}
						}).setNegativeButton("NO", null).show();
	}

	// TODO Don't run shell on UI thread
	private void killPackage(String packageToKill) {
		// code modified from :
		// http://forum.xda-developers.com/showthread.php?t=2235956&page=6
		try { // get superuser
			Process su = Runtime.getRuntime().exec("su");
			if (su == null)
				return;
			DataOutputStream os = new DataOutputStream(su.getOutputStream());
			os.writeBytes("pkill " + packageToKill + "\n");
			os.writeBytes("exit\n");
			su.waitFor();
			os.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void listViewTester() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle(R.string.anim_listview_test);

		ListView modeList = new ListView(this);
		ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1);
		modeAdapter.add("Scroll to see changes.");
		modeAdapter.add("Press back to exit.");

		for (int x = 1; x < 51; x++) {
			modeAdapter.add("ListView Item " + x);
		}
		modeList.setAdapter(modeAdapter);
		builder.setView(modeList);
		builder.show();
	}

	private void fixStringInt(SharedPreferences prefs, String key,
			int defaultValue) {
		// workaround. Previosuly, Common.KEY_* is String, but
		// String doesn't cast to int
		try {
			prefs.getInt(key, 0);
		} catch (Exception e) {
			String defValueString = String.valueOf(defaultValue);
			int value = Integer.valueOf(prefs.getString(key, defValueString));
			prefs.edit().putInt(key, value).commit();
		}
	}

}
