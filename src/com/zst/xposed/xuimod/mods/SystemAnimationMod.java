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

package com.zst.xposed.xuimod.mods;

import android.content.res.XModuleResources;
import android.content.res.XResForwarder;
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
	private static final int Z1_HONAMI_ANIMATION = 3;
	private static final int TOKO_ANIMATION = 4;
	
	public static void handleInitPackageResources(XSharedPreferences pref, InitPackageResourcesParam resparam){
		if (!resparam.packageName.equals("android")) return;
		
		final XModuleResources modRes = XModuleResources.createInstance(XuiMod.MODULE_PATH, resparam.res);
		
		int transition = Integer.parseInt(pref.getString(Common.KEY_WINDOW_TRANSITIONS, Common.DEFAULT_WINDOW_TRANSITIONS));
		switch (transition){
		case NO_ANIMATION:
			return;
			
		case XYLON_ANIMATION:
			initXylon(modRes);
			break;
			
		case TN_ANIMATION:
			initTonyNoobAnim(modRes);
			break;
			
		case Z1_HONAMI_ANIMATION:
			initHonamiAnim(modRes);
			break;
			
		case TOKO_ANIMATION:
			initTokoAnim(modRes);
			break;
			
		}
	}
	
	/* Animations XML grabbed and slightly modified from Xylon ROMs : http://xylon.androidvenue.com/downloads-2/
	 * Inspired by the discontinued mod : http://forum.xda-developers.com/showthread.php?t=2012179 
	 * Xylon Animation Replacement
	 */
	private static void initXylon(final XModuleResources modRes){
		setSystemAnimationReplacement("window_move_from_decor", modRes.fwd(R.anim.xylon_window_move_from_decor));
		setSystemAnimationReplacement("options_panel_enter", modRes.fwd(R.anim.xylon_options_panel_enter));
		setSystemAnimationReplacement("options_panel_exit", modRes.fwd(R.anim.xylon_options_panel_exit));
		setSystemAnimationReplacement("recent_enter", modRes.fwd(R.anim.xylon_recent_enter));
		setSystemAnimationReplacement("recent_exit", modRes.fwd(R.anim.xylon_recent_exit));
		setSystemAnimationReplacement("recents_fade_in", modRes.fwd(R.anim.xylon_recents_fade_in));
		setSystemAnimationReplacement("recents_fade_out", modRes.fwd(R.anim.xylon_recents_fade_out));
		setSystemAnimationReplacement("submenu_enter", modRes.fwd(R.anim.xylon_submenu_enter));
		setSystemAnimationReplacement("submenu_exit", modRes.fwd(R.anim.xylon_submenu_exit));
		setSystemAnimationReplacement("input_method_enter", modRes.fwd(R.anim.xylon_input_method_enter));  
		setSystemAnimationReplacement("input_method_exit", modRes.fwd(R.anim.xylon_input_method_exit));
		setSystemAnimationReplacement("input_method_extract_exit", modRes.fwd(R.anim.xylon_input_method_extract_exit));
		setSystemAnimationReplacement("input_method_extract_enter", modRes.fwd(R.anim.xylon_input_method_extract_enter));
		setSystemAnimationReplacement("input_method_fancy_enter", modRes.fwd(R.anim.xylon_input_method_fancy_enter));
		setSystemAnimationReplacement("input_method_fancy_exit", modRes.fwd(R.anim.xylon_input_method_fancy_exit));
		setSystemAnimationReplacement("app_starting_exit", modRes.fwd(R.anim.xylon_app_starting_exit));
		setSystemAnimationReplacement("activity_close_enter", modRes.fwd(R.anim.xylon_activity_close_enter));
		setSystemAnimationReplacement("activity_close_exit", modRes.fwd(R.anim.xylon_activity_close_exit));
		setSystemAnimationReplacement("activity_open_enter", modRes.fwd(R.anim.xylon_activity_open_enter));
		setSystemAnimationReplacement("activity_open_exit", modRes.fwd(R.anim.xylon_activity_open_exit));
		setSystemAnimationReplacement("dialog_enter", modRes.fwd(R.anim.xylon_dialog_enter));
		setSystemAnimationReplacement("dialog_exit", modRes.fwd(R.anim.xylon_dialog_exit));
		setSystemAnimationReplacement("toast_exit", modRes.fwd(R.anim.xylon_toast_exit)); 
		setSystemAnimationReplacement("toast_enter", modRes.fwd(R.anim.xylon_toast_enter));
		setSystemAnimationReplacement("lock_screen_exit", modRes.fwd(R.anim.xylon_lock_screen_exit));
		setSystemAnimationReplacement("task_close_enter", modRes.fwd(R.anim.xylon_task_close_enter));
		setSystemAnimationReplacement("task_close_exit", modRes.fwd(R.anim.xylon_task_close_exit));
		setSystemAnimationReplacement("task_open_enter", modRes.fwd(R.anim.xylon_task_open_enter));
		setSystemAnimationReplacement("task_open_exit", modRes.fwd(R.anim.xylon_task_open_exit));
	}
	
	/* Animations XML grabbed and slightly modified from tony-noob's Animation : http://forum.xda-developers.com/showthread.php?t=1342643
	 * TN Animation Replacement
	 */
	private static void initTonyNoobAnim(final XModuleResources modRes){
		setSystemAnimationReplacement("window_move_from_decor", modRes.fwd(R.anim.tn_window_move_from_decor));
		setSystemAnimationReplacement("options_panel_enter", modRes.fwd(R.anim.tn_options_panel_enter));
		setSystemAnimationReplacement("options_panel_exit", modRes.fwd(R.anim.tn_options_panel_exit));
		setSystemAnimationReplacement("recent_enter", modRes.fwd(R.anim.tn_recent_enter));
		setSystemAnimationReplacement("recent_exit", modRes.fwd(R.anim.tn_recent_exit));
		setSystemAnimationReplacement("recents_fade_in", modRes.fwd(R.anim.tn_recents_fade_in));
		setSystemAnimationReplacement("recents_fade_out", modRes.fwd(R.anim.tn_recents_fade_out));
		setSystemAnimationReplacement("submenu_enter", modRes.fwd(R.anim.tn_submenu_enter));
		setSystemAnimationReplacement("submenu_exit", modRes.fwd(R.anim.tn_submenu_exit));
		setSystemAnimationReplacement("input_method_enter", modRes.fwd(R.anim.tn_input_method_enter));  
		setSystemAnimationReplacement("input_method_exit", modRes.fwd(R.anim.tn_input_method_exit));
		setSystemAnimationReplacement("input_method_extract_exit", modRes.fwd(R.anim.tn_input_method_extract_exit));
		setSystemAnimationReplacement("input_method_extract_enter", modRes.fwd(R.anim.tn_input_method_extract_enter));
		setSystemAnimationReplacement("input_method_fancy_enter", modRes.fwd(R.anim.tn_input_method_fancy_enter));
		setSystemAnimationReplacement("input_method_fancy_exit", modRes.fwd(R.anim.tn_input_method_fancy_exit));
		setSystemAnimationReplacement("app_starting_exit", modRes.fwd(R.anim.tn_app_starting_exit));
		setSystemAnimationReplacement("activity_close_enter", modRes.fwd(R.anim.tn_activity_close_enter));
		setSystemAnimationReplacement("activity_close_exit", modRes.fwd(R.anim.tn_activity_close_exit));
		setSystemAnimationReplacement("activity_open_enter", modRes.fwd(R.anim.tn_activity_open_enter));
		setSystemAnimationReplacement("activity_open_exit", modRes.fwd(R.anim.tn_activity_open_exit));
		setSystemAnimationReplacement("dialog_enter", modRes.fwd(R.anim.tn_dialog_enter));
		setSystemAnimationReplacement("dialog_exit", modRes.fwd(R.anim.tn_dialog_exit));
		setSystemAnimationReplacement("toast_exit", modRes.fwd(R.anim.tn_toast_exit)); 
		setSystemAnimationReplacement("toast_enter", modRes.fwd(R.anim.tn_toast_enter));
		setSystemAnimationReplacement("lock_screen_exit", modRes.fwd(R.anim.tn_lock_screen_exit));
		setSystemAnimationReplacement("task_close_enter", modRes.fwd(R.anim.tn_task_close_enter));
		setSystemAnimationReplacement("task_close_exit", modRes.fwd(R.anim.tn_task_close_exit));
		setSystemAnimationReplacement("task_open_enter", modRes.fwd(R.anim.tn_task_open_enter));
		setSystemAnimationReplacement("task_open_exit", modRes.fwd(R.anim.tn_task_open_exit));
	}
	
	/* Animations XML grabbed from Sony Xperia Z1(Honami) Framework
	 * Sony Xperia Z1 (Honami) Animation Replacement
	 */
	private static void initHonamiAnim(final XModuleResources modRes){
		setSystemAnimationReplacement("window_move_from_decor", modRes.fwd(R.anim.honami_window_move_from_decor));
		setSystemAnimationReplacement("options_panel_enter", modRes.fwd(R.anim.honami_options_panel_enter));
		setSystemAnimationReplacement("options_panel_exit", modRes.fwd(R.anim.honami_options_panel_exit));
		setSystemAnimationReplacement("recent_enter", modRes.fwd(R.anim.honami_recent_enter));
		setSystemAnimationReplacement("recent_exit", modRes.fwd(R.anim.honami_recent_exit));
		setSystemAnimationReplacement("recents_fade_in", modRes.fwd(R.anim.honami_recents_fade_in));
		setSystemAnimationReplacement("recents_fade_out", modRes.fwd(R.anim.honami_recents_fade_out));
		setSystemAnimationReplacement("submenu_enter", modRes.fwd(R.anim.honami_submenu_enter));
		setSystemAnimationReplacement("submenu_exit", modRes.fwd(R.anim.honami_submenu_exit));
		setSystemAnimationReplacement("app_starting_exit", modRes.fwd(R.anim.honami_app_starting_exit));
		setSystemAnimationReplacement("activity_close_enter", modRes.fwd(R.anim.honami_activity_close_enter));
		setSystemAnimationReplacement("activity_close_exit", modRes.fwd(R.anim.honami_activity_close_exit));
		setSystemAnimationReplacement("activity_open_enter", modRes.fwd(R.anim.honami_activity_open_enter));
		setSystemAnimationReplacement("activity_open_exit", modRes.fwd(R.anim.honami_activity_open_exit));
		setSystemAnimationReplacement("dialog_enter", modRes.fwd(R.anim.honami_dialog_enter));
		setSystemAnimationReplacement("dialog_exit", modRes.fwd(R.anim.honami_dialog_exit));
		setSystemAnimationReplacement("toast_exit", modRes.fwd(R.anim.honami_toast_exit)); 
		setSystemAnimationReplacement("toast_enter", modRes.fwd(R.anim.honami_toast_enter));
		setSystemAnimationReplacement("lock_screen_exit", modRes.fwd(R.anim.honami_lock_screen_exit));
		setSystemAnimationReplacement("task_close_enter", modRes.fwd(R.anim.honami_task_close_enter));
		setSystemAnimationReplacement("task_close_exit", modRes.fwd(R.anim.honami_task_close_exit));
		setSystemAnimationReplacement("task_open_enter", modRes.fwd(R.anim.honami_task_open_enter));
		setSystemAnimationReplacement("task_open_exit", modRes.fwd(R.anim.honami_task_open_exit));
	}
	
	private static void initTokoAnim(final XModuleResources modRes){
		setSystemAnimationReplacement("window_move_from_decor", modRes.fwd(R.anim.toko_window_move_from_decor));
		setSystemAnimationReplacement("options_panel_enter", modRes.fwd(R.anim.toko_options_panel_enter));
		setSystemAnimationReplacement("options_panel_exit", modRes.fwd(R.anim.toko_options_panel_exit));
		setSystemAnimationReplacement("recent_enter", modRes.fwd(R.anim.toko_recent_enter));
		setSystemAnimationReplacement("recent_exit", modRes.fwd(R.anim.toko_recent_exit));
		setSystemAnimationReplacement("recents_fade_in", modRes.fwd(R.anim.toko_recents_fade_in));
		setSystemAnimationReplacement("recents_fade_out", modRes.fwd(R.anim.toko_recents_fade_out));
		setSystemAnimationReplacement("submenu_enter", modRes.fwd(R.anim.toko_submenu_enter));
		setSystemAnimationReplacement("submenu_exit", modRes.fwd(R.anim.toko_submenu_exit));
		setSystemAnimationReplacement("app_starting_exit", modRes.fwd(R.anim.toko_app_starting_exit));
		setSystemAnimationReplacement("activity_close_enter", modRes.fwd(R.anim.toko_activity_close_enter));
		setSystemAnimationReplacement("activity_close_exit", modRes.fwd(R.anim.toko_activity_close_exit));
		setSystemAnimationReplacement("activity_open_enter", modRes.fwd(R.anim.toko_activity_open_enter));
		setSystemAnimationReplacement("activity_open_exit", modRes.fwd(R.anim.toko_activity_open_exit));
		setSystemAnimationReplacement("dialog_enter", modRes.fwd(R.anim.toko_dialog_enter));
		setSystemAnimationReplacement("dialog_exit", modRes.fwd(R.anim.toko_dialog_exit));
		setSystemAnimationReplacement("toast_exit", modRes.fwd(R.anim.toko_toast_exit)); 
		setSystemAnimationReplacement("toast_enter", modRes.fwd(R.anim.toko_toast_enter));
		setSystemAnimationReplacement("lock_screen_exit", modRes.fwd(R.anim.toko_lock_screen_exit));
		setSystemAnimationReplacement("task_close_enter", modRes.fwd(R.anim.toko_task_close_enter));
		setSystemAnimationReplacement("task_close_exit", modRes.fwd(R.anim.toko_task_close_exit));
		setSystemAnimationReplacement("task_open_enter", modRes.fwd(R.anim.toko_task_open_enter));
		setSystemAnimationReplacement("task_open_exit", modRes.fwd(R.anim.toko_task_open_exit));
	}
	
	private static void setSystemAnimationReplacement(String name, XResForwarder replacement) {
		try {
			XResources.setSystemWideReplacement("android", "anim", name, replacement);
		} catch (Throwable t) {
			// Create a separate method to catch any throwable
			// For Example, ICS doesn't have recents_fade_* so it will throw
			// error and the rest of the animation will not work.
		}
	}
	
}
