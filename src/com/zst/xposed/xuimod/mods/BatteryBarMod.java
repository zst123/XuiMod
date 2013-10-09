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
				 FrameLayout mRootView = (FrameLayout)liparam.view;
				 BatteryBarController mLayoutClock = new BatteryBarController(liparam.view.getContext());
				 LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
				 mLayoutClock.setLayoutParams(param);
				 mLayoutClock.setVisibility(View.VISIBLE);
				 mRootView.addView(mLayoutClock);
			 }
		 });
	 }
	 private static String findXML(XResources res){
		 String s = null;
		 for (int x = 1; x < 5; x++){ // Continue until we find the system XML
			 
			 s = getName(x);
			 int id = res.getIdentifier(s, "layout", "com.android.systemui");
			 if (id != 0){
				 Log.i("test",  s  + " - " + x + " is found. ID="+id);
				 break;
			 }
			 Log.d("test",  s  + " - " + x + " not found. ID="+id);
		 }
		 return s;
	 }
	 private static String getName(int i){ 
		 switch (i){
		 case 1: // MediaTek Gemini Phones
			 return "gemini_super_status_bar"; 
		 case 2: // Samsung TouchWiz ROM
			 return "tw_status_bar"; 
		 case 3: // AOSP JellyBean ROM
			 return "super_status_bar"; 
		 case 4: // Break Switch to use "status_bar";
			 break; 
		 }
		 return "status_bar"; // AOSP ICS 
		 //This is last as it's present in JB (and maybe other ROMs) but ISN'T inflated.
		 //It is only used in ICS
		 //So we must check others before using this 
	 }
}
