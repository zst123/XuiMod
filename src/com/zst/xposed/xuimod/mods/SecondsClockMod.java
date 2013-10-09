package com.zst.xposed.xuimod.mods;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import java.util.Calendar;
import java.util.TimeZone;

import com.zst.xposed.xuimod.Common;

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
    			if (thix != null) return; 
    			thix = (TextView)param.thisObject; 
    			if(init()){ //init() will return TRUE when setting is enabled.
    				start(); //Start the seconds handler
    			}
    		}
    	});
	}
	
	private static boolean init(){ // get all the values
		if (stopForever) return false; //Don't continue
		pref = new XSharedPreferences(Common.MY_PACKAGE_NAME);
		enabled = pref.getBoolean(Common.KEY_SECONDS_ENABLE,Common.DEFAULT_SECONDS_ENABLE);
		if(!enabled){ // Disabled, dont change typeface
			stopForever = true; //Stop forever until systemUI reboots. Prevents reading too much off the disk(every minute which is bad)
			thix.setTypeface(null,Typeface.NORMAL);
			return false; 
		}
		bold = pref.getBoolean(Common.KEY_SECONDS_BOLD,Common.DEFAULT_SECONDS_BOLD);
		thix.setTypeface(null, bold ? Typeface.BOLD : Typeface.NORMAL);
		setFormat();
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
	
	private static void setFormat(){
		format = pref.getString(Common.KEY_SECONDS_CUSTOM,"");
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
}
