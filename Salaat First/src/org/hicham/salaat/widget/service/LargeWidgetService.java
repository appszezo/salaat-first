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

package org.hicham.salaat.widget.service;

import static org.arabic.ArabicUtilities.reshapeSentence;
import static org.hicham.salaat.SalaatFirstApplication.TAG;

import java.util.Calendar;

import org.hicham.salaat.R;
import org.hicham.salaat.SalaatFirstApplication;
import org.hicham.salaat.calculating.PrayerTimesCalcluationUtils;
import org.hicham.salaat.settings.Keys;
import org.hicham.salaat.settings.Keys.DefaultValues;
import org.hicham.salaat.widget.LargeWidgetProvider;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.IBinder;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.ahmedsoliman.devel.jislamic.DayPrayers;
import com.ahmedsoliman.devel.jislamic.hijri.HijriCalendar;

public class LargeWidgetService extends Service implements OnSharedPreferenceChangeListener{
	
	public static final int SERVICE_REQUEST_CODE=2;
	protected int nextPrayer;
	private ScreenOnReceiver screenEventsReceiver;
	protected static final long SLEEP_DELAY = 60000; // 1 minute

	/**
	 * The main timer for the service, handle events and update the widget
	 */
	Thread eventsHandler = new Thread() {

		@Override
		public void run() {
			while (true) {

				performCalculsAndUpdate();
				try {
					Thread.sleep(SLEEP_DELAY);
				} catch (InterruptedException e) {
					Log.i(TAG, e.toString());
				}
			}
		}
	};

