package com.example.frodo.utils;

import com.example.frodo.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class Progress extends ProgressDialog {
	private Progress _this;
	private Context context;
	private static final int SHOW = 1;
	private static final int HIDE = 0;
	
	private final Handler handler = new Handler(){
	    @Override
	    public void handleMessage(Message msg) {
	    	switch (msg.what) {
	    		case SHOW:
	    			_this.show();
	    			break;
	    		case HIDE:
	    			_this.hide();
	    			break;	    			
	    	}	    	
	    }
	};
	
	public Progress(Context context) {
		super(context);
		this.context = context;
		this._this = this;
	}
	
	public Progress(Context context, String title) {
		this(context);
		this.setTitle(title);
	}
	
	public Progress(Context context, int titleId) {
		this(context);
		this.setTitle(context.getResources().getString(titleId));
	}
	
	public void toggleProgress(boolean show) {
		String message = context.getResources().getString(R.string.pagetext_loading_quests);
		toggleProgress(show, message);
	}
	
	public void toggleProgress(boolean show, int messageId) {
		String message = context.getResources().getString(messageId);
		toggleProgress(show, message);
	}
	
	public void toggleProgress(boolean show, String message) {					
		this.setMessage(message);
		
		if (show) {
			handler.sendEmptyMessage(SHOW);
		} else {
			handler.sendEmptyMessage(HIDE);
		}
	}
}
