package com.hazam.romaski;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.ListAdapter;

import com.hazam.romaski.model.Resort;
import com.hazam.romaski.model.WebcamImagesAdapter;

public class WebcamsFragment extends ListFragment {

	@Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
		ListAdapter adapter = new WebcamImagesAdapter(getActivity(), Resort.CAMPO_FELICE.getId());
		setListAdapter(adapter);
    }
}
