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

package org.hicham.salaat.settings;

import static org.arabic.ArabicUtilities.reshapeSentence;

import java.util.Calendar;
import java.util.List;

import org.arabic.ArabicUtilities;
import org.hicham.salaat.R;
import org.hicham.salaat.SalaatFirstApplication;
import org.hicham.salaat.location.LocationRefresher;
import org.hicham.salaat.settings.Keys.DefaultValues;
import org.hicham.salaat.settings.preference.ListPreferenceWithSections;
import org.hicham.salaat.settings.preference.SalaatCheckBoxPreference;
import org.hicham.salaat.ui.dialogs.LocationConfirmDialogFragment.LocationConfirmDialogListener;
import org.hicham.salaat.ui.dialogs.LocationSearchDialogFragment;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.preference.CheckBoxPreference;
import org.holoeverywhere.preference.ListPreference;
import org.holoeverywhere.preference.Preference;
import org.holoeverywhere.preference.Preference.OnPreferenceChangeListener;
import org.holoeverywhere.preference.Preference.OnPreferenceClickListener;
import org.holoeverywhere.preference.PreferenceScreen;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.preference.SharedPreferences.OnSharedPreferenceChangeListener;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;

public class CitySettings extends CustomPreferenceActivity implements
		OnSharedPreferenceChangeListener, LocationConfirmDialogListener {
	
	private class OnCountryChangedListener implements
			OnPreferenceChangeListener {
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			String[] cities = SalaatFirstApplication.dBAdapter.getCities((String) newValue);
			citiesList.setEntries(cities);
			citiesList.setEntryValues(cities);
			return true;
		}
	}
	
	public static final int LOCATION_REFRESH_REQUEST_CODE="refresh".hashCode();
	
	private ListPreference locationRefreshFrequency;
	private ListPreferenceWithSections countriesList;
	private ListPreferenceWithSections citiesList;
	private ListPreference locationRefreshMode;
	private CheckBoxPreference automaticLocation;

	private boolean isAutomaticLocationButtonClicked;

	private void createPreferenceHierarchy() {
		PreferenceScreen root = getPreferenceScreen();
		locationRefreshMode=new ListPreference(this);
		locationRefreshFrequency=new ListPreference(this);
		automaticLocation = new CheckBoxPreference(this);

		if (hasLocationSupport()) {
			
			locationRefreshMode.setEntries(ArabicUtilities.reshapeText(getResources().getStringArray(R.array.location_refresh_mode_entries)));
			locationRefreshMode.setEntryValues(getResources().getStringArray(R.array.location_refresh_mode_entryvalues));
			locationRefreshMode.setTitle(reshapeSentence(R.string.location_refresh_mode_title));
			locationRefreshMode.setSummary(reshapeSentence(R.string.location_refresh_mode_summary));
			locationRefreshMode.setKey(Keys.LOCATION_REFRESH_MODE_KEY);
			locationRefreshMode.setDefaultValue(DefaultValues.LOCATION_REFRESH_MODE);
			root.addPreference(locationRefreshMode);
			
			locationRefreshMode.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					if(newValue.equals("automatic"))
						activateAutomaticLocationRefresh();
					else
						cancelAutomaticLocationRefresh();
					return true;
				}
			});
			
			locationRefreshFrequency.setEntries(ArabicUtilities.reshapeText(getResources().getStringArray(R.array.location_refresh_periods_entries)));
			locationRefreshFrequency.setEntryValues(getResources().getStringArray(R.array.location_refresh_periods_entryvalues));
			locationRefreshFrequency.setKey(Keys.LOCATION_REFRESH_FREQUENCY_KEY);
			locationRefreshFrequency.setDefaultValue(DefaultValues.LOCATION_REFRESH_FREQUENCY);
			locationRefreshFrequency.setTitle(reshapeSentence(R.string.location_refresh_period_title));
			locationRefreshFrequency.setSummary(reshapeSentence(R.string.location_refresh_period_summary));
			locationRefreshFrequency.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					locationRefreshFrequency.setValue((String) newValue);
					activateAutomaticLocationRefresh();
					return true;
				}
			});
			
			root.addPreference(locationRefreshFrequency);
			
			automaticLocation.setKey(Keys.USE_AUTOMATIC_LOCATION_KEY);
			automaticLocation.setDefaultValue(DefaultValues.USE_AUTOMATIC_LOCATION);
			automaticLocation.setTitle(reshapeSentence(R.string.custom_city_title));
			automaticLocation
					.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
						
						public boolean onPreferenceChange(Preference preference, Object newValue) {
							if (newValue.equals(true)) {
								showLocationSearchDialog();
								/*
								 * don't validate the change, it will be handled using the
								 * dialogFragment callbacks
								 */
								return false;
							}
							/* the AutomaticLocation is cancelled, validate the change */
							return true;
						}
					});
			automaticLocation
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						public boolean onPreferenceClick(Preference preference) {
							isAutomaticLocationButtonClicked = true;
							return true;
						}
					});
			root.addPreference(automaticLocation);
		}
		countriesList = new ListPreferenceWithSections(this);
		countriesList.setKey(Keys.COUNTRY_KEY);
		countriesList
				.setTitle(reshapeSentence(R.string.country_settings_title));
		countriesList.setDefaultValue(DefaultValues.COUNTRY);
		countriesList
				.setOnPreferenceChangeListener(new OnCountryChangedListener());
		root.addPreference(countriesList);
		citiesList = new ListPreferenceWithSections(this);
		citiesList.setTitle(reshapeSentence(R.string.city_choice_title));
		citiesList.setKey(Keys.CITY_KEY);
		citiesList.setDefaultValue(DefaultValues.CITY);
		root.addPreference(citiesList);
		ListPreference timeZone = new ListPreference(this);
		timeZone.setKey(Keys.TIME_ZONE_KEY);
		timeZone.setTitle(reshapeSentence(R.string.time_zone_title));
		timeZone.setEntries(new String[] { "GMT-12:00", "GMT-11:00",
				"GMT-10:00", "GMT-9:30", "GMT-9:00", "GMT-8:00", "GMT-7:00",
				"GMT-6:00", "GMT-5:00", "GMT-4:30", "GMT-4:00", "GMT-3:30",
				"GMT-3:00", "GMT-2:00", "GMT-1:00", "GMT", "GMT+1:00",
				"GMT+2:00", "GMT+3:00", "GMT+3:30", "GMT+4:00", "GMT+4:30",
				"GMT+5:00", "GMT+5:30", "GMT+6:00", "GMT+6:30", "GMT+7:00",
				"GMT+8:00", "GMT+9:00", "GMT+9:30", "GMT+10:00", "GMT+10:30",
				"GMT+11:00", "GMT+11:30", "GMT+12:00", "GMT+13:00", "GMT+14:00" });
		timeZone.setEntryValues(new String[] { "-12", "-11", "-10", "-9.5",
				"-9", "-8", "-7", "-6", "-5", "-4.5", "-4", "-3.5", "-3", "-2",
				"-1", "0", "1", "2", "3", "3.5", "4", "4.5", "5", "5.5", "6",
				"6.5", "7", "8", "9", "9.5", "10", "10.5", "11", "11.5", "12",
				"13", "14" });
		timeZone.setDefaultValue(DefaultValues.TIME_ZONE);
		root.addPreference(timeZone);
		SalaatCheckBoxPreference useDSTMode= new SalaatCheckBoxPreference(this);
		useDSTMode.setDefaultValue(DefaultValues.USE_DST_MODE);
		useDSTMode.setKey(Keys.USE_DST_MODE_KEY);
		useDSTMode.setTitle(reshapeSentence(R.string.use_dst_mode_title));
		root.addPreference(useDSTMode);
		if(locationRefreshMode.getValue().equals("manual"))
		{
			root.removePreference(locationRefreshFrequency);
			if (automaticLocation.isChecked()) {
				root.removePreference(countriesList);
				root.removePreference(citiesList);
			}
		}
		else
		{
			root.removePreference(automaticLocation);
			root.removePreference(countriesList);
			root.removePreference(citiesList);
		}
	}

	private void cancelAutomaticLocationRefresh() {
		getPreferenceScreen().removePreference(locationRefreshFrequency);
		getPreferenceScreen().addPreference(automaticLocation);
		getPreferenceScreen().addPreference(countriesList);
		getPreferenceScreen().addPreference(citiesList);

		Intent intent=new Intent(LocationRefresher.ACTION_LOCATION_REFRESH);
		PendingIntent pi=PendingIntent.getService(this, LOCATION_REFRESH_REQUEST_CODE, intent, PendingIntent.FLAG_NO_CREATE);
		if(pi!=null)
			{
			AlarmManager alarmManager = (AlarmManager) this
				.getSystemService(Context.ALARM_SERVICE);
			alarmManager.cancel(pi);
		}
	}

	private void activateAutomaticLocationRefresh() {
		getPreferenceScreen().addPreference(locationRefreshFrequency);
		getPreferenceScreen().removePreference(automaticLocation);
		getPreferenceScreen().removePreference(countriesList);
		getPreferenceScreen().removePreference(citiesList);

		Intent intent=new Intent(LocationRefresher.ACTION_LOCATION_REFRESH);
		PendingIntent pi=PendingIntent.getService(this, LOCATION_REFRESH_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) this
				.getSystemService(Context.ALARM_SERVICE);
		Calendar now=Calendar.getInstance();
		int repeatDelay=Integer.parseInt(locationRefreshFrequency.getValue());
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, now.getTimeInMillis(), 1000*60*repeatDelay, pi);
	}

	private boolean hasLocationSupport() {
		LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		List<String> list = manager.getAllProviders();
		if (manager == null || list.isEmpty())
			return false;
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle(
				reshapeSentence(R.string.city_settings_title));

		// addPreferencesFromResource(R.xml.settings);
		setPreferenceScreen(getPreferenceManager().createPreferenceScreen(this));
		createPreferenceHierarchy();
		String[] countries = SalaatFirstApplication.dBAdapter.getCountries();
		countriesList.setEntries(countries);
		countriesList.setEntryValues(countries);
		countriesList
				.setOnPreferenceChangeListener(new OnCountryChangedListener());
		String[] cities = SalaatFirstApplication.dBAdapter.getCities(countriesList.getValue());
		citiesList.setEntries(cities);
		citiesList.setEntryValues(cities);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		if (automaticLocation.isChecked()) {
			MenuItem refreshGps = menu.add(0, 1, 0,
					reshapeSentence(R.string.refresh_gps));
			MenuItemCompat.setShowAsAction(refreshGps,
					MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
			refreshGps.setIcon(R.drawable.ic_action_refresh);
		}
		return true;
	}

	public void onLocationConfirmed() {
		if (isAutomaticLocationButtonClicked) {
			automaticLocation.setChecked(true);
			isAutomaticLocationButtonClicked = false;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case 1:
			showLocationSearchDialog();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(!citiesList.getValue().equals("custom"))
			citiesList.setSummary(citiesList.getValue());
		countriesList.setSummary(countriesList.getValue());
		getPreferenceManager().getSharedPreferences()
		.registerOnSharedPreferenceChangeListener(this);

	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Preference pref = findPreference(key);

		if (pref instanceof ListPreferenceWithSections) {
			ListPreferenceWithSections listPref = (ListPreferenceWithSections) pref;
			pref.setSummary(listPref.getEntry());
		}

		if (pref instanceof CheckBoxPreference && pref.getKey().equals(Keys.USE_AUTOMATIC_LOCATION_KEY)) {
			if (((CheckBoxPreference) pref).isChecked()) {
				getPreferenceScreen().removePreference(countriesList);
				getPreferenceScreen().removePreference(citiesList);
			} else {
				getPreferenceScreen().addPreference(countriesList);
				getPreferenceScreen().addPreference(citiesList);
			}
			supportInvalidateOptionsMenu();
		}

	}
	

	
	@Override
	protected void onStop() {
		super.onStop();
		getPreferenceManager().getSharedPreferences()
		.unregisterOnSharedPreferenceChangeListener(this);

	}

	@SuppressWarnings("deprecation")
	private void showLocationSearchDialog() {
		DialogFragment dialog = new LocationSearchDialogFragment();
		dialog.show(this);
	}

}
