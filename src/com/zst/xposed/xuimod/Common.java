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

package com.zst.xposed.xuimod;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;

public class Common {
	
	public static final String MY_PACKAGE_NAME = Common.class.getPackage().getName();
	public static final String LISTVIEW_PREFERENCE_FILENAME = "listview_blacklist";
	public static final String ANIM_CONTROLS_PREFERENCE_FILENAME = "animation_controls";
	public static final String RANDOM_COLOR_PREFERENCE_FILENAME = "random_color";
	
	public static final String ACTION_TINTED_STATUSBAR_COLOR_CHANGE = "gravitybox.intent.action.STATUSBAR_COLOR_CHANGED";
	public static final String ACTION_TINTED_NAVBAR_COLOR_CHANGE = "gravitybox.intent.action.ACTION_NAVBAR_CHANGED";
	// Use GravityBox's intents as the main intent is for internal use.
	public static final String ACTION_SETTINGS_CHANGED = "com.zst.xposed.xuimod.SETTINGS_CHANGED";
	
	public static final String COLOR_HOLO_BLUE = "FF33B5E5";
	
	/* Preference keys */
	public static final String KEY_SECONDS_ENABLE = "seconds_enable";
	public static final String KEY_SECONDS_BOLD = "seconds_bold";
	public static final String KEY_SECONDS_SIZE = "seconds_size";
	public static final String KEY_SECONDS_CUSTOM = "seconds_custom";
	public static final String KEY_SECONDS_USE_HTML = "seconds_use_html";
	public static final String KEY_SECONDS_LETTER_CASE = "seconds_lettercase";
	
	public static final String KEY_LISTVIEW_INTERPOLATOR = "listview_interpolator";
	public static final String KEY_LISTVIEW_ANIMATION = "listview_animation";
	public static final String KEY_LISTVIEW_ANIMATE_ONCE = "listview_animate_once";
	public static final String KEY_LISTVIEW_CACHE = "listview_cache";
	public static final String KEY_LISTVIEW_DURATION = "listview_duration";
	public static final String KEY_LISTVIEW_BLACKLIST = "listview_blacklist";
	
	public static final String KEY_WINDOW_TRANSITIONS = "window_transitions";
	
	public static final String KEY_VOLUME_TIMEOUT = "volume_timeout";
	public static final String KEY_VOLUME_ALPHA = "volume_alpha";
	public static final String KEY_LOCKSCREEN_VOLUME = "lockscreen_volume";
	
	public static final String KEY_BATTERYBAR_ENABLE = "batterybar_enable";
	public static final String KEY_BATTERYBAR_ANIMATE = "batterybar_animate";
	public static final String KEY_BATTERYBAR_STYLE = "batterybar_style";
	public static final String[] KEYS_BATTERYBAR_POSITION = new String[] {
		"batterybar_position_sb",
		"batterybar_position_nbt",
		"batterybar_position_nbb",
		"batterybar_position_sbb",
	};
	
	public static final String KEY_BATTERYBAR_COLOR_100 = "batterybar_color";
	public static final String KEY_BATTERYBAR_COLOR_99 = "batterybar_color_99";
	public static final String KEY_BATTERYBAR_COLOR_80 = "batterybar_color_80";
	public static final String KEY_BATTERYBAR_COLOR_60 = "batterybar_color_60";
	public static final String KEY_BATTERYBAR_COLOR_40 = "batterybar_color_40";
	public static final String KEY_BATTERYBAR_COLOR_20 = "batterybar_color_20";
	public static final String KEY_BATTERYBAR_COLOR_CHARGING = "batterybar_color_charge";
	
	public static final String KEY_BATTERYBAR_COLOR_MODE = "batterybar_color_mode";
	public static final String KEY_BATTERYBAR_HEIGHT = "batterybar_height";
	public static final String KEY_BATTERYBAR_BACKGROUND_COLOR = "batterybar_bg_color";
	
