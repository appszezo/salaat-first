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
import static org.hicham.salaat.SalaatFirstApplication.prefs;

import java.util.Calendar;

import org.hicham.salaat.SalaatFirstApplication;
import org.hicham.salaat.calculating.PrayerTimesCalcluationUtils;
import org.hicham.salaat.settings.Keys;
import org.hicham.salaat.settings.Keys.DefaultValues;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.ahmedsoliman.devel.jislamic.DayPrayers;

/**
 * schedule events: show prayer activity, activating silent .... 1st version
 * without optimization, we use alarm for scheduling all events, including
 * events non activated.
 */

public class EventsHandler {
	public final static int SHOW_NOTIIFCATION = 0;
	public final static int SHOW_ADHAN = 1;
	public final static int ACTIVATING_SILENT = 2;
	public final static int DEACTIVATING_SILENT = 3;
	
	public final static int SHOW_ADKAR_ASSABAH=4;
	public final static int SHOW_ADKAR_ALMASSAE=5;
	public final static int SHOW_ADKAR_ALNAWM=6;

	
	private final static int REQUEST_CODE = "salaat_first".hashCode();
	public final static int SHOW_NOTIFICATION_REQUEST_CODE = REQUEST_CODE
			+ SHOW_NOTIIFCATION;
	public final static int SHOW_ADHAN_REQUEST_CODE = REQUEST_CODE + SHOW_ADHAN;
	public final static int ACTIVATING_SILENT_REQUEST_CODE = REQUEST_CODE
			+ ACTIVATING_SILENT;
	public final static int DEACTIVATING_SILENT_REQUEST_CODE = REQUEST_CODE
			+ DEACTIVATING_SILENT;

	
	public final static int SHOW_ADKAR_ASSABAH_REQUEST_CODE=REQUEST_CODE+SHOW_ADKAR_ASSABAH;
	public final static int SHOW_ADKAR_ALMASSAE_REQUEST_CODE=REQUEST_CODE+SHOW_ADKAR_ALMASSAE;
	public final static int SHOW_ADKAR_ALNAWM_REQUEST_CODE=REQUEST_CODE+SHOW_ADKAR_ALNAWM;
	
	public static final String EVENTS_RECEIVER_ACTION = "org.hicham.salaat.alarm.AlarmReceiver.action";
	public final static String EVENT_TYPE_KEY = "event type";
	public static final String PRAYER_KEY = "prayer_id";
	public static final String OLD_RINGER_MODE_KEY="OldRingerMode";

	/*Alarm Shared Preferences keys*/
	public static final String LAST_ADHAN_KEY="last_adhan";

	private Context context;
	private SharedPreferences alarmPreferences;
	private int oldRingerMode = -1;

	public EventsHandler(Context context) {
		this.context = context;
		alarmPreferences=context.getSharedPreferences("alarm-preferences", Context.MODE_PRIVATE);
	}

	
	public void cancelAlarm(int requestCode)
	{
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(EVENTS_RECEIVER_ACTION);
		PendingIntent sender = PendingIntent.getBroadcast(context,
				requestCode, intent,
				PendingIntent.FLAG_NO_CREATE);
		if (sender != null) {
			Log.i(TAG, "Event is scheduled, cancel it");
			sender.cancel();
			alarmManager.cancel(sender);
		}

	}
	
	public void cancelAlarm() {
		Log.i(TAG, "cancel show notification event");
		cancelAlarm(SHOW_NOTIFICATION_REQUEST_CODE);	

		Log.i(TAG, "cancel show adhan event");
		cancelAlarm(SHOW_ADHAN_REQUEST_CODE);
		
		Log.i(TAG, "cancel silent activation event");
		cancelAlarm(ACTIVATING_SILENT_REQUEST_CODE);
		
		Log.i(TAG, "cancel silent deactivation event");
		cancelAlarm(DEACTIVATING_SILENT_REQUEST_CODE);
	}

	public int getOldRingerMode() {
		return oldRingerMode;
	}

	/**
	 * Schedules the next adhan event
	 * 
	 * @param nextPrayer
	 */
	protected void scheduleNextPrayerAdhan(int nextPrayer) {
		Log.i(TAG, "Show next adhan scheduling");
		// calculate the prayer time, as the notification may be show in a
		// shorter value than the configured one
		Calendar nextEventTime;
		if(nextPrayer==DayPrayers.JUMUA)
			nextEventTime = PrayerTimesCalcluationUtils
				.getCurrentPrayerTimes().getPrayers()[DayPrayers.DHUHR]
						.getPrayerTimeAsCalendar();
		else
			nextEventTime = PrayerTimesCalcluationUtils
			.getCurrentPrayerTimes().getPrayers()[nextPrayer]
					.getPrayerTimeAsCalendar();

		scheduleEvent(nextPrayer, SHOW_ADHAN_REQUEST_CODE, SHOW_ADHAN, nextEventTime);
	}

