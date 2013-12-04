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

import com.zst.xposed.xuimod.R;

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

public class SeekBarDialog extends DialogPreference implements SeekBar.OnSeekBarChangeListener {

	private SeekBar mSeekBar;
	private TextView mValue;

	private String mKey = "";
	private String mSuffix = "";
	private int mMin;
	private int mMax;
	private int mDefault;
	
	public SeekBarDialog(Context context, AttributeSet attrs) {
		super(context, attrs);

		setDialogLayoutResource(R.layout.pref_seekbar);
		mDefault = Integer.parseInt(attrs.getAttributeValue(null, "defaultValue"));
		mMin = Integer.parseInt(attrs.getAttributeValue(null, "minimum"));
		mMax = Integer.parseInt(attrs.getAttributeValue(null, "maximum"));
		mSuffix = attrs.getAttributeValue(null, "suffix");
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
				mSeekBar.setProgress(mDefault - mMin);
			}
		});

		final SharedPreferences prefs = getSharedPreferences();
		mKey = getKey();
		int value = prefs.getInt(mKey, mDefault);
		value -= mMin;
		mSeekBar.setMax(mMax - mMin);
		mSeekBar.setProgress(value);

	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			int realValue = mSeekBar.getProgress() + mMin;
			Editor editor = getEditor();
			editor.putInt(mKey, realValue);
			editor.commit();
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		int realValue = progress + mMin;
		mValue.setText(realValue + mSuffix);
		if (realValue == -1){
			mValue.setText(R.string.settings_default);
		}
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
