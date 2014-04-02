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

import java.util.Calendar;

import org.hicham.salaat.PrayerTimesActivity;
import org.hicham.salaat.calculating.PrayerTimesCalculator;
import org.hicham.salaat.db.DbAdapter;
import org.hicham.salaat.settings.Keys;
import org.hicham.salaat.settings.Settings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * schedule events: show prayer activity, activating silent ....
 * 1st version without optimization, we use alarm for scheduling all events, including
 * events non activated.
 */

public class EventsHandler {
	
	public final static int SHOW_NOTIIFCATION=0;
	public final static int SHOW_ADHAN=1;
	public final static int ACTIVATING_SILENT=2;
	public final static int DEACTIVATING_SILENT=3;
	public final static String EVENT_TYPE_KEY="event type";
	public static final String PRAYER_NAME_KEY="prayer_name";
	private PrayerTimesCalculator calculator;
	private Context context;
	private final static int REQUEST_CODE=91823;
	private int oldRingerMode=-1;
	
	public EventsHandler(Context context)
	{
		this.context=context;
	}
	/**
	 * 0 ----> fajr
	 * 1 ----> Dhuhr
	 * 2 ----> Asr
	 * 3 ----> Maghrib
	 * 4 ----> Ichaa
	 * -1 ----> starting the application
	 * 
	 * @param previousPrayer an integer representing the previous prayer
	 */
	public void scheduleNextEvent(int nextPrayer, int previousEvent)
	{
		initCalculator();
		int previousPrayer;
		int nextEvent;
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		prefs.edit().putBoolean("Event_scheduled", true).commit();
		Calendar nextEventTime=Calendar.getInstance();
		if(nextPrayer==-1)
			nextPrayer=calculator.getNextPrayer();
		if(nextPrayer==PrayerTimesCalculator.FAJR)
			previousPrayer=PrayerTimesCalculator.ICHAA;
		else if(nextPrayer==PrayerTimesCalculator.DHUHR)
			previousPrayer=PrayerTimesCalculator.FAJR;
		else
			previousPrayer=nextPrayer-1;
		/*else if(previousPrayer==4)
			nextPrayer=0;
		else nextPrayer=previousPrayer+1;*/
		if(previousEvent==DEACTIVATING_SILENT||previousEvent==-1)//schedule basing on the nextPrayerTime
		{
			double nextPrayerTime=calculator.getPrayerTime(nextPrayer);
			//nextEventTime.setTimeInMillis(round(nextPrayerTime * 60 * 60 * 1000));
			nextEventTime.set(Calendar.HOUR_OF_DAY, (int)Math.floor(nextPrayerTime));
			nextEventTime.set(Calendar.MINUTE, (int)((nextPrayerTime-Math.floor(nextPrayerTime))*60));
			Calendar currentTime_1=Calendar.getInstance();
			currentTime_1.add(Calendar.MINUTE, -1);//add a minute to be sure that this is fajr
			if(nextEventTime.before(currentTime_1))//we schedule adhan for fajr prayer after ichaa prayer
				nextEventTime.add(Calendar.DAY_OF_MONTH, 1);
			nextEventTime.add(Calendar.MINUTE, -5);
			if(nextEventTime.after(Calendar.getInstance()))//show notification
				{
					nextEvent=SHOW_NOTIIFCATION;
					Log.i(PrayerTimesActivity.TAG,"Show notification scheduling");
				}
			else //show adhan directly
			{
				nextEvent=SHOW_ADHAN;
				nextEventTime.add(Calendar.MINUTE, 5);
				Log.i(PrayerTimesActivity.TAG,"Show next adhan scheduling");
			}
		}
		else //schedule using the previous event
		{
			if(previousEvent==SHOW_NOTIIFCATION)
				{
					nextEventTime.add(Calendar.MINUTE, 5);
					nextEvent=SHOW_ADHAN;
					Log.i(PrayerTimesActivity.TAG,"Show next adhan scheduling");
				}
			else if(previousEvent==SHOW_ADHAN)
			{
				int activateSilent=prefs.getInt(Keys.TIME_BEFORE_SILENT_KEY+previousPrayer, 5);
				nextEventTime.add(Calendar.MINUTE, activateSilent);
				nextEvent=ACTIVATING_SILENT;
				Log.i(PrayerTimesActivity.TAG,"Activating silent scheduling");
			}
			else
			{
				int deactivateSilent=prefs.getInt(Keys.TIME_AFTER_SILENT_KEY+previousPrayer, 25);;
				nextEventTime.add(Calendar.MINUTE, deactivateSilent);
				nextEvent=DEACTIVATING_SILENT;
				Log.i(PrayerTimesActivity.TAG,"Deactivating silent scheduling");
			}
		}
		Intent intent = new Intent(context, AlarmReceiver.class);
		intent.putExtra(EVENT_TYPE_KEY, nextEvent);
		intent.putExtra(PRAYER_NAME_KEY, nextPrayer);
		if(nextEvent==DEACTIVATING_SILENT)
			intent.putExtra("OldRingerMode", oldRingerMode);
		PendingIntent sender = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, nextEventTime.getTimeInMillis(), sender);
		Log.i(PrayerTimesActivity.TAG, "Event scheduled for "+nextEventTime.get(Calendar.HOUR_OF_DAY)+":"+nextEventTime.get(Calendar.MINUTE));
	}
	
	private void initCalculator() {
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(context);
    	int asrMadhab=Integer.parseInt(pref.getString(Keys.ASR_MADHAB_KEY, "0"));
    	boolean isCustomCitySelected=pref.getBoolean(Keys.CUSTOM_CITY_KEY, false);
    	double timeZone=Double.parseDouble(pref.getString(Keys.TIME_ZONE_KEY, "0"));
    	String city;
    	if(isCustomCitySelected)
    		city="custom";
    			
    	else
    		city=pref.getString(Keys.CITY_KEY, "Rabat et Salé");
    	DbAdapter dbAdapter=new DbAdapter(context);
    	dbAdapter.open();
    	double[] coordonnees=dbAdapter.getLocation(city); //{latitude, longitude,altitude}
    	double[] offsets=new double[6];
    	for(int i=0;i<6;i++)
    	{
    		offsets[i]=(double)(pref.getInt(Keys.TIME_OFFSET_KEY+i, 0))/60;
    	}
    	calculator=new PrayerTimesCalculator(coordonnees[1], coordonnees[0], Settings.getOrganization(context), timeZone, asrMadhab, coordonnees[2], offsets);
    	calculator.performCalculs(Calendar.getInstance());
    	dbAdapter.close();
	}

	public void cancelAlarm()
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		prefs.edit().putBoolean("Event_scheduled", false).commit();
		Intent intent = new Intent(context, AlarmReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}
	public void setOldRingerMode(int oldRingerMode) {
		this.oldRingerMode = oldRingerMode;
	}
	public int getOldRingerMode() {
		return oldRingerMode;
	}
}
