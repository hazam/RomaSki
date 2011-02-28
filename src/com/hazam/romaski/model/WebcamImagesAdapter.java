package com.hazam.romaski.model;

import java.util.ArrayList;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;

import com.hazam.romaski.R;
import com.hazam.widget.RemoteImageView;

public class WebcamImagesAdapter extends BaseAdapter {
	private Context ctx;
	private Resort resort;
	private LayoutInflater inflater;
	private ArrayList<Uri> seen = new ArrayList<Uri>();
	
	public WebcamImagesAdapter(Context srcCtx, String resortTag) {
		ctx = srcCtx;
		resort = Resort.ALL.get(resortTag);
		inflater = LayoutInflater.from(ctx);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Uri targetUri = (Uri) getItem(position);
		System.out.println("Getting View for uri "+targetUri+", pos: "+position+", convert "+convertView);
		RemoteImageView toret = null;
		if (convertView == null) {
			toret = (RemoteImageView) inflater.inflate(R.layout.gallery_item, parent, false);
		} else {
			toret = (RemoteImageView) convertView;
		}
		toret.setImageURI(targetUri);
		if (!seen.contains(targetUri)) {
			toret.netReload();
		}
		seen.add(targetUri);
		return toret;
	}

	@Override
	public long getItemId(int position) {
		return resort.get(position).hashCode();
	}
	
	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public Object getItem(int position) {
		return resort.get(position);
	}

	@Override
	public int getCount() {
		return resort.size();
	}
	
	@Override
	public int getItemViewType(int position) {
		return 0;
	}
	
	@Override
	public int getViewTypeCount() {
		return 1;
	}
}
