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

import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class BatteryBarMod {
	
	public static void handleLoadPackage(final LoadPackageParam lpp, final XSharedPreferences pref) {
		if (!lpp.packageName.equals("com.android.systemui")) return;
		if (!pref.getBoolean(Common.KEY_BATTERYBAR_ENABLE, Common.DEFAULT_BATTERYBAR_ENABLE))
			return;
		
		hookStatusBar(lpp);
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
			XposedBridge.hookAllMethods(phoneStatusBar, "onFinishInflate", hook);
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
		try {
			final Class<?> tabletStatusBar = XposedHelpers.findClass(
					"com.android.systemui.statusbar.tablet.TabletStatusBarView", lpp.classLoader);
			XposedBridge.hookAllMethods(tabletStatusBar, "onFinishInflate", hook);
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
	}
}
