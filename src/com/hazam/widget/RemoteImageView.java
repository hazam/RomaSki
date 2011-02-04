package com.hazam.widget;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.cookie.DateUtils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.hazam.handy.fs.FileUtils;
import com.hazam.handy.net.BetterHttpClient;
import com.hazam.romaski.R;
import com.hazam.widget.DownloadTask.DownloadListener;

/**
 * Extended ImageView to handle http:// loading of the images. Includes caching on SDCARD (if available)
 * 
 * @author Emanuele Di Saverio
 */
public class RemoteImageView extends ImageView implements DownloadListener {

	private static final String TAG = "RemoteImageView";
	private Uri remoteUri = null;

	public RemoteImageView(Context context) {
		super(context);
	}

	public RemoteImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RemoteImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private static void trace(String msg) {
		Log.v(TAG, msg);
	}

	@Override
	public void setImageURI(Uri uri) {
		if (uri.toString().startsWith("http")) {
			remoteUri = uri;
			super.setImageResource(R.drawable.hourglass);
			reload();
		} else {
			super.setImageURI(uri);
		}
	}

	public void reload() {
		DownloadTask task = new DownloadTask(getContext(), remoteUri);
		task.setListener(this);
		task.execute();
	}

	@Override
	public void onResourceUpdate(Uri uri) {
		super.setImageURI(uri);
	}

	@Override
	public void onError(Throwable e) {
		// TODO Auto-generated method stub
		
	}
}
