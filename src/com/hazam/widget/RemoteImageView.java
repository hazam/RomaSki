package com.hazam.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask.Status;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.hazam.handy.HandyApplication;
import com.hazam.handy.fs.ImageCache;
import com.hazam.handy.net.Base64;
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
	private ImageCache cache;
	private DownloadTask currentTask = null;

	public RemoteImageView(Context context) {
		super(context);
		init(context);
	}

	public RemoteImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public RemoteImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context ctx) {
		cache = (ImageCache) HandyApplication.getAppService(HandyApplication.IMAGE_CACHE_APPSERVICE);
		if (cache == null) {
			throw new RuntimeException("No ImageCache service registered!");
		}
	}

	private static void trace(String msg) {
		Log.v(TAG, msg);
	}

	@Override
	public void setImageURI(Uri uri) {
		if (uri.toString().startsWith("http")) {
			remoteUri = uri;
			super.setImageResource(R.drawable.hourglass);
			cacheReload();
		} else {
			super.setImageURI(uri);
		}
	}

	public void netReload() {
		if (currentTask != null) {
			if (currentTask.getStatus() != Status.FINISHED) {
				// should we abort the currentTask? don'think so
			}
			currentTask.removeListener(this);
			currentTask = null;
		}
		currentTask = new DownloadTask(getContext(), remoteUri, cache);
		currentTask.addListener(this);
		trace("Start download for: " + remoteUri);
		currentTask.executeManaged();
	}

	public void cacheReload() {
		if (remoteUri == null) {
			return;
		}
		setImageFromCache();
		trace("currentTask "+currentTask);
		if (currentTask == null) {
			currentTask = DownloadTask.retrievePending(remoteUri);
			if (currentTask != null) {
				currentTask.addListener(this);
			}
			trace("pending "+currentTask+ " listener "+this);
		}
	}
	
	private void setImageFromCache() {
		String id = Base64.encodeToString(remoteUri.toString().getBytes(), Base64.DEFAULT);
		if (cache.hasEntryFor( id )) {
			trace("Found in cache!" + remoteUri);
			Bitmap orig = cache.getBitmap(id);
			setImageBitmap(orig);
		}
	}

	@Override
	public void onResourceUpdate(Uri uri) {
		trace("RESOURCE IS DOWNLOADED " + uri);
		setImageFromCache();
	}

	@Override
	public void onError(Throwable e) {
		// TODO Auto-generated method stub

	}
}
