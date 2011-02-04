package com.hazam.widget;

import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;

public class DownloadedBitmapDrawable extends BitmapDrawable {
	
	private Uri target;
	
	public void setRemoteUri(Uri _t) {
		this.target = _t;
	}
}
