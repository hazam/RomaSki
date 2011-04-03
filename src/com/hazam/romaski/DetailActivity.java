package com.hazam.romaski;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class DetailActivity extends FragmentActivity {
	public static final String EXTRA_URI = "extra_uri";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);
		FragmentManager fm = getSupportFragmentManager();
		PinchableImageFragment detailFrag = (PinchableImageFragment) fm.findFragmentById(R.id.detail);
		Uri target = Uri.parse(getIntent().getStringExtra(EXTRA_URI));
		detailFrag.setTarget(target);
	}
}
