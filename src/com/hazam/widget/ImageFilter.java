package com.hazam.widget;

import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;

import com.hazam.handy.fs.ImageCache.OnLoadFilter;
import com.hazam.handy.fs.ImageCache.OnSaveFilter;
import com.hazam.romaski.R;
import com.hazam.romaski.R.dimen;

public class ImageFilter implements OnSaveFilter, OnLoadFilter {

	private Context appCtx;
	private int SIZE;
	private int CORNER;
	private Paint mPaint;
	private Paint mPaintXfer;
	private Rect rect;
	private RectF rectF;
	private Bitmap stampinoBuffer;
	private Matrix m;

	public ImageFilter(Context ctx) {
		appCtx = ctx.getApplicationContext();
		init();
	}

	private void init() {
		SIZE = (int) appCtx.getResources().getDimension(R.dimen.webcam_thumb_size);
		rect = new Rect(0, 0, SIZE, SIZE);
		rectF = new RectF(rect);
		CORNER = (int) appCtx.getResources().getDimension(R.dimen.webcam_thumb_rounded_corner);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.BLACK);
		mPaintXfer = new Paint();
		mPaintXfer.setAntiAlias(true);
		mPaintXfer.setColor(Color.BLACK);
		mPaintXfer.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		stampinoBuffer = Bitmap.createBitmap(SIZE, SIZE, Config.ARGB_8888);
		Canvas canvas = new Canvas(stampinoBuffer);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, CORNER, CORNER, mPaint);
		m = new Matrix();
	}

	private Bitmap createNewTavolozza() {
		return stampinoBuffer.copy(stampinoBuffer.getConfig(), true);
	}
	
	@Override
	public void saveResolve(String name, InputStream src, OutputStream out) {
		Bitmap srcBitmap = BitmapFactory.decodeStream(src);
		Bitmap tavola = createNewTavolozza();
		Canvas canvas = new Canvas(tavola);
		int scaledW, scaledH;
		final int width = srcBitmap.getWidth();
		final int height = srcBitmap.getHeight();
		if (width > height) {
			scaledW = SIZE;
			scaledH = (int) (height * (((float) scaledW) / width));
		} else {
			scaledH = SIZE;
			scaledW = (int) (width * (((float) scaledH) / height));
		}
		final float sx = scaledW / (float) width;
		final float sy = scaledH / (float) height;
		int left = (SIZE - scaledW) / 2;
		int top = (SIZE - scaledH) / 2;
		left = left < 0 ? 0 : left;
		top = top < 0 ? 0 : top;
		synchronized (ImageFilter.class) {
			m.setScale(sx, sy);
			m.postTranslate(left, top);
			canvas.drawBitmap(srcBitmap, m, mPaint);
		}

		tavola.compress(CompressFormat.JPEG, 70, out);
	}

	@Override
	public Bitmap loadResolve(String name, InputStream src) {
		Bitmap srcBitmap = BitmapFactory.decodeStream(src);
		Bitmap tavola = createNewTavolozza();
		Canvas canvas = new Canvas(tavola);
		canvas.drawBitmap(srcBitmap, rect, rect, mPaintXfer);
		return tavola;
	}
}
