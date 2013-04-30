/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.frodo;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * This shows how to create a simple activity with a map and a marker on the map.
 * <p>
 * Notice how we deal with the possibility that the Google Play services APK is not
 * installed/enabled/updated on a user's device.
 */
public class MapActivity extends FragmentActivity {
    /**
     * Note that this may be null if the Google Play services APK is not available.
     */
    private GoogleMap mMap;
    private SupportMapFragment fMapfragment;
    private boolean followCamera = true;
    Location currentLocation = null;
    Location lastLocation = null;
    Context context = null;
    HashMap<String, String> parseMarkers = null;
    TextView mapInformation = null;
    Bundle params = null;
    Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);              
        
        context = this;
        
        // get the arguments passed to this activity		
		intent = getIntent();
		params = intent.getExtras();
		if (params == null) {
			params = new Bundle();
		}
        
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not have been
     * completely destroyed during this process (it is likely that it would only be stopped or
     * paused), {@link #onCreate(Bundle)} may not be called again so we should call this method in
     * {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
        	fMapfragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        	mMap = fMapfragment.getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
        mapInformation = (TextView) findViewById(R.id.mapInformation);
        mapInformation.setText(R.string.pagetext_location_finding);
        // Acquire a reference to the system Location Manager
 		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
 		// get location using same provider criteria as we use for the maps
     	String provider = new CurrentLocationProvider(context).getProvider();
 		Location currentLocation = locationManager.getLastKnownLocation(provider);
		mapInformation.setText(R.string.pagetext_loading_quests);
		
		String lookupId = params.getString("ID");		
 		getQuests(currentLocation, lookupId);
    }
    
    private OnMyLocationChangeListener myLocationChangeListener = new OnMyLocationChangeListener() {

		@Override
		public void onMyLocationChange(Location location) {
			// Our location should only change at least every two minutes now that the location service is custom			
			// get the current location
			if (currentLocation != null) {
				lastLocation = new Location(currentLocation);			
			}
			currentLocation = location;
	    	if (currentLocation != null) {
	    		// update camera   			    			    	    		
	    		if (followCamera) {
	    			moveCamera(currentLocation);
		    	}
		    	
	    		if (lastLocation != null) {
			    	// if we have a location, go away and get some information about our location	
			    	// get near quests
		    		// only do this if we have significantly changed location (4 decimal places)
		    		DecimalFormat df = new DecimalFormat("###.####");					 
					String lastLat = df.format(lastLocation.getLatitude());
					String lastLng = df.format(lastLocation.getLongitude());
					String currentLat = df.format(currentLocation.getLatitude());
					String currentLng = df.format(currentLocation.getLongitude());					
					if (!lastLat.equals(currentLat) && !lastLng.equals(currentLng)) {
						mapInformation.setText(R.string.pagetext_loading_quests_location_changed);
				    	getQuests(currentLocation);
					}		    	
	    		}
	    	}
		}				
    	
	};
	
	private OnCameraChangeListener cameraChangeListener = new OnCameraChangeListener() {
		
		@Override
		public void onCameraChange(CameraPosition position) {
			// we've moved the camera, toggle flag so that the camera stops following our location
			// unless we move the camera back to our current location...
			followCamera = false;			
			if (position != null) {
				LatLng coords = position.target;
				if (currentLocation != null) {
					DecimalFormat df = new DecimalFormat("###.####");					 
					String cameraLat = df.format(coords.latitude);
					String cameraLng = df.format(coords.longitude);
					String myLat = df.format(currentLocation.getLatitude());
					String myLng = df.format(currentLocation.getLongitude());					
					if (cameraLat.equals(myLat) && cameraLng.equals(myLng)) {
						followCamera = true;
					}
				}
			}			
		}
	};
	
	private OnMarkerClickListener markerClickListener = new OnMarkerClickListener(){

		@Override
		public boolean onMarkerClick(Marker marker) {
			// We have clicked on one of the markers on the map
			// find out what it was, and what to do with it
			if (marker.isInfoWindowShown()) {
				marker.hideInfoWindow();
			} else {
				marker.showInfoWindow();
			}
			return false;
		}
		
	};
	
	private OnInfoWindowClickListener infoWindowClickListener = new OnInfoWindowClickListener(){

		@Override
		public void onInfoWindowClick(Marker marker) {
			// if we click on the information marker, I want to go to the show page for the quest.
			// get the parseObjectId from our hash			
			String objectId = parseMarkers.get(marker.getId());
			if (objectId != null) {
				Intent intent = new Intent(context, QuestActivity.class);
				intent.setAction("SHOW");
				intent.putExtra("ID", objectId);				
				startActivity(intent);
			} else {
				Toast.makeText(getApplicationContext(), R.string.common_unknown_problem, Toast.LENGTH_LONG).show();
			}
		}
		
	};
	
	private void getQuests(Location currentLocation) {
		getQuests(currentLocation, null);
	}
	
	private void getQuests(Location currentLocation, String questId){
		final String lookupId = questId;
		ParseQuery query = new ParseQuery("Quest");
		ParseGeoPoint coords = new ParseGeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());		
		query.whereWithinMiles("coordinates", coords, 25).findInBackground(new FindCallback() {
			
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					ParseObject lookupObj = null;
					Marker lookupMarker = null;
					
					// do something with the quests
					// for each quest returned, I want to add a placemarker to the current map
					// clear the hash which we use to map a marker against a quest
					if (parseMarkers != null) {
						parseMarkers.clear();
					} else {
						parseMarkers = new HashMap<String, String>();
					}
					mMap.clear();
					for (ParseObject obj : objects) {						
						// now that we have the quests, we want to display them. But quests come in different shapes
						// new quests (not picked up)
						MarkerOptions mo = new MarkerOptions();
						mo.title(obj.getString("title"));
						
						ParseGeoPoint parseCoords = obj.getParseGeoPoint("coordinates");
						LatLng coords = new LatLng(parseCoords.getLatitude(), parseCoords.getLongitude());
						mo.position(coords);	
						
						mo.snippet(obj.getString("description"));											
						
						Marker marker = mMap.addMarker(mo);						
						parseMarkers.put(marker.getId(), obj.getObjectId());
						
						// while we're going through these, see if we've got an id to lookup
						if (lookupId != null) {
							if (obj.getObjectId().equals(lookupId)) {
								lookupObj = obj;
								lookupMarker = marker;
							}
						}
					}
					mapInformation.setText(R.string.pagetext_loaded_quests);
					
					// once we finish loading the quests, see if we have retreived a lookupObject
					if (lookupObj != null) {
						// we have, lets change our camera to the coordinates of the object						
						moveCamera(lookupObj.getParseGeoPoint("coordinates"));
						// then we want to expand the information for that quest
						lookupMarker.showInfoWindow();
					}
				} else {
					Toast.makeText(getApplicationContext(), R.string.parse_query_fail, Toast.LENGTH_LONG).show();
				}						
			}
		});
	}

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {   	
    	mMap.setLocationSource(new CurrentLocationProvider(this));
    	mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    	mMap.setMyLocationEnabled(true);
    	mMap.getUiSettings().setMyLocationButtonEnabled(true);    	       	    
    	
    	moveCamera();  	
	    	
    	mMap.setOnMyLocationChangeListener(myLocationChangeListener);
    	mMap.setOnCameraChangeListener(cameraChangeListener);
    	mMap.setOnMarkerClickListener(markerClickListener);
    	mMap.setOnInfoWindowClickListener(infoWindowClickListener);
    }
    
    private void moveCamera() {
    	// default the position on brecon
    	LatLng l = null;
    	moveCamera(l);
    }
    
    private void moveCamera(ParseGeoPoint geo) {
    	// convert parsegeopoint into latlng
    	LatLng position = new LatLng(geo.getLatitude(), geo.getLongitude());
    	moveCamera(position);
    }
    
    private void moveCamera(Location l) {
    	// convert location into latlng
    	LatLng position = new LatLng(l.getLatitude(), l.getLongitude());
    	moveCamera(position);
    }
    
    private void moveCamera(LatLng position) {
    	float zoomLevel = mMap.getMaxZoomLevel() - 1;
    	if (position == null){
    		position = new LatLng(52.414357, -4.087629);
    		zoomLevel = 7;
    	}
    	
    	CameraPosition cameraPosition = new CameraPosition.Builder()
	        .target(position)      // Sets the center of the map to Brecon Beacons
	        .zoom(zoomLevel)                   // Sets the zoom
	        .bearing(0)                // Sets the orientation of the camera to east
	        .tilt(0)                   // Sets the tilt of the camera to 30 degrees
	        .build();                   // Creates a CameraPosition from the builder
    	mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));	  
    }
}
