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

import static com.ahmadiv.dari.DariGlyphUtils.reshapeText;
import static java.lang.Math.floor;
import static org.hicham.salaat.PrayerTimesActivity.isReshapingNessecary;

import java.util.Calendar;
import java.util.Locale;

import org.hicham.salaat.calculating.PrayerTimesCalculator;
import org.hicham.salaat.db.DbAdapter;
import org.hicham.salaat.settings.Keys;
import org.hicham.salaat.settings.Settings;
import org.hicham.salaat.widget.PrayerAppWidgetProvider;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

public class WidgetService extends Service implements OnSharedPreferenceChangeListener{
	private PrayerTimesCalculator calculator;
	private int nextPrayer;
	protected static final long SLEEP_DELAY = 60000; //1 minute

	//private int oldRingerMode=-1;
	
	/**
	 * The main timer for the service, handle events and update the widget
	 */
	Thread eventsHandler=new Thread() {
	
		
		public void run()
		{
			while(true)
			{	
			
			performCalculsAndUpdate();
			try
			{
				Thread.sleep(SLEEP_DELAY);
			}
			catch(InterruptedException e)
			{
				Log.i(PrayerTimesActivity.TAG, e.toString());
			}
			}
		}
	};
	
	private void performCalculsAndUpdate()
	{
		nextPrayer = calculator.getNextPrayer();
		Log.i(PrayerTimesActivity.TAG, "service is running");
		int previousPrayer=0;
		nextPrayer=calculator.getNextPrayer();
		if(nextPrayer==PrayerTimesCalculator.FAJR)
			previousPrayer=PrayerTimesCalculator.ICHAA;
		else if(nextPrayer==PrayerTimesCalculator.DHUHR)
			previousPrayer=PrayerTimesCalculator.FAJR;
		else
			previousPrayer=nextPrayer-1;
		Calendar cal=Calendar.getInstance();
		double nextPrayerTime=calculator.getPrayerTime(nextPrayer);
		double previousPrayerTime=calculator.getPrayerTime(previousPrayer);
		double now=cal.get(Calendar.HOUR_OF_DAY)+(double)cal.get(Calendar.MINUTE)/60;
		updateWidget(nextPrayer, previousPrayer, nextPrayerTime-now, nextPrayerTime-previousPrayerTime);
	}
	
