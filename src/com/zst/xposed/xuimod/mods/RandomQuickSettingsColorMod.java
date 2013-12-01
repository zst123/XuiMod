package com.zst.xposed.xuimod.mods;

import static de.robv.android.xposed.XposedHelpers.findClass;

import java.util.Random;

import com.zst.xposed.xuimod.Common;

import android.os.Build;
import android.view.View;
import android.widget.FrameLayout;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class RandomQuickSettingsColorMod {
	
	static String[] mColorArray = new String[8];
	static boolean mQuickSettingColorEnabled;
	
	public static void loadPackage(final LoadPackageParam lp, final XSharedPreferences main_pref) {
		if (!lp.packageName.equals("com.android.systemui")) return;
		if (Build.VERSION.SDK_INT < 17) return;
		
		mQuickSettingColorEnabled = main_pref.getBoolean(
				Common.KEY_NOTIFICATION_RANDOM_QS_TILE_COLOR,
				Common.DEFAULT_NOTIFICATION_RANDOM_QS_TILE_COLOR);
		
		if (!mQuickSettingColorEnabled) return;
		
		XSharedPreferences random_color_pref = new XSharedPreferences(Common.MY_PACKAGE_NAME,
				Common.RANDOM_COLOR_PREFERENCE_FILENAME);
		for (int x = 0; x < 8; x++) {
			mColorArray[x] = random_color_pref.getString(
					Common.RANDOM_COLOR_PREFERENCE_FILENAME + "_" + x, Common.COLOR_HOLO_BLUE);
		}
		
		if (mQuickSettingColorEnabled) {
			try {
				hookQuickSettingsTileView(lp);
			} catch (Throwable e) {
			}
		}
	}
	
	private static void hookQuickSettingsTileView(final LoadPackageParam lpp) throws Throwable {
		final Class<?> c = findClass("com.android.systemui.statusbar.phone.QuickSettingsTileView",
				lpp.classLoader);
		XposedBridge.hookAllConstructors(c, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				setBackgroundStyle((FrameLayout) param.thisObject);
			}
		});
	}
	
	private static void setBackgroundStyle(View v) {
		final String color_str = mColorArray[new Random().nextInt(mColorArray.length)];
		final int color = Common.parseColorFromString(color_str, Common.COLOR_HOLO_BLUE);
		v.setBackgroundColor(color);
	}
}
