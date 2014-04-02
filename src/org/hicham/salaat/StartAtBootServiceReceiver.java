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

package org.hicham.salaat;

import org.hicham.alarm.EventsHandler;
import org.hicham.salaat.settings.Keys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class StartAtBootServiceReceiver extends BroadcastReceiver {
	
	/**
	 * Used to launch the service on starting the phone
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(context);
		if(pref.getBoolean(Keys.STARTING_SERVICE_ON_BOOT_COMPLETE_KEY, true))
		{
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			Log.i(PrayerTimesActivity.TAG, "Starting service from the BroadCastReceiver");
			EventsHandler handler=new EventsHandler(context);
			handler.cancelAlarm();
			handler.scheduleNextEvent(-1, -1);
		}
		}
	}

}
