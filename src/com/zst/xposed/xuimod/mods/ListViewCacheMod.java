package com.zst.xposed.xuimod.mods;

import android.widget.AbsListView;

import com.zst.xposed.xuimod.Common;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;

public class ListViewCacheMod {

	private static XSharedPreferences mPref;
	private static int cache;

	public static void handleLoadPackage(XSharedPreferences pref){
		mPref = pref;
		cache = Integer.parseInt(mPref.getString(Common.KEY_LISTVIEW_CACHE, Common.DEFAULT_LISTVIEW_CACHE));
		XposedBridge.hookAllMethods(AbsListView.class, "initAbsListView", new XC_MethodHook(){
			@Override 
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {

				AbsListView item = (AbsListView)param.thisObject; 
				item.setPersistentDrawingCache(cache);
				// Get object & Apply persistent cache value.
			}			
		});	
	}
}
