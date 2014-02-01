/*
 * Copyright (C) 2014 XuiMod
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zst.xposed.xuimod.mods.animation;

import java.util.Random;
import com.zst.xposed.xuimod.R;

public class ToastAnimationHelper {
	
	public final static int ANIMATION_RANDOM = -1;
	public final static int ANIMATION_DEFAULT = 0;
	public final static int ANIMATION_TOKO = 14;
	public final static int ANIMATION_TN = 15;
	public final static int ANIMATION_XYLON = 16;
	public final static int ANIMATION_Z1 = 17;
	
	public static int[] getAnimationInt(int index) {
		int[] anim = new int[2];
		if (index == ANIMATION_RANDOM) {
			index = (new Random()).nextInt(18);
			// Random from 0 to 17
		}
		
		switch (index) {
		case ANIMATION_DEFAULT:
			break;
		
		case ANIMATION_TOKO:
			anim[0] = R.anim.toko_toast_exit;
			anim[1] = R.anim.toko_toast_enter;
			break;
		
		case ANIMATION_TN:
			anim[0] = R.anim.tn_toast_exit;
			anim[1] = R.anim.tn_toast_enter;
			break;
		
		case ANIMATION_XYLON:
			anim[0] = R.anim.xylon_toast_exit;
			anim[1] = R.anim.xylon_toast_enter;
			break;
		
		case ANIMATION_Z1:
			anim[0] = R.anim.honami_toast_exit;
			anim[1] = R.anim.honami_toast_enter;
			break;
		
		default:
			anim = AwesomeAnimationHelper.getAnimations(index);
		}
		return anim;
	}
}
