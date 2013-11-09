/*
 * Copyright (C) 2011 The Android Open Source Project
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

/* 
 * This class contains all the constants from AppTransition.java from Android 4.3 
 * Directly copied from source
 */
public class AppTransitionConstants {
	
	/** Bit mask that is set for all enter transition. */
    public static final int TRANSIT_ENTER_MASK = 0x1000;

    /** Bit mask that is set for all exit transitions. */
    public static final int TRANSIT_EXIT_MASK = 0x2000;

    /** Not set up for a transition. */
    public static final int TRANSIT_UNSET = -1;
    /** No animation for transition. */
    public static final int TRANSIT_NONE = 0;
    /** A window in a new activity is being opened on top of an existing one in the same task. */
    public static final int TRANSIT_ACTIVITY_OPEN = 6 | TRANSIT_ENTER_MASK;
    /** The window in the top-most activity is being closed to reveal the
     * previous activity in the same task. */
    public static final int TRANSIT_ACTIVITY_CLOSE = 7 | TRANSIT_EXIT_MASK;
    /** A window in a new task is being opened on top of an existing one
     * in another activity's task. */
    public static final int TRANSIT_TASK_OPEN = 8 | TRANSIT_ENTER_MASK;
    /** A window in the top-most activity is being closed to reveal the
     * previous activity in a different task. */
    public static final int TRANSIT_TASK_CLOSE = 9 | TRANSIT_EXIT_MASK;
    /** A window in an existing task is being displayed on top of an existing one
     * in another activity's task. */
    public static final int TRANSIT_TASK_TO_FRONT = 10 | TRANSIT_ENTER_MASK;
    /** A window in an existing task is being put below all other tasks. */
    public static final int TRANSIT_TASK_TO_BACK = 11 | TRANSIT_EXIT_MASK;
    /** A window in a new activity that doesn't have a wallpaper is being opened on top of one that
     * does, effectively closing the wallpaper. */
    public static final int TRANSIT_WALLPAPER_CLOSE = 12 | TRANSIT_EXIT_MASK;
    /** A window in a new activity that does have a wallpaper is being opened on one that didn't,
     * effectively opening the wallpaper. */
    public static final int TRANSIT_WALLPAPER_OPEN = 13 | TRANSIT_ENTER_MASK;
    /** A window in a new activity is being opened on top of an existing one, and both are on top
     * of the wallpaper. */
    public static final int TRANSIT_WALLPAPER_INTRA_OPEN = 14 | TRANSIT_ENTER_MASK;
    /** The window in the top-most activity is being closed to reveal the previous activity, and
     * both are on top of the wallpaper. */
    public static final int TRANSIT_WALLPAPER_INTRA_CLOSE = 15 | TRANSIT_EXIT_MASK;

    public static final int NEXT_TRANSIT_TYPE_NONE = 0;
    public static final int NEXT_TRANSIT_TYPE_CUSTOM = 1;
    public static final int NEXT_TRANSIT_TYPE_SCALE_UP = 2;
    public static final int NEXT_TRANSIT_TYPE_THUMBNAIL_SCALE_UP = 3;
    public static final int NEXT_TRANSIT_TYPE_THUMBNAIL_SCALE_DOWN = 4;
}
