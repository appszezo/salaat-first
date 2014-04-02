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

package org.hicham.salaat;

import static org.arabic.ArabicUtilities.reshapeSentence;

import java.util.Calendar;
import java.util.Locale;

import org.arabic.ArabicUtilities;
import org.hicham.salaat.alarm.EventsHandler;
import org.hicham.salaat.db.DbAdapter;
import org.hicham.salaat.gcm.GCMIntentService;
import org.hicham.salaat.gcm.PushNotificationDialog;
import org.hicham.salaat.location.LocationRefresher;
import org.hicham.salaat.settings.CitySettings;
import org.hicham.salaat.settings.Keys;
import org.hicham.salaat.settings.Keys.DefaultValues;
import org.hicham.salaat.ui.activities.MainActivity;
import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.app.Application;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.preference.SharedPreferences.OnSharedPreferenceChangeListener;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;

import com.ahmedsoliman.devel.jislamic.DayPrayers;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper.SQLiteAssetException;

public class SalaatFirstApplication extends Application implements
		OnSharedPreferenceChangeListener {
	public static final String TAG = "org.hicham.salaat";
	public static SharedPreferences prefs;
	public static boolean isConfigurationChanged;
	public static boolean isLanguageChanged;
	public static DbAdapter dBAdapter;
	private Locale language;
	private static SalaatFirstApplication lastInstance;

	static {
		ThemeManager.setDefaultTheme(ThemeManager.MIXED);
		//ThemeManager.map(ThemeManager.MIXED, R.style.SalaatFirstTheme);

		// ThemeManager.map(ThemeManager.MIXED,
		// R.style.Holo_Theme_Slider_Light_DarkActionBar);

	}

	public static SalaatFirstApplication getLastInstance() {
		return lastInstance;
	}

	/**
	 * return the name of the prayer given in the parameter
	 * 
	 * @param prayer
	 *            the order of the prayer
	 * @param context
	 *            the context of application
	 * @return
	 */
	public static String getPrayerName(int prayer) {
		switch (prayer) {
		case DayPrayers.FAJR:
			return reshapeSentence(R.string.fajr_name);
		case DayPrayers.CHOROUK:
			return reshapeSentence(R.string.sunrise_name);
		case DayPrayers.DHUHR:
			return reshapeSentence(R.string.dhuhr_name);
		case DayPrayers.ASR:
			return reshapeSentence(R.string.asr_name);
		case DayPrayers.MAGHRIB:
			return reshapeSentence(R.string.maghrib_name);
		case DayPrayers.ICHAA:
			return reshapeSentence(R.string.ishaa_name);
		case DayPrayers.JUMUA:
			return reshapeSentence(R.string.jumua_name);
		}
		return null;
	}

	public Locale getLanguage() {
		return language;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		lastInstance = this;
		prefs = getDefaultSharedPreferences();
		prefs.registerOnSharedPreferenceChangeListener(this);
		refreshLanguage();
		/* Open the database */
		dBAdapter = new DbAdapter(getApplicationContext());
		try
		{
			dBAdapter.open();
		}
		catch(SQLiteAssetException e)
		{
			String error=e.getMessage();
			if(error.contains("space"))
				error="No Space Left on the device, please try uninstalling some applications";
			Intent intent=new Intent(this, PushNotificationDialog.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("message", error);
			startActivity(intent);
			return;
		}
		/* Schedule or update next prayer alarm */
		Log.i(TAG, "Schedule next Event from Application.onCreate");
		scheduleNextPrayerNotification();
				
		if(prefs.getString(Keys.LOCATION_REFRESH_MODE_KEY, DefaultValues.LOCATION_REFRESH_MODE).equals("automatic"))
			activateAutomaticLocationRefresh();
		
		try{
			GCMIntentService.registerAtGCM(this);
		}
		catch(UnsupportedOperationException e)
		{
			Log.e(TAG, e.getMessage());
		}
	}

	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		if (key.equals(Keys.CITY_KEY)) {
			scheduleNextPrayerNotification();
			isConfigurationChanged=true;
			/* clear the value stored of the city name formatted */
			prefs.edit().putString(Keys.CITY_NAME_FORMATTED, "").commit();
		}
		if (key.equals(Keys.LANGUAGE_KEY)) {
			isLanguageChanged = true;
			refreshLanguage();
			/* clear the value stored of the city name formatted */
			prefs.edit().putString(Keys.CITY_NAME_FORMATTED, "").commit();
		}
		if(key.equals(Keys.ORGANIZATION_KEY)||key.equals(Keys.ASR_MADHAB_KEY)
			||key.contains(Keys.TIME_OFFSET_KEY))
		{
			scheduleNextPrayerNotification();
			isConfigurationChanged=true;
		}
		if (key.equals(Keys.CANCEL_ALL_ALARMS_KEY)) {
			if (prefs.getBoolean(Keys.CANCEL_ALL_ALARMS_KEY, false)) {
				new EventsHandler(getBaseContext()).cancelAlarm();
			} else {
				new EventsHandler(getBaseContext()).scheduleNextPrayerEvent(false);
			}
		}
	}
	
	private void activateAutomaticLocationRefresh() {
		Intent intent=new Intent(LocationRefresher.ACTION_LOCATION_REFRESH);
		PendingIntent pi=PendingIntent.getService(this, CitySettings.LOCATION_REFRESH_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) this
				.getSystemService(Context.ALARM_SERVICE);
		Calendar now=Calendar.getInstance();
		alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, now.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pi);
	}


	public void refreshLanguage() {
		String lang = prefs.getString(Keys.LANGUAGE_KEY, DefaultValues.LANGUAGE);
		language = new Locale(lang);
		Locale.setDefault(language);
		Configuration config = new Configuration();
		config.locale = language;
		getResources().updateConfiguration(config,
				getResources().getDisplayMetrics());
		
		/*the system changes sometimes the configuration, so we store the configured locale in this field*/
		ArabicUtilities.lastConfiguration=new Configuration(config);
	}

	public void scheduleNextPrayerNotification() {
		if(prefs.getBoolean(MainActivity.FIRST_RUN_KEY, true))
			return;
		/* Check wether the city exists in database or not */
		if (dBAdapter.getLocation(prefs.getString(Keys.CITY_KEY,
				DefaultValues.CITY)) == null) {
			return; /*
					 * don't handle the next events, the MainActivity will
					 * recall this
					 */
		}
		new EventsHandler(this).scheduleNextPrayerEvent(true);
	}
	
	
}
