package com.hazam.romaski;

import com.hazam.widget.PinchImageView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class PinchableImageFragment  extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		PinchImageView piw = new PinchImageView(getActivity());
		LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT);
		//params.height = ViewGroup.LayoutParams.FILL_PARENT;
		//params.width = ViewGroup.LayoutParams.FILL_PARENT;
		piw.setImageResource(R.drawable.ovindolimagnola_pda480);
		piw.setLayoutParams(params);
		return piw;
	}
}
