/*
 * Copyright (C) 2013 XuiMod
 * Based on source obtained from the PACman Project, Copyright (C) 2012
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

package com.zst.xposed.xuimod.mods.batterybar;

import com.zst.xposed.xuimod.Common;

import de.robv.android.xposed.XSharedPreferences;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.BatteryManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class BatteryBarController extends LinearLayout {
		
	private static int LAST_BATTERY_LEVEL = 0;

	BatteryBarView mainBar;
	BatteryBarView alternateStyleBar;

	public static final int STYLE_UNKNOWN = -1;
	public static final int STYLE_REGULAR = 0;
	public static final int STYLE_SYMMETRIC = 1;

	int mStyle = STYLE_UNKNOWN;
	int mLocation = 0;

	protected final static int CURRENT_LOC = 1;
	int mLocationToLookFor = 1;

	private int mBatteryLevel = 0;
	private boolean mBatteryCharging = false;

	boolean isAttached = false;
	boolean isVertical = false;

	public BatteryBarController(Context context) {
		this(context, null);
		mLocationToLookFor = 1;
	}

	public BatteryBarController(Context context, AttributeSet attrs) {
		super(context, attrs);
		mLocationToLookFor = 1;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (!isAttached) {
			isVertical = (getLayoutParams().height == LayoutParams.MATCH_PARENT);

			isAttached = true;
			IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_BATTERY_CHANGED);
			filter.addAction(Common.ACTION_SETTINGS_CHANGED);

			getContext().registerReceiver(mIntentReceiver, filter);

			updateSettings();
		}
	}

	private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
				mBatteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,
						0);
				mBatteryCharging = intent.getIntExtra(
						BatteryManager.EXTRA_STATUS, 0) == BatteryManager.BATTERY_STATUS_CHARGING;
				LAST_BATTERY_LEVEL = mBatteryLevel;
			} else if (Common.ACTION_SETTINGS_CHANGED.equals(action)) {
				XSharedPreferences pref = new XSharedPreferences(Common.MY_PACKAGE_NAME);
				boolean enabled = pref.getBoolean(Common.KEY_BATTERYBAR_ENABLE,
						Common.DEFAULT_BATTERYBAR_ENABLE);
				if (enabled){
					updateSettings();
				}else{
					removeBars();
					mStyle = STYLE_UNKNOWN; 
					// Reset the style so oldStyle isn't the same
				}
			}
		}
	};

	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (isAttached) {
			getHandler().postDelayed(new Runnable() {
				@Override
				public void run() {
					updateSettings();
				}
			}, 500);

		}
	}

	public void setHeight(int pixels) {

		ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) getLayoutParams();

		if (isVertical) {
			params.width = pixels;
		} else {
			params.height = pixels;
		}
		setLayoutParams(params);
	}
	
	public void addBar() {	
		LayoutParams ll = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1);

		mBatteryLevel = LAST_BATTERY_LEVEL;
		if (mStyle == STYLE_REGULAR) {
			BatteryBarView bar = new BatteryBarView(getContext(),
					mBatteryCharging, mBatteryLevel, isVertical);

			addView(bar, ll);
		} else if (mStyle == STYLE_SYMMETRIC) {
			BatteryBarView bar1 = new BatteryBarView(getContext(),
					mBatteryCharging, mBatteryLevel, isVertical);
			BatteryBarView bar2 = new BatteryBarView(getContext(),
					mBatteryCharging, mBatteryLevel, isVertical);

			if (isVertical) {
				bar2.setRotation(180);
				addView(bar2, ll);
				addView(bar1, ll);
			} else {
				bar1.setRotation(180);
				addView(bar1, ll);
				addView(bar2, ll);
			}
		}
	}

	public int getSettingsHeight(XSharedPreferences pref) {
		DisplayMetrics metrics = getContext().getResources()
				.getDisplayMetrics();
		float dp = (float) pref.getInt(Common.KEY_BATTERYBAR_HEIGHT,
				Common.DEFAULT_BATTERYBAR_HEIGHT);
		return (int) ((metrics.density * dp) + 0.5);
	}

	public void removeBars() {
		removeAllViews();
	}

	public void updateSettings() {
		int oldStyle = mStyle;
		XSharedPreferences pref = new XSharedPreferences(Common.MY_PACKAGE_NAME);
		mStyle = pref.getBoolean(Common.KEY_BATTERYBAR_STYLE,
				Common.DEFAULT_BATTERYBAR_STYLE) ? STYLE_SYMMETRIC
				: STYLE_REGULAR;
		mLocation = 1;
		setHeight(getSettingsHeight(pref));
		if (oldStyle == mStyle) return;
		
		if (isLocationValid(mLocation)) {
			removeBars();
			addBar();
			setVisibility(View.VISIBLE);
		} else {
			removeBars();
			setVisibility(View.GONE);
		}
	}

	protected boolean isLocationValid(int location) {
		return mLocationToLookFor == location;
	}
}