	/**
	 * Schedule the next Prayer notification
	 * @param isNeedCalculationOfAllAdkars TODO
	 */
	public void scheduleNextPrayerEvent(boolean isNeedCalculationOfAllAdkars) {

		/* If alarms are cancelled then quit without handling any events */
		if (prefs.getBoolean(Keys.CANCEL_ALL_ALARMS_KEY, DefaultValues.CANCEL_ALL_ALARMS))
			return;
		
		DayPrayers prayerTimes = PrayerTimesCalcluationUtils
				.getCurrentPrayerTimes();
		int nextPrayer = prayerTimes.getNextPrayer();
		Calendar nextEventTime;

		/* get next prayer date*/
		nextEventTime = prayerTimes.getPrayers()[nextPrayer]
				.getPrayerTimeAsCalendar();
		Calendar currentTime_1 = Calendar.getInstance();
		currentTime_1.add(Calendar.MINUTE, -1);/* add a minute to be sure
													that this is fajr*/
		if (nextEventTime.before(currentTime_1))/* we schedule adhan for
			fajr prayer after ichaa prayer*/
			nextEventTime.add(Calendar.DAY_OF_MONTH, 1); /* TODO need recalculation*/

		/*Build an int in the format of: nextPrayer+DayOfmonth, by this parametr we will be sure that after an adhan is shown
		 * it will not be rescheduled in case of changing the location
		 */
		int nextAdhan=Integer.parseInt(nextPrayer+""+nextEventTime.get(Calendar.DAY_OF_MONTH));
		int lastAdhan=alarmPreferences.getInt(LAST_ADHAN_KEY, -1);

		if(nextAdhan==lastAdhan)
		{
			/*this prayer was scheduled and run, silent events will arrange the next preyer scheduling*/
			return;
		}
		
		if(nextEventTime.get(Calendar.DAY_OF_WEEK)==Calendar.FRIDAY&&nextPrayer==DayPrayers.DHUHR
				&&prefs.getBoolean(Keys.USE_CUSTOM_SETTINGS_FOR_JUMUA_KEY, DefaultValues.USE_CUSTOM_SETTINGS_FOR_JUMUA))
		{
			/*schedule Friday prayer events*/
			scheduleFridayPrayerEvents(nextEventTime);
			return;
		}

		int notificationTime = SalaatFirstApplication.prefs.getInt(
				Keys.NOTIFICATION_TIME_KEY, DefaultValues.NOTIFICATION_TIME);
		if ((nextEventTime.getTimeInMillis() - System.currentTimeMillis()) < notificationTime * 60 * 1000) {
			// show notification after 5 seconds
			nextEventTime = Calendar.getInstance();
			nextEventTime.add(Calendar.SECOND, 5);
		} else
			nextEventTime.add(Calendar.MINUTE, -notificationTime);
		Log.i(TAG, "Show notification scheduling");
		scheduleEvent(nextPrayer, SHOW_NOTIFICATION_REQUEST_CODE, SHOW_NOTIIFCATION, nextEventTime);
		if(!isNeedCalculationOfAllAdkars)
			scheduleAdkarEvents(nextPrayer,prayerTimes);
		else
			scheduleAdkarEvents(-1, prayerTimes);
	}

