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

import static de.robv.android.xposed.XposedHelpers.findClass;
import android.os.Build;
import android.view.KeyEvent;

import com.zst.xposed.xuimod.Common;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class LockscreenVolumeMod {
	
	protected static XSharedPreferences mPref;
	
	public static void handleLoadPackage(LoadPackageParam lpparam, XSharedPreferences pref ) {
		if (!lpparam.packageName.equals("android")) return;
		mPref = pref;
		try {
			Lockscreen_Volume_Button(lpparam);
		} catch (Throwable t) {
		}
	}
	
	private static String getClassStringFromSdk(int sdk) {
		switch (sdk) {
		case 14:
		case 15:
			return "com.android.internal.policy.impl.KeyguardViewBase";
			/* Ice Cream Sandwich */
		case 16:
		case 17:
		case 18:
			return "com.android.internal.policy.impl.keyguard.KeyguardViewManager.ViewManagerHost";
			/* Jelly Bean */
		case 19:
			return "com.android.keyguard.KeyguardViewManager.ViewManagerHost";
			/* KitKat */
		}
		return null;
	}

    private static void Lockscreen_Volume_Button(final LoadPackageParam lpparam) { 
		String classs = getClassStringFromSdk(Build.VERSION.SDK_INT);
		Class<?> hookClass = findClass(classs, lpparam.classLoader);
		XposedBridge.hookAllMethods(hookClass, "dispatchKeyEvent", new XC_MethodHook(XC_MethodHook.PRIORITY_HIGHEST){
			@Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				KeyEvent event = (KeyEvent)param.args[0];
				if (!isVolume(event))return; //Key is not a volume key. Let system handle
				mPref.reload();
				if (mPref.getBoolean(Common.KEY_LOCKSCREEN_VOLUME, Common.DEFAULT_LOCKSCREEN_VOLUME)){
					param.setResult(Boolean.TRUE);
				} // Change return result to TRUE so system knows we are handling it	
			}
		});		
    }
    private static boolean isVolume(KeyEvent event){
    	if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) return true; 
    	if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) return true;
    	return false;
    }
}