	private void updateWidget(int nextPrayer, int previousPrayer, double remainigTime, double totalDifferenceTime)
	{
		remainigTime=fixhour(remainigTime);
		totalDifferenceTime=fixhour(totalDifferenceTime);
		// Build the widget update for today
		RemoteViews updateViews = new RemoteViews(WidgetService.this.getPackageName(), R.layout.widgetlayout);
		if(isReshapingNessecary) //arab
		{
			String nextPrayer_=" :"+reshapeText(WidgetService.this.getString(R.string.next_prayer_text),isReshapingNessecary);
			updateViews.setTextViewText(R.id.prayerName2, nextPrayer_);
			updateViews.setTextViewText(R.id.prayerName, PrayerTimesCalculator.getPrayerName(nextPrayer, WidgetService.this));
			updateViews.setTextColor(R.id.prayerName, 0xFFFFFFCC);
			updateViews.setTextViewText(R.id.remainingTime, 
					calculator.floatToStr(remainigTime)+
					" :"+reshapeText(WidgetService.this.getString(R.string.remaining_time), isReshapingNessecary));

		}
		else
		{
			String nextPrayer_=WidgetService.this.getString(R.string.next_prayer_text)+": ";
			updateViews.setTextViewText(R.id.prayerName, nextPrayer_);
			updateViews.setTextViewText(R.id.prayerName2, PrayerTimesCalculator.getPrayerName(nextPrayer, WidgetService.this));
			updateViews.setTextColor(R.id.prayerName2, 0xFFFFFFCC);
			updateViews.setTextViewText(R.id.remainingTime, 
					WidgetService.this.getString(R.string.remaining_time)+": "
					+calculator.floatToStr(remainigTime));
		}
		
		updateViews.setProgressBar(R.id.remainingTimeProgressBar, (int)(totalDifferenceTime*60), (int)((totalDifferenceTime-remainigTime)*60), false);
		Log.d(PrayerTimesActivity.TAG, "updating widget");
        
		Intent intent=new Intent(this, WidgetService.class);
		PendingIntent pi=PendingIntent.getService(this, 0, intent, 0);
		updateViews.setOnClickPendingIntent(R.id.widgetLayout, pi);

        // Push update for this widget to the home screen
        ComponentName thisWidget = new ComponentName(WidgetService.this, PrayerAppWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(WidgetService.this);
        manager.updateAppWidget(thisWidget, updateViews);
        Log.d(PrayerTimesActivity.TAG, "widget updated");
	}
	
	private double fixhour(double a) {
        a = a - 24.0 * floor(a / 24.0);
        a = a < 0 ? (a + 24) : a;
        return a;
    }
	
	@Override
	public void onCreate() {
		Log.i(PrayerTimesActivity.TAG, "Updating calculator");
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);
    	String language=pref.getString(Keys.LANGUAGE_KEY, "ar");
        Locale locale=new Locale(language);
        Locale.setDefault(locale);
        Configuration config=new Configuration();
        config.locale=locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
 		if(language.equalsIgnoreCase("ar"))
 			//determine if language needs reshaping
 			isReshapingNessecary=true;
 		else
 			isReshapingNessecary=false;
		int asrMadhab=Integer.parseInt(pref.getString(Keys.ASR_MADHAB_KEY, "0"));
    	boolean isCustomCitySelected=pref.getBoolean(Keys.CUSTOM_CITY_KEY, false);
    	double timeZone=Double.parseDouble(pref.getString(Keys.TIME_ZONE_KEY, "0"));
    	String city;
    	if(isCustomCitySelected)
    		city="custom";
    			
    	else
    	city=pref.getString(Keys.CITY_KEY, "Rabat et Salé");
    	DbAdapter dbAdapter=new DbAdapter(this);
    	dbAdapter.open();
    	double[] coordonnees=dbAdapter.getLocation(city); //{latitude, longitude,altitude}
    	double[] offsets=new double[6];
    	for(int i=0;i<6;i++)
    	{
    		offsets[i]=(double)(pref.getInt(Keys.TIME_OFFSET_KEY+i, 0))/60;
    	}
    	calculator=new PrayerTimesCalculator(coordonnees[1], coordonnees[0], Settings.getOrganization(this), timeZone, asrMadhab, coordonnees[2], offsets);
    	calculator.performCalculs(Calendar.getInstance());
    	dbAdapter.close();// closing database
    	eventsHandler.start();
    	Calendar date=Calendar.getInstance();
    	date.set(Calendar.HOUR_OF_DAY, 24);
    	date.set(Calendar.MINUTE, 00);
		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
	}
	
	private void updateCalculator()
	{
		Log.i(PrayerTimesActivity.TAG, "Updating calculator");
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);
    	int asrMadhab=Integer.parseInt(pref.getString(Keys.ASR_MADHAB_KEY, "0"));
    	boolean isCustomCitySelected=pref.getBoolean(Keys.CUSTOM_CITY_KEY, false);
    	double timeZone=Double.parseDouble(pref.getString(Keys.TIME_ZONE_KEY, "0"));
    	String city;
    	if(isCustomCitySelected)
    		city="custom";
    	else
    		city=pref.getString(Keys.CITY_KEY, "Rabat et Salé");
    	DbAdapter dbAdapter=new DbAdapter(this);
    	dbAdapter.open();
    	double[] coordonnees=dbAdapter.getLocation(city); //{latitude, longitude,altitude}
    	dbAdapter.close();
    	//updating the calculator
    	calculator.setAsrMadhab(asrMadhab);
    	calculator.setOrganization(Settings.getOrganization(this));
    	calculator.setLatitude(coordonnees[0]);
    	calculator.setLongitude(coordonnees[1]);
    	calculator.setElev(coordonnees[2]);
    	calculator.setTimeZone(timeZone);
    	//updating prayer times
    	calculator.performCalculs(Calendar.getInstance());
	}
	

	
	//for compatibility with OSs before 2.0
	@Override
	public void onStart(Intent intent, int startId) {
		performCalculsAndUpdate();
		Log.i(PrayerTimesActivity.TAG, "Service started");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		performCalculsAndUpdate();
		Log.i(PrayerTimesActivity.TAG, "Service started");
		return START_STICKY;
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		updateCalculator();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
