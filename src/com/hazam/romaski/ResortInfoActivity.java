package com.hazam.romaski;

import com.hazam.romaski.model.Resort;
import com.hazam.widget.RemoteImageView;

import android.app.Activity;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SpinnerAdapter;

public class ResortInfoActivity extends Activity {

	public static String EXTRA_RESORT_KEY = "resort";

	private Gallery webcams;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		String tag = getIntent().getExtras().getString( EXTRA_RESORT_KEY );
		final Resort selReport = Resort.ALL.get(tag);
		webcams = (Gallery) findViewById(R.id.webcams);
		webcams.setAdapter(new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				RemoteImageView wv = new RemoteImageView(ResortInfoActivity.this);
				wv.setLayoutParams( new Gallery.LayoutParams(
						(int) (280 * getResources().getDisplayMetrics().density),
						(int) (280 * getResources().getDisplayMetrics().density)) );
				wv.setImageURI( (Uri) getItem(position));
				return wv;
			}
			
			@Override
			public long getItemId(int position) {
				return position;
			}
			
			@Override
			public Object getItem(int position) {
				return selReport.get(position);
			}
			
			@Override
			public int getCount() {
				return selReport.size();
			}
		});
		/*final ScrollView sv = new ScrollView(this);
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
		setContentView(sv);*/
	}
}
