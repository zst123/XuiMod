package com.zst.xposed.xuimod;

import com.zst.xposed.xuimod.mods.BatteryBarMod;
import com.zst.xposed.xuimod.mods.ListViewAnimationMod;
import com.zst.xposed.xuimod.mods.LockscreenVolumeMod;
import com.zst.xposed.xuimod.mods.SecondsClockMod;
import com.zst.xposed.xuimod.mods.VolumePanelMod;
import com.zst.xposed.xuimod.mods.XylonAnimMod;

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
	}
	
	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {		
		SecondsClockMod.handleLoadPackage(lpparam);
		LockscreenVolumeMod.handleLoadPackage(lpparam);
		ListViewAnimationMod.handleLoadPackage();
		VolumePanelMod.handleLoadPackage(lpparam);
	}

	@Override
	public void handleInitPackageResources(InitPackageResourcesParam resparam) throws Throwable {
		pref = new XSharedPreferences(Common.MY_PACKAGE_NAME);
		if (pref.getBoolean(Common.KEY_XYLON_ANIM, Common.DEFAULT_XYLON_ANIM)) XylonAnimMod.handleInitPackageResources(resparam);
		if (pref.getBoolean(Common.KEY_BATTERYBAR_ENABLE, Common.DEFAULT_BATTERYBAR_ENABLE)) BatteryBarMod.initResources(resparam);
	}
	
}
