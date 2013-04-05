package com.example.frodo.adapters;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.text.WordUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.frodo.R;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

public class ParseObjectAdapter extends BaseAdapter {

    private Context context = null;
    private List<ParseObject> objects = null;
    private Set<String> columns = null;
    private int columnWidth = 100;

    public ParseObjectAdapter(Context context) {
    	super();
    	this.context = context;    	
    }
    
    public ParseObjectAdapter(Context context, List<ParseObject> objects) {
    	super();
    	this.context = context;
    	this.objects = objects;
    }
    
    public ParseObjectAdapter(Context context, List<ParseObject> objects, Set<String> columns) {
    	super();
    	this.context = context;
    	this.objects = objects;
    	this.columns = columns;
    }
    
    public ParseObjectAdapter(Context context, List<ParseObject> objects, Set<String> columns, int columnWidth) {
    	super();
    	this.context = context;
    	this.objects = objects;
    	this.columns = columns;
    	this.columnWidth = columnWidth;
    }
    
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout ll = null;
		LinearLayout columnLayout = null;
		LinearLayout valueLayout = null;
		TextView noResultsTextView = null;
		TextView tv = null;
		TextView col = null;
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);	    
		
		// if user scrolls, reuse view without re-inflating
		if (convertView == null) {
			ll = (LinearLayout) inflater.inflate(R.layout.partial_parseobjectadapter_row, null); 
		} else {
			ll = (LinearLayout) convertView;			
		}
		
		valueLayout = (LinearLayout) ll.findViewById(R.id.ParseObjectAdapterValueLayout);
		columnLayout = (LinearLayout) ll.findViewById(R.id.ParseObjectAdapterColumnLayout);
		noResultsTextView = (TextView) ll.findViewById(R.id.ParseObjectAdapterNoResults);
		
		// set up the displays of our xml controls
		if (position == 0) {
			//if we're setting up the columns, ensure the columnlayout is visible
			columnLayout.setVisibility(View.VISIBLE);
		} else {
			columnLayout.setVisibility(View.INVISIBLE);
		}
		noResultsTextView.setVisibility(View.INVISIBLE);
		
		if (objects != null && !objects.isEmpty()){
			ParseObject object = (ParseObject) objects.get(position);
			if (object != null) {				
				int i = 0;				
				// get the total columns and make a textview for each
				// check if we've passed some columns				
				Set<String> collection = object.keySet();
				if (columns != null) {
					collection = columns;
				}
				// now loop through the columns
				for (String key : collection) {					
					if (position == 0) {
						// set up columns
						col = (TextView) inflater.inflate(R.layout.partial_parseobjectadapter_row_cell_header, null);
						col.setWidth(columnWidth);
						
						int col_id =  context.getResources().getIdentifier("parse_attributes_quest_" + key.toLowerCase(), "string", context.getPackageName());
						if (col_id != 0) {
							col.setText(context.getResources().getString(col_id));
						} else {							
							String column_text = "";
							String words[] = key.split("_");
							for (String word : words) {
								column_text = column_text + WordUtils.capitalize(word) + " ";
							}
							column_text = column_text.trim();
							col.setText(column_text);
						}
						columnLayout.addView(col);
					}
					
					tv = (TextView) inflater.inflate(R.layout.partial_parseobjectadapter_row_cell, null);
					tv.setWidth(columnWidth);
					
					// depending on our type of parse object, get the string correctly
					Object value = object.get(key);
					if (value instanceof ParseGeoPoint) {
						value = String.valueOf(((ParseGeoPoint) value).getLatitude()) + ", " + 
								String.valueOf(((ParseGeoPoint) value).getLongitude());
					}
					
					tv.setText(value.toString());					
					valueLayout.addView(tv);						
					i++;
				}
			}
		} else {
			columnLayout.setVisibility(View.INVISIBLE);		
			valueLayout.setVisibility(View.INVISIBLE);
			
			noResultsTextView.setVisibility(View.VISIBLE);
			noResultsTextView.setText(R.string.parse_no_results);			
		}
		
		return ll;
	}

	@Override
	public int getCount() {
		if (objects == null || objects.isEmpty()){
			return  1; // We always want one row, so we can show 'No Data'
		} else {
			return objects.size();
		}
	}

	@Override
	public Object getItem(int position) {
		if (objects == null || objects.isEmpty()){
			return null;
		} else {
			return objects.get(position);
		}
	}
	
	public List<ParseObject> getParseObjects(){
		return objects;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
}