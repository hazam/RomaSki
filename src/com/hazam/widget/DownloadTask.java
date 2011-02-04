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

public class DownloadTask extends AsyncTask<Uri, Long, Uri> implements FileUtils.Tick {

	private final Context ctx;
	private final ConnectivityManager connectivityManager;
	private final BetterHttpClient httpClient;
	private long clength;
	private Throwable error = null;

	public DownloadTask(final Context _ctx) {
		this.ctx = _ctx.getApplicationContext();
		this.connectivityManager = (ConnectivityManager) ctx.getSystemService(Activity.CONNECTIVITY_SERVICE);
		this.httpClient = new BetterHttpClient(null, false);
	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected void onPostExecute(final Uri result) {
		if (error != null) {
			//exception
		} else if (result != null) {
			//handle succesfull download or found in cache
		} else {
			//no exception, no uri: we don't have connection and no cached res is available
		}
	}

	private final File composeTargetFile(final Uri src) {
		final String computed = src.getLastPathSegment();
		return new File(ctx.getCacheDir(), computed);
	}

	private void augmentWithIfModifiedSince(final File targetFile, final AbstractHttpMessage mess) {
		if (targetFile.exists()) {
			final String lastModifiedFormatted = DateUtils.formatDate(new Date(targetFile.lastModified()));
			mess.addHeader("If-Modified-Since", lastModifiedFormatted);
		}
	}

	@Override
	protected Uri doInBackground(final Uri... params) {
		error = null;
		final Uri target = params[0];
		final File targetFile = composeTargetFile(target);
		if (weAreOnline()) {
			final HttpGet getFile = new HttpGet(target.toString());
			augmentWithIfModifiedSince(targetFile, getFile);
			try {
				final File tempFile = File.createTempFile(TAG, ".tmp", ctx.getCacheDir());
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
					throw new RuntimeException("Http Status Code: "+statusCode);
				}

				if (ent != null) {
					ent.consumeContent();
				}
			} catch (Throwable e) {
				error = new RuntimeException(TAG+": error", e);
			}
		}

		if (targetFile.exists()) {
			return Uri.parse(targetFile.getAbsolutePath());
		} else {
			return null;
		}
	}

	private void handleEntity(final HttpEntity ent, final File targetFile, final File tempFile) throws IllegalStateException, IOException {
		clength = ent.getContentLength();
		InputStream in = ent.getContent();
		FileOutputStream f = new FileOutputStream(tempFile);
		FileUtils.decantStreams(in, f, this);
		f.close();
		targetFile.delete();
		FileUtils.copyFile(tempFile, targetFile);
		tempFile.delete();
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
}