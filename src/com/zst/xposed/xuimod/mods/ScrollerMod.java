/*
 * Copyright (C) 2013 XuiMod
 * Based on source code from AOKP by Steve Spear - Stevespear426, Copyright (C) 2012
 * Based on source code by Jason Fry, Copyright (C) 2011
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

/**
 * Based on:
 * 
 * (commit)
 * https://github.com/IceColdSandwich/android_frameworks_base/commit/
 * 28ef92eb671ab18372c89bf24dfd6f8e47598283
 * 
 * (article)
 * http://jasonfry.co.uk/blog/android-overscroll-revisited/
 */

package com.zst.xposed.xuimod.mods;

import com.zst.xposed.xuimod.Common;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.ViewConfiguration;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;

public class ScrollerMod {
	
	static XSharedPreferences mPref;
	static float mDensity = -1;
	
	static final float MULTIPLIER_SCROLL_FRICTION = 10000f;
	
	public static void handleLoadPackage(XSharedPreferences pref) {
		mPref = pref;
		hookViewConfiguration(ViewConfiguration.class);
		hookScrollbarNoFading(ViewConfiguration.class);
		hookOverscrollDistance(ViewConfiguration.class);
		hookOverflingDistance(ViewConfiguration.class);
		hookMaxFlingVelocity(ViewConfiguration.class);
		hookScrollFriction(ViewConfiguration.class);
		
		// TODO velocity 0 to 100000 / def 8000 // try 2000
		// overscroll dist 0 to 1000 / def 0
		// overfling dist 0 to 1000 / def 6
		// friction * 10000 // 0 to 2000 //def 150 // try 50
	}
	
	private static void hookViewConfiguration(final Class<?> clazz) {
		XposedBridge.hookAllConstructors(clazz, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				if (param.args == null) return;
				// Not null means there's a context for density scaling
				Context context = (Context) param.args[0];
				final Resources res = context.getResources();
				final float density = res.getDisplayMetrics().density;
				if (res.getConfiguration().isLayoutSizeAtLeast(
						Configuration.SCREENLAYOUT_SIZE_XLARGE)) {
					mDensity = density * 1.5f;
				} else {
					mDensity = density;
				}
			}
		});
	}

	private static void hookMaxFlingVelocity(final Class<?> clazz) {
		XposedBridge.hookAllMethods(clazz, "getMaximumFlingVelocity", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				if (!isEnabled()) return;
				param.setResult(mPref.getInt(Common.KEY_SCROLLING_VELOCITY,
						Common.DEFAULT_SCROLLING_VELOCITY));
			}
		});
		
		XposedBridge.hookAllMethods(clazz, "getScaledMaximumFlingVelocity", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				if (!isEnabled()) return;
				final int max_velocity = mPref.getInt(Common.KEY_SCROLLING_VELOCITY,
						Common.DEFAULT_SCROLLING_VELOCITY);
				if (mDensity == -1) {
					param.setResult(max_velocity);
				} else {
					final int scaled_velocity = (int) (mDensity * max_velocity + 0.5f);
					param.setResult(scaled_velocity);
				}
			}
		});
	}
	
	private static void hookScrollFriction(final Class<?> clazz) {
		XposedBridge.hookAllMethods(clazz, "getScrollFriction", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				if (!isEnabled()) return;
				final int raw_friction = mPref.getInt(Common.KEY_SCROLLING_FRICTION,
						Common.DEFAULT_SCROLLING_FRICTION);
				final float actual_friction = ((float)raw_friction) / MULTIPLIER_SCROLL_FRICTION;
				param.setResult(actual_friction);
			}
		});
	}
	
	private static void hookOverscrollDistance(final Class<?> clazz) {
		XposedBridge.hookAllMethods(clazz, "getScaledOverscrollDistance", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				if (!isEnabled()) return;
				final int overscroll_distance = mPref.getInt(Common.KEY_SCROLLING_OVERSCROLL,
						Common.DEFAULT_SCROLLING_OVERSCROLL);
				if (mDensity == -1) {
					param.setResult(overscroll_distance);
				} else {
					final int scaled_dist = (int) (mDensity * overscroll_distance + 0.5f);
					param.setResult(scaled_dist);
				}
			}
		});
	}
	
	private static void hookOverflingDistance(final Class<?> clazz) {
		XposedBridge.hookAllMethods(clazz, "getScaledOverflingDistance", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				if (!isEnabled()) return;
				final int overfling = mPref.getInt(Common.KEY_SCROLLING_OVERFLING,
						Common.DEFAULT_SCROLLING_OVERFLING);
				if (mDensity == -1) {
					param.setResult(overfling);
				} else {
					final int scaled_dist = (int) (mDensity * overfling + 0.5f);
					param.setResult(scaled_dist);
				}
			}
		});
	}
	
	private static void hookScrollbarNoFading(final Class<?> clazz) {
		XposedBridge.hookAllMethods(clazz, "isScrollbarFadingEnabled", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				if (!isEnabled()) return;
				param.setResult(!mPref.getBoolean(Common.KEY_SCROLLING_NO_FADING,
						Common.DEFAULT_SCROLLING_NO_FADING));
			}
		});
	}
	
	private static boolean isEnabled() {
		mPref.reload();
		return mPref.getBoolean(Common.KEY_SCROLLING_ENABLE, Common.DEFAULT_SCROLLING_ENABLE);
	}
}
