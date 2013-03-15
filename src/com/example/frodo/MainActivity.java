package com.example.frodo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseUser;

public class MainActivity extends Activity {
	TextView main_text = null;
	ParseUser current_user = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// try and set up Parse, using the keys in the manifest file
		try {
		    ApplicationInfo ai = getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
		    Bundle bundle = ai.metaData;
		    String application_key = bundle.getString("parse_application_key");
		    String client_key = bundle.getString("parse_client_key");
		    Parse.initialize(this, application_key, client_key);     
		} catch (Exception e) {			
		    Log.e("Error Loading Parse", "Failed to load meta-data, NameNotFound: " + e.getMessage());
		}					
		
		// setup some variables
		main_text = (TextView) findViewById(R.id.main_text);
		current_user = ParseUser.getCurrentUser(); 
		if (current_user != null) {
			main_text.setText(getString(R.string.parse_login_success, current_user.getUsername()));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected (MenuItem item){
		Intent intent = null;
		switch (item.getItemId()){
		case R.id.menu_map:
			intent = new Intent(this, MapActivity.class);
			startActivity(intent);
			return true;
		case R.id.menu_login:
			intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			return true;
		case R.id.menu_logout:
			logout();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}		
	
	private void logout(){
		final TextView main_text = (TextView) findViewById(R.id.main_text);
		main_text.setText(getString(R.string.parse_logout_init));		
		ParseUser.logOut();		
		main_text.setText(getString(R.string.parse_logout_success));
	}
}
