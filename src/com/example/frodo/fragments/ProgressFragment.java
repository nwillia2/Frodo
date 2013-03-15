package com.example.frodo.fragments;

import com.example.frodo.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProgressFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_progress, container, false);
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();		
		
		Bundle args = getArguments();
		String progressText = args.get("progressText").toString();
		if (TextUtils.isEmpty(progressText)) progressText = getText(R.string.pagetext_signing_in).toString();
		
		TextView progressMessage = (TextView) getView().findViewById(R.id.progress_status_message);
		progressMessage.setText(progressText);
	}
}
