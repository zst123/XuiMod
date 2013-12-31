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

import static de.robv.android.xposed.XposedHelpers.findClass;

import com.zst.xposed.xuimod.Common;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class LockscreenTorchMod {
	private static final int CM_TORCH = 0;
	private static final int TESLA_TORCH = 1;
	private static final int DASHLIGHT_TORCH = 2;
	
	private static final int LONGPRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();
	private static final int VIBRATE_DURATION = 25;
	private static final int WAKELOCK_MAX_TIME = 1000 * 60 * 3; //3 min converted to ms
	private static final String WAKELOCK_TAG = "XuiMod - LockscreenFlash";
	
	private static Context sContext;
	private static WakeLock sWakelock;

	private static boolean isKeyDown = false;
	private static boolean isTorchOn = false;
	protected static XSharedPreferences mPref;
	public static void handleLoadPackage(LoadPackageParam lpparam, XSharedPreferences pref) {
		if (!lpparam.packageName.equals("android")) return;
		mPref = pref;
		try {
			hook(lpparam);
		} catch (Throwable e) {
		}
	}
	
	private static String getClassStringFromSdk(int sdk){
		switch(sdk) {
		case 14:
		case 15:
			return "com.android.internal.policy.impl.KeyguardViewBase";
			/* Ice Cream Sandwich */
		case 16:
		case 17:
		case 18:
			return "com.android.internal.policy.impl.keyguard.KeyguardViewManager.ViewManagerHost";
			/* Jelly Bean */
		case 19:
			return "com.android.keyguard.KeyguardViewManager.ViewManagerHost";
			/* KitKat */
		}
		return null;
		
	}
	
	private static void hook(LoadPackageParam o) {
		String cls = getClassStringFromSdk(Build.VERSION.SDK_INT);
		if (cls == null) return;
		
	    Class<?> hookClass = findClass(cls, o.classLoader);
	    XposedBridge.hookAllMethods(hookClass, "dispatchKeyEvent", new XC_MethodHook(){
		@Override 
		protected void afterHookedMethod(MethodHookParam param) throws Throwable {
			mPref.reload();
			FrameLayout thiss = (FrameLayout)param.thisObject;
		    sContext = thiss.getContext();
		    KeyEvent event = (KeyEvent)param.args[0];
		    if (keyCodeEnabled(event)){
		    	handleButton(event);
		    	param.setResult(Boolean.TRUE);
		    	//Change to TRUE so system knows we're handling it
		    }
		}
	    });		
	}
	
	private static boolean keyCodeEnabled(KeyEvent event){

		boolean enabled = mPref.getBoolean(Common.KEY_LOCKSCREEN_TORCH_ENABLE,
				Common.DEFAULT_LOCKSCREEN_TORCH_ENABLE);
		if (!enabled){
			return false;
		}
		
	    if(event.getKeyCode() == KeyEvent.KEYCODE_HOME){
	    	boolean homeEnable = mPref.getBoolean(Common.KEY_LOCKSCREEN_TORCH_HOME,
					Common.DEFAULT_LOCKSCREEN_TORCH_HOME);
	    	return homeEnable;
	    	
	    }else if(event.getKeyCode() == KeyEvent.KEYCODE_MENU){
	    	boolean menuEnable = mPref.getBoolean(Common.KEY_LOCKSCREEN_TORCH_MENU,
					Common.DEFAULT_LOCKSCREEN_TORCH_MENU);
	    	return menuEnable;
	    	
	    }else if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
	    	boolean menuEnable = mPref.getBoolean(Common.KEY_LOCKSCREEN_TORCH_BACK,
					Common.DEFAULT_LOCKSCREEN_TORCH_BACK);
	    	return menuEnable;
	    }else if(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP){
	    	return mPref.getBoolean(Common.KEY_LOCKSCREEN_TORCH_VOLUME_UP,
					Common.DEFAULT_LOCKSCREEN_TORCH_VOLUME);
	    }else if(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN){
	    	return mPref.getBoolean(Common.KEY_LOCKSCREEN_TORCH_VOLUME_DOWN,
					Common.DEFAULT_LOCKSCREEN_TORCH_VOLUME);
	    }
		return false;	
	}
    
    private static void handleButton(KeyEvent event){
		switch(event.getAction()){
		case KeyEvent.ACTION_DOWN:
			if (!isKeyDown){
				longPressTimeout();
				isKeyDown = true;
			}
			break;
			
		case KeyEvent.ACTION_UP:
			isKeyDown = false;
			toggleTorch(false);
			break;
		}
	}
    
    // We make a Handler to wait for Long Press Timeout then toggle the Torch
    private static void longPressTimeout() {
    	Handler handler = new Handler(sContext.getMainLooper());
    	Runnable r = new Runnable(){
    		public void run() {
    			if (isKeyDown){
    				toggleTorch(true);
    				vibrate();
    				// After waiting for 2 sec, it's possible 
    				// home has been released. We must check
    				// for isKeyDown first.
    			}		
    		}
    	}; 
    	handler.postDelayed(r, LONGPRESS_TIMEOUT); 
    }
    
    /*
	 * Checks if haptic feedback enabled in Settings
	 * and vibrate accordingly
	 */
    private static void vibrate() {
		Vibrator mVibrator = (Vibrator) 
				sContext.getSystemService(Context.VIBRATOR_SERVICE);
        final boolean hapticEnabled = Settings.System.getInt(sContext.getContentResolver(),
        		Settings.System.HAPTIC_FEEDBACK_ENABLED, 1) != 0;
        if (mVibrator != null && hapticEnabled) 
        	mVibrator.vibrate(VIBRATE_DURATION);
    }
    
    private static void toggleWakelock(boolean turnOn){
    	if (turnOn){
    		final PowerManager pm = 
    				(PowerManager) sContext.getSystemService(Context.POWER_SERVICE);
    		sWakelock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, WAKELOCK_TAG);
    		sWakelock.acquire(WAKELOCK_MAX_TIME);
    	}else{ 
    		sWakelock.release();
    	}
	}
    
    private static void toggleTorch(boolean turnOn) {
		int type = Integer.parseInt(mPref.getString(Common.KEY_LOCKSCREEN_TORCH_TYPE, Common.DEFAULT_LOCKSCREEN_TORCH_TYPE));
		
		if(type == CM_TORCH){
			toggleCyanogenmodTorch(turnOn);
		}else if(type == TESLA_TORCH){
			toggleTeslaLedTorch(turnOn);
		}else if(type == DASHLIGHT_TORCH){
			toggleDashLightTorch(turnOn);
		}
		
		vibrate();
		toggleWakelock(turnOn);
    }
    
    //TODO: Add more Flashlight apps or implement own flashlight code.
    /* 
	 * Sends broadcast to default Torch app found in CM, AOKP, etc.
	 */
    private static void toggleCyanogenmodTorch(boolean turnOn) {
    	if (isTorchOn != turnOn){
    		Intent intent = new Intent("net.cactii.flash2.TOGGLE_FLASHLIGHT");
    		intent.putExtra("bright", false);
    		sContext.sendBroadcast(intent);
    		isTorchOn = !isTorchOn;	
    	}
    }
    
    /* Sends broadcast to TeslaLED */
    private static void toggleTeslaLedTorch(boolean turnOn) {
    	Intent i = new Intent("com.teslacoilsw.intent.FLASHLIGHT");
    	if (turnOn) {
    		i.putExtra("on", true);
    		i.putExtra("timeout", 10);
    		// Turn on & set max time to 10 min (user can't possibly hold for 10 min)
    	}else{
    		i.putExtra("off", true);
    	}
        sContext.startService(i);
    	isTorchOn = !isTorchOn;	
    }
    
    /* Sends broadcast to Dashlight Torch  */
    private static void toggleDashLightTorch(boolean turnOn) {
    	Intent intent;
    	if (turnOn){
    		intent = new Intent("com.spectrl.dashlight.FLASHLIGHT_ON");
    	}else{
    		intent = new Intent("com.spectrl.dashlight.FLASHLIGHT_OFF");
    	}
    	
    	sContext.sendBroadcast(intent);
    	isTorchOn = !isTorchOn;	
    }
    
}
