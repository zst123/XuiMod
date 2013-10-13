package com.zst.xposed.xuimod.mods;

import com.zst.xposed.xuimod.mods.batterybar.BatteryBarController;

import android.content.res.XResources;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;

public class BatteryBarMod {
    private static final String[] layouts = {
		 "gemini_super_status_bar", // MediaTek Gemini Phones
		 "tw_status_bar", // Samsung TouchWiz ROM
		 "super_status_bar", // AOSP JellyBean ROM
		 "status_bar" 
		 // AOSP ICS
		 // This is last as it's present in JB (and maybe other ROMs) but ISN'T inflated.
		 // It is only used in ICS So we must check others before using this
	 };
    
	 public static void initResources( final InitPackageResourcesParam resparam) {
		 if (!resparam.packageName.equals("com.android.systemui")) return;
	        try {
	        	hookLayout(resparam);
	        } catch (Throwable t) {
	           t.printStackTrace();
	        }
	    }
	 private static void hookLayout(final InitPackageResourcesParam resparam) throws Throwable{
		 String name = findXML(resparam.res);
		 if (name == null) return;
		 //boolean isJellyBean = Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;
         //String name = isJellyBean ? "super_status_bar" : "status_bar"; 
		 resparam.res.hookLayout("com.android.systemui", "layout", name, new XC_LayoutInflated() {
			 @Override
			 public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
				 FrameLayout root_view = (FrameLayout)liparam.view;
				 BatteryBarController battery_bar = new BatteryBarController(liparam.view.getContext());
				 LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
				 battery_bar.setLayoutParams(param);
				 battery_bar.setVisibility(View.VISIBLE);
				 root_view.addView(battery_bar);
			 }
		 });
	 }

	private static String findXML(XResources res) {
		String s = null;
		// Continue until we find the system XML
		for (String layout : layouts) {
			int id = res.getIdentifier(s, "layout", "com.android.systemui");
			if (id != 0) {
				s = layout;
				break;
			}
		}
		return s;
	}
}
