package com.hazam.romaski;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Gallery;

import com.hazam.romaski.model.WebcamImagesAdapter;

public class ResortInfoActivity extends Activity {

	public static String EXTRA_RESORT_KEY = "resort";

	private Gallery webcams;
	private WebcamImagesAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		String tag = getIntent().getExtras().getString( EXTRA_RESORT_KEY );
		webcams = (Gallery) findViewById(R.id.webcams);
		adapter = new WebcamImagesAdapter(this, tag);
		webcams.setAdapter(adapter);
	}
}
