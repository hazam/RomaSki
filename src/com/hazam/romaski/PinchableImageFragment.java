package com.hazam.romaski;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hazam.widget.Pinch;
import com.hazam.widget.RemoteImageView;

public class PinchableImageFragment  extends Fragment {
	
	private RemoteImageView riw;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View toret = inflater.inflate(R.layout.pinchable_fragment, container);
		riw = (RemoteImageView) toret.findViewById(R.id.img);
		return toret;
	}
	
	public void setTarget(Uri object) {
		riw.setImageURI(object);
		riw.netReload();
		Pinch.makePinchable(riw);
	}
}
