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

import java.util.List;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);              
        
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
    }
    
    private OnMyLocationChangeListener myLocationChangeListener = new OnMyLocationChangeListener() {

		@Override
		public void onMyLocationChange(Location arg0) {
			// Our location should only change at least every two minutes now that the location service is custom			
			// get the current location
			Location currentLocation = mMap.getMyLocation();
	    	if (currentLocation != null) {
	    		// update camera   			    			    	    		
		    	CameraPosition cameraPosition = new CameraPosition.Builder()
		        .target(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
		        .zoom(mMap.getMaxZoomLevel() - 1)
		        .build();                   
		    	mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		    	
		    	// if we have a location, go away and get some information about our location	
		    	// get near quests
		    	getQuests(currentLocation);
	    	}
		}				
    	
	};
	
	private void getQuests(Location currentLocation){
		ParseQuery query = new ParseQuery("Quest");
		ParseGeoPoint coords = new ParseGeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());		
		query.whereWithinMiles("coordinates", coords, 25).findInBackground(new FindCallback() {
			
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					// do something with the quests
					// for each quest returned, I want to add a placemarker to the current map
					for (ParseObject obj : objects) {
						// now that we have the quests, we want to display them. But quests come in different shapes
						// new quests (not picked up)
						MarkerOptions mo = new MarkerOptions();
						mo.title(obj.getString("title"));
						
						ParseGeoPoint parseCoords = obj.getParseGeoPoint("coordinates");
						LatLng coords = new LatLng(parseCoords.getLatitude(), parseCoords.getLongitude());
						mo.position(coords);
						
						mMap.addMarker(mo);
					}
				} else {
					// something went wrong
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
    	mMap.getUiSettings().setMyLocationButtonEnabled(false);    	       	    
    	
    	CameraPosition cameraPosition = new CameraPosition.Builder()
	        .target(new LatLng(52.414357, -4.087629))      // Sets the center of the map to Brecon Beacons
	        .zoom(7)                   // Sets the zoom
	        .bearing(0)                // Sets the orientation of the camera to east
	        .tilt(0)                   // Sets the tilt of the camera to 30 degrees
	        .build();                   // Creates a CameraPosition from the builder
	    	mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));    		    
	    	
    	mMap.setOnMyLocationChangeListener(myLocationChangeListener);
    	
    	
    }
}
