package com.hazam.romaski;

import com.hazam.romaski.model.Resort;
import com.hazam.romaski.model.WebcamImagesAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;

public class TabsFragment extends Fragment implements TabContentFactory, OnTabChangeListener {
    
    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
		ViewGroup toret = (ViewGroup) inflater.inflate(R.layout.tabs_fragment, container, false);
		TabHost tabHost = (TabHost) toret.findViewById(android.R.id.tabhost);
		tabHost.setup();
		for (Resort r : Resort.ALL.values()) {
			TabSpec spec = tabHost.newTabSpec(r.getId());
			spec.setIndicator(null, getResources().getDrawable(r.getDrawableId()) );
			spec.setContent(this);
			tabHost.addTab(spec);
		}
		tabHost.setOnTabChangedListener(this);
		return toret;
    }

	@Override
	public View createTabContent(String tag) {
		return new TextView(getActivity());
	}

	@Override
	public void onTabChanged(String tag) {
		ListAdapter adapter = new WebcamImagesAdapter(getActivity(), tag);
		FragmentManager fm = getActivity().getSupportFragmentManager();
		WebcamsFragment wf = (WebcamsFragment) fm.findFragmentById(R.id.webcams);
		wf.setListAdapter(adapter);
	}
}
