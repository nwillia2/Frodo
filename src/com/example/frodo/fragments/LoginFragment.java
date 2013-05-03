package com.example.frodo.fragments;

import com.example.frodo.MainActivity;
import com.example.frodo.R;
import com.example.frodo.utils.Keyboard;
import com.example.frodo.utils.Progress;
import com.example.frodo.utils.Validate;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginFragment extends Fragment {

	Progress progress;
	Context context;
	
	// Values for email and password at the time of the login attempt.
	private String email;
	private String password;

	// UI references.
	private EditText emailTextView;
	private EditText passwordTextView;	
	
	@Override	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {	
		return inflater.inflate(R.layout.fragment_login, container, false);
	}
	
	public void onStart() {
		super.onStart();
		
		context = getActivity();		
		progress = new Progress(context, R.string.common_please_wait);
		
		// Set up the login form.
		emailTextView = (EditText) getView().findViewById(R.id.email);
		emailTextView.setText(email);

		passwordTextView = (EditText) getView().findViewById(R.id.password);
		passwordTextView.setOnEditorActionListener(
				new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				}
		);	

		getView().findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				}
		);
		
		getView().findViewById(R.id.sign_up_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						signup();
					}
				}
		);
	}
	
	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		// Reset errors.
		emailTextView.setError(null);
		passwordTextView.setError(null);

		// Store values at the time of the login attempt.
		email = emailTextView.getText().toString();
		password = passwordTextView.getText().toString();

		boolean cancel = false;

		// Check for a valid email address.
		cancel = Validate.PresenceOf(emailTextView);
		if (!cancel) cancel = Validate.PatternOf(emailTextView, Patterns.EMAIL_ADDRESS);
		
		// check valid password
		if (!cancel) cancel = Validate.PresenceOf(passwordTextView);		

		if (!cancel) {	
			Keyboard.hide(getActivity());
			login();
		}
	}
	
	private void login(){
		// log in as a user	
		progress.toggleProgress(true, R.string.pagetext_signing_in);
		ParseUser.logInInBackground(email.split("@")[0], password, new LogInCallback() {
			
			@Override
			public void done(ParseUser user, ParseException e) {
				if (e == null) {
					progress.toggleProgress(false);
					Intent intent = new Intent(context, MainActivity.class);
					startActivity(intent);
				} else {																								
					// else, incorrect password
					Toast.makeText(context, 
								   getString(R.string.parse_login_fail, email), 
								   Toast.LENGTH_LONG).show();					
					Log.e("Error Logging into Parse", "Details: " + e.getMessage());
				}
			}							
		});
	}
	
	private void signup() {
		// Create fragment and give it an argument specifying the article it should show
		SignupFragment newFragment = new SignupFragment();					
		
		// if the username doesn't exist, load the signup fragment
		FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

		// Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack so the user can navigate back
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);

		// Commit the transaction
		transaction.commit();
	}	
}
