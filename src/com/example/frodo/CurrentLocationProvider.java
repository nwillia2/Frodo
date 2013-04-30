package com.example.frodo;
import com.google.android.gms.maps.LocationSource;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class CurrentLocationProvider implements LocationSource, LocationListener
{
    private OnLocationChangedListener listener;
    private LocationManager locationManager;
    private String provider; 
    
    private static final int WAIT_TIME = 1000 * 10; // 10 seconds

    public CurrentLocationProvider(Context context)
    {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
    	criteria.setPowerRequirement(Criteria.POWER_LOW);    	
    	provider = locationManager.getBestProvider(criteria, true);
    }

    @Override
    public void activate(OnLocationChangedListener listener)
    {
        this.listener = listener;                  
    	locationManager.requestLocationUpdates(provider, WAIT_TIME, 0, this);
    }

    @Override
    public void deactivate()
    {
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location)
    {
        if(listener != null)
        {
            listener.onLocationChanged(location);
        }
    }

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	public String getProvider() {
		return provider;
	}
}