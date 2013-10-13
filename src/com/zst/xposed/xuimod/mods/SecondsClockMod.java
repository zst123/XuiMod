package com.zst.xposed.xuimod.mods;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import java.util.Calendar;
import java.util.TimeZone;

import com.zst.xposed.xuimod.Common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.format.DateFormat;
import android.widget.TextView;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class SecondsClockMod {
    public static TextView thix; //Reference to StatusBar clock
	public static boolean enabled = false; //Clock Enabled?
	public static boolean bold = false; //clock bold?
	public static CharSequence format = null; // Format of Clock
	public static boolean stopForever = false; // stop until systemui restarts
	
	static XSharedPreferences pref; 
	static Calendar mCalendar; 
	static Handler mHandler; 
	static Runnable mTicker ;

	public static void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
    	if (!lpparam.packageName.equals("com.android.systemui")) return;
		try{
			hookClock(lpparam);
		}catch(Throwable t){
		}
	}
	private static void hookClock(final LoadPackageParam lpparam){
		findAndHookMethod("com.android.systemui.statusbar.policy.Clock", lpparam.classLoader, "updateClock", new XC_MethodHook() {
    		@Override
    		protected void afterHookedMethod(MethodHookParam param) throws Throwable {
    			//only the last Clock TextView will be set(statusbar). 
    			//The notification panel and lockscreen also use Clock class. But they are not visible and handler will screw up 
    			if (thix == null){
    			thix = (TextView)param.thisObject; 
    			if(init()){ //init() will return TRUE when setting is enabled.
    				start(); //Start the seconds handler
    			}
    			}
    			if (!enabled){
    				customSettingWhenDisabled();
    			}else{
    				tick();
    			}
    			IntentFilter filter = new IntentFilter();
    			filter.addAction(Common.ACTION_SETTINGS_CHANGED);
    			thix.getContext().registerReceiver(broadcastReceiver, filter);
    			//else tick() is to apply our format IMMEDIATELY when the clock refreshes every second
    			//fixes the skipping bug from 59sec to 01 sec
    		}
    	});
	}
	
	private static boolean init(){ // get all the values
		if (stopForever) return false; //Don't continue
		pref = new XSharedPreferences(Common.MY_PACKAGE_NAME);
		enabled = pref.getBoolean(Common.KEY_SECONDS_ENABLE,Common.DEFAULT_SECONDS_ENABLE);
		bold = pref.getBoolean(Common.KEY_SECONDS_BOLD,Common.DEFAULT_SECONDS_BOLD);
		thix.setTypeface(null, bold ? Typeface.BOLD : Typeface.NORMAL);
		if(!enabled){ 
			stopForever = true; //Stop forever until systemUI reboots. Prevents reading too much off the disk(every minute which is bad)
			return false; 
		}
		setFormat(true);
		return enabled;
	}
	
	private static void start() { // start handlers
		mHandler = new Handler(thix.getContext().getMainLooper());
	    mTicker = new Runnable() {
	        public void run() {
	        	if (enabled){ // must check if enabled before you update seconds
	        		tick(); 
	        		waitOneSecond();
	        	}
	        }
	    };
	    mHandler.postDelayed(mTicker, 800); // Initial wait only. This will never be called again.
	}
	
	private static void setFormat(boolean seconds_enabled){
		format = pref.getString(Common.KEY_SECONDS_CUSTOM,"");
		if (!seconds_enabled)return;
		if (format.equals("")){
			boolean is24hr =  DateFormat.is24HourFormat(thix.getContext()) ;
			format = (is24hr ? "kk:mm:ss" /*24 hr*/ : "hh:mm:ss a" /*12 hr*/);
		}	
	}
	private static void waitOneSecond() { 
		mHandler.postDelayed(mTicker, 990);//wait 1 sec (slightly less to overlap the lag)	
	}
	private static void tick() { // A new second, get the time
		mCalendar = Calendar.getInstance( TimeZone.getDefault());
        thix.setText(DateFormat.format(format, mCalendar));
        thix.invalidate();
	}
	
	private static void customSettingWhenDisabled(){ //Change Clock Format even when seconds disabled
		if (format == null) setFormat(false);
		if (!format.equals("")) tick(); // If the setting is not empty, then use our custom format
		}
	
	private final static BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Common.ACTION_SETTINGS_CHANGED.equals(action)){
				mHandler = null;
				mTicker = null;
				enabled = false;
				stopForever = false;
				format = null;
				//Reset all the variables
				Intent i = new Intent(Intent.ACTION_CONFIGURATION_CHANGED);
				thix.getContext().sendBroadcast(i);
				/* Broadcast to system that time changed. 
				 * System will run "updateClock" on receiving (the hooked method above)
				 * and this simulates restarting SystemUI. "updateClock" also runs 
				 * init() and start() for us.
				 */
			}
		}
	};
}
