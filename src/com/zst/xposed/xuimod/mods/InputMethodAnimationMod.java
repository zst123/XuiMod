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

import java.lang.reflect.Field;

import com.zst.xposed.xuimod.Common;
import com.zst.xposed.xuimod.XuiMod;
import com.zst.xposed.xuimod.mods.animation.InputMethodAnimationHelper;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.XModuleResources;
import android.content.res.XmlResourceParser;
import android.inputmethodservice.InputMethodService;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Interpolator;

public class InputMethodAnimationMod {
	
	/* This mod uses a "hack" to display our animation. Since window.setWindowAnimations(ID)
	 * only accepts android.R.anim ID's , we display our animation by hooking the show and 
	 * hide methods of the InputMethodService */
	
	/* Cached preference variables */
	static XSharedPreferences mPref;
	static int mDuration;
	static int mAnimationEnterIndex;
	static int mAnimationExitIndex;
	static int mInterpolaterIndex;
	
	/* Cached class variables */
	static Field mRootField;
	static View mRootView;
	static Window mWindow;
	
	public static void handleLoadPackage(final LoadPackageParam lp, final XSharedPreferences pref){//ADD to FRONT
		mPref = pref;
		hook_onWindowShown(lp);
		hook_initView(lp);
		hook_hideWindow(lp);
	}
	
	private static void hook_initView(final LoadPackageParam lpp){
		XposedBridge.hookAllMethods(InputMethodService.class, "initViews", new XC_MethodHook(){
			@Override 
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				InputMethodService thiz = (InputMethodService) param.thisObject;
				Dialog dialog = thiz.getWindow(); //Yes, this getWindow is misleading.
				mWindow = dialog.getWindow();
				
		        find_mRootView();
				updateSettings();
		        setUpBroadcastReceiver(mWindow.getContext());
			}
		});
	}
	
	/* "Hack": Use a loop to find the field
	 * 
	 * I didn't use 'class.getDeclaredFields("mRootView")' because it's always null for some
	 * STRANGE reason. But since we are only finding this field in initViews, we are calling it
	 * only once. So performance impact is not so much */
	private static void find_mRootView(){
		for ( Field m : InputMethodService.class.getDeclaredFields() ) {
			if (m.getName().equals("mRootView")){
				mRootField = m;
				mRootField.setAccessible(true);
				break;
			}
		}
	}
	
	/* onWindowShown is called JUST BEFORE the view is visible. */
	private static void hook_onWindowShown(final LoadPackageParam lpp) {
		XposedBridge.hookAllMethods(InputMethodService.class, "onWindowShown", new XC_MethodHook(){
			@Override 
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {			
				if (mAnimationEnterIndex == 0) return;
				InputMethodService thiz = (InputMethodService) param.thisObject;
				Dialog dialog = thiz.getWindow();
				mWindow = dialog.getWindow();
				mWindow.setWindowAnimations(-1); 
				//Set Animation to nothing
				View decor = mWindow.getDecorView();
				decor.setVisibility(View.VISIBLE);
				/* We want our animation to be shown ASAP, set it to visible in advance*/
				Animation anim = retrieveAnimation(true, mWindow.getContext());
				mRootView = ((View)mRootField.get(thiz));
				mRootView.startAnimation(anim);
				/* Get the view (and cache to static variable) and start animating*/		
			}
		});
	}
	
	private static void hook_hideWindow(final LoadPackageParam lpp) {
		XposedBridge.hookAllMethods(InputMethodService.class, "onWindowHidden", 
				new XC_MethodHook(){
			@Override 
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				if (mAnimationExitIndex == 0) return;
				
				final InputMethodService thiz = (InputMethodService) param.thisObject;
				final Dialog d = thiz.getWindow();
				mWindow = d.getWindow();
				
				final View decor = mWindow.getDecorView();
				decor.setVisibility(View.VISIBLE);
				/* This is a "hack" since onWindowHidden is called JUST AFTER the view has 
				 * been hidden. So we immediately set it to visible before the view can be
				 * invalidated by the system */
				Animation anim = retrieveAnimation(false, mWindow.getContext());
				anim.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {}
					@Override
					public void onAnimationRepeat(Animation animation) {}
					@Override	
					public void onAnimationEnd(Animation animation) {
						decor.setVisibility(View.GONE);
					}
				});
				/* We need this listener because startAnimation is Async and we need to set
				 * the visibility to GONE right after the animation */
				
				mRootView.startAnimation(anim);
				/* After all our hard work, animate!! */

			}
		});
	}
	
	/** Preference Retrieving Methods*/
	private final static BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateSettings();
		}
	};
	
	private static void setUpBroadcastReceiver(Context ctx){
		IntentFilter filter = new IntentFilter();
		filter.addAction(Common.ACTION_SETTINGS_CHANGED);
		ctx.registerReceiver(broadcastReceiver, filter);
	}
	
	private static void updateSettings() {
		mPref.reload();
		mDuration = mPref.getInt(Common.KEY_ANIMATION_IME_DURATION,
				Common.DEFAULT_ANIMATION_IME_DURATION);
		mInterpolaterIndex = Integer.parseInt(mPref.getString(Common.KEY_ANIMATION_IME_INTEPOLATER,
				Common.DEFAULT_ANIMATION_IME_INTEPOLATER));
		mAnimationEnterIndex = Integer.parseInt(mPref.getString(Common.KEY_ANIMATION_IME_ENTER,
				Common.DEFAULT_ANIMATION_IME_ENTER));
		mAnimationExitIndex = Integer.parseInt(mPref.getString(Common.KEY_ANIMATION_IME_EXIT,
				Common.DEFAULT_ANIMATION_IME_EXIT));
	}
	
	private static Animation retrieveAnimation(boolean enter, Context ctx){
    	int[]animArray = InputMethodAnimationHelper
    			.getAnimationInt(enter ? mAnimationEnterIndex : mAnimationExitIndex);
    	int animInt = enter ? animArray[1] : animArray[0];
    	
    	XModuleResources modRes = XModuleResources.createInstance(XuiMod.MODULE_PATH, null);
		XmlResourceParser parser = modRes.getAnimation(animInt);
		Animation anim = (Animation) XposedHelpers.callStaticMethod(AnimationUtils.class,
				"createAnimationFromXml", ctx, parser);
    	/** Creating XML from a parser instead of res ID is hidden in APIs. Using reflection */
		Interpolator intplr= InputMethodAnimationHelper.getInterpolator(ctx, mInterpolaterIndex);
		if (intplr != null) anim.setInterpolator(intplr);
		anim.setDuration(mDuration);

		return anim;
	}
}
