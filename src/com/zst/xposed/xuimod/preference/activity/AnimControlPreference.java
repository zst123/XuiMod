/*
 * Copyright (C) 2013 AOKP by Steve Spear - Stevespear426
 * This code has been modified. Portions copyright (C) 2013, XuiMod
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
package com.zst.xposed.xuimod.preference.activity;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.zst.xposed.xuimod.Common;
import com.zst.xposed.xuimod.R;
import com.zst.xposed.xuimod.mods.animation.AwesomeAnimationHelper;

public class AnimControlPreference extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);
        getPreferenceManager().setSharedPreferencesName(Common.ANIM_CONTROLS_PREFERENCE_FILENAME);
        addPreferencesFromResource(R.xml.pref_anim_control);
        reloadSummary(getPreferenceManager().getSharedPreferences());
        getPreferenceManager().getSharedPreferences()
        		.registerOnSharedPreferenceChangeListener(this);
    }
    
    @Override
	public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
    	Common.settingsChanged(this);
    	reloadSummary(pref);
	}
    
    private void reloadSummary(SharedPreferences pref) {
    	for (int x = 0; x < Common.KEYS_ANIMATION_CONTROLS_ACTIVITY.length; x++){
    		String key = Common.KEYS_ANIMATION_CONTROLS_ACTIVITY[x];
    		String value = pref.getString(key, Common.DEFAULT_ANIMATION_CONTROLS_ACTIVITY);
    		String summary = AwesomeAnimationHelper.getProperName(getResources(),
    				Integer.valueOf(value));
    		findPreference(key).setSummary(summary);
    	}
    	
    	int duration = pref.getInt(Common.KEY_ANIMATION_CONTROLS_DURATION,
				Common.DEFAULT_ANIMATION_CONTROLS_DURATION);
    	String summary = (duration < 0) ? getResources().getString(R.string.settings_default) : (duration + " ms");
    	findPreference(Common.KEY_ANIMATION_CONTROLS_DURATION).setSummary(summary);

    }	
}