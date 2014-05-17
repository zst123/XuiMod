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
import android.app.Dialog;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.zst.xposed.xuimod.Common;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class VolumePanelMod {

	protected static XSharedPreferences mPref;
	static final int MSG_TIMEOUT = 5; // Constant value from source
	
	public static void handleLoadPackage(LoadPackageParam lpparam, XSharedPreferences pref) {
		if (!lpparam.packageName.equals("android")) return;
		mPref=pref;
		VolumePanel_Hook(lpparam);
	}

	
	private static void VolumePanel_Hook(final LoadPackageParam lpparam) { 
		Class<?> hookClass = findClass("android.view.VolumePanel", lpparam.classLoader);
		/* When Volume Button is pressed, Volume Panel shows up.
		 * Handler shows Volume Panel then calls "resetTimeout"
		 * to close the Volume Panel after 3 seconds(default)
		 */
		XposedBridge.hookAllMethods(hookClass, "resetTimeout", new XC_MethodReplacement(){ 
			@Override 
			protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
				mPref.reload();
				Handler h = (Handler) param.thisObject;
				
				try {
					Dialog mDialog = (Dialog) XposedHelpers.getObjectField(h, "mDialog");
					Window window = mDialog.getWindow();
					/* Set Transparency */
					WindowManager.LayoutParams lp = window.getAttributes();
					lp.alpha = (getAlphaInDecimal(mPref));
					window.setAttributes(lp);
				} catch (Throwable t) {
					// If we cannot find mDialog, nevermind, make the timeout mod continue.
				}
		        
		        /* Set Timeout */
				h.removeMessages(MSG_TIMEOUT); // Code from source
		        h.sendMessageDelayed(h.obtainMessage(MSG_TIMEOUT), getTimeout(mPref)); 

		        return null;
			}
		});
    }
	
	private static int getTimeout(XSharedPreferences pref){
		try{
			String timeString = pref.getString(Common.KEY_VOLUME_TIMEOUT, Common.DEFAULT_VOLUME_TIMEOUT);
			timeString = timeString.replaceAll( "[^\\d]", "" ); //Remove all non-numeric characters
			int timeInt = Integer.parseInt(timeString);
			return timeInt;
		}catch(Exception e){ //If timeout string is empty, it throws exception.
			return Integer.parseInt(Common.DEFAULT_VOLUME_TIMEOUT);
		}
	}
	
	private static float getAlphaInDecimal(XSharedPreferences pref) {
		final int percentage = pref.getInt(Common.KEY_VOLUME_ALPHA, Common.DEFAULT_VOLUME_ALPHA); 
		return (percentage * 0.01f);
	}
}
