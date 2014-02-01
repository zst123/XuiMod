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

import com.zst.xposed.xuimod.preference.activity.AnimControlPreference;
import com.zst.xposed.xuimod.preference.activity.BatteryBarColor;
import com.zst.xposed.xuimod.preference.activity.ChooseRandomColor;
import com.zst.xposed.xuimod.preference.activity.ListViewBlacklist;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

@SuppressLint("WorldReadableFiles")
@SuppressWarnings("deprecation")
public class SettingActivity extends PreferenceActivity implements
		OnPreferenceClickListener, OnSharedPreferenceChangeListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesMode(PreferenceActivity.MODE_WORLD_READABLE);
		addPreferencesFromResource(R.xml.pref_setting);
		findPreference("batterybar_color_screen").setOnPreferenceClickListener(this);
		findPreference("batterybar_restart").setOnPreferenceClickListener(this);
		findPreference("seconds_restart").setOnPreferenceClickListener(this);
		findPreference("notif_restart").setOnPreferenceClickListener(this);
		findPreference("toggle_launcher").setOnPreferenceClickListener(this);
		findPreference(Common.KEY_LISTVIEW_BLACKLIST).setOnPreferenceClickListener(this);
		findPreference(Common.KEY_NOTIFICATION_CHOOSE_COLOR).setOnPreferenceClickListener(this);
		findPreference(Common.KEY_ANIMATION_CONTROLS_PREF_SCREEN).setOnPreferenceClickListener(this);
		findPreference(Common.KEY_ANIMATION_TOAST_TEST).setOnPreferenceClickListener(this);

		final boolean sdk17 = Build.VERSION.SDK_INT >= 17;
		
		Preference qs_random_color = findPreference(Common.KEY_NOTIFICATION_RANDOM_QS_TILE_COLOR);
		Preference select_random_color = findPreference(Common.KEY_NOTIFICATION_CHOOSE_COLOR);
		String qs_summary = getResources().getString(R.string.notif_quick_settings_random_summary);
		if (!sdk17) { /* if not Android 4.2, use unsupported summary text */
			qs_summary = String.format(getResources().getString
					(R.string.version_unsupported), "4.2", Build.VERSION.RELEASE);
		}
		qs_random_color.setSummary(qs_summary);
		qs_random_color.setEnabled(sdk17);
		select_random_color.setSummary(qs_summary);
		select_random_color.setEnabled(sdk17);
		
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String key) {
		Common.settingsChanged(this);
	}
	
	@Override
	public boolean onPreferenceClick(Preference p) {
		if (p.getKey().equals("seconds_restart") ||
			p.getKey().equals("batterybar_restart") ||
			p.getKey().equals("notif_restart")) {
			dialog_killSystemUI();
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
		if (p.getKey().equals("batterybar_color_screen")){
			Intent i = new Intent(this, BatteryBarColor.class);
			startActivity(i);
		}
		if (p.getKey().equals(Common.KEY_NOTIFICATION_CHOOSE_COLOR)) {
			Intent i = new Intent(this, ChooseRandomColor.class);
			startActivity(i);
		}
		if (p.getKey().equals("toggle_launcher")) {
			showLauncherIconDialog();
		}
		if (p.getKey().equals(Common.KEY_ANIMATION_TOAST_TEST)) {
			Toast.makeText(this, R.string.anim_toast_test_title, Toast.LENGTH_SHORT).show();
		}
		return false;
	}

	private void dialog_killSystemUI() {
		new AlertDialog.Builder(this)
				.setMessage(R.string.systemui_restart_dialog)
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								killPackage("com.android.systemui");
							}
						}).setNegativeButton(android.R.string.no, null).show();
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
	
	private void showLauncherIconDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setMessage(R.string.toggle_launcher_msg);
		dialog.setPositiveButton(R.string.toggle_launcher_show,
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				setLauncherIconVisible(true);
			}
		});
		dialog.setNegativeButton(R.string.toggle_launcher_hide,
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				setLauncherIconVisible(false);
			}
		});
		dialog.show();
	}
	
	// Code modified from kennethso168's Advanced Power Menu
	private void setLauncherIconVisible(boolean visible) {
		int state = visible ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
				: PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
		final ComponentName alias = new ComponentName(this, "com.zst.xposed.xuimod.SettingActivity-Alias");
		getPackageManager().setComponentEnabledSetting(alias, state, PackageManager.DONT_KILL_APP);
	}
}
