package com.hazam.romaski;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
import android.graphics.PorterDuff.Mode;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.hazam.handy.HandyApplication;
import com.hazam.handy.fs.ImageCache;
import com.hazam.handy.fs.ImageCache.OnSaveFilter;
import com.hazam.handy.graphics.ImageUtils;

public class RomaSkiApplication extends HandyApplication implements OnSaveFilter {
	public static final String TAG = "RomaSki";

	@Override
	public void onCreate() {
		super.onCreate();
		ImageCache cache = new ImageCache(getApplicationContext(), TAG);
		cache.setFilter(this);
		registerAppService(HandyApplication.IMAGE_CACHE_APPSERVICE, cache);
	}

	@Override
	public void saveResolve(String name, InputStream src, OutputStream out) {
		final int SIZE = (int) getApplicationContext().getResources().getDimension(R.dimen.webcam_thumb_size);
		final int CORNER = (int) getApplicationContext().getResources().getDimension(
				R.dimen.webcam_thumb_rounded_corner);
		Bitmap srcBitmap = BitmapFactory.decodeStream(src);
		Bitmap tavola = Bitmap.createBitmap(SIZE, SIZE, Config.ARGB_8888);
		Canvas canvas = new Canvas(tavola);
		canvas.drawARGB(0, 0, 0, 0);
		Rect rect = new Rect(0, 0, SIZE, SIZE);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);
		canvas.drawRoundRect(new RectF(rect), CORNER, CORNER, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		int scaledW, scaledH;
		if (srcBitmap.getWidth() > srcBitmap.getHeight()) {
			scaledW = SIZE;
			scaledH = (int) (srcBitmap.getHeight() * (((float) scaledW) / srcBitmap.getWidth()));
		} else {
			scaledH = SIZE;
			scaledW = (int) (srcBitmap.getWidth() * (((float) scaledH) / srcBitmap.getHeight()));
		}
		final int width = srcBitmap.getWidth();
		final int height = srcBitmap.getHeight();
		final float sx = scaledW / (float) width;
		final float sy = scaledH / (float) height;
		Matrix m = new Matrix();
		m.setScale(sx, sy);
		int left = (SIZE - scaledW) / 2;
		int top = (SIZE - scaledH) / 2;
		left = left < 0 ? 0 : left;
		top = top < 0 ? 0 : top;
		m.postTranslate(left, top);
		canvas.drawBitmap(srcBitmap, m, paint);
		
		tavola.compress(CompressFormat.JPEG, 50, out);
	}
}
