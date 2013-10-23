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
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class VolumePanelMod {

	protected static XSharedPreferences mPref;
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
				mPref = new XSharedPreferences(Common.MY_PACKAGE_NAME);
				Handler h = (Handler) param.thisObject;
				Dialog mDialog = (Dialog) Common.getReflection(h, "mDialog");
				Window window = mDialog.getWindow();

				/* Set Transparency */
		        WindowManager.LayoutParams lp = window.getAttributes();
		        lp.alpha = (getAlpha(mPref) * 0.01f); // Convert Percentage to Decimal
		        window.setAttributes(lp);
		        
		        /* Set Timeout */
				int MSG_TIMEOUT = 5; // Constant value from source
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
	private static int getAlpha(XSharedPreferences pref){
		try{
			int aInteger = pref.getInt(Common.KEY_VOLUME_ALPHA, Common.DEFAULT_VOLUME_ALPHA);
			if (aInteger > Common.LIMIT_MAX_VOLUME_ALPHA) aInteger = Common.LIMIT_MAX_VOLUME_ALPHA; // Higher than 100%? Ignore & use 100%
			if (aInteger < Common.LIMIT_MIN_VOLUME_ALPHA) aInteger = Common.LIMIT_MIN_VOLUME_ALPHA;  // Lower than 25%? Ignore & use 25%
			return aInteger;
		}catch(Exception e){ //If alpha string is empty, it throws exception.
			return Common.DEFAULT_VOLUME_ALPHA;
		}
	}
}
