package com.hazam.romaski;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

import com.hazam.handy.util.L;
import com.hazam.romaski.model.Resort;
import com.hazam.romaski.model.WebcamImagesAdapter;

public class WebcamsFragment extends Fragment implements OnItemClickListener {
	private AdapterView<BaseAdapter> mAdapterView;
	private WebcamImagesAdapter mAdapter;
	@Override
	@SuppressWarnings("unchecked")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View toret = inflater.inflate(R.layout.webcams_list, container, true);
		mAdapterView = (AdapterView<BaseAdapter>) toret.findViewById(R.id.webcams);
		mAdapter = new WebcamImagesAdapter(getActivity(), Resort.CAMPO_FELICE.getId());
		mAdapterView.setAdapter(mAdapter);
		mAdapterView.setOnItemClickListener(this);
		return toret;
	}

	public void setAdapter(WebcamImagesAdapter adapter) {
		mAdapter = adapter;
		mAdapterView.setAdapter(adapter);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View parent, int pos, long id) {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		PinchableImageFragment detailFrag = (PinchableImageFragment) fm.findFragmentById(R.id.detail);
		if (detailFrag != null) {
			//detail fragment on screen, talk to him directly
			detailFrag.setTarget((Uri)mAdapter.getItem(pos));
		} else {
			//launch detail activity
			Intent i = new Intent(getActivity(), DetailActivity.class);
			i.putExtra(DetailActivity.EXTRA_URI, mAdapter.getItem(pos).toString());
			startActivity(i);
		}
	}
}
