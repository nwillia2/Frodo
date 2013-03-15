package com.example.frodo.fragments;

import java.util.regex.Pattern;

import com.example.frodo.MainActivity;
import com.example.frodo.R;
import com.example.frodo.utils.Keyboard;
import com.example.frodo.utils.Validate;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
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
			Keyboard.hideKeyboard(getActivity());
			login();
		}
	}
	
	private void login(){
		// log in as a user	
		showProgress();
		ParseUser.logInInBackground(email.split("@")[0], password, new LogInCallback() {
			
			@Override
			public void done(ParseUser user, ParseException e) {
				if (e == null) {
					Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
					startActivity(intent);
				} else {																								
					// else, incorrect password
					Toast.makeText(getActivity().getApplicationContext(), 
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
	
	private void showProgress() {		
		ProgressFragment progressFragment = new ProgressFragment();
		Bundle args = new Bundle();
		args.putString("progressText", getText(R.string.pagetext_signing_in).toString());
		progressFragment.setArguments(args);
		
		// render the progress fragment
		// we know what the parent of this current fragment is, and we know it's not going to be reused, so no harm in directly referencing things from the parent
		FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
		
		transaction.replace(R.id.fragment_container, progressFragment);
		transaction.addToBackStack(null);

		// Commit the transaction
		transaction.commit();	
	}	
}
