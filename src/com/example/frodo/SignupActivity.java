package com.example.frodo;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class SignupActivity extends Activity {

	private TextView emailTextView;
	private TextView passwordTextView;
	private TextView firstNameTextView;
	private TextView lastNameTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_signup, menu);
		return true;
	}

	private void signup(){
		// Signup User
		ParseUser u = new ParseUser();
		u.setUsername(emailTextView.getText().toString().split("@")[0]);
		u.setPassword(passwordTextView.getText().toString());
		u.setEmail(emailTextView.getText().toString());
		
		u.add("firstName", firstNameTextView.getText().toString());
		u.add("lastName", lastNameTextView.getText().toString());
		
		u.signUpInBackground(new SignUpCallback() {
			
			@Override
			public void done(ParseException e) {
				if (e == null) {
					// Signed up successfully
				} else {
					// error on signup
				}
			}
		});		
	}
}
