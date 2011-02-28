package com.hazam.romaski;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.hazam.romaski.model.Resort;

public class RomaTabsActivity extends TabActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		TabHost tabHost = getTabHost();
		for (Resort r : Resort.ALL.values()) {
			//tabHost.addTab(tabHost.newTabSpec(r.getId()).setIndicator(null, getResources().getDrawable(r.getDrawableId())).setContent(this));
			TabSpec spec = tabHost.newTabSpec(r.getId());
			spec.setIndicator(null, getResources().getDrawable(r.getDrawableId()) );
			Intent intent = new Intent(this, ResortInfoActivity.class);
			intent.putExtra(ResortInfoActivity.EXTRA_RESORT_KEY, r.getId());
			spec.setContent(intent);
			tabHost.addTab(spec);
		}
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);
		getWindow().setFormat(PixelFormat.RGBA_8888);
	}
}