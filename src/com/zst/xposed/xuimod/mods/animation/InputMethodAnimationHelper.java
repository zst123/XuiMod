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

package com.zst.xposed.xuimod.mods.animation;

import java.util.Random;

import android.content.Context;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import com.zst.xposed.xuimod.R;

public class InputMethodAnimationHelper {

	public static final int INTERPOLATOR_NONE = 0;
	public static final int INTERPOLATOR_ACCELERATE = 1;
	public static final int INTERPOLATOR_DECELERATE = 2;
	public static final int INTERPOLATOR_ACCELERATE_DECELERATE = 3;
	public static final int INTERPOLATOR_ANTICIPATE = 4;
	public static final int INTERPOLATOR_OVERSHOOT = 5;
	public static final int INTERPOLATOR_ANTICIPATE_OVERSHOOT = 6;
	public static final int INTERPOLATOR_BOUNCE = 7;
	public static final int INTERPOLATOR_CYCLE = 8;
	public static final int INTERPOLATOR_LINEAR = 9;
	
	
	public final static int ANIMATION_RANDOM = -1;
	public final static int ANIMATION_DEFAULT = 0;
	public final static int ANIMATION_FANCY = 14;
	public final static int ANIMATION_TN = 15;
	public final static int ANIMATION_XYLON = 16;

	public static int[] getAnimationInt(int index) {
		int[] anim = new int[2];
		if (index == ANIMATION_RANDOM) {
			index = (new Random()).nextInt(18);
			// Random from 0 to 17
		}

		switch (index) {
		case ANIMATION_DEFAULT:
			break;

		case ANIMATION_FANCY:
			anim[0] = R.anim.xylon_input_method_fancy_exit;
			anim[1] = R.anim.xylon_input_method_fancy_enter;
			break;

		case ANIMATION_TN:
			anim[0] = R.anim.tn_input_method_exit;
			anim[1] = R.anim.tn_input_method_enter;
			break;

		case ANIMATION_XYLON:
			anim[0] = R.anim.xylon_input_method_exit;
			anim[1] = R.anim.xylon_input_method_enter;
			break;

		default:
			anim = AwesomeAnimationHelper.getAnimations(index);
		}
		return anim;
	}

	public static Interpolator getInterpolator(Context c, int index) {
		Interpolator itplr = null;
		switch(index) {
		case INTERPOLATOR_ACCELERATE:
			itplr = AnimationUtils.loadInterpolator(c, android.R.anim.accelerate_interpolator);
			break;
		case INTERPOLATOR_DECELERATE:
			itplr = AnimationUtils.loadInterpolator(c, android.R.anim.decelerate_interpolator);
			break;
		case INTERPOLATOR_ACCELERATE_DECELERATE:
			itplr = AnimationUtils.loadInterpolator(c, android.R.anim.accelerate_decelerate_interpolator);
			break;
		case INTERPOLATOR_ANTICIPATE:
			itplr = AnimationUtils.loadInterpolator(c, android.R.anim.anticipate_interpolator);
			break;
		case INTERPOLATOR_OVERSHOOT:
			itplr = AnimationUtils.loadInterpolator(c, android.R.anim.overshoot_interpolator);
			break;
		case INTERPOLATOR_ANTICIPATE_OVERSHOOT:
			itplr = AnimationUtils.loadInterpolator(c, android.R.anim.anticipate_overshoot_interpolator);
			break;
		case INTERPOLATOR_BOUNCE:
			itplr = AnimationUtils.loadInterpolator(c, android.R.anim.bounce_interpolator);
			break;
		case INTERPOLATOR_CYCLE:
			itplr = AnimationUtils.loadInterpolator(c, android.R.anim.cycle_interpolator);
			break;
		case INTERPOLATOR_LINEAR:
			itplr = AnimationUtils.loadInterpolator(c, android.R.anim.linear_interpolator);
			break;
		}
		return itplr;
	}
}