	protected long fixhour(long a) {
		a = a < 0 ? (a + DateUtils.DAY_IN_MILLIS / 1000) : a;
		return a;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void onCreate() {
		Log.i(TAG, "Updating calculator");
		eventsHandler.start();
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "stopping "+this.getClass().getName());
		if(screenEventsReceiver!=null)
		{
			try
			{
				unregisterReceiver(screenEventsReceiver);
			}
			catch(Exception e)
			{
				
			}
		}
		super.onDestroy();
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if(key.equals(Keys.CITY_KEY)
			||key.equals(Keys.ORGANIZATION_KEY)
			||key.equals(Keys.ASR_MADHAB_KEY)
			||key.equals(Keys.USE_DST_MODE_KEY)
			||key.equals(Keys.LANGUAGE_KEY))
			
			performCalculsAndUpdate();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		performCalculsAndUpdate();
		if(screenEventsReceiver==null)
			screenEventsReceiver=new ScreenOnReceiver();
		registerReceiver(screenEventsReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
		SalaatFirstApplication.prefs.registerOnSharedPreferenceChangeListener(this);
		Log.i(TAG, "Service started");
		return START_STICKY;
	}
	
	protected void performCalculsAndUpdate() {
		// TODO Auto-generated method stub
	DayPrayers prayers = PrayerTimesCalcluationUtils
			.getCurrentPrayerTimes();
	nextPrayer = prayers.getNextPrayer();
	Log.i(TAG, "service is running");
	int previousPrayer = 0;
	if (nextPrayer == DayPrayers.FAJR)
		previousPrayer = DayPrayers.ICHAA;
	else if (nextPrayer == DayPrayers.DHUHR)
		previousPrayer = DayPrayers.FAJR;
	else
		previousPrayer = nextPrayer - 1;
	Calendar cal = Calendar.getInstance();
	long nextPrayerTime = prayers.getPrayers()[nextPrayer].getPrayerTimeAsCalendar().getTimeInMillis()/1000;
	long previousPrayerTime = prayers.getPrayers()[previousPrayer]
			.getPrayerTimeAsCalendar().getTimeInMillis()/1000;
	long now = cal.getTimeInMillis() / 1000;
	updateWidget(nextPrayer, prayers, nextPrayerTime - now,
			nextPrayerTime - previousPrayerTime);
}

private void updateWidget(int nextPrayer, DayPrayers prayersTimes,
		long remainigTime, long totalDifferenceTime) {
	Log.d(TAG, "updating large widget");
	SalaatFirstApplication.getLastInstance().refreshLanguage();
	String lang = SalaatFirstApplication.prefs.getString(Keys.LANGUAGE_KEY,
			DefaultValues.LANGUAGE);
	int layoutId=lang.contains("ar")?R.layout.large_widget_layout_ar:R.layout.large_widget_layout;
	remainigTime = fixhour(remainigTime);
	totalDifferenceTime = fixhour(totalDifferenceTime);
	// Build the widget update for today
	RemoteViews updateViews = new RemoteViews(
			this.getPackageName(), layoutId);
	updateViews.setTextViewText(R.id.nextPrayer, reshapeSentence(R.string.next_prayer_text)+": ");
	updateViews.setTextViewText(R.id.nextPrayerName, SalaatFirstApplication.getPrayerName(nextPrayer));
	updateViews.setTextViewText(R.id.remainingTime, "-"+PrayerTimesCalcluationUtils.floatToStr(remainigTime));	
	Calendar todayMiladi=Calendar.getInstance();
	Calendar todayHijri=new HijriCalendar(todayMiladi);
	String dayName=reshapeSentence(getResources().getStringArray(R.array.days)[todayMiladi.get(Calendar.DAY_OF_WEEK)-1]);
	String month=reshapeSentence(SalaatFirstApplication.getLastInstance().getResources().getStringArray(R.array.months_hijri)[todayHijri.get(Calendar.MONTH)]);	
	updateViews.setTextViewText(R.id.dayname, dayName);
	updateViews.setTextViewText(R.id.dayofmonth, " " + todayHijri.get(Calendar.DAY_OF_MONTH) + " ");
	updateViews.setTextViewText(R.id.month, " " + month + " ");
	updateViews.setTextViewText(R.id.year, ""+todayHijri.get(Calendar.YEAR));	
		
	updateViews.setProgressBar(R.id.progressBar1,
			(int) (totalDifferenceTime * 60),
			(int) ((totalDifferenceTime - remainigTime) * 60), false);
	
	String[] prayerNames=getResources().getStringArray(R.array.prayer_short_names);
	updateViews.setTextViewText(R.id.fajr, reshapeSentence(prayerNames[0]));
	updateViews.setTextViewText(R.id.duhr, reshapeSentence(prayerNames[2]));
	updateViews.setTextViewText(R.id.asr, reshapeSentence(prayerNames[3]));
	updateViews.setTextViewText(R.id.maghrib, reshapeSentence(prayerNames[4]));
	updateViews.setTextViewText(R.id.ichaa, reshapeSentence(prayerNames[5]));
	
	updateViews.setTextViewText(R.id.fajrTime, prayersTimes.fajr().toString());
	updateViews.setTextViewText(R.id.duhrTime, prayersTimes.duhr().toString());
	updateViews.setTextViewText(R.id.asrTime, prayersTimes.assr().toString());
	updateViews.setTextViewText(R.id.maghribTime, prayersTimes.maghrib().toString());
	updateViews.setTextViewText(R.id.ichaaTime, prayersTimes.ishaa().toString());

	
	
	Intent intent = new Intent(this, LargeWidgetService.class);
	PendingIntent pi = PendingIntent.getService(this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	updateViews.setOnClickPendingIntent(R.id.largeWidgetLayout, pi);

	// Push update for this widget to the home screen
	ComponentName largeWidget = new ComponentName(this,
			LargeWidgetProvider.class);
	AppWidgetManager manager = AppWidgetManager
			.getInstance(this);
	//int[] ids=manager.getAppWidgetIds(largeWidget);
	manager.updateAppWidget(largeWidget, updateViews);
	Log.d(TAG, "large widget updated");
}

private class ScreenOnReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
			{
				performCalculsAndUpdate();
			}
		}
	}
}



}
