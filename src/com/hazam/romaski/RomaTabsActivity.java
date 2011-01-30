package com.hazam.romaski;

import android.app.TabActivity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;

import com.hazam.romaski.model.Resort;
import com.hazam.widget.RemoteImageView;

public class RomaTabsActivity extends TabActivity implements TabContentFactory {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TabHost tabHost = getTabHost();
		for (Resort r : Resort.ALL.values()) {
			tabHost.addTab(tabHost.newTabSpec(r.getId()).setIndicator(null, getResources().getDrawable(r.getDrawableId())).setContent(this));
		}
	}

	public View createTabContent(String tag) {
		final ScrollView sv = new ScrollView(this);
		sv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		sv.setFillViewport(true);
		Resort selReport = Resort.ALL.get(tag);
		final LinearLayout anf = new LinearLayout(this);
		anf.setOrientation(LinearLayout.VERTICAL);
		LayoutParams wrap_wrap = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT); 
		anf.setLayoutParams( wrap_wrap );
		anf.setGravity(Gravity.CENTER_HORIZONTAL);
		for (Uri s : selReport) {
			RemoteImageView wv = new RemoteImageView(this);
			wv.setLayoutParams( wrap_wrap);
			wv.setImageURI(s);
			//wv.setAdjustViewBounds(true);
			anf.addView(wv, new LayoutParams(
					(int) (280 * getResources().getDisplayMetrics().density),
					(int) (280 * getResources().getDisplayMetrics().density)));
		}
		sv.addView(anf);
		return sv;
	}
}