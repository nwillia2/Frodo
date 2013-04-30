package com.example.frodo;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Layout;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.frodo.adapters.ParseObjectAdapter;
import com.example.frodo.fragments.ProgressFragment;
import com.example.frodo.utils.Constants;
import com.example.frodo.utils.Constants.ACTION;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class QuestActivity extends FragmentActivity {

	private Context context;
	private ProgressDialog progress;
	private Bundle params;
	private Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		context = this;
		
		progress = new ProgressDialog(context);
		progress.setTitle(R.string.common_please_wait);					
		
		// get the arguments passed to this activity
		// find out the action, then decide on what to do
		intent = getIntent();
		params = intent.getExtras();
		if (params == null) {
			params = new Bundle();
		}
		
		switch(ACTION.valueOf(intent.getAction())) {
		case NEW:
			actionNew();
			break;
		case EDIT:
			actionEdit();
			break;					
		case SHOW:
			actionShow();
			break;		
		default:
			// INDEX
			actionIndex();
			break;
		}				
	}

	/****** EVENTS *****/
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_quest, menu);
		return true;
	}
	
	@Override
	// Context menu is used for all actions on this activity. 
	// Case statements exist to differentiate between the actions
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {		
		AdapterView.AdapterContextMenuInfo info =
	            (AdapterView.AdapterContextMenuInfo) menuInfo;				
		
		View targetView = info.targetView;		
		if (targetView != null) {
			// we will assume that targetView is the first element INSIDE the control which we want
			// so lets get the parent
			targetView = (View) targetView.getParent();
			if (targetView != null){
				switch (targetView.getId()) {
				case R.id.questListView:
					menu.setHeaderTitle(R.string.contextmenu_quest_index);
					MenuInflater inflater = getMenuInflater();
					inflater.inflate(R.menu.context_quest_index, menu);				
					break;
				default:
					super.onCreateContextMenu(menu, v, menuInfo);
					break;
				}
			}
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		ParseObject obj = null;
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		AdapterView.AdapterContextMenuInfo info =
	            (AdapterView.AdapterContextMenuInfo) menuInfo;				
		
		View targetView = info.targetView;		
		if (targetView != null) {
			// we will assume that targetView is the first element INSIDE the control which we want
			// so lets get the parent
			targetView = (View) targetView.getParent();
			if (targetView != null){
				switch (targetView.getId()) {
				case R.id.questListView:
					switch (item.getItemId()) {
					case R.id.contextMenuQuestIndexShowrecord:
						// get the associated parseobject to the selected item
						obj = (ParseObject) ((ListView) targetView).getAdapter().getItem(info.position);
						showRecord(obj);						
						return true;
					case R.id.contextMenuQuestIndexShowonmap:
						// load the map acitivty, passing in the id of our object
						obj = (ParseObject) ((ListView) targetView).getAdapter().getItem(info.position);
						// load the map activity with our ID
						intent = new Intent(context, MapActivity.class);
						intent.putExtra("ID", obj.getObjectId());
						startActivity(intent);			
					}
					break;			
				}
			}
		}
		return super.onContextItemSelected(item);
	};
	
	private OnClickListener pickupQuestListener = new OnClickListener() {

		@Override
		public void onClick(View button) {			
			// assign the id stored in the button's tag, to the current user
			// in other words, pick up the quest
			String objectId = (String) button.getTag();
			if (objectId != null) {
				
			}
		}
		
	};
	
	private OnClickListener showOnMapListener = new OnClickListener() {

		@Override
		public void onClick(View button) {			
			// assign the id stored in the button's tag, to the current user
			// in other words, pick up the quest
			String objectId = (String) button.getTag();
			if (objectId != null) {
				// load the map activity with our ID
				Intent intent = new Intent(context, MapActivity.class);
				intent.putExtra("ID", objectId);
				startActivity(intent);
			}
		}
		
	};
	
	/***************** ACTIONS ****************/
	
	// Actions
	private void actionIndex() {				
		setContentView(R.layout.activity_quest_index);
		
		TextView questLocationHelp = (TextView) findViewById(R.id.questLocationHelp);
		questLocationHelp.setText(R.string.pagetext_location_finding);

		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// get location using same provider criteria as we use for the maps
    	String provider = new CurrentLocationProvider(context).getProvider();
		Location currentLocation = locationManager.getLastKnownLocation(provider);
		
		if (currentLocation != null) {
			String displayCoordinates = currentLocation.getLatitude() + ", " + currentLocation.getLongitude();			
			questLocationHelp.setText(getResources().getString(R.string.pagetext_location_found, displayCoordinates));
			
			// get the data
			ParseQuery query = new ParseQuery("Quest");
			ParseGeoPoint coords = new ParseGeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());		
			// Always load from Cache if we have some
			// Only go to the network if we request a refresh
			query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
			
			toggleProgress(true);
			query.whereWithinMiles("coordinates", coords, 25).findInBackground(new FindCallback() {
				
				@Override
				public void done(List<ParseObject> objects, ParseException e) {				
					if (e == null) {
						// If we are in landscape mode, add more columns if required?
						LinkedHashSet<String> columns = new LinkedHashSet<String>();
						columns.add("title");
						columns.add("coordinates");
						ParseObjectAdapter pa = new ParseObjectAdapter(context, objects, columns);
						ListView lv = (ListView) findViewById(R.id.questListView);
						lv.setAdapter(pa);
					} else {
						// we want to ignore any errors if they are caused by the cache not being found...
						if (e.getCode() != ParseException.CACHE_MISS) {
							Toast.makeText(getApplicationContext(), R.string.parse_query_fail, Toast.LENGTH_LONG).show();
						}
					}
					toggleProgress(false);
				}
			});
			
			ListView questListView = (ListView) findViewById(R.id.questListView);
			// register for a context menu on long press
			// the context menu is associated to the activity, so anything which registers the use of the context menu
			// will use the same methods to set them up and click on them etc.
			registerForContextMenu(questListView);
			
			// subscribe to the itemselected event of the list
			questListView.setOnItemClickListener(new OnItemClickListener() {
	
				@Override
				public void onItemClick(AdapterView<?> parent, 
						View view, int position, long id) {
					
					ParseObject object = (ParseObject) parent.getItemAtPosition(position);				
					showRecord(object);				
				}
			});
			
		} else {
			// no location information available
			questLocationHelp.setText(R.string.pagetext_location_notfound);
		}
	}
	
	private void actionShow() {
		toggleProgress(true, getResources().getString(R.string.pagetext_loading_quest));
		setContentView(R.layout.activity_quest_show);
		
		final TextView titleTextView = (TextView) findViewById(R.id.titleTextView);
		final TextView descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
		
		// get the id from the params, and go and get the record from parse
		String id = params.getString("ID");
		ParseQuery query = new ParseQuery("Quest");
		query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
				
		query.getInBackground(id, new GetCallback() {
			
			@Override
			public void done(ParseObject object, ParseException e) {
				if (e == null) {
					titleTextView.setText(object.getString("title"));
					descriptionTextView.setText(object.getString("description"));	
					
					// show the relevant buttons
					// check if this quest has already been picked up by the current user
					// if it has, show the objectives
//					LinkedHashSet<String> columns = new LinkedHashSet<String>();
//					columns.add("title");
//					columns.add("coordinates");
//					ParseObjectAdapter pa = new ParseObjectAdapter(context, objects, columns);
//					ListView lv = (ListView) findViewById(R.id.questListView);
//					lv.setAdapter(pa);
					
					// otherwise, allow the user to 'pick up' this quest
					Button b = (Button) findViewById(R.id.pickupQuestButton);
					b.setTag(object.getObjectId());
					b.setOnClickListener(pickupQuestListener);
					
					Button b2 = (Button) findViewById(R.id.mapButton);
					b2.setTag(object.getObjectId());
					b2.setOnClickListener(showOnMapListener);
				} else {
					Toast.makeText(getApplicationContext(), R.string.parse_query_fail, Toast.LENGTH_LONG).show();
				}					
				toggleProgress(false);
			}
		});				
	}
	
	
	private void actionNew() {
		setContentView(R.layout.activity_quest_form);
	}
	
	private void actionEdit() {
		setContentView(R.layout.activity_quest_form);
		
		
	}
	
	/**************** END ACTIONS *******************/
	
	/**************** PRIVATE METHODS ***************/	
	private void toggleProgress(boolean show) {
		String message = getResources().getString(R.string.pagetext_loading_quests);
		toggleProgress(show, message);
	}
	
	private void toggleProgress(boolean show, String message) {					
		progress.setMessage(message);
		
		final boolean bShow = show;
		runOnUiThread(new Runnable(){

			@Override
			public void run() {				
				if (bShow) {
					progress.show();
				} else {
					progress.dismiss();
				}
			}
			
		});			
	}
	
	private void showRecord(ParseObject object) {
		if (object != null) { 
			// load the show action of the quest activity
			Intent intent = new Intent(context, QuestActivity.class);
			intent.setAction("SHOW");
			intent.putExtra("ID", object.getObjectId());
			startActivity(intent);
		} else {
			Toast.makeText(context, R.string.common_unknown_problem, Toast.LENGTH_LONG).show();
		}
	}
}
