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

/**
 * Extended ImageView to handle http:// loading of the images. Includes caching on SDCARD (if available)
 * 
 * @author Emanuele Di Saverio
 */
public class RemoteImageView extends ImageView {

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
			reload();
		} else {
			super.setImageURI(uri);
		}
	}

	public void reload() {
		new DownloaderTask(remoteUri).execute();
	}

	public class DownloaderTask extends AsyncTask<Void, Long, Uri> {

		private final Uri target;

		public DownloaderTask(Uri _target) {
			target = _target;
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onPostExecute(Uri result) {
			if (result != null) {
				setImageURI(result);
			}
		}

		@Override
		protected Uri doInBackground(Void... params) {
			String encoded = target.getLastPathSegment();
			File targetFile = new File(getContext().getCacheDir() + "/" + encoded);
			ConnectivityManager nm = (ConnectivityManager) getContext().getSystemService(Activity.CONNECTIVITY_SERVICE);
			NetworkInfo ni = nm.getActiveNetworkInfo();
			if (ni != null && ni.isConnected()) {
				BetterHttpClient client = new BetterHttpClient(null, false);
				HttpGet getFile = new HttpGet(target.toString());
				if (targetFile.exists()) {
					String lastModifiedFormatted = DateUtils.formatDate(new Date(targetFile.lastModified()));
					getFile.addHeader("If-Modified-Since", lastModifiedFormatted);
				}
				try {
					HttpResponse resp = client.execute(getFile);
					HttpEntity ent = resp.getEntity();
					if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						final long length = ent.getContentLength();
						InputStream in = ent.getContent();
						FileOutputStream f = new FileOutputStream(targetFile);
						FileUtils.decantStreams(in, f, new FileUtils.Tick() {

							@Override
							public void tick(long current) {
								publishProgress(current, length);
							}
						});
						f.close();
					}
					ent.consumeContent();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			if (targetFile.exists()) {
				return Uri.parse(targetFile.getAbsolutePath());
			} else {
				return null;
			}
		}

		@Override
		protected void onProgressUpdate(Long... values) {
			trace("Just tracing..." + values[0] + " over " + values[1]);
		}
	}
}
