package com.zst.xposed.xuimod.mods;

import java.lang.reflect.Method;

import android.content.Context;
import android.util.Log;
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

	protected static final String TAG = ListViewAnimationMod.class
			.getSimpleName();
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

	public static final int ANIMATION_NONE = 0;
	public static final int ANIMATION_WAVE_LEFT = 1;
	public static final int ANIMATION_WAVE_RIGHT = 2;
	public static final int ANIMATION_SCALE = 3;
	public static final int ANIMATION_ALPHA = 4;
	public static final int ANIMATION_STACK_TOP = 5;
	public static final int ANIMATION_STACK_BOTTOM = 6;
	public static final int ANIMATION_UNFOLD = 7;
	public static final int ANIMATION_FOLD = 8;
	public static final int ANIMATION_TRANSLATE_LEFT = 9;
	public static final int ANIMATION_TRANSLATE_RIGHT = 10;
	public static final int ANIMATION_ROTATE = 11;

	static boolean mIsScrolling;
	static int sWidth, sHeight = 0;
	static int svPosition;

	public static void handleLoadPackage() {
		initAbsListView();
		on_Layout();
		reportScrollStateChange();
		obtainView();
	}

	private static void initAbsListView() {
		XposedBridge.hookAllMethods(AbsListView.class, "initAbsListView",
				new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						mHeight = 0;
						sWidth = 0;
						// Init-ing new AbsListView so we must reset static
						// values from previous view

						XSharedPreferences pref = new XSharedPreferences(
								Common.MY_PACKAGE_NAME);
						int cache = Integer.parseInt(pref.getString(
								Common.KEY_LISTVIEW_CACHE,
								Common.DEFAULT_LISTVIEW_CACHE));
						// Get our pref value in String and parse to Integer

						AbsListView abs_list_view = (AbsListView) param.thisObject;
						abs_list_view.setPersistentDrawingCache(cache);
						// Get object & Apply persistent cache value.

					}
				});
	}

	private static void on_Layout() {
		XposedBridge.hookAllMethods(AbsListView.class, "onLayout",
				new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						AbsListView abs_list_view = (AbsListView) param.thisObject;
						mHeight = abs_list_view.getHeight();
						sWidth = abs_list_view.getWidth();
						// Called when listView changes layout(rotation, first
						// init)
						// We then gather size for the below anim codes.
					}
				});
	}

	private static void reportScrollStateChange() { // TODO documentation
		XposedBridge.hookAllMethods(AbsListView.class,
				"reportScrollStateChange", new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param)
							throws Throwable {
						final int newState = (Integer) param.args[0];
						mIsScrolling = newState != OnScrollListener.SCROLL_STATE_IDLE;
					}
				});
	}

	private static void obtainView() {
		XposedBridge.hookAllMethods(AbsListView.class, "obtainView",
				new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						AbsListView thix = (AbsListView) param.thisObject;

						String activity_name = thix.getContext().getClass()
								.getName();
						Log.i(TAG, "Activity: " + activity_name);
						// TODO Make GUI for disable.
						if (mIsScrolling
								&& !activity_name
										.startsWith("com.quoord.tapatalkpro.activity.forum.ThreadActiv")) {

							View newResult = setAnimation(thix,
									(View) param.getResult(), thix.getContext());
							param.setResult(newResult);
						}
						/*
						 * In actual ROM, the animation is done before returning
						 * view but In Xposed, we must modify the return value
						 * since we can't execute codes before return.
						 * 
						 * We use "param.getResult()" to retrieve our view and
						 * apply animation using "setAnimation".
						 * 
						 * After which we return our animated view using
						 * "param.setResult(newResult)"
						 */
					}
				});
	}

	/*
	 * Not much documentation for setAnimation as actual commit didnt have any
	 * For more info on animations, you'll have to ask MoKee ROM people who did
	 * this
	 */
	private static View setAnimation(Object thisObject, View view,
			Context mContext) {
		XSharedPreferences pref = new XSharedPreferences(Common.MY_PACKAGE_NAME);
		int mAnim = Integer.parseInt(pref.getString(
				Common.KEY_LISTVIEW_ANIMATION,
				Common.DEFAULT_LISTVIEW_ANIMATION));

		if (mAnim == 0)
			return view;

		int scrollY = 0;
		boolean mDown = false;

		try {
			Method showsb = thisObject.getClass().getMethod(
					"computeVerticalScrollOffset");
			Object result = showsb.invoke(thisObject);
			scrollY = (Integer) result;
			/*
			 * Actual non-reflection code is
			 * "scrollY = computeVerticalScrollOffset();" I didn't use
			 * XposedHelper.callMethod as I want to control the Throwable myself
			 */
		} catch (Throwable e) {
			scrollY = mvPosition;
			/*
			 * Actual code from source caught an Exception when as
			 * computeVerticalScrollOffset throws Exception. We catch Throwable
			 * since we have another possibility that the invoke fails and
			 * ForceClose the listView.
			 */
		}

		if (mvPosition < scrollY) {
			mDown = true;
		}

		mvPosition = scrollY;

		Animation anim = null;
		switch (mAnim) {
		case ANIMATION_WAVE_LEFT:
			anim = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f);
			break;
		case ANIMATION_WAVE_RIGHT:
			anim = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f,
					Animation.RELATIVE_TO_SELF, 1.0f,
					Animation.RELATIVE_TO_SELF, 1.0f);
			break;
		case ANIMATION_SCALE:
			anim = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			break;
		case ANIMATION_ALPHA:
			anim = new AlphaAnimation(0.0f, 1.0f);
			break;
		case ANIMATION_STACK_TOP:
			anim = new TranslateAnimation(0.0f, 0.0f, -mHeight, 0.0f);
			break;
		case ANIMATION_STACK_BOTTOM:
			anim = new TranslateAnimation(0.0f, 0.0f, mHeight, 0.0f);
			break;
		case ANIMATION_UNFOLD:
			if (mDown)
				anim = new TranslateAnimation(0.0f, 0.0f, -mHeight, 0.0f);
			else
				anim = new TranslateAnimation(0.0f, 0.0f, mHeight, 0.0f);
			break;
		case ANIMATION_FOLD:
			if (mDown)
				anim = new TranslateAnimation(0.0f, 0.0f, mHeight, 0.0f);
			else
				anim = new TranslateAnimation(0.0f, 0.0f, -mHeight, 0.0f);
			break;
		case ANIMATION_TRANSLATE_LEFT:
			anim = new TranslateAnimation(-sWidth, 0.0f, 0.0f, 0.0f);
			break;
		case ANIMATION_TRANSLATE_RIGHT:
			anim = new TranslateAnimation(sWidth, 0.0f, 0.0f, 0.0f);
			break;
		case ANIMATION_ROTATE:
			anim = new RotateAnimation(180, 0.0f, Animation.RELATIVE_TO_SELF,
					0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			break;
		}
		anim.setDuration(500);

		int mInterpolator = Integer.parseInt(pref.getString(
				Common.KEY_LISTVIEW_INTERPOLATOR,
				Common.DEFAULT_LISTVIEW_INTERPOLATOR));
		switch (mInterpolator) {
		case INTERPOLATOR_ACCELERATE:
			anim.setInterpolator(AnimationUtils.loadInterpolator(mContext,
					android.R.anim.accelerate_interpolator));
			break;
		case INTERPOLATOR_DECELERATE:
			anim.setInterpolator(AnimationUtils.loadInterpolator(mContext,
					android.R.anim.decelerate_interpolator));
			break;
		case INTERPOLATOR_ACCELERATE_DECELERATE:
			anim.setInterpolator(AnimationUtils.loadInterpolator(mContext,
					android.R.anim.accelerate_decelerate_interpolator));
			break;
		case INTERPOLATOR_ANTICIPATE:
			anim.setInterpolator(AnimationUtils.loadInterpolator(mContext,
					android.R.anim.anticipate_interpolator));
			break;
		case INTERPOLATOR_OVERSHOOT:
			anim.setInterpolator(AnimationUtils.loadInterpolator(mContext,
					android.R.anim.overshoot_interpolator));
			break;
		case INTERPOLATOR_ANTICIPATE_OVERSHOOT:
			anim.setInterpolator(AnimationUtils.loadInterpolator(mContext,
					android.R.anim.anticipate_overshoot_interpolator));
			break;
		case INTERPOLATOR_BOUNCE:
			anim.setInterpolator(AnimationUtils.loadInterpolator(mContext,
					android.R.anim.bounce_interpolator));
			break;
		case INTERPOLATOR_CYCLE:
			anim.setInterpolator(AnimationUtils.loadInterpolator(mContext,
					android.R.anim.cycle_interpolator));
			break;
		case INTERPOLATOR_LINEAR:
			anim.setInterpolator(AnimationUtils.loadInterpolator(mContext,
					android.R.anim.linear_interpolator));
			break;
		}
		if (view != null) {
			view.startAnimation(anim);
		}
		return view;
	}
}