	public static final String KEY_LOCKSCREEN_TORCH_ENABLE = "lockscreen_torch";
	public static final String KEY_LOCKSCREEN_TORCH_BACK = "lockscreen_torch_back";
	public static final String KEY_LOCKSCREEN_TORCH_HOME = "lockscreen_torch_home";
	public static final String KEY_LOCKSCREEN_TORCH_MENU = "lockscreen_torch_menu";
	public static final String KEY_LOCKSCREEN_TORCH_VOLUME_UP = "lockscreen_torch_volup";
	public static final String KEY_LOCKSCREEN_TORCH_VOLUME_DOWN = "lockscreen_torch_voldown";
	public static final String KEY_LOCKSCREEN_TORCH_TYPE = "lockscreen_torch_type";
	
	public static final String KEY_ANIMATION_CONTROLS_PREF_SCREEN = "animation_controls";
	public static final String KEY_ANIMATION_CONTROLS_ENABLE = "animation_controls_enable";
	public static final String KEY_ANIMATION_CONTROLS_DURATION = "animation_controls_duration";
	public static final String KEY_ANIMATION_CONTROLS_NO_OVERRIDE = "animation_controls_no_override";
	public static final String[] KEYS_ANIMATION_CONTROLS_ACTIVITY = new String[] {
		"activity_open",
		"activity_close",
		"task_open",
		"task_close",
		"task_to_front",
		"task_to_back",
		"wallpaper_open",
		"wallpaper_close",
		"wallpaper_intra_open",
		"wallpaper_intra_close",
	};
	
	public static final String KEY_ANIMATION_IME_ENTER = "anim_ime_enter";
	public static final String KEY_ANIMATION_IME_EXIT = "anim_ime_exit";
	public static final String KEY_ANIMATION_IME_INTEPOLATER = "anim_ime_interpolater";
	public static final String KEY_ANIMATION_IME_DURATION = "anim_ime_duration";
	public static final String KEY_ANIMATION_IME_DELAY = "anim_ime_delay";
	
	public static final String KEY_ANIMATION_TOAST_ENABLED = "anim_toast_enable";
	public static final String KEY_ANIMATION_TOAST_ENTER = "anim_toast_enter";
	public static final String KEY_ANIMATION_TOAST_EXIT = "anim_toast_exit";
	public static final String KEY_ANIMATION_TOAST_INTEPOLATER = "anim_toast_interpolater";
	public static final String KEY_ANIMATION_TOAST_DURATION = "anim_toast_duration";
	public static final String KEY_ANIMATION_TOAST_TEST = "anim_toast_test";
	
	public static final String KEY_ANIMATION_TICKER_ENABLED = "anim_ticker_enable";
	public static final String KEY_ANIMATION_TICKER_MAIN_ENTER = "anim_ticker_main_enter";
	public static final String KEY_ANIMATION_TICKER_MAIN_EXIT = "anim_ticker_main_exit";
	public static final String KEY_ANIMATION_TICKER_MAIN_INTEPOLATER = "anim_ticker_main_interpolater";
	public static final String KEY_ANIMATION_TICKER_SWITCHER_ENTER = "anim_ticker_switcher_enter";
	public static final String KEY_ANIMATION_TICKER_SWITCHER_EXIT = "anim_ticker_switcher_exit";
	public static final String KEY_ANIMATION_TICKER_SWITCHER_INTEPOLATER = "anim_ticker_switcher_interpolater";
	public static final String KEY_ANIMATION_TICKER_DURATION = "anim_ticker_duration";
	public static final String KEY_ANIMATION_TICKER_TEST = "anim_ticker_test";
	
	public static final String KEY_NOTIFICATION_RANDOM_QS_TILE_COLOR = "notif_quick_settings_random";
	public static final String KEY_NOTIFICATION_CHOOSE_COLOR = "notif_choose_color_random";
	
	public static final String KEY_CLASSIC_RECENTS = "classic_recents";
	
