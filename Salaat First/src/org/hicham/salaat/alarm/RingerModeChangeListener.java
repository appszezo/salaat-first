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

package org.hicham.salaat.alarm;

import static org.hicham.salaat.SalaatFirstApplication.TAG;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class RingerModeChangeListener extends Service{

	
	private RingerModeChangeReceiver receiver;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "Start RingerModeChangeListener service");
		IntentFilter filter=new IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION);
		if(receiver==null)
			receiver=new RingerModeChangeReceiver();
		registerReceiver(receiver, filter);

		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		if(receiver!=null)
			try
			{
				unregisterReceiver(receiver);
			}
		catch(Exception e)
		{
			
		}
		super.onDestroy();
	}
	
	
	public class RingerModeChangeReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(AudioManager.RINGER_MODE_CHANGED_ACTION))
			{
				Bundle extras=intent.getExtras();
				if(extras!=null&&extras.containsKey(AudioManager.EXTRA_RINGER_MODE))
				{
					if(extras.getInt(AudioManager.EXTRA_RINGER_MODE)==AudioManager.RINGER_MODE_NORMAL)
					{	
						/*the mode is not silent, so the user change it*/
						Log.i(TAG, "ringer mode changed manually, cancel the silent deactivation event");
						EventsHandler handler=new EventsHandler(context);
						handler.cancelAlarm(EventsHandler.DEACTIVATING_SILENT_REQUEST_CODE);
						handler.scheduleNextPrayerEvent(false);
						RingerModeChangeListener.this.stopSelf();
					}
				}
			}
		}
	}
}
