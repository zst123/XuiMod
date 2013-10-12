package com.zst.xposed.xuimod.preference;

import com.zst.xposed.xuimod.Common;
import com.zst.xposed.xuimod.R;
import static com.zst.xposed.xuimod.Common.KEY_VOLUME_ALPHA;
import static com.zst.xposed.xuimod.Common.DEFAULT_VOLUME_ALPHA;
import static com.zst.xposed.xuimod.Common.LIMIT_MAX_VOLUME_ALPHA;
import static com.zst.xposed.xuimod.Common.LIMIT_MIN_VOLUME_ALPHA;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class VolumePanelAlpha extends DialogPreference implements
		SeekBar.OnSeekBarChangeListener {

	private static final String TAG = VolumePanelAlpha.class.getName();

	private SeekBar mSeekBar;
	private TextView mValue;

	public VolumePanelAlpha(Context context, AttributeSet attrs) {
		super(context, attrs);

		setDialogLayoutResource(R.layout.volume_panel_alpha);
	}

	@Override
	protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
		builder.setNeutralButton(R.string.settings_default,
				new DialogInterface.OnClickListener() {
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
				mSeekBar.setProgress(DEFAULT_VOLUME_ALPHA);
			}
		});
		
		final SharedPreferences prefs = getSharedPreferences();
		int value = prefs.getInt(KEY_VOLUME_ALPHA, DEFAULT_VOLUME_ALPHA);
		value -= LIMIT_MIN_VOLUME_ALPHA;
		
		mSeekBar.setMax(LIMIT_MAX_VOLUME_ALPHA - LIMIT_MIN_VOLUME_ALPHA);
		mSeekBar.setProgress(value);
		
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if (positiveResult) {

			int realValue = mSeekBar.getProgress() + LIMIT_MIN_VOLUME_ALPHA;
			Editor editor = getEditor();
			editor.putInt(KEY_VOLUME_ALPHA, realValue);
			editor.commit();
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		int realValue = progress + LIMIT_MIN_VOLUME_ALPHA;

		Dialog dialog = getDialog();
		if (dialog != null) {
			Window window = dialog.getWindow();
			WindowManager.LayoutParams lp = window.getAttributes();
			lp.alpha = (realValue * 0.01f); // Convert Percentage to Decimal
			window.setAttributes(lp);
		}
		mValue.setText(realValue + "%");
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