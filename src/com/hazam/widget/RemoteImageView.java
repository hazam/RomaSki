package com.hazam.widget;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.hazam.handy.net.BetterHttpClient;

/**
 * Extended ImageView to handle http:// loading of the images. Includes caching on SDCARD (if available)
 * 
 * @author Emanuele Di Saverio
 * 
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
			setImageURI(result);
		}

		@Override
		protected Uri doInBackground(Void...params) {
			BetterHttpClient client = new BetterHttpClient(null, false);
			HttpGet getFile = new HttpGet(target.toString());
			try {
				HttpResponse resp = client.execute(getFile);
				HttpEntity ent = resp.getEntity();
				long length = ent.getContentLength();
				InputStream in = ent.getContent();
				FileOutputStream f = new FileOutputStream(new File("/sdcard/" + target.getLastPathSegment()));
				byte[] buffer = new byte[1024];
				int len1 = 0;
				long cumul = 0;
				while ((len1 = in.read(buffer)) > 0) {
					f.write(buffer, 0, len1);
					cumul += len1;
					publishProgress(cumul, length);
				}
				f.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}

			return Uri.parse("/sdcard/" + target.getLastPathSegment());
		}

		@Override
		protected void onProgressUpdate(Long... values) {
			// here should update the progress bar of something
		}
	}
}
