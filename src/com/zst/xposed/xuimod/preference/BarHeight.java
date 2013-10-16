package com.zst.xposed.xuimod.preference;

import com.zst.xposed.xuimod.Common;
import com.zst.xposed.xuimod.R;
import static com.zst.xposed.xuimod.Common.KEY_BATTERYBAR_HEIGHT;
import static com.zst.xposed.xuimod.Common.DEFAULT_BATTERYBAR_HEIGHT;
import static com.zst.xposed.xuimod.Common.LIMIT_MAX_BATTERYBAR_HEIGHT;
import static com.zst.xposed.xuimod.Common.LIMIT_MIN_BATTERYBAR_HEIGHT;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class BarHeight extends DialogPreference implements
		SeekBar.OnSeekBarChangeListener {

	private static final String TAG = BarHeight.class.getName();
	
	private View mLine;
	private SeekBar mSeekBar;
	private TextView mValue;

	public BarHeight(Context context, AttributeSet attrs) {
		super(context, attrs);

		setDialogLayoutResource(R.layout.bar_height);
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
		
		mLine = (View) view.findViewById(R.id.line);
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
				mSeekBar.setProgress(DEFAULT_BATTERYBAR_HEIGHT-LIMIT_MIN_BATTERYBAR_HEIGHT);
			}
		});
		
		final SharedPreferences prefs = getSharedPreferences();
		
		int value = prefs.getInt(KEY_BATTERYBAR_HEIGHT, DEFAULT_BATTERYBAR_HEIGHT);
		value -= LIMIT_MIN_BATTERYBAR_HEIGHT;
		
		mSeekBar.setMax(LIMIT_MAX_BATTERYBAR_HEIGHT - LIMIT_MIN_BATTERYBAR_HEIGHT);
		mSeekBar.setProgress(value);
		
        String colorString = prefs.getString(Common.KEY_BATTERYBAR_COLOR, Common.DEFAULT_BATTERYBAR_COLOR);
        mLine.setBackgroundColor(parseLineColor(colorString));
	}

	public int parseLineColor(String colorString) {
        try{
        	return Color.parseColor(colorString);
        }catch(Throwable t){ //Error parsing color, try removing non-numeric characters
        	 try{
        		 colorString = colorString.replaceAll( "[^\\d]", "" ); //Remove non-numeric characters
                 return Color.parseColor("#" + colorString);
             }catch(Throwable t1){ // Error parsing the string. Use default anyway.
             	 return 0xFF33B5E5;
             }
        }
	}
	
	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		
		if (positiveResult) {
			int realValue = mSeekBar.getProgress() + LIMIT_MIN_BATTERYBAR_HEIGHT;
			Editor editor = getEditor();
			editor.putInt(KEY_BATTERYBAR_HEIGHT, realValue);
			editor.commit();
			
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		int realValue = progress + LIMIT_MIN_BATTERYBAR_HEIGHT;

		mLine.setMinimumHeight(realValue);
		mValue.setText(realValue + "dp");
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