	public void scheduleAdkarEvents(int nextPrayer, DayPrayers prayers) {
		/*it's clear from this design that if the application was killed after 
		 * Fajr Prayer (or Asr prayer), then the adkar will not be fired.
		 * But this is OK as the adkars alarms are not critical.
		 */
		switch(nextPrayer)
		{
		case DayPrayers.FAJR:
			if(prefs.getBoolean(Keys.ADKAR_SABAH_NOTIFICATION_KEY, DefaultValues.ADKAR_NOTIFICATIONS))
				scheduleAdkarSabah(prayers.shuruq().getPrayerTimeAsCalendar());
			break;
		case DayPrayers.ASR:
			if(prefs.getBoolean(Keys.ADKAR_ALMASSAE_NOTIFICATION_KEY, DefaultValues.ADKAR_NOTIFICATIONS))
				scheduleAdkarAlmassae(prayers.maghrib().getPrayerTimeAsCalendar());
			break;
		case DayPrayers.ICHAA:
			if(prefs.getBoolean(Keys.ADKAR_ANNAWM_NOTIFICATION_KEY, DefaultValues.ADKAR_NOTIFICATIONS))
				scheduleAdkarAnnawm();
			break;
		case -1 :
			if(prefs.getBoolean(Keys.ADKAR_SABAH_NOTIFICATION_KEY, DefaultValues.ADKAR_NOTIFICATIONS))
				scheduleAdkarSabah(prayers.shuruq().getPrayerTimeAsCalendar());
			if(prefs.getBoolean(Keys.ADKAR_ALMASSAE_NOTIFICATION_KEY, DefaultValues.ADKAR_NOTIFICATIONS))
				scheduleAdkarAlmassae(prayers.maghrib().getPrayerTimeAsCalendar());
			if(prefs.getBoolean(Keys.ADKAR_ANNAWM_NOTIFICATION_KEY, DefaultValues.ADKAR_NOTIFICATIONS))
				scheduleAdkarAnnawm();
		}
	}

	private void scheduleAdkarAnnawm() {
		Calendar nextEventTime=Calendar.getInstance();
		nextEventTime.setTimeInMillis(prefs.getLong(Keys.ADKAR_ANNAWM_TIME_KEY, DefaultValues.ADKAR_ANNAWM_TIME));
		
		Calendar currentTime_1 = Calendar.getInstance();
		nextEventTime.set(currentTime_1.get(Calendar.YEAR), currentTime_1.get(Calendar.MONTH), currentTime_1.get(Calendar.DAY_OF_MONTH));
		currentTime_1.add(Calendar.MINUTE, -1);
		if (nextEventTime.before(currentTime_1))// we schedule adkar for the next day
			nextEventTime.add(Calendar.DAY_OF_MONTH, 1); 	
		Bundle extras=new Bundle();
		extras.putInt(EVENT_TYPE_KEY, SHOW_ADKAR_ALNAWM);
		scheduleEvent(extras, SHOW_ADKAR_ALNAWM_REQUEST_CODE, nextEventTime);
	}


	private void scheduleAdkarSabah(Calendar sunriseTime) {
		Calendar nextEventTime=sunriseTime;
		Calendar currentTime_1 = Calendar.getInstance();
		currentTime_1.add(Calendar.MINUTE, -1);// add a minute to be sure
		// that this is fajr
		if (nextEventTime.before(currentTime_1))// we schedule adkar for the next day
			nextEventTime.add(Calendar.DAY_OF_MONTH, 1); 
		
		int notificationTime=SalaatFirstApplication.prefs.getInt(Keys.ADKAR_SABAH_TIME_KEY, DefaultValues.ADKAR_SABAH_TIME);
		if ((nextEventTime.getTimeInMillis() - System.currentTimeMillis()) < notificationTime * 60 * 1000) {
			nextEventTime = Calendar.getInstance();
			nextEventTime.add(Calendar.SECOND, 5);
		}
		else
		{
			nextEventTime.add(Calendar.MINUTE, -notificationTime);
		}
		Bundle extras=new Bundle();
		extras.putInt(EVENT_TYPE_KEY, SHOW_ADKAR_ASSABAH);
		scheduleEvent(extras, SHOW_ADKAR_ASSABAH_REQUEST_CODE, nextEventTime);
	}
	
	private void scheduleAdkarAlmassae(Calendar maghribTime) {
		Calendar nextEventTime=maghribTime;
		Calendar currentTime_1 = Calendar.getInstance();
		currentTime_1.add(Calendar.MINUTE, -1);// add a minute to be sure
		// that this is fajr
		if (nextEventTime.before(currentTime_1))// we schedule adkar for the next day
			nextEventTime.add(Calendar.DAY_OF_MONTH, 1); 

		
		int notificationTime=SalaatFirstApplication.prefs.getInt(Keys.ADKAR_ALMASSAE_TIME_KEY, DefaultValues.ADKAR_ALMASSAE_TIME);
		if ((nextEventTime.getTimeInMillis() - System.currentTimeMillis()) < notificationTime * 60 * 1000) {
			nextEventTime = Calendar.getInstance();
			nextEventTime.add(Calendar.SECOND, 5);
		}
		else
		{
			nextEventTime.add(Calendar.MINUTE, -notificationTime);
		}
		Bundle extras=new Bundle();
		extras.putInt(EVENT_TYPE_KEY, SHOW_ADKAR_ALMASSAE);
		scheduleEvent(extras, SHOW_ADKAR_ALMASSAE_REQUEST_CODE, nextEventTime);
	}


