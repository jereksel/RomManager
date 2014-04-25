package com.jereksel.rommanager;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Status extends Fragment {

	TextView tvItemName;
	
	
	public Status() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.status_layout, container,
				false);
		
		tvItemName = (TextView) view.findViewById(R.id.android_version_text);
		tvItemName.setText(Build.VERSION.RELEASE);


		return view;
	}

}
