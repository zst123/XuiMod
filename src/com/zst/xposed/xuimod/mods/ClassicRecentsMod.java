/*
 * Copyright (C) 2013 XuiMod
 * Based on source code from SlimRoms, Copyright (C) 2013
 * Contains portions from The CyanogenMod Project, Copyright (C) 2012
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zst.xposed.xuimod.mods;

import static de.robv.android.xposed.XposedHelpers.findClass;

import java.lang.reflect.Method;

import com.zst.xposed.xuimod.Common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class ClassicRecentsMod {
	
	private static final String ACTION_SHOW_RECENT_APP_DIALOG = Common.MY_PACKAGE_NAME
			+ ".SHOW_RECENT_APP_DIALOG";
	
	public static void loadPackage(final LoadPackageParam lp, final XSharedPreferences pref) {
		if (!lp.packageName.equals("android")) return;
		try {
			final Class<?> phone_window_manager_class = findClass(
					"com.android.internal.policy.impl.PhoneWindowManager", lp.classLoader);
			final Class<?> status_bar_class = findClass(
					"com.android.server.StatusBarManagerService", lp.classLoader);
			hookRecentApps(status_bar_class, pref);
			hookForBroadcastReceiver(phone_window_manager_class);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	private static void hookRecentApps(final Class<?> hookClass, final XSharedPreferences pref)
			throws Throwable {
		XposedBridge.hookAllMethods(hookClass, "toggleRecentApps", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				pref.reload();
				if (!pref.getBoolean(Common.KEY_CLASSIC_RECENTS, Common.DEFAULT_CLASSIC_RECENTS)) {
					return;
				}
				final Context ctx = (Context) Common.getReflection(param.thisObject, "mContext");
				ctx.sendBroadcast(new Intent(ACTION_SHOW_RECENT_APP_DIALOG));
				param.setResult(null);
			}
		});
	}
	
	/* Register a receiver to call showOrHideRecentAppsDialog() */
	private static void hookForBroadcastReceiver(final Class<?> hookClass) throws Throwable {
		XposedBridge.hookAllMethods(hookClass, "init", new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
				try {
					final Context context = (Context) param.args[0];
					final Method showDialog = param.thisObject.getClass().getDeclaredMethod(
							"showOrHideRecentAppsDialog", int.class);
					final BroadcastReceiver receiver = new BroadcastReceiver() {
						@Override
						public void onReceive(Context context, Intent intent) {
							try {
								showDialog.invoke(param.thisObject, (int) 0);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					};
					final IntentFilter filter = new IntentFilter();
					filter.addAction(ACTION_SHOW_RECENT_APP_DIALOG);
					context.registerReceiver(receiver, filter);
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		});
	}
	
}
