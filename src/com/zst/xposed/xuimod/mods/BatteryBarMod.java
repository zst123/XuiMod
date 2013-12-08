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

import android.content.res.XResources;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;

public class BatteryBarMod {
	
	 public static void initResources(final XSharedPreferences pref,
			 final InitPackageResourcesParam resparam) {
		 
		 if (!resparam.packageName.equals("com.android.systemui")) {
			 return;
		 }
		 if (!pref.getBoolean(Common.KEY_BATTERYBAR_ENABLE, Common.DEFAULT_BATTERYBAR_ENABLE)) {
			 return;
		 }
	        try {
	        	hookLayout(resparam);
	        } catch (Throwable t) {
	           t.printStackTrace();
	        }
	    }
	 private static void hookLayout(final InitPackageResourcesParam resparam) throws Throwable{
		 String name = findXML(resparam.res);
		 if (name == null) return;
		 resparam.res.hookLayout("com.android.systemui", "layout", name, new XC_LayoutInflated() {
			 @Override
			 public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
				 FrameLayout mRootView = (FrameLayout)liparam.view;
				 BatteryBarController battery_bar = new BatteryBarController(liparam.view.getContext());
				 LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
				 battery_bar.setLayoutParams(param);
				 battery_bar.setVisibility(View.VISIBLE);
				 mRootView.addView(battery_bar);
			 }
		 });
	 }
	 private static String findXML(XResources res){
		 String s = null;
		 for (int x = 0; x < layouts.length; x++){ // Continue until we find the system XML
			 
			 s = layouts[x];
			 int id = res.getIdentifier(s, "layout", "com.android.systemui");
			 if (id != 0){
				 Log.i("test",  s  + " - " + x + " is found. ID="+id);
				 break;
			 }
			 Log.d("test",  s  + " - " + x + " not found. ID="+id);
		 }
		 return s;
	 }
	 
	 final static String[] layouts = {
			 "gemini_super_status_bar", // MediaTek Gemini Phones
			 "tw_status_bar", // Samsung TouchWiz ROM
			 "zzz_status_bar_gemini_cu", // SGS3 Chinese Clone MT6575
			 "super_status_bar", // AOSP JellyBean ROM
			 "status_bar" 
			 // AOSP ICS
			 // This is last as it's present in JB (and maybe other ROMs) but ISN'T inflated.
			 // It is only used in ICS So we must check others before using this
	 };
		
}
