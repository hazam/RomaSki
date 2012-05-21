package com.hazam.romaski;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;

public class DetailActivity extends Activity {
	public static final String EXTRA_URI = "extra_uri";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);
		FragmentManager fm = getFragmentManager();
		PinchableImageFragment detailFrag = (PinchableImageFragment) fm.findFragmentById(R.id.detail);
		Uri target = Uri.parse(getIntent().getStringExtra(EXTRA_URI));
		detailFrag.setTarget(target);
		
		
		
	}
}
