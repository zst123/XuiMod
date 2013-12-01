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

package com.zst.xposed.xuimod;

import com.zst.xposed.xuimod.mods.AnimationControlsMod;
import com.zst.xposed.xuimod.mods.BatteryBarMod;
import com.zst.xposed.xuimod.mods.InputMethodAnimationMod;
import com.zst.xposed.xuimod.mods.ListViewAnimationMod;
import com.zst.xposed.xuimod.mods.LockscreenTorchMod;
import com.zst.xposed.xuimod.mods.LockscreenVolumeMod;
import com.zst.xposed.xuimod.mods.RandomQuickSettingsColorMod;
import com.zst.xposed.xuimod.mods.SecondsClockMod;
import com.zst.xposed.xuimod.mods.SystemAnimationMod;
import com.zst.xposed.xuimod.mods.VolumePanelMod;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class XuiMod implements IXposedHookZygoteInit,IXposedHookLoadPackage,IXposedHookInitPackageResources{
	
	public static String MODULE_PATH = null;
	public static XSharedPreferences pref;
	
	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		MODULE_PATH = startupParam.modulePath;		
		pref = new XSharedPreferences(Common.MY_PACKAGE_NAME);
		AnimationControlsMod.initZygote();
	}
	
	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {		
		pref.reload();
		SecondsClockMod.handleLoadPackage(lpparam);
		LockscreenVolumeMod.handleLoadPackage(lpparam,pref);
		ListViewAnimationMod.handleLoadPackage(pref);
		VolumePanelMod.handleLoadPackage(lpparam,pref);
		LockscreenTorchMod.handleLoadPackage(lpparam,pref);
		AnimationControlsMod.handleLoadPackage(lpparam);
		InputMethodAnimationMod.handleLoadPackage(lpparam,pref);
		RandomQuickSettingsColorMod.loadPackage(lpparam,pref);
	}

	@Override
	public void handleInitPackageResources(InitPackageResourcesParam resparam) throws Throwable {
		pref.reload();
		SystemAnimationMod.handleInitPackageResources(pref, resparam);
		BatteryBarMod.initResources(pref, resparam);
	}
	
}
