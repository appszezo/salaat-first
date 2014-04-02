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

package org.hicham.salaat;

import static org.hicham.salaat.SalaatFirstApplication.TAG;
import static org.hicham.salaat.SalaatFirstApplication.prefs;

import org.hicham.salaat.alarm.EventsHandler;
import org.hicham.salaat.settings.Keys;
import org.hicham.salaat.settings.Keys.DefaultValues;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartAtBootServiceReceiver extends BroadcastReceiver {

	/**
	 * Used to launch the service on starting the phone
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		if (prefs.getBoolean(Keys.STARTING_SERVICE_ON_BOOT_COMPLETE_KEY, DefaultValues.STARTING_SERVICE_ON_BOOT_COMPLETE)) {
			if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
				Log.i(TAG, "Starting service from the BroadCastReceiver");
				if (SalaatFirstApplication.dBAdapter.getLocation(prefs.getString(Keys.CITY_KEY,
						DefaultValues.CITY)) == null) {
					return; /*
							 * don't handle the next events, the MainActivity will
							 * recall this
							 */
				}
				EventsHandler handler = new EventsHandler(context);
				handler.cancelAlarm();
				handler.scheduleNextPrayerEvent(false);
			}
		}
	}

}