	public static final String KEY_SCROLLING_ENABLE = "scrolling_enabled";
	public static final String KEY_SCROLLING_NO_FADING = "scrolling_no_fading";
	public static final String KEY_SCROLLING_OVERSCROLL = "scrolling_overscroll";
	public static final String KEY_SCROLLING_OVERFLING = "scrolling_overfling";
	public static final String KEY_SCROLLING_FRICTION = "scrolling_friction";
	public static final String KEY_SCROLLING_VELOCITY = "scrolling_velocity";

	public static final String KEY_SCROLLING_GLOW_ENABLE = "scrolling_glow_enabled";
	public static final String KEY_SCROLLING_GLOW_DISABLE = "scrolling_glow_effect_disabled";
	public static final String KEY_SCROLLING_GLOW_GLOW = "scrolling_glow_glow";
	public static final String KEY_SCROLLING_GLOW_EDGE = "scrolling_glow_edge";

	/* Preference default values */
	public static final boolean DEFAULT_SECONDS_ENABLE = false;
	public static final boolean DEFAULT_SECONDS_BOLD = false;
	public static final int DEFAULT_SECONDS_SIZE = 100;
	public static final String DEFAULT_SECONDS_CUSTOM = "";
	public static final boolean DEFAULT_SECONDS_USE_HTML = false;
	public static final String DEFAULT_SECONDS_LETTER_CASE = "0";
	
	public static final String DEFAULT_LISTVIEW_INTERPOLATOR = "0";
	public static final String DEFAULT_LISTVIEW_ANIMATION = "0";
	public static final boolean DEFAULT_LISTVIEW_ANIMATE_ONCE = false;
	public static final String DEFAULT_LISTVIEW_CACHE = "0";
	public static final int DEFAULT_LISTVIEW_DURATION = 500;
	
	public static final String DEFAULT_WINDOW_TRANSITIONS = "0";
	
	public static final String DEFAULT_VOLUME_TIMEOUT = "3000";
	public static final int DEFAULT_VOLUME_ALPHA = 100;
	public static final boolean DEFAULT_LOCKSCREEN_VOLUME = false;
	
	public static final boolean DEFAULT_BATTERYBAR_ENABLE = false;
	public static final boolean DEFAULT_BATTERYBAR_ANIMATE = false;
	public static final boolean DEFAULT_BATTERYBAR_STYLE = false;
	public static final String DEFAULT_BATTERYBAR_COLOR_MODE = "0";
	public static final String DEFAULT_BATTERYBAR_COLOR = COLOR_HOLO_BLUE;
	public static final int DEFAULT_BATTERYBAR_HEIGHT = 2;
	public static final boolean DEFAULT_BATTERYBAR_POSITION_STATBAR = true;
	public static final boolean DEFAULT_BATTERYBAR_POSITION_NAVBAR = false;
	
	public static final boolean DEFAULT_LOCKSCREEN_TORCH_ENABLE = false;
	public static final boolean DEFAULT_LOCKSCREEN_TORCH_BACK = false;
	public static final boolean DEFAULT_LOCKSCREEN_TORCH_HOME = false;
	public static final boolean DEFAULT_LOCKSCREEN_TORCH_MENU = false;
	public static final boolean DEFAULT_LOCKSCREEN_TORCH_VOLUME = false;
	public static final String DEFAULT_LOCKSCREEN_TORCH_TYPE = "0";
	
	public static final boolean DEFAULT_ANIMATION_CONTROLS_ENABLE = false;
	public static final boolean DEFAULT_ANIMATION_CONTROLS_NO_OVERRIDE = false;
	public static final int DEFAULT_ANIMATION_CONTROLS_DURATION = -1;
	public static final String DEFAULT_ANIMATION_CONTROLS_ACTIVITY = "0";
	
