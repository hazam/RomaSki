package com.hazam.widget;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.cookie.DateUtils;
import org.apache.http.message.AbstractHttpMessage;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.hazam.handy.fs.FileUtils;
import com.hazam.handy.fs.FilesystemCache;
import com.hazam.handy.net.BetterHttpClient;

public class DownloadTask extends AsyncTask<Void, Long, Uri> implements FileUtils.Tick {

	private final Context ctx;
	private final ConnectivityManager connectivityManager;
	private static final BetterHttpClient httpClient = BetterHttpClient.buildClient(null, false);

	private long clength;
	private Throwable error = null;
	private Uri targetUri;
	private FilesystemCache cache;

	private ArrayList<DownloadListener> listeners = new ArrayList<DownloadListener>();

	private static HashMap<Uri, DownloadTask> pending = new HashMap<Uri, DownloadTask>();

	public static DownloadTask retrievePending(Uri targetUri) {
		if (pending.containsKey(targetUri)) {
			return pending.get(targetUri);
		}
		return null;
	}

	public DownloadTask(final Context _ctx, Uri targetUri, FilesystemCache cache) {
		this.ctx = _ctx.getApplicationContext();
		this.connectivityManager = (ConnectivityManager) ctx.getSystemService(Activity.CONNECTIVITY_SERVICE);
		this.targetUri = targetUri;
		this.cache = cache;
	}

	public static interface DownloadListener {

		public void onResourceUpdate(Uri uri);

		public void onError(Throwable e);
	}

	@Override
	protected void onPreExecute() {
		pending.put(targetUri, this);
	}

	@Override
	protected void onPostExecute(Uri result) {
		if (error != null) {
			// exception
			for (DownloadListener listener : listeners) {
				if (listener != null) {
					listener.onError(error);
				}
			}
		} else {
			// handle succesfull download or found in cache
			for (DownloadListener listener : listeners) {
				if (result != null && listener != null) {
					listener.onResourceUpdate(targetUri);
				}
			}
		}
		if (pending.containsKey(targetUri)) {
			pending.remove(targetUri);
		}
	}

	private void augmentWithIfModifiedSince(final File targetFile, final AbstractHttpMessage mess) {
		if (targetFile.exists()) {
			final String lastModifiedFormatted = DateUtils.formatDate(new Date(targetFile.lastModified()));
			mess.addHeader("If-Modified-Since", lastModifiedFormatted);
		}
	}

	@Override
	protected Uri doInBackground(final Void... params) {
		error = null;
		if (weAreOnline()) {
			final HttpGet getFile = new HttpGet(targetUri.toString());
			try {
				final HttpResponse resp = httpClient.execute(getFile);

				final HttpEntity ent = resp.getEntity();
				final int statusCode = resp.getStatusLine().getStatusCode();
				switch (statusCode) {
				case HttpStatus.SC_OK:
					handleEntity(ent);
					break;
				case HttpStatus.SC_NOT_MODIFIED:
					break;
				default:
					throw new RuntimeException("Http Status Code: " + statusCode);
				}

				if (ent != null) {
					ent.consumeContent();
				}
				return targetUri;
			} catch (Throwable e) {
				error = new RuntimeException(TAG + ": error", e);
				e.printStackTrace();
				return null;
			} finally {
			}
		} else {
			return null;
		}
	}

	private void handleEntity(final HttpEntity ent) throws IllegalStateException, IOException {
		clength = ent.getContentLength();
		InputStream in = ent.getContent();
		if (cache != null) {
			cache.save(targetUri.getLastPathSegment(), in);
		}
	}

	private static final String TAG = "DownloadTask";

	private static final void trace(final String msg) {
		Log.v(TAG, msg);
	}

	@Override
	public final void tick(final long current) {
		publishProgress(current, clength);
	}

	@Override
	protected void onProgressUpdate(final Long... values) {
		trace("Just tracing..." + values[0] + " over " + values[1]);
	}

	private final boolean weAreOnline() {
		final NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
		return ni != null && ni.isConnected();
	}

	public void addListener(DownloadListener listener) {
		listeners.add(listener);
	}

	public void removeListener(DownloadListener listener) {
		listeners.remove(listener);
	}
}