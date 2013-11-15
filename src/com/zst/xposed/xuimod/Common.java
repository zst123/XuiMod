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

import java.lang.reflect.Field;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;


public class Common {

	public static final String MY_PACKAGE_NAME = Common.class.getPackage().getName();
	public static final String LISTVIEW_PREFERENCE_FILENAME = "listview_blacklist";
	public static final String ANIM_CONTROLS_PREFERENCE_FILENAME = "animation_controls";
	public static final String ACTION_SETTINGS_CHANGED = "com.zst.xposed.xuimod.SETTINGS_CHANGED";
	
	public static final String COLOR_HOLO_BLUE ="FF33B5E5";
	
	/* Preference keys */
	public static final String KEY_SECONDS_ENABLE ="seconds_enable";
	public static final String KEY_SECONDS_BOLD ="seconds_bold";
	public static final String KEY_SECONDS_CUSTOM ="seconds_custom";
	public static final String KEY_SECONDS_USE_HTML ="seconds_use_html";
	
	public static final String KEY_LISTVIEW_INTERPOLATOR ="listview_interpolator";
	public static final String KEY_LISTVIEW_ANIMATION ="listview_animation";
	public static final String KEY_LISTVIEW_CACHE ="listview_cache";
	public static final String KEY_LISTVIEW_DURATION ="listview_duration";
	public static final String KEY_LISTVIEW_BLACKLIST ="listview_blacklist";
	
	public static final String KEY_WINDOW_TRANSITIONS ="window_transitions";

	public static final String KEY_VOLUME_TIMEOUT ="volume_timeout";
	public static final String KEY_VOLUME_ALPHA ="volume_alpha";
	public static final String KEY_LOCKSCREEN_VOLUME ="lockscreen_volume";
	
	public static final String KEY_BATTERYBAR_ENABLE ="batterybar_enable";
	public static final String KEY_BATTERYBAR_ANIMATE ="batterybar_animate";
	public static final String KEY_BATTERYBAR_STYLE ="batterybar_style";
	public static final String KEY_BATTERYBAR_COLOR ="batterybar_color";
	public static final String KEY_BATTERYBAR_HEIGHT ="batterybar_height";
	public static final String KEY_BATTERYBAR_BACKGROUND_COLOR ="batterybar_bg_color";

	public static final String KEY_LOCKSCREEN_TORCH_ENABLE ="lockscreen_torch";
	public static final String KEY_LOCKSCREEN_TORCH_BACK ="lockscreen_torch_back";
	public static final String KEY_LOCKSCREEN_TORCH_HOME ="lockscreen_torch_home";
	public static final String KEY_LOCKSCREEN_TORCH_MENU ="lockscreen_torch_menu";
	public static final String KEY_LOCKSCREEN_TORCH_TYPE ="lockscreen_torch_type";
	
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

	/* Preference default values */
	public static final boolean DEFAULT_SECONDS_ENABLE =false;
	public static final boolean DEFAULT_SECONDS_BOLD =false;
	public static final String DEFAULT_SECONDS_CUSTOM ="";
	public static final boolean DEFAULT_SECONDS_USE_HTML =false;
	
	public static final String DEFAULT_LISTVIEW_INTERPOLATOR = "0";
	public static final String DEFAULT_LISTVIEW_ANIMATION = "0";
	public static final String DEFAULT_LISTVIEW_CACHE = "0";
	public static final int DEFAULT_LISTVIEW_DURATION = 500;
	
	public static final String DEFAULT_WINDOW_TRANSITIONS = "0";

	public static final String DEFAULT_VOLUME_TIMEOUT = "3000";
	public static final int DEFAULT_VOLUME_ALPHA = 100;
	public static final boolean DEFAULT_LOCKSCREEN_VOLUME =false;
	
	public static final boolean DEFAULT_BATTERYBAR_ENABLE = false;
	public static final boolean DEFAULT_BATTERYBAR_ANIMATE = false;
	public static final boolean DEFAULT_BATTERYBAR_STYLE = false;
	public static final String DEFAULT_BATTERYBAR_COLOR = COLOR_HOLO_BLUE;
	public static final int DEFAULT_BATTERYBAR_HEIGHT = 2;
	
	public static final boolean DEFAULT_LOCKSCREEN_TORCH_ENABLE = false;
	public static final boolean DEFAULT_LOCKSCREEN_TORCH_BACK = false;
	public static final boolean DEFAULT_LOCKSCREEN_TORCH_HOME = false;
	public static final boolean DEFAULT_LOCKSCREEN_TORCH_MENU = false;
	public static final String DEFAULT_LOCKSCREEN_TORCH_TYPE = "0";
	
    public static final boolean DEFAULT_ANIMATION_CONTROLS_ENABLE = false;
    public static final boolean DEFAULT_ANIMATION_CONTROLS_NO_OVERRIDE = false;
    public static final int DEFAULT_ANIMATION_CONTROLS_DURATION = -1;
    public static final String DEFAULT_ANIMATION_CONTROLS_ACTIVITY = "0";

    public static final String DEFAULT_ANIMATION_IME_ENTER = "0";
    public static final String DEFAULT_ANIMATION_IME_EXIT = "0";
    public static final String DEFAULT_ANIMATION_IME_INTEPOLATER = "0";
    public static final int DEFAULT_ANIMATION_IME_DURATION = 500;
    
	/* Preference limits values */
	public static final int LIMIT_MAX_LISTVIEW_DURATION = 2500;
	public static final int LIMIT_MIN_LISTVIEW_DURATION = 100;
	
	public static final int LIMIT_MAX_VOLUME_ALPHA = 100;
	public static final int LIMIT_MIN_VOLUME_ALPHA = 25;
	
	public static final int LIMIT_MAX_BATTERYBAR_HEIGHT = 25;
	public static final int LIMIT_MIN_BATTERYBAR_HEIGHT = 1;

	public static final int LIMIT_MAX_ANIMATION_CONTROLS_DURATION = 2000;
	public static final int LIMIT_MIN_ANIMATION_CONTROLS_DURATION = -1;
	
	public static final int LIMIT_MAX_ANIMATION_IME_DURATION = 3000;
	public static final int LIMIT_MIN_ANIMATION_IME_DURATION = 0;
	
	public static final boolean TEST_FREATURE = true;
	
	public static void settingsChanged(Context ctx){
		Intent i = new Intent(Common.ACTION_SETTINGS_CHANGED);
		ctx.sendBroadcast(i);
	}
	
	/* Helper Methods */
	public static Object getReflection(Object itemToGetObject , String objectName){
		try {
		Class<?> clazz = itemToGetObject.getClass();
		Field field;
		field = clazz.getDeclaredField(objectName);
		field.setAccessible(true);
		return field.get(itemToGetObject);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/*
	 * @param return true when successful
	 */
	public static boolean setReflection(Object itemToGetObject , String objectName , Object newValue){
		try {
		Class<?> clazz = itemToGetObject.getClass();
		Field field;
		field = clazz.getDeclaredField(objectName);
		field.setAccessible(true);
		field.set(itemToGetObject, newValue);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static int parseColorFromString(String str, String defColorWithoutSymbols){
		str.replaceAll("\\s+", ""); 
		//Remove all spaces
		if (str.equals("")){
			str = defColorWithoutSymbols;
		}
		if (!str.startsWith("#")){
			str = "#"+ str;
		}
		try{
			return Color.parseColor(str);
		}catch(Exception e){
			return Color.parseColor("#"+defColorWithoutSymbols);
		}
	}
}
