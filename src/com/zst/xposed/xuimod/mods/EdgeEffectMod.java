package com.zst.xposed.xuimod.mods;

import com.zst.xposed.xuimod.Common;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.widget.EdgeEffect;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class EdgeEffectMod {
	
	static XSharedPreferences mPref;
	
	public static void handleLoadPackage(LoadPackageParam lpparam, XSharedPreferences pref) {
		mPref = pref;
		hook(lpparam);
	}
	
	private static void hook(LoadPackageParam o) {
		XposedBridge.hookAllConstructors(EdgeEffect.class, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
				mPref.reload();
				if (!mPref.getBoolean(Common.KEY_SCROLLING_GLOW_ENABLE,
						Common.DEFAULT_SCROLLING_GLOW_ENABLE))
					return;
				
				final Drawable edge = (Drawable) XposedHelpers.getObjectField(param.thisObject, "mEdge");
				final Drawable glow = (Drawable) XposedHelpers.getObjectField(param.thisObject, "mGlow");
				
				if (mPref.getBoolean(Common.KEY_SCROLLING_GLOW_DISABLE,
						Common.DEFAULT_SCROLLING_GLOW_DISABLE)) {
					//Disable glow by setting transparent color
					final Drawable transparent = new ColorDrawable(Color.TRANSPARENT);
					XposedHelpers.setObjectField(param.thisObject, "mEdge", transparent);
					XposedHelpers.setObjectField(param.thisObject, "mGlow", transparent);
					return;
				}
				
				final String str_edge = mPref.getString(Common.KEY_SCROLLING_GLOW_EDGE,
						Common.DEFAULT_SCROLLING_GLOW_EDGE);
				final String str_glow = mPref.getString(Common.KEY_SCROLLING_GLOW_GLOW,
						Common.DEFAULT_SCROLLING_GLOW_GLOW);
				
				final int edge_color = Common.parseColorFromString(str_edge, Common.COLOR_HOLO_BLUE);
				final int glow_color = Common.parseColorFromString(str_glow, Common.COLOR_HOLO_BLUE);
				
				glow.setColorFilter(ignoreColorAlpha(glow_color), PorterDuff.Mode.SRC_ATOP);
				edge.setColorFilter(ignoreColorAlpha(edge_color), PorterDuff.Mode.SRC_ATOP);
				
				/* When setting a color filter with translucency, the previous color will be
				 * see through like in layers. We don't want that, so we separate the RGB 
				 * Also, "drawable.setAlpha" has no effect on the glow, it's probably the 
				 * canvas drawing (??) */
				
				XposedHelpers.setObjectField(param.thisObject, "mEdge", edge);
				XposedHelpers.setObjectField(param.thisObject, "mGlow", glow);
			}
		});
	}
	
	private static int ignoreColorAlpha(int color) {
		return Color.argb(255, Color.red(color), Color.green(color), Color.blue(color));
	}
}
