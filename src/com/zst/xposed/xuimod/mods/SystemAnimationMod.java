package com.zst.xposed.xuimod.mods;

import android.content.res.XModuleResources;
import android.content.res.XResources;

import com.zst.xposed.xuimod.Common;
import com.zst.xposed.xuimod.R;
import com.zst.xposed.xuimod.XuiMod;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

public class SystemAnimationMod {

	private static final int NO_ANIMATION = 0;
	private static final int XYLON_ANIMATION = 1;
	private static final int TN_ANIMATION = 2;
	
	public static void handleInitPackageResources(XSharedPreferences pref, InitPackageResourcesParam resparam){
		if (!resparam.packageName.equals("android")) return;
		
		int transition = Integer.parseInt(pref.getString(Common.KEY_WINDOW_TRANSITIONS, Common.DEFAULT_WINDOW_TRANSITIONS));
		switch (transition){
		case NO_ANIMATION:
			return;
			
		case XYLON_ANIMATION:
			initXylon(resparam);
			break;
			
		case TN_ANIMATION:
			initTonyNoobAnim(resparam);
			break;
		}
	}
	
	/* Animations XML grabbed and slightly modified from Xylon ROMs : http://xylon.androidvenue.com/downloads-2/
	 * Inspired by the discontinued mod : http://forum.xda-developers.com/showthread.php?t=2012179 
	 * Xylon Animation Replacement
	 */
	private static void initXylon(InitPackageResourcesParam resparam){
		XModuleResources modRes = XModuleResources.createInstance(XuiMod.MODULE_PATH, resparam.res);
		XResources.setSystemWideReplacement("android", "anim", "window_move_from_decor", modRes.fwd(R.anim.xylon_window_move_from_decor));
		XResources.setSystemWideReplacement("android", "anim", "options_panel_enter", modRes.fwd(R.anim.xylon_options_panel_enter));
		XResources.setSystemWideReplacement("android", "anim", "options_panel_exit", modRes.fwd(R.anim.xylon_options_panel_exit));
		XResources.setSystemWideReplacement("android", "anim", "recent_enter", modRes.fwd(R.anim.xylon_recent_enter));
		XResources.setSystemWideReplacement("android", "anim", "recent_exit", modRes.fwd(R.anim.xylon_recent_exit));
		XResources.setSystemWideReplacement("android", "anim", "recents_fade_in", modRes.fwd(R.anim.xylon_recents_fade_in));
		XResources.setSystemWideReplacement("android", "anim", "recents_fade_out", modRes.fwd(R.anim.xylon_recents_fade_out));
		XResources.setSystemWideReplacement("android", "anim", "submenu_enter", modRes.fwd(R.anim.xylon_submenu_enter));
		XResources.setSystemWideReplacement("android", "anim", "submenu_exit", modRes.fwd(R.anim.xylon_submenu_exit));
		XResources.setSystemWideReplacement("android", "anim", "input_method_enter", modRes.fwd(R.anim.xylon_input_method_enter));  
		XResources.setSystemWideReplacement("android", "anim", "input_method_exit", modRes.fwd(R.anim.xylon_input_method_exit));
		XResources.setSystemWideReplacement("android", "anim", "input_method_extract_exit", modRes.fwd(R.anim.xylon_input_method_extract_exit));
		XResources.setSystemWideReplacement("android", "anim", "input_method_extract_enter", modRes.fwd(R.anim.xylon_input_method_extract_enter));
		XResources.setSystemWideReplacement("android", "anim", "input_method_fancy_enter", modRes.fwd(R.anim.xylon_input_method_fancy_enter));
		XResources.setSystemWideReplacement("android", "anim", "input_method_fancy_exit", modRes.fwd(R.anim.xylon_input_method_fancy_exit));
		XResources.setSystemWideReplacement("android", "anim", "app_starting_exit", modRes.fwd(R.anim.xylon_app_starting_exit));
		XResources.setSystemWideReplacement("android", "anim", "activity_close_enter", modRes.fwd(R.anim.xylon_activity_close_enter));
		XResources.setSystemWideReplacement("android", "anim", "activity_close_exit", modRes.fwd(R.anim.xylon_activity_close_exit));
		XResources.setSystemWideReplacement("android", "anim", "activity_open_enter", modRes.fwd(R.anim.xylon_activity_open_enter));
		XResources.setSystemWideReplacement("android", "anim", "activity_open_exit", modRes.fwd(R.anim.xylon_activity_open_exit));
		XResources.setSystemWideReplacement("android", "anim", "dialog_enter", modRes.fwd(R.anim.xylon_dialog_enter));
		XResources.setSystemWideReplacement("android", "anim", "dialog_exit", modRes.fwd(R.anim.xylon_dialog_exit));
		XResources.setSystemWideReplacement("android", "anim", "toast_exit", modRes.fwd(R.anim.xylon_toast_exit)); 
		XResources.setSystemWideReplacement("android", "anim", "toast_enter", modRes.fwd(R.anim.xylon_toast_enter));
		XResources.setSystemWideReplacement("android", "anim", "lock_screen_exit", modRes.fwd(R.anim.xylon_lock_screen_exit));
		XResources.setSystemWideReplacement("android", "anim", "task_close_enter", modRes.fwd(R.anim.xylon_task_close_enter));
		XResources.setSystemWideReplacement("android", "anim", "task_close_exit", modRes.fwd(R.anim.xylon_task_close_exit));
		XResources.setSystemWideReplacement("android", "anim", "task_open_enter", modRes.fwd(R.anim.xylon_task_open_enter));
		XResources.setSystemWideReplacement("android", "anim", "task_open_exit", modRes.fwd(R.anim.xylon_task_open_exit));
	}
	
