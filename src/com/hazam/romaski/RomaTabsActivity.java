package com.hazam.romaski;

import android.app.TabActivity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.ImageView.ScaleType;
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
		anf.setLayoutParams( new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT) );
		for (Uri s : selReport) {
			/*WebView wv = new WebView(this);
			wv.loadUrl(s.toString());*/
			RemoteImageView wv = new RemoteImageView(this);
			wv.setLayoutParams( new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			wv.setImageURI(s);
			wv.setScaleType(ScaleType.CENTER_INSIDE);
			anf.addView(wv);
		}
		sv.addView(anf);
		return sv;
	}
}