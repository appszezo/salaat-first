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

package org.hicham.salaat.settings;

import static com.ahmadiv.dari.DariGlyphUtils.reshapeText;
import static org.hicham.salaat.PrayerTimesActivity.isReshapingNessecary;

import java.util.List;

import org.hicham.salaat.MainActivity;
import org.hicham.salaat.PrayerTimesActivity;
import org.hicham.salaat.R;
import org.hicham.salaat.cities.LocationController;
import org.hicham.salaat.cities.LocationListenerExtended;
import org.hicham.salaat.db.DbAdapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class CitySettings extends PreferenceActivity implements OnSharedPreferenceChangeListener, LocationListenerExtended{
	public static final int WAITING_GPS=1;
	public static final int CONFIRM_LOCATION=2;

	
	private Bundle prefs;
	private ListPreference countriesList;
	private ListPreference citiesList;
	private CheckBoxPreference customCity;
	private LocationController controller;
	private boolean isDialogPreviouslyShown;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//addPreferencesFromResource(R.xml.settings);
		setPreferenceScreen(createPreferenceHierarchy());
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		DbAdapter dbAdabter=new DbAdapter(this);
		dbAdabter.open();
		String[] countries=dbAdabter.getCountries();
		countriesList.setEntries(countries);
		countriesList.setEntryValues(countries);
		countriesList.setOnPreferenceChangeListener(new OnCountryChangedListener());
		String[] cities=dbAdabter.getCities(countriesList.getValue());
		citiesList.setEntries(cities);
		citiesList.setEntryValues(cities);
		dbAdabter.close();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		if(customCity.isChecked())
    		{
    			MenuItem refreshGps=menu.add(0,1,0,reshapeText(getString(R.string.refresh_gps),isReshapingNessecary));
    			refreshGps.setIcon(R.drawable.refresh);
    		}
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// TODO Auto-generated method stub
    	switch(item.getItemId())
    	{
    	case 1:
    		showDialog(WAITING_GPS);
    		if(controller!=null) //controller shouldn't be null here
    			controller.requestUpdates();
    		break;
    	}
    	return super.onOptionsItemSelected(item);
    }
	
	@Override
	protected void onResume() {
		super.onResume();
		citiesList.setSummary(citiesList.getValue());
		countriesList.setSummary(countriesList.getValue());
	}

	private PreferenceScreen createPreferenceHierarchy() {
		PreferenceScreen root=getPreferenceManager().createPreferenceScreen(this);
		countriesList=new ListPreference(this);
		countriesList.setKey(Keys.COUNTRY_KEY);
		countriesList.setTitle(reshapeText(getString(R.string.country_settings_title), isReshapingNessecary));
		countriesList.setDefaultValue("Morocco");
		countriesList.setOnPreferenceChangeListener(new OnCountryChangedListener());
		root.addPreference(countriesList);
		citiesList=new ListPreference(this);
		citiesList.setTitle(reshapeText(getString(R.string.city_choice_title),isReshapingNessecary));
		citiesList.setKey(Keys.CITY_KEY);
		citiesList.setDefaultValue("Rabat et Salé");
		root.addPreference(citiesList);
		//don't show it if device don't have any location provider
		if(hasLocationSupport())
		{customCity=new CheckBoxPreference(this);
		customCity.setKey(Keys.CUSTOM_CITY_KEY);
		customCity.setTitle(reshapeText(getString(R.string.custom_city_title), isReshapingNessecary));
		customCity.setOnPreferenceClickListener(new OnCustomCityPreferenceClickListener());
		root.addPreference(customCity);
		if(customCity.isChecked())
		{
			citiesList.setEnabled(false);
			countriesList.setEnabled(false);
		}
		}
		ListPreference timeZone=new ListPreference(this);
		timeZone.setKey(Keys.TIME_ZONE_KEY);
		timeZone.setTitle(reshapeText(getString(R.string.time_zone_title), isReshapingNessecary));
		timeZone.setSummary(reshapeText(getString(R.string.time_zone_summary), isReshapingNessecary));
		timeZone.setEntries(new String[]{
				"GMT-12:00","GMT-11:00","GMT-10:00","GMT-9:30","GMT-9:00","GMT-8:00",
				"GMT-7:00","GMT-6:00","GMT-5:00","GMT-4:30","GMT-4:00","GMT-3:30","GMT-3:00","GMT-2:00",
				"GMT-1:00","GMT","GMT+1:00","GMT+2:00","GMT+3:00","GMT+3:30","GMT+4:00","GMT+4:30","GMT+5:00",
				"GMT+5:30","GMT+6:00","GMT+6:30","GMT+7:00","GMT+8:00","GMT+9:00","GMT+9:30",
				"GMT+10:00","GMT+10:30","GMT+11:00","GMT+11:30","GMT+12:00","GMT+13:00","GMT+14:00"
		});
		timeZone.setEntryValues(new String[]{
				"-12","-11","-10","-9.5","-9","-8",
				"-7","-6","-5","-4.5","-4","-3.5","-3","-2",
				"-1","0","1","2","3","3.5","4","4.5","5",
				"5.5","6","6.5","7","8","9","9.5",
				"10","10.5","11","11.5","12","13","14"
		});
		root.addPreference(timeZone);
		return root;
	}

	public void onLocationChanged(Location location) {
		if (isDialogPreviouslyShown) {
			try
			{	
				dismissDialog(WAITING_GPS);
				prefs = new Bundle();
				prefs.putDouble("longitude", location.getLongitude());
				prefs.putDouble("latitude", location.getLatitude());
				prefs.putDouble("altitude", location.getAltitude());
				showDialog(CONFIRM_LOCATION);
			}
			catch(IllegalArgumentException e)
			{
				Log.e(PrayerTimesActivity.TAG, "IllegalArgumentException, no dialog previously shown");
			}
			controller.removeLocationUpdates();
		}
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	public void GpsIsDisabled() {
		dismissDialog(WAITING_GPS);
		Toast.makeText(this, reshapeText(getString(R.string.gps_disabled), isReshapingNessecary), Toast.LENGTH_SHORT).show();
		customCity.setChecked(false);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog=null;
		switch(id)
		{
		case WAITING_GPS:
			isDialogPreviouslyShown=true;
			dialog=new ProgressDialog(this);
			dialog.setOnCancelListener(new OnCancelListener() {
				
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					customCity.setChecked(false);
					dialog.dismiss();
				}
			});
			((ProgressDialog)dialog).setMessage(reshapeText(getString(R.string.wait_gps),isReshapingNessecary));
			dialog.show();
			break;
		case CONFIRM_LOCATION:
			AlertDialog.Builder adbuilder=new AlertDialog.Builder(this);
			adbuilder.setMessage(reshapeText(getString(R.string.confirming_gps_data), isReshapingNessecary));
			adbuilder.setPositiveButton(reshapeText(getString(R.string.confirm),isReshapingNessecary), null);
			adbuilder.setNegativeButton(reshapeText(getString(R.string.cancel), isReshapingNessecary), null);
			dialog=adbuilder.create();
			break;
		}
		return dialog;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if(id==CONFIRM_LOCATION)
		{
			((AlertDialog)dialog).setButton(DialogInterface.BUTTON_POSITIVE, reshapeText(getString(R.string.confirm),isReshapingNessecary), new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				DbAdapter dbAdapter=new DbAdapter(CitySettings.this);
				dbAdapter.open();
				dbAdapter.setCustomCity(prefs.getDouble("longitude"), prefs.getDouble("latitude"), prefs.getDouble("altitude"));
				dbAdapter.close();
				dialog.dismiss();
			}
		});
		((AlertDialog)dialog).setButton(DialogInterface.BUTTON_NEGATIVE, reshapeText(getString(R.string.cancel), isReshapingNessecary), new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		dialog.setOnCancelListener(new OnCancelListener() {
			
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				customCity.setChecked(false);
			}
		});
		dialog.show();}
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Preference pref = findPreference(key);

	    if (pref instanceof ListPreference) {
	        ListPreference listPref = (ListPreference) pref;
	        pref.setSummary(listPref.getEntry());
	    }

		
		if(pref instanceof CheckBoxPreference)
	    {
	    	if(((CheckBoxPreference) pref).isChecked())
	    	{
	    		countriesList.setEnabled(false);
	    		citiesList.setEnabled(false);
	    	}
	    	else
	    	{
	    		countriesList.setEnabled(true);
	    		citiesList.setEnabled(true);
	    	}
	    }
		
	}

	private class OnCountryChangedListener implements OnPreferenceChangeListener
	{
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			DbAdapter dbAdapter=new DbAdapter(CitySettings.this);
			dbAdapter.open();
			String[] cities=dbAdapter.getCities((String)newValue);
			citiesList.setEntries(cities);
			citiesList.setEntryValues(cities);
			dbAdapter.close();
			return true;
		}
	}

	private class OnCustomCityPreferenceClickListener implements OnPreferenceClickListener
	{
		public boolean onPreferenceClick(Preference preference) {
			if(((CheckBoxPreference)preference).isChecked())
			{	
				showDialog(WAITING_GPS);
				controller=new LocationController(CitySettings.this, CitySettings.this);
				controller.requestUpdates();
			}
			return true;
		}
	}
	
	private boolean hasLocationSupport()
	{
		LocationManager manager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
		List<String> list=manager.getAllProviders();
		if(manager==null||list.isEmpty())
			return false;
		return true;
	}
	
}