	/* Animations XML grabbed and slightly modified from tony-noob's Animation : http://forum.xda-developers.com/showthread.php?t=1342643
	 * TN Animation Replacement
	 */
	private static void initTonyNoobAnim(InitPackageResourcesParam resparam){
		XModuleResources modRes = XModuleResources.createInstance(XuiMod.MODULE_PATH, resparam.res);
		XResources.setSystemWideReplacement("android", "anim", "window_move_from_decor", modRes.fwd(R.anim.tn_window_move_from_decor));
		XResources.setSystemWideReplacement("android", "anim", "options_panel_enter", modRes.fwd(R.anim.tn_options_panel_enter));
		XResources.setSystemWideReplacement("android", "anim", "options_panel_exit", modRes.fwd(R.anim.tn_options_panel_exit));
		XResources.setSystemWideReplacement("android", "anim", "recent_enter", modRes.fwd(R.anim.tn_recent_enter));
		XResources.setSystemWideReplacement("android", "anim", "recent_exit", modRes.fwd(R.anim.tn_recent_exit));
		XResources.setSystemWideReplacement("android", "anim", "recents_fade_in", modRes.fwd(R.anim.tn_recents_fade_in));
		XResources.setSystemWideReplacement("android", "anim", "recents_fade_out", modRes.fwd(R.anim.tn_recents_fade_out));
		XResources.setSystemWideReplacement("android", "anim", "submenu_enter", modRes.fwd(R.anim.tn_submenu_enter));
		XResources.setSystemWideReplacement("android", "anim", "submenu_exit", modRes.fwd(R.anim.tn_submenu_exit));
		XResources.setSystemWideReplacement("android", "anim", "input_method_enter", modRes.fwd(R.anim.tn_input_method_enter));  
		XResources.setSystemWideReplacement("android", "anim", "input_method_exit", modRes.fwd(R.anim.tn_input_method_exit));
		XResources.setSystemWideReplacement("android", "anim", "input_method_extract_exit", modRes.fwd(R.anim.tn_input_method_extract_exit));
		XResources.setSystemWideReplacement("android", "anim", "input_method_extract_enter", modRes.fwd(R.anim.tn_input_method_extract_enter));
		XResources.setSystemWideReplacement("android", "anim", "input_method_fancy_enter", modRes.fwd(R.anim.tn_input_method_fancy_enter));
		XResources.setSystemWideReplacement("android", "anim", "input_method_fancy_exit", modRes.fwd(R.anim.tn_input_method_fancy_exit));
		XResources.setSystemWideReplacement("android", "anim", "app_starting_exit", modRes.fwd(R.anim.tn_app_starting_exit));
		XResources.setSystemWideReplacement("android", "anim", "activity_close_enter", modRes.fwd(R.anim.tn_activity_close_enter));
		XResources.setSystemWideReplacement("android", "anim", "activity_close_exit", modRes.fwd(R.anim.tn_activity_close_exit));
		XResources.setSystemWideReplacement("android", "anim", "activity_open_enter", modRes.fwd(R.anim.tn_activity_open_enter));
		XResources.setSystemWideReplacement("android", "anim", "activity_open_exit", modRes.fwd(R.anim.tn_activity_open_exit));
		XResources.setSystemWideReplacement("android", "anim", "dialog_enter", modRes.fwd(R.anim.tn_dialog_enter));
		XResources.setSystemWideReplacement("android", "anim", "dialog_exit", modRes.fwd(R.anim.tn_dialog_exit));
		XResources.setSystemWideReplacement("android", "anim", "toast_exit", modRes.fwd(R.anim.tn_toast_exit)); 
		XResources.setSystemWideReplacement("android", "anim", "toast_enter", modRes.fwd(R.anim.tn_toast_enter));
		XResources.setSystemWideReplacement("android", "anim", "lock_screen_exit", modRes.fwd(R.anim.tn_lock_screen_exit));
		XResources.setSystemWideReplacement("android", "anim", "task_close_enter", modRes.fwd(R.anim.tn_task_close_enter));
		XResources.setSystemWideReplacement("android", "anim", "task_close_exit", modRes.fwd(R.anim.tn_task_close_exit));
		XResources.setSystemWideReplacement("android", "anim", "task_open_enter", modRes.fwd(R.anim.tn_task_open_enter));
		XResources.setSystemWideReplacement("android", "anim", "task_open_exit", modRes.fwd(R.anim.tn_task_open_exit));
	}
}
