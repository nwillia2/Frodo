package com.example.frodo.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class Keyboard {
	public static void hideKeyboard(Activity c){
		 InputMethodManager inputManager = (InputMethodManager)            
				  c.getSystemService(Context.INPUT_METHOD_SERVICE); 
				    inputManager.hideSoftInputFromWindow(c.getCurrentFocus().getWindowToken(),      
				    InputMethodManager.HIDE_NOT_ALWAYS);
	}
}
