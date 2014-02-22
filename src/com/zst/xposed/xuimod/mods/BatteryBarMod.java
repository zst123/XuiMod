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

package com.zst.xposed.xuimod.mods;

import com.zst.xposed.xuimod.Common;
import com.zst.xposed.xuimod.mods.batterybar.BatteryBarController;

import android.os.Build;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class BatteryBarMod {
	
	static boolean mStatusBarEnabled;
	static boolean mNavBarTopEnabled;
	static boolean mNavBarBottomEnabled;
	
	public static void handleLoadPackage(final LoadPackageParam lpp, final XSharedPreferences pref) {
		if (!lpp.packageName.equals("com.android.systemui")) return;
		if (!pref.getBoolean(Common.KEY_BATTERYBAR_ENABLE, Common.DEFAULT_BATTERYBAR_ENABLE))
			return;

		mStatusBarEnabled = pref.getBoolean(Common.KEYS_BATTERYBAR_POSITION[0],
				Common.DEFAULT_BATTERYBAR_POSITION_STATBAR);
		mNavBarTopEnabled = pref.getBoolean(Common.KEYS_BATTERYBAR_POSITION[1],
				Common.DEFAULT_BATTERYBAR_POSITION_NAVBAR);
		mNavBarBottomEnabled = pref.getBoolean(Common.KEYS_BATTERYBAR_POSITION[2],
				Common.DEFAULT_BATTERYBAR_POSITION_NAVBAR);
		
		if (mStatusBarEnabled) {
			hookStatusBar(lpp);
		}
		if (mNavBarTopEnabled || mNavBarBottomEnabled) {
			hookNavigationBar(lpp);
		}
	}
	
	private static void hookStatusBar(final LoadPackageParam lpp) {
		final XC_MethodHook hook = new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				FrameLayout root = (FrameLayout) param.thisObject;
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, 1);
				BatteryBarController battery_bar = new BatteryBarController(root.getContext());
				battery_bar.setLayoutParams(params);
				battery_bar.setVisibility(View.VISIBLE);
				root.addView(battery_bar);
			}
		};
		
		try {
			final Class<?> phoneStatusBar = XposedHelpers.findClass(
					"com.android.systemui.statusbar.phone.PhoneStatusBarView", lpp.classLoader);
			XposedBridge.hookAllMethods(phoneStatusBar, "onAttachedToWindow", hook);
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
		try {
			if (Build.VERSION.SDK_INT < 19) {
				// Google removed tablet layouts in kitkat. 
				final Class<?> tabletStatusBar = XposedHelpers.findClass(
						"com.android.systemui.statusbar.tablet.TabletStatusBarView", lpp.classLoader);
				XposedBridge.hookAllMethods(tabletStatusBar, "onAttachedToWindow", hook);
			}
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
	}
	
	private static void hookNavigationBar(final LoadPackageParam lpp) {
		Class<?> navBar;
		try {
			navBar = XposedHelpers.findClass(
					"com.android.systemui.statusbar.phone.NavigationBarView", lpp.classLoader);
		} catch (Throwable t) {
			navBar = XposedHelpers.findClass(
					"com.android.systemui.statusbar.NavigationBarView", lpp.classLoader);
		}
		XposedBridge.hookAllMethods(navBar, "onFinishInflate", new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				final LinearLayout thizz = (LinearLayout) param.thisObject;
				final View[] rotated_views = (View[]) param.thisObject.getClass()
						.getDeclaredField("mRotatedViews").get(thizz);
				
				final FrameLayout portrait = (FrameLayout) rotated_views[Surface.ROTATION_0];
				final FrameLayout landscape = (FrameLayout) rotated_views[Surface.ROTATION_90];
				
				if (mNavBarTopEnabled) {
				/* Portrait Top */
				BatteryBarController portrait_topbar = new BatteryBarController(thizz.getContext());
				portrait_topbar.setLayoutParams(new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, 1));
				portrait_topbar.setVisibility(View.VISIBLE);
				portrait.addView(portrait_topbar);
				
				/* Landscape Top */
				BatteryBarController landscape_top_bar = new BatteryBarController(thizz
						.getContext());
				landscape_top_bar.setLayoutParams(new LinearLayout.LayoutParams(1,
						LinearLayout.LayoutParams.MATCH_PARENT));
				landscape_top_bar.setVisibility(View.VISIBLE);
				landscape.addView(landscape_top_bar);
				}
				
				if (mNavBarBottomEnabled) {
				/* Portrait Bottom */
				BatteryBarController portrait_bottom_bar = new BatteryBarController(thizz
						.getContext());
				portrait_bottom_bar.setVisibility(View.VISIBLE);
				portrait.addView(portrait_bottom_bar, new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.MATCH_PARENT, 1, Gravity.BOTTOM));
				
				/* Landscape Bottom */
				BatteryBarController landscape_bottom_bar = new BatteryBarController(thizz
						.getContext());
				landscape_bottom_bar.setVisibility(View.VISIBLE);
				landscape.addView(landscape_bottom_bar, new FrameLayout.LayoutParams(1,
						FrameLayout.LayoutParams.MATCH_PARENT, Gravity.RIGHT));
				}
			}
		});
	}
}
