package com.hazam.romaski;

import com.hazam.widget.Pinch;
import com.hazam.widget.PinchListener;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class DetailActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);
		ImageView piw = (ImageView) findViewById(R.id.detail);
		piw.setImageResource(R.drawable.ovindolimagnola_pda480);
		Pinch.makePinchable(piw);
	}
}