	protected void scheduleSilentActivation(int currentPrayer) {
		Log.i(TAG, "Activating silent scheduling");
		// use the actual time as starting point
		Calendar nextEventTime = Calendar.getInstance();
		int activateSilent = prefs.getInt(Keys.DELAY_BEFORE_SILENT_KEY
				+ currentPrayer, DefaultValues.DELAY_BEFORE_SILENT);
		nextEventTime.add(Calendar.MINUTE, activateSilent);
		scheduleEvent(currentPrayer, ACTIVATING_SILENT_REQUEST_CODE, ACTIVATING_SILENT,  nextEventTime);
	}

	protected void scheduleSilentDeactivation(int currentPrayer) {
		Log.i(TAG, "Deactivating silent scheduling");
		// use the actual time as starting point
		Calendar nextEventTime = Calendar.getInstance();
		int defaultDuration=(currentPrayer==DayPrayers.JUMUA?DefaultValues.SILENT_MODE_DURATION_JUMUA:DefaultValues.SILENT_MODE_DURATION);
		int deactivateSilent = prefs.getInt(Keys.SILENT_MODE_DURATION_KEY
				+ currentPrayer, defaultDuration);
		nextEventTime.add(Calendar.MINUTE, deactivateSilent);
		scheduleEvent(currentPrayer, DEACTIVATING_SILENT_REQUEST_CODE, DEACTIVATING_SILENT,  nextEventTime);
	}


	/**
	 * schedules Prayer and silent events for Friday Prayer.
	 */
	protected void scheduleFridayPrayerEvents(Calendar prayerTime)
	{
		Calendar notificationEventTime=(Calendar) prayerTime.clone();
		/*schedule Jumua Notification prayer*/
		Log.i(TAG, "Show Jumua notification scheduling");
		int notificationTime = SalaatFirstApplication.prefs.getInt(
				Keys.NOTIFICATION_TIME_KEY, DefaultValues.NOTIFICATION_TIME);
		if ((notificationEventTime.getTimeInMillis() - System.currentTimeMillis()) < notificationTime * 60 * 1000) {
			// show notification after 5 seconds
			notificationEventTime = Calendar.getInstance();
			notificationEventTime.add(Calendar.SECOND, 5);
		} else
			notificationEventTime.add(Calendar.MINUTE, -notificationTime);
		scheduleEvent(DayPrayers.JUMUA, SHOW_NOTIFICATION_REQUEST_CODE, SHOW_NOTIIFCATION, notificationEventTime);
						
		/*schedule silent activation*/
		Log.i(TAG, "silent activation for Jumua scheduling");
		Calendar silentStartTime=(Calendar)prayerTime.clone();
		int silentDelay=prefs.getInt(Keys.DELAY_BEFORE_SILENT_KEY+DayPrayers.JUMUA, DefaultValues.DELAY_BEFORE_SILENT_JUMUA);
		if ((silentStartTime.getTimeInMillis() - System.currentTimeMillis()) < silentDelay * 60 * 1000) {
			silentStartTime=Calendar.getInstance();
			silentStartTime.add(Calendar.SECOND, 10);
		}
		else
			silentStartTime.add(Calendar.MINUTE, -silentDelay);
		scheduleEvent(DayPrayers.JUMUA, ACTIVATING_SILENT_REQUEST_CODE, ACTIVATING_SILENT, silentStartTime);
		
	}
	
	private void scheduleEvent(int prayer, int requestCode, int eventType, Calendar eventTime)
	{
		Bundle extras=new Bundle();
		extras.putInt(EVENT_TYPE_KEY, eventType);
		extras.putInt(PRAYER_KEY, prayer);
		extras.putInt(OLD_RINGER_MODE_KEY, oldRingerMode);
		scheduleEvent(extras, requestCode, eventTime);
	}
	
	private void scheduleEvent(Bundle extras, int requestCode, Calendar eventTime)
	{
		Intent intent = new Intent(EVENTS_RECEIVER_ACTION);
		
		intent.putExtras(extras);
		
		PendingIntent sender = PendingIntent.getBroadcast(context,
				requestCode, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP,
				eventTime.getTimeInMillis(), sender);
		Log.i(TAG,
				"Event scheduled for "
						+ eventTime.get(Calendar.HOUR_OF_DAY) + ":"
						+ eventTime.get(Calendar.MINUTE));

	}

	public void setOldRingerMode(int oldRingerMode) {
		this.oldRingerMode = oldRingerMode;
	}

	public SharedPreferences getAlarmPreferences()
	{
		return alarmPreferences;
	}
	
}
