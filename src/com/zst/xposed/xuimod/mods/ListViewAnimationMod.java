package com.zst.xposed.xuimod.mods;


import java.lang.reflect.Method;

import android.content.Context;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.zst.xposed.xuimod.Common;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;

public class ListViewAnimationMod {
	
	static boolean mIsScrolling;
	static int mWidth, mHeight = 0;
	static int mvPosition;

	public static void handleLoadPackage() {
		initAbsListView();
		on_Layout();
		reportScrollStateChange();
		obtainView();
	}
	private static void initAbsListView() { 
		XposedBridge.hookAllMethods(AbsListView.class, "initAbsListView", new XC_MethodHook(){
			@Override 
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				mHeight = 0; mWidth = 0;
				// Init-ing new AbsListView so we must reset static values from previous view
				
				XSharedPreferences pref = new XSharedPreferences(Common.MY_PACKAGE_NAME);
				int cache = Integer.parseInt(pref.getString(Common.KEY_LISTVIEW_CACHE, Common.DEFAULT_LISTVIEW_CACHE));
				// Get our pref value in String and parse to Integer
				
				AbsListView item = (AbsListView)param.thisObject; 
				item.setPersistentDrawingCache(cache);
				// Get object & Apply persistent cache value.
			}			
		});	
	}
	
	private static void on_Layout() { 
		XposedBridge.hookAllMethods(AbsListView.class, "onLayout", new XC_MethodHook(){
			@Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				AbsListView item = (AbsListView)param.thisObject;
				mHeight = item.getHeight();
		        mWidth = item.getWidth(); 
		        // Called when listView changes layout(rotation, first init)
		        // We then gather size for the below anim codes.
			} 
		});	
	}
	
	private static void reportScrollStateChange() { //TODO documentation
		XposedBridge.hookAllMethods(AbsListView.class, "reportScrollStateChange", new XC_MethodHook(){
			@Override protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				final int newState = (Integer) param.args[0];
				mIsScrolling = newState != OnScrollListener.SCROLL_STATE_IDLE;
			}			
		});	
	}
	
	private static void obtainView(){ 
		XposedBridge.hookAllMethods(AbsListView.class, "obtainView", new XC_MethodHook(){
			@Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				View v = (View) param.getResult();
				if(mIsScrolling && v.getAnimation() == null) {
					AbsListView thix = (AbsListView)param.thisObject;
					View newResult = setAnimation(thix,v ,thix.getContext());
					param.setResult(newResult);
				}
				/* In actual ROM, the animation is done before returning view but
				 * In Xposed, we must modify the return value since we can't execute
				 * codes before return. 
				 * 
				 * We use "param.getResult()" to retrieve our view and
				 * apply animation using "setAnimation".
				 * 
				 * After which we return our
				 * animated view using "param.setResult(newResult)"
				 */
			}
		});	
	}
	
	/* Not much documentation for setAnimation as actual commit didnt have any
	 * For more info on animations, you'll have to ask MoKee ROM people who did this
	 */
	private static View setAnimation(Object thisObject, View view, Context mContext) {
		XSharedPreferences pref = new XSharedPreferences(Common.MY_PACKAGE_NAME);
		int mAnim = Integer.parseInt( pref.getString(Common.KEY_LISTVIEW_ANIMATION, Common.DEFAULT_LISTVIEW_ANIMATION) );

		if(mAnim == 0) return view;
		
		int scrollY = 0;
		boolean mDown = false;
		        
		try {
			Method showsb = thisObject.getClass().getMethod("computeVerticalScrollOffset");
			Object result = showsb.invoke( thisObject );	
			scrollY = (Integer)result;
			/* Actual non-reflection code is "scrollY = computeVerticalScrollOffset();"
			 * I didn't use XposedHelper.callMethod as I want to control the Throwable myself
			 */
		} catch (Throwable e) {
			scrollY = mvPosition;
			/* Actual code from source caught an Exception when as computeVerticalScrollOffset
			 * throws Exception.
			 * We catch Throwable since we have another possibility that the invoke fails
			 * and ForceClose the listView.
			 */
		}
		
		if(mvPosition < scrollY){
			mDown = true;
		}
		
		mvPosition = scrollY;
			
		Animation anim = null;
		switch (mAnim) {
		case 1:
			anim = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f);
			break;
		case 2:
			anim = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF,1.0f, Animation.RELATIVE_TO_SELF, 1.0f);
			break;
		case 3:
			anim = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF, 0.5f);  
			break;
		case 4:
			anim = new AlphaAnimation(0.0f, 1.0f);
			break;
		case 5:
			anim = new TranslateAnimation(0.0f, 0.0f, -mHeight, 0.0f);
			break;
		case 6:
			anim = new TranslateAnimation(0.0f, 0.0f, mHeight, 0.0f);
			break;
		case 7:
			if(mDown)
				anim = new TranslateAnimation(0.0f, 0.0f, -mHeight, 0.0f);
			else
				anim = new TranslateAnimation(0.0f, 0.0f, mHeight, 0.0f);
			break;
		case 8:
			if(mDown)
				anim = new TranslateAnimation(0.0f, 0.0f, mHeight, 0.0f);
			else
				anim = new TranslateAnimation(0.0f, 0.0f, -mHeight, 0.0f);
			break;
		case 9:
			anim = new TranslateAnimation(-mWidth, 0.0f, 0.0f, 0.0f);
			break;
		case 10:
			anim = new TranslateAnimation(mWidth, 0.0f, 0.0f, 0.0f);
			break;
		case 11:
			anim = new RotateAnimation(180, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			break;
		}
		anim.setDuration(500);
		        	
		int mInterpolator = Integer.parseInt( pref.getString(Common.KEY_LISTVIEW_INTERPOLATOR, Common.DEFAULT_LISTVIEW_INTERPOLATOR) );
		switch (mInterpolator) {
		case 1:
			anim.setInterpolator(AnimationUtils.loadInterpolator(mContext, android.R.anim.accelerate_interpolator));
			break;
		case 2:
			anim.setInterpolator(AnimationUtils.loadInterpolator(mContext, android.R.anim.decelerate_interpolator));
			break;
		case 3:
			anim.setInterpolator(AnimationUtils.loadInterpolator(mContext, android.R.anim.accelerate_decelerate_interpolator));
			break;
		case 4:
			anim.setInterpolator(AnimationUtils.loadInterpolator(mContext, android.R.anim.anticipate_interpolator));
			break;
		case 5:
			anim.setInterpolator(AnimationUtils.loadInterpolator(mContext, android.R.anim.overshoot_interpolator));
			break;
		case 6:
			anim.setInterpolator(AnimationUtils.loadInterpolator(mContext, android.R.anim.anticipate_overshoot_interpolator));
			break;
		case 7:
			anim.setInterpolator(AnimationUtils.loadInterpolator(mContext, android.R.anim.bounce_interpolator));
			break;
		case 8:
			anim.setInterpolator(AnimationUtils.loadInterpolator(mContext, android.R.anim.cycle_interpolator));
			break;
		case 9:
			anim.setInterpolator(AnimationUtils.loadInterpolator(mContext, android.R.anim.linear_interpolator));
			break;
		}
		if (view != null){
			view.startAnimation(anim);
		}
		return view;
	}
}
/*************************************
 *  ListView Animations
 * 0 == None
 * 1 == Wave (Left)
 * 2 == Wave (Right)
 * 3 == Scale
 * 4 == Alpha
 * 5 == Stack (Top)
 * 6 == Stack (Bottom)
 * 7 == Unfold
 * 8 == Fold
 * 9 == Translate (Left)
 * 10 == Translate (Right)
 * 11 == Rotate
 * 
 *************************************
 *
 * ListView Interpolators
 * 0 == None
 * 1 == accelerate_interpolator
 * 2 == decelerate_interpolator
 * 3 == accelerate_decelerate_interpolator
 * 4 == anticipate_interpolator
 * 5 == overshoot_interpolator
 * 6 == anticipate_overshoot_interpolator
 * 7 == bounce_interpolator
 * 8 == cycle_interpolator
 * 9 == linear_interpolator
 * 
 *************************************
 */

        
        
        