	public static final String DEFAULT_ANIMATION_IME_ENTER = "0";
	public static final String DEFAULT_ANIMATION_IME_EXIT = "0";
	public static final String DEFAULT_ANIMATION_IME_INTEPOLATER = "0";
	public static final int DEFAULT_ANIMATION_IME_DURATION = 500;
	public static final int DEFAULT_ANIMATION_IME_DELAY = 0;
	
	public static final boolean DEFAULT_ANIMATION_TOAST_ENABLED = false;
	public static final String DEFAULT_ANIMATION_TOAST_ENTER = "0";
	public static final String DEFAULT_ANIMATION_TOAST_EXIT = "0";
	public static final String DEFAULT_ANIMATION_TOAST_INTEPOLATER = "0";
	public static final int DEFAULT_ANIMATION_TOAST_DURATION = 500;
	
	public static final boolean DEFAULT_ANIMATION_TICKER_ENABLED = false;
	public static final String DEFAULT_ANIMATION_TICKER_MAIN_ENTER = "0";
	public static final String DEFAULT_ANIMATION_TICKER_MAIN_EXIT = "0";
	public static final String DEFAULT_ANIMATION_TICKER_MAIN_INTEPOLATER = "0";
	public static final String DEFAULT_ANIMATION_TICKER_SWITCHER_ENTER = "0";
	public static final String DEFAULT_ANIMATION_TICKER_SWITCHER_EXIT = "0";
	public static final String DEFAULT_ANIMATION_TICKER_SWITCHER_INTEPOLATER = "0";
	public static final int DEFAULT_ANIMATION_TICKER_DURATION = 500;
	
	public static final boolean DEFAULT_NOTIFICATION_RANDOM_QS_TILE_COLOR = false;
	
	public static final boolean DEFAULT_CLASSIC_RECENTS = false;
	
	public static final boolean DEFAULT_SCROLLING_ENABLE = false;
	public static final boolean DEFAULT_SCROLLING_NO_FADING = false;
	public static final int DEFAULT_SCROLLING_OVERSCROLL = 0;
	public static final int DEFAULT_SCROLLING_OVERFLING = 6;
	public static final int DEFAULT_SCROLLING_FRICTION = 150;
	public static final int DEFAULT_SCROLLING_VELOCITY = 8000;
	
	public static final boolean DEFAULT_SCROLLING_GLOW_ENABLE = false;
	public static final boolean DEFAULT_SCROLLING_GLOW_DISABLE = false;
	public static final String DEFAULT_SCROLLING_GLOW_GLOW = COLOR_HOLO_BLUE;
	public static final String DEFAULT_SCROLLING_GLOW_EDGE = COLOR_HOLO_BLUE;
	
	/* Preference limits values */
	public static final int LIMIT_MAX_VOLUME_ALPHA = 100;
	public static final int LIMIT_MIN_VOLUME_ALPHA = 25;
	
	public static final int LIMIT_MAX_BATTERYBAR_HEIGHT = 25;
	public static final int LIMIT_MIN_BATTERYBAR_HEIGHT = 1;
	
	public static final boolean TEST_FREATURE = true;
	
	public static void settingsChanged(final Context ctx) {
		final Handler handler = new Handler(ctx.getMainLooper());
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent i = new Intent(Common.ACTION_SETTINGS_CHANGED);
				ctx.sendBroadcast(i);
			}
		}, 1000);
		/* The 1 second delay is to give enough time for system to write the
		 * preferences. When the preference is read while it's being written,
		 * the hooks might retrieve the wrong value. */
	}
	
	public static int parseColorFromString(String str, String defColorWithoutSymbols) {
		str.replaceAll("\\s+", "");
		// Remove all spaces
		if (str.equals("")) {
			str = defColorWithoutSymbols;
		}
		if (!str.startsWith("#")) {
			str = "#" + str;
		}
		try {
			return Color.parseColor(str);
		} catch (Exception e) {
			return Color.parseColor("#" + defColorWithoutSymbols);
		}
	}
}
