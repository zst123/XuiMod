package com.zst.xposed.xuimod;

import java.io.DataOutputStream;

import com.zst.xposed.xuimod.mods.SecondsClockMod;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
