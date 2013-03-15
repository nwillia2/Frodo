package com.example.frodo.utils;

import java.util.regex.Pattern;

import com.example.frodo.R;
import android.text.TextUtils;
import android.widget.TextView;

public class Validate {
	public static String PASSWORD_PATTERN = "^[a-zA-Z]\\w{3,14}$";
	
	public static boolean PresenceOf(Object object) {
		String value = null;
		boolean cancel = false;
		
		if (object instanceof TextView) {
			value = ((TextView) object).getText().toString();
		} else {
			throw new IllegalArgumentException("PresenceOf accepts a TextView as an argument");
		}
		
		if (TextUtils.isEmpty(value)) {
			((TextView) object).setError(((TextView) object).getContext().getString(R.string.error_field_required, ((TextView) object).getHint()));		
			((TextView) object).requestFocus();
			cancel = true;
		} else {
			cancel = false;
		}
		
		return cancel;
	}
	
	public static boolean PatternOf(Object object, Pattern pattern) {
		String value = null;
		boolean cancel = false;
		
		if (object instanceof TextView) {
			value = ((TextView) object).getText().toString();
		} else {
			throw new IllegalArgumentException("PatternOf accepts a TextView as an argument");
		}
		
		if (!pattern.matcher(value).matches()) {
			((TextView) object).setError(((TextView) object).getContext().getString(R.string.error_field_pattern, ((TextView) object).getHint()));
			((TextView) object).requestFocus();
			cancel = true;
		} else {
			cancel = false;
		}
		
		return cancel;
	}
}
