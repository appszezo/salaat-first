/** This file is part of Salaat First.
 *
 *   Licensed under the Creative Commons Attribution-NonCommercial 4.0 International Public License;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at:
 *   
 *   http://creativecommons.org/licenses/by-nc/4.0/legalcode
 *  	
 *	@author Hicham BOUSHABA 2014 <hicham.boushaba@gmail.com>
 *	
 */

package org.hicham.salaat.location;

import java.util.Locale;

import org.hicham.salaat.SalaatFirstApplication;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

public class LocationController implements LocationListener,
		AddressTaskListener {
	private static final int TWO_MINUTES = 1000 * 60 * 2;

	private Context context;
	private CustomLocationListener locationListener;
	LocationManager locationManager;
	private long minTime = 500;
	private float minDistance = 0;
	private GetAddressTask task;
	private long startTime;
	private boolean isLocationSent=false;

	private Location betterLocation;

	public LocationController(Context context,
			CustomLocationListener locationListener) {
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		this.context = context;
		this.locationListener = locationListener;
	}

	public LocationController(Context context,
			CustomLocationListener locationListener, long minTime,
			float minDistance) {
		this(context, locationListener);
		this.minTime = minTime;
		this.minDistance = minDistance;
	}

	/**
	 * Determines whether one Location reading is better than the current
	 * Location fix
	 * 
	 * @param location
	 *            The new Location that you want to evaluate
	 * @param currentBestLocation
	 *            The current Location fix, to which you want to compare the new
	 *            one
	 */
	protected boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		if (location == null) {
			return false;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	public void onFailure() {
		com.ahmedsoliman.devel.jislamic.astro.Location location = SalaatFirstApplication.dBAdapter
				.getNearestLocation(betterLocation.getLongitude(),
						betterLocation.getLatitude());
		location.setDegreeLat(betterLocation.getLatitude());
		location.setDegreeLong(betterLocation.getLongitude());
		String countryName = SalaatFirstApplication.dBAdapter
				.getCountryName(location.getName());
		locationListener.sendLocation(location, true, countryName);
	}

	public void onLocationChanged(Location location) {
		Log.i(SalaatFirstApplication.TAG,"onLocationChanged, "+location.getProvider());
		if (betterLocation == null
				|| isBetterLocation(location, betterLocation))
				betterLocation = location;
		else
		{
			Log.i(SalaatFirstApplication.TAG,"lastKnownLocation used");
		}
		if(betterLocation.getAccuracy()>200&&System.currentTimeMillis()-startTime</*20 seconds*/20*1000)
		{
			/*wait getting a better accuracy*/
			return;
		}
		removeLocationUpdates();

		if (task == null&&!isLocationSent) {
			task = new GetAddressTask(context, this, true, new Locale("en"));
			task.execute(betterLocation.getLatitude(),
					betterLocation.getLongitude());
			isLocationSent=true;
		}
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	public void publishResult(String cityName) {
		/* the name found by GetAddressTask*/
		/*check if the city is on the database*/
		com.ahmedsoliman.devel.jislamic.astro.Location location = SalaatFirstApplication.dBAdapter
				.getLocation(cityName, betterLocation.getLatitude(),
						betterLocation.getLongitude());
		if (location != null) {
			String countryName = SalaatFirstApplication.dBAdapter
					.getCountryName(location.getName());
			locationListener.sendLocation(location, false, countryName);
		} else {
			/*Use the altitude of the GPS location using the nearest location from database*/
			location = SalaatFirstApplication.dBAdapter
					.getNearestLocation(betterLocation.getLongitude(),
							betterLocation.getLatitude());
			location.setDegreeLat(betterLocation.getLatitude());
			location.setDegreeLong(betterLocation.getLongitude());
			String countryName = SalaatFirstApplication.dBAdapter
					.getCountryName(location.getName());
			locationListener.sendLocation(location, true, countryName);
		}
	}

	public void removeLocationUpdates() {
		locationManager.removeUpdates(this);
		if (task != null) {
			task.cancel(false);
			task=null;
		}
	}

	public void requestUpdates() {
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
				&& !locationManager
						.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			locationListener.gpsIsDisabled();
		} else {
			// at least one of the two providers is enabled, we prefer
			// GPS_PROVIDER
			startTime=System.currentTimeMillis();
			if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, minTime, minDistance,
						this);
			if (locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
				locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, minTime, minDistance,
						this);

			Location gpsLastKnownLocation = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			Location networkLastKnownLocation = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

			if (isBetterLocation(gpsLastKnownLocation, networkLastKnownLocation))
				betterLocation = gpsLastKnownLocation;
			else
				betterLocation = networkLastKnownLocation;
			new CountDownTimer(40 * 1000, 40 * 1000) {

				@Override
				public void onFinish() {
					if (betterLocation != null) {
						Log.i(SalaatFirstApplication.TAG,"LocationController timer expired");
						onLocationChanged(betterLocation);
					}
				}

				@Override
				public void onTick(long millisUntilFinished) {
					// TODO Auto-generated method stub
				}
			}.start();
		}
	}

}
