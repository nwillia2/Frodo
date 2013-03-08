package com.example.frodo;

<<<<<<< HEAD
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
=======
import com.parse.Parse;
>>>>>>> 58f233dd63afbe2e63e2affcf1b03a228afa4726

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
<<<<<<< HEAD
import android.text.TextUtils.StringSplitter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

	TextView main_text = null;
	
=======
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

>>>>>>> 58f233dd63afbe2e63e2affcf1b03a228afa4726
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
<<<<<<< HEAD
=======
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
>>>>>>> 58f233dd63afbe2e63e2affcf1b03a228afa4726
		
		// try and set up Parse, using the keys in the manifest file
		try {
		    ApplicationInfo ai = getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
		    Bundle bundle = ai.metaData;
		    String application_key = bundle.getString("parse_application_key");
		    String client_key = bundle.getString("parse_client_key");
		    Parse.initialize(this, application_key, client_key);     
<<<<<<< HEAD
		} catch (Exception e) {			
		    Log.e("Error Loading Parse", "Failed to load meta-data, NameNotFound: " + e.getMessage());
		}		
		
		// setup some variables
		main_text = (TextView) findViewById(R.id.main_text);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
=======
		} catch (Exception e) {
		    Log.e("Error Loading Parse", "Failed to load meta-data, NameNotFound: " + e.getMessage());
		}				
>>>>>>> 58f233dd63afbe2e63e2affcf1b03a228afa4726
		
		return true;
	}
	
	public boolean onOptionsItemSelected (MenuItem item){
		switch (item.getItemId()){
		case R.id.menu_map:
			Intent intent = new Intent(this, MapActivity.class);
			startActivity(intent);
			return true;
<<<<<<< HEAD
		case R.id.menu_login:
			login();
			return true;
		case R.id.menu_logout:
			logout();
			return true;
=======
>>>>>>> 58f233dd63afbe2e63e2affcf1b03a228afa4726
		}
		
		return super.onOptionsItemSelected(item);
	}
<<<<<<< HEAD
	
	private void login(){
		// log in as a user		
		main_text.setText(getString(R.string.parse_login_init));
		ParseUser.logInInBackground("test", "test", new LogInCallback() {
			
			@Override
			public void done(ParseUser user, ParseException e) {
				if (e == null) {
					main_text.setText(getString(R.string.parse_login_success, user.getUsername()));
				} else {
					main_text.setText(getString(R.string.parse_login_fail, "test"));
					Log.e("Error Logging into Parse", "Details: " + e.getMessage());
				}
			}
							
		});
	}
	
	private void logout(){
		final TextView main_text = (TextView) findViewById(R.id.main_text);
		main_text.setText(getString(R.string.parse_logout_init));		
		ParseUser.logOut();		
		main_text.setText(getString(R.string.parse_logout_success));
	}
=======
>>>>>>> 58f233dd63afbe2e63e2affcf1b03a228afa4726
}
