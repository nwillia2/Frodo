package com.example.frodo;

import com.parse.Parse;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		
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
		
		return true;
	}
	
	public boolean onOptionsItemSelected (MenuItem item){
		switch (item.getItemId()){
		case R.id.menu_map:
			Intent intent = new Intent(this, MapActivity.class);
			startActivity(intent);
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
}
