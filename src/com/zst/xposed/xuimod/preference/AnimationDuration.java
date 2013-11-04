/*
 * Copyright (C) 2013 XuiMod
 * Copyright (C) 2013 Dzakus
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

package com.zst.xposed.xuimod.preference;

import com.zst.xposed.xuimod.Common;
import com.zst.xposed.xuimod.R;
import static com.zst.xposed.xuimod.Common.KEY_LISTVIEW_DURATION;
import static com.zst.xposed.xuimod.Common.DEFAULT_LISTVIEW_DURATION;
import static com.zst.xposed.xuimod.Common.LIMIT_MAX_LISTVIEW_DURATION;
import static com.zst.xposed.xuimod.Common.LIMIT_MIN_LISTVIEW_DURATION;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class AnimationDuration extends DialogPreference implements SeekBar.OnSeekBarChangeListener {

	@SuppressWarnings("unused")
	private static final String TAG = AnimationDuration.class.getName();

	private SeekBar mSeekBar;
	private TextView mValue;

	public AnimationDuration(Context context, AttributeSet attrs) {
		super(context, attrs);

		setDialogLayoutResource(R.layout.pref_seekbar);
	}

	@Override
	protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
		builder.setNeutralButton(R.string.settings_default, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);

		mValue = (TextView) view.findViewById(R.id.value);
		mSeekBar = (SeekBar) view.findViewById(R.id.seekbar);
		mSeekBar.setOnSeekBarChangeListener(this);
	}

	@Override
	protected void showDialog(Bundle state) {
		super.showDialog(state);

		// can't use onPrepareDialogBuilder for this as we want the dialog
		// to be kept open on click
		AlertDialog d = (AlertDialog) getDialog();
		Button defaultsButton = d.getButton(DialogInterface.BUTTON_NEUTRAL);
		defaultsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mSeekBar.setProgress(DEFAULT_LISTVIEW_DURATION - LIMIT_MIN_LISTVIEW_DURATION);
			}
		});

		final SharedPreferences prefs = getSharedPreferences();

		int value = prefs.getInt(Common.KEY_LISTVIEW_DURATION, DEFAULT_LISTVIEW_DURATION);
		value -= LIMIT_MIN_LISTVIEW_DURATION;

		mSeekBar.setMax(LIMIT_MAX_LISTVIEW_DURATION - LIMIT_MIN_LISTVIEW_DURATION);
		mSeekBar.setProgress(value);

	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			int realValue = mSeekBar.getProgress() + LIMIT_MIN_LISTVIEW_DURATION;
			Editor editor = getEditor();
			editor.putInt(KEY_LISTVIEW_DURATION, realValue);
			editor.commit();
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		int realValue = progress + LIMIT_MIN_LISTVIEW_DURATION;
		mValue.setText(realValue + " ms");
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
	}
}
