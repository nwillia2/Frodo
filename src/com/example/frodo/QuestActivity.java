package com.example.frodo;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
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
			actionEdit(params);
			break;					
		case SHOW:
			actionShow(params);
			break;		
		default:
			// INDEX
			actionIndex();
			break;
		}				
	}

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
						ParseObject object = (ParseObject) ((ListView) targetView).getAdapter().getItem(info.position);
						showRecord(object);						
						return true;
					}
					break;			
				}
			}
		}
		return super.onContextItemSelected(item);
	};
	
	/***************** ACTIONS ****************/
	
	// Actions
	private void actionIndex() {		
		setContentView(R.layout.activity_quest_index);				

		// get the data
		ParseQuery query = new ParseQuery("Quest");
		// Always load from Cache if we have some
		// Only go to the network if we request a refresh
		query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
		
		toggleProgress(true);
		query.findInBackground(new FindCallback() {
			
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
					Toast.makeText(getApplicationContext(), R.string.parse_query_fail, Toast.LENGTH_LONG).show();
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
	}
	
	private void actionShow(Bundle params) {
		toggleProgress(true, getResources().getString(R.string.pagetext_loading_quest));
		setContentView(R.layout.activity_quest_show);
		
		final TextView titleTextView = (TextView) findViewById(R.id.titleTextView);
		final TextView descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
		Button mapButton = (Button) findViewById(R.id.mapButton);
		
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
	
	private void actionEdit(Bundle params) {
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
			Intent intent = new Intent(this, QuestActivity.class);
			intent.setAction("SHOW");
			intent.putExtra("ID", object.getObjectId());
			startActivity(intent);
		} else {
			Toast.makeText(context, R.string.common_unknown_problem, Toast.LENGTH_LONG).show();
		}
	}
}
