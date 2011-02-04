package com.hazam.widget;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

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
import com.hazam.handy.net.BetterHttpClient;

public class DownloadTask extends AsyncTask<Void, Long, Uri> implements FileUtils.Tick {

	private final Context ctx;
	private final File cacheDir;
	private final ConnectivityManager connectivityManager;
	private final BetterHttpClient httpClient;

	private long clength;
	private Throwable error = null;
	private Uri targetUri;
	private File targetFile;
	private DownloadListener listener;

	public DownloadTask(final Context _ctx, Uri targetUri) {
		this.ctx = _ctx.getApplicationContext();
		this.connectivityManager = (ConnectivityManager) ctx.getSystemService(Activity.CONNECTIVITY_SERVICE);
		this.httpClient = new BetterHttpClient(null, false);
		this.cacheDir = new File(ctx.getCacheDir(), TAG);
		this.targetUri = targetUri;
	}

	public static interface DownloadListener {
		
		public void onResourceUpdate(Uri uri);

		public void onError(Throwable e);
	}

	@Override
	protected void onPreExecute() {
		String computed = targetUri.getLastPathSegment();
		targetFile = new File(cacheDir, computed);
		if (targetFile.exists() && listener != null) {
			listener.onResourceUpdate(Uri.parse(targetFile.getAbsolutePath()));
		}
	}

	@Override
	protected void onPostExecute(final Uri result) {
		if (error != null) {
			// exception
			if (listener != null) {
				listener.onError(error);
			}
		} else if (result != null) {
			// handle succesfull download or found in cache
			if (listener != null) {
				listener.onResourceUpdate(result);
			}
		} else {
			// no exception, no uri: we don't have connection and no cached res is available
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
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
		error = null;
		if (weAreOnline()) {
			final HttpGet getFile = new HttpGet(targetUri.toString());
			augmentWithIfModifiedSince(targetFile, getFile);
			File tempFile = null;
			try {
				tempFile = File.createTempFile(TAG, ".tmp", cacheDir);
				final HttpResponse resp = httpClient.execute(getFile);
				final HttpEntity ent = resp.getEntity();
				final int statusCode = resp.getStatusLine().getStatusCode();
				switch (statusCode) {
				case HttpStatus.SC_OK:
					handleEntity(ent, targetFile, tempFile);
					break;
				case HttpStatus.SC_NOT_MODIFIED:
					break;
				default:
					throw new RuntimeException("Http Status Code: " + statusCode);
				}

				if (ent != null) {
					ent.consumeContent();
				}
			} catch (Throwable e) {
				error = new RuntimeException(TAG + ": error", e);
				e.printStackTrace();
			} finally {
				if (tempFile != null && tempFile.exists()) {
					tempFile.delete();
				}
			}
		}

		if (targetFile.exists()) {
			return Uri.parse(targetFile.getAbsolutePath());
		} else {
			return null;
		}
	}

	private void handleEntity(final HttpEntity ent, final File targetFile, final File tempFile)
			throws IllegalStateException, IOException {
		clength = ent.getContentLength();
		InputStream in = ent.getContent();
		FileOutputStream out = new FileOutputStream(tempFile);
		FileUtils.copy(in, out, this);
		out.close();

		// if everything went fine you want to switch temp and target file, guarded
		if (targetFile.exists()) {
			targetFile.delete();
		}
		FileUtils.copyFile(tempFile, targetFile);
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
	
	public void setListener(DownloadListener listener) {
		this.listener = listener;
	}
}