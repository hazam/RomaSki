package com.hazam.romaski;

import com.hazam.romaski.model.Resort;
import com.hazam.widget.RemoteImageView;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class ResortInfoActivity extends Activity {
	
	public static String EXTRA_RESORT_KEY = "resort";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String tag = getIntent().getExtras().getString( EXTRA_RESORT_KEY );
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
		setContentView(sv);
	}
}
