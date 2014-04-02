/** This file is part of Salaat First.
 *
 *   Licensed under the Creative Commons Attribution-NonCommercial 4.0 International Public License;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at:
 *   
 *   http://creativecommons.org/licenses/by-nc/4.0/legalcode
 *  	
*	@author Hicham BOUSHABA 2012 <hicham.boushaba@gmail.com>
*	
*/

package org.hicham.alarm;

import static com.ahmadiv.dari.DariGlyphUtils.reshapeText;
import static org.hicham.salaat.PrayerTimesActivity.isReshapingNessecary;

import org.hicham.salaat.AdhanActivity;
import org.hicham.salaat.MainActivity;
import org.hicham.salaat.PrayerTimesActivity;
import org.hicham.salaat.R;
import org.hicham.salaat.calculating.PrayerTimesCalculator;
import org.hicham.salaat.settings.Keys;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
	
	private Context context;
	private int nextPrayer;
	private NotificationManager mNotificationManager;
	private int oldRingerMode=-1;
	private static final int NOTIFICATION_ID = 1;
	public static final String PRAYER_NAME_KEY="prayer_name";


	@Override
	public void onReceive(Context context, Intent intent) {
		this.context=context;
		nextPrayer=intent.getIntExtra(EventsHandler.PRAYER_NAME_KEY, 0);
		switch(intent.getIntExtra(EventsHandler.EVENT_TYPE_KEY, 0))
		{
		case EventsHandler.SHOW_NOTIIFCATION:
			showPrayerNotification(nextPrayer);
			break;
		case EventsHandler.SHOW_ADHAN:
			showAdhanActivity(nextPrayer);
			break;
		case EventsHandler.ACTIVATING_SILENT:
			activateSilent();
			break;
		case EventsHandler.DEACTIVATING_SILENT:
			oldRingerMode=intent.getIntExtra("OldRingerMode", -1);
			deactivateSilent();
			break;
		}
	}
	
	private void showAdhanActivity(int prayer) {
		Log.i(PrayerTimesActivity.TAG, "Adhan: Prayer="+prayer);
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.cancel(NOTIFICATION_ID);
		if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Keys.SHOW_ADHAN_KEY+prayer, true))
		{
				Intent intent=new Intent(context,AdhanActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(PRAYER_NAME_KEY, PrayerTimesCalculator.getPrayerName(prayer, context));
				context.startActivity(intent);
		}
		EventsHandler handler=new EventsHandler(context);
		if(nextPrayer==PrayerTimesCalculator.FAJR)
			nextPrayer=PrayerTimesCalculator.DHUHR;
		else if(nextPrayer==PrayerTimesCalculator.ICHAA)
			nextPrayer=PrayerTimesCalculator.FAJR;
		else
			nextPrayer+=1;
		handler.scheduleNextEvent(nextPrayer, EventsHandler.SHOW_ADHAN);
	}
	private void showPrayerNotification(int prayer)
	{
		Log.i(PrayerTimesActivity.TAG, "notification: Prayer="+prayer);
		if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
				Keys.SHOW_NOTIFICATION_KEY, true)) {
			mNotificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			int icon = R.drawable.notificationicon;
			CharSequence tickerText = "Salaat First";
			long when = System.currentTimeMillis();
			Notification notification = new Notification(icon, tickerText, when);
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.defaults |= Notification.DEFAULT_SOUND;

			CharSequence contentTitle = PrayerTimesCalculator.getPrayerName(
					prayer, context);
			CharSequence contentText = reshapeText(
					context.getString(R.string.notification_text),
					isReshapingNessecary);
			Intent notificationIntent = new Intent(context, MainActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					notificationIntent, 0);

			notification.setLatestEventInfo(context, contentTitle, contentText,
					contentIntent);
			mNotificationManager.notify(NOTIFICATION_ID, notification);
		}
		EventsHandler handler=new EventsHandler(context);
		handler.scheduleNextEvent(nextPrayer, EventsHandler.SHOW_NOTIIFCATION);
	}
	
	
	private void activateSilent()
	{
		int previousPrayer;
		if(nextPrayer==PrayerTimesCalculator.FAJR)
			previousPrayer=PrayerTimesCalculator.ICHAA;
		else if(nextPrayer==PrayerTimesCalculator.DHUHR)
			previousPrayer=PrayerTimesCalculator.FAJR;
		else
			previousPrayer=nextPrayer-1;
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		EventsHandler handler = new EventsHandler(context);
		if(prefs.getBoolean(Keys.ACTIVATING_SILENT_KEY+previousPrayer, true))
		{
			Log.i(PrayerTimesActivity.TAG, "silent");
			AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			oldRingerMode = audioManager.getRingerMode();
			audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			handler.setOldRingerMode(oldRingerMode);
			handler.scheduleNextEvent(nextPrayer, EventsHandler.ACTIVATING_SILENT);
		}
		handler.scheduleNextEvent(nextPrayer, EventsHandler.ACTIVATING_SILENT);
	}
	
	private void deactivateSilent()
	{
		Log.i(PrayerTimesActivity.TAG, "deactivate silent");
		AudioManager audioManager=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		Log.i(PrayerTimesActivity.TAG, "older ringer mode value: "+oldRingerMode);
		if(oldRingerMode!=-1)//if oldRingerMode==1 this means that we didn't activate silent
			{
				audioManager.setRingerMode(oldRingerMode); 
				oldRingerMode=-1;
			}
		EventsHandler handler=new EventsHandler(context);
		handler.scheduleNextEvent(-1, EventsHandler.DEACTIVATING_SILENT);
	}

}
