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

import com.zst.xposed.xuimod.Common;
import com.zst.xposed.xuimod.XuiMod;
import com.zst.xposed.xuimod.mods.animation.AwesomeAnimationHelper;
import com.zst.xposed.xuimod.mods.animation.ToastAnimationHelper;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import android.content.Context;
import android.content.res.XModuleResources;
import android.content.res.XmlResourceParser;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.ViewManager;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

public class ToastAnimationMod {
	
	/*
	 * This mod is based on IME Animations "hack" to display our animation.
	 * We hook the actual methods where our Toast is added to the WindowManager
	 * and inject an animation.
	 */
	static final int ID_TOAST = 0x12345678;
	
	static XModuleResources mModRes;
	static XSharedPreferences mPref;
	
	static boolean mEnabled;
	static int mDuration;
	static int mAnimationEnterIndex;
	static int mAnimationExitIndex;
	static int mInterpolaterIndex;
	
	public static void handleLoadPackage(final LoadPackageParam lp, final XSharedPreferences pref) {
		mPref = pref;
		mModRes = XModuleResources.createInstance(XuiMod.MODULE_PATH, null);
		hook(lp);
	}
	
	private static void hook(final LoadPackageParam lpp) {
		Class<?> t = XposedHelpers.findClass("android.widget.Toast.TN", lpp.classLoader);
		XposedBridge.hookAllMethods(t, "handleShow", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				updateSettings();
				if (!mEnabled) return;
				Object thiz = param.thisObject;
				fixToastParent(thiz);
				modifyToastView(thiz);
				
				WindowManager.LayoutParams lp = (WindowManager.LayoutParams)
				XposedHelpers.findField(thiz.getClass(), "mParams").get(thiz);
				lp.windowAnimations = -1; // remove the default animation
				XposedHelpers.setObjectField(thiz, "mParams", lp);
			}
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				if (!mEnabled) return;
				Object thiz = param.thisObject;
				final View view = (View) XposedHelpers.findField(thiz.getClass(), "mView")
						.get(thiz);
				final Animation anim = retrieveAnimation(true, view.getContext());
				if (anim != null) {
					view.findViewById(ID_TOAST).startAnimation(anim);
				}
			}
		});
		
		XposedBridge.hookAllMethods(t, "handleHide", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {		
				if (!mEnabled) return;
				
				final Object thiz = param.thisObject;
				final View view = (View) XposedHelpers.findField(thiz.getClass(), "mView")
						.get(thiz);
				if (view == null) return;
				final WindowManager wm = (WindowManager) XposedHelpers.findField(thiz.getClass(),
						"mWM").get(thiz);
				final Handler handler = new Handler(view.getContext().getMainLooper());
				final Runnable runnable = new Runnable() {
					@Override
					public void run() {
						removeViewSafely(wm, view);
					}
				};
				final Animation anim = retrieveAnimation(false, view.getContext());
				anim.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {}
					@Override
					public void onAnimationRepeat(Animation animation) {}					
					@Override
					public void onAnimationEnd(Animation animation) {
						removeViewSafely(wm, view);
						handler.removeCallbacks(runnable);
					}
				});
				if (view != null && view.getParent() != null && isScreenOn(view.getContext())) {
					// Don't animate if screen is off since the handler is not
					// run when screen is off
					// and the toast remains on screen forever.
					// If parent is null, view is not added properly and may
					// cause a crash when removing.
					
					handler.postDelayed(runnable, (mDuration * 2));
					// set a handler to hide the toast in case our anim listener fails
					// or else the toast will stay on screen forever.
					
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							try {
								view.findViewById(ID_TOAST).startAnimation(anim);
							} catch (NullPointerException e) {
								// view might be null if developer calls toast.show() on the
								// same reference toast without calling toast.hide();
								removeViewSafely(wm, view);
							}
						}
					}, mDuration);
					// delay the exit animation since the default timer starts
					// when we do our enter animation
					param.setResult(null);
					
				}
			}
		});
		
	}
	
	// remove view without causing a crash by catching exception
	private static void removeViewSafely(WindowManager wm, View view) {
		try {
			wm.removeView(view);
		} catch (IllegalArgumentException npe) {
			// view might be already removed before and throws this.
		}
	}
	
	/*
	 * WindowManager doesn't allow animation but ViewGroup's within
	 * it can animate. So we hack the view to have a surrounding
	 * ViewGroup.
	 */
	private static View modifyToastView(Object thiz) throws Exception {
		final View toast_view = (View) XposedHelpers.findField(thiz.getClass(), "mNextView").get(thiz);
		toast_view.setId(ID_TOAST);
		FrameLayout frame = new FrameLayout(toast_view.getContext());
		frame.addView(toast_view);
		XposedHelpers.setObjectField(thiz, "mNextView", frame);
		return frame;
	}
	
	private static View fixToastParent(Object thiz) throws Exception {
		final View v = (View) XposedHelpers.findField(thiz.getClass(), "mNextView").get(thiz);
		if (v.getParent() != null) {
			((ViewManager) v.getParent()).removeView(v);
		}
		// already created toasts that are shown multiple times from the same object
		// will have a parent due to our framelayout mod from previous times.
		// here, we remove the view from the framelayout if necessary.
		return v;
	}
	
	private static void updateSettings() {
		mPref.reload();
		mEnabled = mPref.getBoolean(Common.KEY_ANIMATION_TOAST_ENABLED,
				Common.DEFAULT_ANIMATION_TOAST_ENABLED);
		mInterpolaterIndex = Integer.parseInt(mPref.getString(Common.KEY_ANIMATION_TOAST_INTEPOLATER,
				Common.DEFAULT_ANIMATION_TOAST_INTEPOLATER));
		mAnimationEnterIndex = Integer.parseInt(mPref.getString(Common.KEY_ANIMATION_TOAST_ENTER,
						Common.DEFAULT_ANIMATION_TOAST_ENTER));
		mAnimationExitIndex = Integer.parseInt(mPref.getString(Common.KEY_ANIMATION_TOAST_EXIT,
						Common.DEFAULT_ANIMATION_TOAST_EXIT));
		mDuration = mPref.getInt(Common.KEY_ANIMATION_TOAST_DURATION,
				Common.DEFAULT_ANIMATION_TOAST_DURATION);
	}
	
	private static boolean isScreenOn(Context ctx) {
		PowerManager powerManager = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
		return powerManager.isScreenOn();
	}
	
	private static Animation retrieveAnimation(boolean enter, Context ctx) {
		int[] animArray = ToastAnimationHelper.getAnimationInt(enter ? mAnimationEnterIndex
				: mAnimationExitIndex);
		int animInt = enter ? animArray[1] : animArray[0];
		if (animInt == 0) return null;
		
		XmlResourceParser parser = mModRes.getAnimation(animInt);
		Animation anim = (Animation) XposedHelpers.callStaticMethod(AnimationUtils.class,
				"createAnimationFromXml", ctx, parser);
		/* Creating animation from a parser instead of res ID is hidden in APIs. */
		
		Interpolator intplr = AwesomeAnimationHelper.getInterpolator(ctx, mInterpolaterIndex);
		if (intplr != null) anim.setInterpolator(intplr);
		
		anim.setDuration(mDuration);
		
		return anim;
	}
}
