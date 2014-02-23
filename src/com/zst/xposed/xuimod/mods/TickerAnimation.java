/*
 * Copyright (C) 2014 XuiMod
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

import com.zst.xposed.xuimod.Common;
import com.zst.xposed.xuimod.XuiMod;
import com.zst.xposed.xuimod.mods.animation.AwesomeAnimationHelper;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import android.content.Context;
import android.content.res.XModuleResources;
import android.content.res.XmlResourceParser;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ViewSwitcher;

public class TickerAnimation {
	
	// Module Variables
	static XModuleResources mModRes;
	static XSharedPreferences mPref;
	
	// SystemUI Variables
	static boolean mEntering;
	static Context mContext;
	static Animation mDefaultInAnim;
	static Animation mDefaultOutAnim;
	
	// Setting Variables
	static int mDuration;
	
	static int mMainAnimationEnterIndex;
	static int mMainAnimationExitIndex;
	static int mMainInterpolaterIndex;
	
	static int mSwitcherAnimationEnterIndex;
	static int mSwitcherAnimationExitIndex;
	static int mSwitcherInterpolaterIndex;
	
	public static void handleLoadPackage(final LoadPackageParam lp, final XSharedPreferences pref) {
		if (!lp.packageName.equals("com.android.systemui")) return;
		if (!pref.getBoolean(Common.KEY_ANIMATION_TICKER_ENABLED,
						Common.DEFAULT_ANIMATION_TICKER_ENABLED)) return;
		mPref = pref;
		mModRes = XModuleResources.createInstance(XuiMod.MODULE_PATH, null);
		hookMainAnimations(lp);
		hookMyTicker(lp);
	}
	
	// Hook to change the main entering and exiting animation
	// we hook the startAnimation method since the animation is
	// hardcoded and cannot be modified.
	private static void hookMainAnimations(final LoadPackageParam lpp) {
		XposedBridge.hookAllMethods(View.class, "startAnimation", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				if (!(param.thisObject instanceof ViewGroup)) return;
				ViewGroup thiz = (ViewGroup) param.thisObject;
				
				try {
					if (isChildOfPhoneStatusBarView(thiz)) {
						int ticker_id = thiz.getContext().getResources()
								.getIdentifier("ticker", "id", "com.android.systemui");
						
						Animation anim = retrieveAnimation(true,
								thiz.getId() == ticker_id ? mEntering : !mEntering,
										thiz.getContext());
						/* If statusbar items is exiting, ticker enters.
						 * If statusbar items is entering, ticker exits
						 * So we change the animation based on this */
						
						if (anim != null) {
							param.args[0] = anim;
						}
					}
				} catch (Exception e) {
					Log.d("test1", "inside3");
				}
			}
		});
	}
	
	// Hook the MyTicker class to get the ticker state.
	private static void hookMyTicker(final LoadPackageParam lpp) {
		final Class<?> clazz = findClass(
				"com.android.systemui.statusbar.phone.PhoneStatusBar.MyTicker", lpp.classLoader);
		
		XposedBridge.hookAllConstructors(clazz, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				final Object thix = param.thisObject;
				
				ViewSwitcher iconSwitcher = (ViewSwitcher) XposedHelpers.
						findField(thix.getClass(), "mIconSwitcher").get(thix);
				mDefaultOutAnim = iconSwitcher.getOutAnimation();
				mDefaultInAnim = iconSwitcher.getInAnimation();
				// save the default anim so we can revert to it if the anim is null later
			}
		});
		
		XposedBridge.hookAllMethods(clazz, "tickerStarting", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				updateSettings();
				changeSwitcherAnimation(param.thisObject);
				// set the switcher animation here as it is hardcoded before this method.
				mEntering = true;
			}
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				mEntering = false;
			}
		});
	}
	
	// change the switching entering and exiting animation
	private static void changeSwitcherAnimation(final Object thix) throws IllegalArgumentException, IllegalAccessException {
		ViewSwitcher iconSwitcher = (ViewSwitcher) XposedHelpers.
				findField(thix.getClass(), "mIconSwitcher").get(thix);
		ViewSwitcher textSwitcher = (ViewSwitcher) XposedHelpers.
				findField(thix.getClass(), "mTextSwitcher").get(thix);
		
		Animation enter_anim = retrieveAnimation(false, true, textSwitcher.getContext());
		Animation exit_anim = retrieveAnimation(false, false, textSwitcher.getContext());
		
		if (enter_anim == null) 
			enter_anim = mDefaultInAnim;
		
		if (exit_anim == null) 
			exit_anim = mDefaultOutAnim;
		
		textSwitcher.setInAnimation(enter_anim);
		textSwitcher.setOutAnimation(exit_anim);
		iconSwitcher.setInAnimation(enter_anim);
		iconSwitcher.setOutAnimation(exit_anim);
		
	}
	
	private static void updateSettings() {
		mPref.reload();
		
		mDuration = mPref.getInt(Common.KEY_ANIMATION_TICKER_DURATION,
				Common.DEFAULT_ANIMATION_TICKER_DURATION);
		
		mMainInterpolaterIndex = Integer.parseInt(mPref.getString(
				Common.KEY_ANIMATION_TICKER_MAIN_INTEPOLATER,
				Common.DEFAULT_ANIMATION_TICKER_MAIN_INTEPOLATER));
		mMainAnimationEnterIndex = Integer.parseInt(mPref.getString(
				Common.KEY_ANIMATION_TICKER_MAIN_ENTER,
				Common.DEFAULT_ANIMATION_TICKER_MAIN_ENTER));
		mMainAnimationExitIndex = Integer.parseInt(mPref.getString(
				Common.KEY_ANIMATION_TICKER_MAIN_EXIT,
				Common.DEFAULT_ANIMATION_TICKER_MAIN_EXIT));
		
		mSwitcherInterpolaterIndex = Integer.parseInt(mPref.getString(
				Common.KEY_ANIMATION_TICKER_SWITCHER_INTEPOLATER,
				Common.DEFAULT_ANIMATION_TICKER_SWITCHER_INTEPOLATER));
		mSwitcherAnimationEnterIndex = Integer.parseInt(mPref.getString(
				Common.KEY_ANIMATION_TICKER_SWITCHER_ENTER,
				Common.DEFAULT_ANIMATION_TICKER_SWITCHER_ENTER));
		mSwitcherAnimationExitIndex = Integer.parseInt(mPref.getString(
				Common.KEY_ANIMATION_TICKER_SWITCHER_EXIT,
				Common.DEFAULT_ANIMATION_TICKER_SWITCHER_EXIT));
	}
	
	// Check if the parent or parent's parent is a PhoneStatusBarView
	private static boolean isChildOfPhoneStatusBarView(ViewGroup vg) {
		try {
			return vg.getParent().getClass().getName().endsWith("PhoneStatusBarView") ||
					vg.getParent().getParent().getClass().getName().endsWith("PhoneStatusBarView");
		}catch (Exception e) {
			return false;
		}
	}
	
	private static Animation retrieveAnimation(boolean main, boolean enter, Context ctx) {
		int animation_int;
		if (main) {
			animation_int = enter ? mMainAnimationEnterIndex : mMainAnimationExitIndex;
		} else {
			animation_int = enter ? mSwitcherAnimationEnterIndex : mSwitcherAnimationExitIndex;
		}
		
		int[] animArray = AwesomeAnimationHelper.getAnimations(animation_int);
		int animInt = enter ? animArray[1] : animArray[0];
		if (animInt == 0) return null;
		
		XmlResourceParser parser = mModRes.getAnimation(animInt);
		Animation anim = (Animation) XposedHelpers.callStaticMethod(AnimationUtils.class,
				"createAnimationFromXml", ctx, parser);
		/* Creating animation from a parser instead of res ID is hidden in APIs. */
		
		Interpolator intplr = AwesomeAnimationHelper.getInterpolator(ctx, 
				main ? mMainInterpolaterIndex : mSwitcherInterpolaterIndex);
		if (intplr != null) anim.setInterpolator(intplr);
		
		anim.setDuration(mDuration);
		
		return anim;
	}
}
