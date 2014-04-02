/** This file is part of Salaat First.
 *
 *   Licensed under the Creative Commons Attribution-NonCommercial 4.0 International Public License;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at:
 *   
 *   http://creativecommons.org/licenses/by-nc/4.0/legalcode
 *   
 *
 *	@author Hicham BOUSHABA 2014 <hicham.boushaba@gmail.com>
 *	
 */

package org.hicham.salaat.location;

import static org.hicham.salaat.SalaatFirstApplication.TAG;
import static org.hicham.salaat.SalaatFirstApplication.dBAdapter;

import org.hicham.salaat.SalaatFirstApplication;
import org.hicham.salaat.settings.Keys;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.ahmedsoliman.devel.jislamic.astro.Location;

public class LocationRefresher extends Service implements CustomLocationListener {

	private LocationController locationController;
	private WakeLock wakeLock;
	public static final String ACTION_LOCATION_REFRESH="org.hicham.salaat.ACTION_LOCATION_REFRESH";
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		acquireWakeLock();
		if(locationController==null)
			locationController=new LocationController(this, this);
		Log.i(TAG, "Location refresher service starting");
		locationController.requestUpdates();
		return super.onStartCommand(intent, flags, startId);
	}
	
	public void gpsIsDisabled() {
		
	}
	public void sendLocation(Location location, boolean isCustomCity,
			String country) {
		
		Log.i(TAG, "LocationRefresher: location getted "+location.getName()+ " isCustom "+isCustomCity);
		
		if (isCustomCity) {
			dBAdapter.setCustomCity(
					location.getDegreeLong(),
					location.getDegreeLat(),
					location.getSeaLevel(), country);
			SalaatFirstApplication.prefs.edit()
					.putString(Keys.CITY_KEY, "custom")
					.commit();
			SalaatFirstApplication.prefs.edit().putString(
					Keys.CITY_NAME_FORMATTED, "");
			SalaatFirstApplication.prefs.edit()
			.putString(Keys.COUNTRY_KEY, country)
			.commit();
			SalaatFirstApplication.getLastInstance().onSharedPreferenceChanged(SalaatFirstApplication.prefs, Keys.CITY_KEY);
		} else {
			SalaatFirstApplication.prefs
					.edit()
					.putString(Keys.CITY_KEY,
							location.getName()).commit();
			SalaatFirstApplication.prefs.edit()
					.putString(Keys.COUNTRY_KEY, country)
					.commit();
	}
		stopSelf();
	}

	  /**
	   * Releases the wake lock.
	   */
	  private void releaseWakeLock() {
	    if (wakeLock != null && wakeLock.isHeld()) {
	      wakeLock.release();
	      wakeLock = null;
	    }
	  }
	    
	  private void acquireWakeLock() {
	        Log.i(TAG, "Acquiring wake lock.");
	        try {
	          PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
	          if (powerManager == null) {
	            Log.e(TAG, "Power manager null.");
	            return;
	          }
	          if (wakeLock == null) {
	            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
	            if (wakeLock == null) {
	              Log.e(TAG, "Cannot create a new wake lock.");
	              return;
	            }
	          }
	          if (!wakeLock.isHeld()) {
	            wakeLock.acquire();
	            if (!wakeLock.isHeld()) {
	              Log.e(TAG, "Cannot acquire wake lock.");
	            }
	          }
	        } catch (RuntimeException e) {
	          Log.e(TAG, e.getMessage(), e);
	        }
	}

	@Override
	public void onDestroy() {
		releaseWakeLock();
		super.onDestroy();
	}
}
