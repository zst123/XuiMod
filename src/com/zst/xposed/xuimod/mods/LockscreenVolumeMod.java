package com.zst.xposed.xuimod.mods;

import static de.robv.android.xposed.XposedHelpers.findClass;


import android.view.KeyEvent;

import com.zst.xposed.xuimod.Common;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class LockscreenVolumeMod {
	
	public static void handleLoadPackage(LoadPackageParam lpparam ) {
		if (!lpparam.packageName.equals("android")) return;
		Lockscreen_Volume_Button(lpparam);
	}
    private static void Lockscreen_Volume_Button(final LoadPackageParam lpparam) { 
		Class<?> hookClass = findClass("com.android.internal.policy.impl.keyguard.KeyguardViewManager.ViewManagerHost", lpparam.classLoader);
		XposedBridge.hookAllMethods(hookClass, "dispatchKeyEvent", new XC_MethodHook(){
			@Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				KeyEvent event = (KeyEvent)param.args[0];
				if (!isVolume(event))return; //Key is not a volume key. Let system handle
				
				XSharedPreferences pref = new XSharedPreferences(Common.MY_PACKAGE_NAME);
				if (pref.getBoolean(Common.KEY_LOCKSCREEN_VOLUME, Common.DEFAULT_LOCKSCREEN_VOLUME)){
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