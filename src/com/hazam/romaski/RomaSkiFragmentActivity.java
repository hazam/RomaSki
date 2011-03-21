package com.hazam.romaski;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Window;

public class RomaSkiFragmentActivity extends FragmentActivity {
	private boolean isHandset = false;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		int scrSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
		switch (scrSize) {
		case Configuration.SCREENLAYOUT_SIZE_SMALL:
		case Configuration.SCREENLAYOUT_SIZE_NORMAL:
			isHandset = true;
			break;
		}
		if (isHandset) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setTheme(R.style.Theme_RomaSki);
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
			setContentView(R.layout.handset_main);
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);
		} else {//we are tablet
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			setContentView(R.layout.tablet_main);
		}
		getWindow().setFormat(PixelFormat.RGBA_8888);
	}
}
