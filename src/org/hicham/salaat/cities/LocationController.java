/** This file is part of Salaat First.
 *
 *   Licensed under the Creative Commons Attribution-NonCommercial 4.0 International Public License;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at:
 *   
 *   http://creativecommons.org/licenses/by-nc/4.0/legalcode
 *
*
*	@author Hicham BOUSHABA 2011 <hicham.boushaba@gmail.com>
*	
*/


package org.hicham.salaat.cities;

import android.content.Context;
import android.location.Criteria;
import android.location.LocationManager;

public class LocationController{
	
	private LocationListenerExtended locationListener;
	LocationManager locationManager;
	public LocationController(Context context, LocationListenerExtended locationListener) {
		locationManager= (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		this.locationListener=locationListener;
	}
	public void requestUpdates()
	{
		if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)&&!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
			locationListener.GpsIsDisabled();
		else
		{
			//at least one of the two providers is enabled, we prefer GPS_PROVIDER
			if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
			if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		}
	}
	
	public void removeLocationUpdates()
	{
		locationManager.removeUpdates(locationListener);
	}
}
