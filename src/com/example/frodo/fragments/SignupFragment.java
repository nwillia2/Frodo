package com.example.frodo.fragments;

import java.util.regex.Pattern;

import com.example.frodo.R;
import com.example.frodo.utils.Keyboard;
import com.example.frodo.utils.Validate;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

public class SignupFragment extends Fragment {
	
	private TextView emailTextView;
	private TextView passwordTextView;
	private TextView passwordConfirmTextView;
	private TextView firstNameTextView;
	private TextView lastNameTextView;
	
	private String email;
	private String password;
	private String passwordConfirm;
	private String firstName;
	private String lastName;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_signup, container, false);
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		// Set up the login form.
		emailTextView = (EditText) getView().findViewById(R.id.email);
		passwordTextView = (EditText) getView().findViewById(R.id.password);
		passwordConfirmTextView = (EditText) getView().findViewById(R.id.password_confirm);
		firstNameTextView = (EditText) getView().findViewById(R.id.firstName);
		lastNameTextView = (EditText) getView().findViewById(R.id.lastName);		
		
		emailTextView.setText(email);

		getView().findViewById(R.id.signupButton).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptSignup();
					}
				}
		);
	}
	
	private void attemptSignup() {
		// Reset errors.
		emailTextView.setError(null);
		passwordTextView.setError(null);
		passwordConfirmTextView.setError(null);
		firstNameTextView.setError(null);
		lastNameTextView.setError(null);

		// Store values at the time of the login attempt.
		email = emailTextView.getText().toString();
		password = passwordTextView.getText().toString();
		passwordConfirm = passwordConfirmTextView.getText().toString();
		firstName = firstNameTextView.getText().toString();
		lastName = lastNameTextView.getText().toString();

		boolean cancel = false;		

		// Check for a valid email address.
		cancel = Validate.PresenceOf(emailTextView);
		if (!cancel) cancel = Validate.PatternOf(emailTextView, Patterns.EMAIL_ADDRESS);
		
		// check valid password
		if (!cancel) cancel = Validate.PresenceOf(passwordTextView);
		if (!cancel) cancel = Validate.PatternOf(passwordTextView, Pattern.compile(Validate.PASSWORD_PATTERN));
		
		if (!cancel) cancel = Validate.PresenceOf(passwordConfirmTextView);
				
		if (!cancel) {
			cancel = (!password.equals(passwordConfirm));
			if (cancel) passwordConfirmTextView.setError(getText(R.string.error_field_password_match));
		}
		
		if (!cancel) cancel = Validate.PresenceOf(firstNameTextView);
		if (!cancel) cancel = Validate.PresenceOf(lastNameTextView);

		if (!cancel) {	
			Keyboard.hide(getActivity());
			signup();
		}
	}	
	
	private void signup(){		
		showProgress();
		
		// Signup User
		ParseUser u = new ParseUser();
		u.setUsername(email.split("@")[0]);
		u.setPassword(password);		
		u.setEmail(email);
		u.put("firstName", firstName);
		u.put("lastName", lastName);			
		
		u.signUpInBackground(new SignUpCallback() {
			
			@Override
			public void done(ParseException e) {
				if (e == null) {
					// Signed up successfully
					// Login the user
					// To log them in, flick back to the login fragment, but pass the username and password through in the bundle
					// the login fragment will read this and automatically log the user in
					
				} else {
					// error on signup
					// display a toast
					Toast.makeText(getActivity().getApplicationContext(), 
							   getString(R.string.parse_signup_fail, email), 
							   Toast.LENGTH_LONG).show();					
					Log.e("Error Signing up to Parse", "Details: " + e.getMessage());
				}
			}
		});		
	}
	
	private void showProgress(){
		ProgressFragment progressFragment = new ProgressFragment();
		Bundle args = new Bundle();
		args.putString("progressText", getText(R.string.pagetext_signing_up).toString());
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
