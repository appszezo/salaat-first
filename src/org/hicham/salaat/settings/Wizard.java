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

package org.hicham.salaat.settings;

import static com.ahmadiv.dari.DariGlyphUtils.reshapeText;
import static org.hicham.salaat.PrayerTimesActivity.isReshapingNessecary;

import java.util.List;

import org.hicham.alarm.EventsHandler;
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
import android.content.res.Configuration;
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
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.text.InputType;
import android.widget.Toast;

public class Wizard extends PreferenceActivity implements OnSharedPreferenceChangeListener, LocationListenerExtended{
	
	public static final int WAITING_GPS=1;
	public static final int CONFIRM_LOCATION=2;

	
	private Bundle prefs;
	private ListPreference countriesList;
	private ListPreference citiesList;
	private CheckBoxPreference customCity;
	private LocationController controller;
	private boolean isDialogPreviouslyShown;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//addPreferencesFromResource(R.xml.settings);
		setPreferenceScreen(createPreferenceHierarchy());
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
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		citiesList.setSummary(citiesList.getValue());
		countriesList.setSummary(countriesList.getValue());
	}
	
	private PreferenceScreen createPreferenceHierarchy() {
		PreferenceScreen root=getPreferenceManager().createPreferenceScreen(this);
		PreferenceCategory citySettings=new PreferenceCategory(this);
		citySettings.setTitle(reshapeText(getString(R.string.city_settings_title), isReshapingNessecary));
		root.addPreference(citySettings);
		countriesList=new ListPreference(this);
		countriesList.setKey(Keys.COUNTRY_KEY);
		countriesList.setTitle(reshapeText(getString(R.string.country_settings_title), isReshapingNessecary));
		countriesList.setDefaultValue("Morocco");
		countriesList.setOnPreferenceChangeListener(new OnCountryChangedListener());
		citySettings.addPreference(countriesList);
		citiesList=new ListPreference(this);
		citiesList.setTitle(reshapeText(getString(R.string.city_choice_title),isReshapingNessecary));
		citiesList.setKey(Keys.CITY_KEY);
		citiesList.setDefaultValue("Rabat et Salé");
		citySettings.addPreference(citiesList);
		//don't show it if device don't have any location provider
		if(hasLocationSupport())
		{customCity=new CheckBoxPreference(this);
		customCity.setKey(Keys.CUSTOM_CITY_KEY);
		customCity.setOnPreferenceClickListener(new OnCustomCityPreferenceClickListener());
		customCity.setTitle(reshapeText(getString(R.string.custom_city_title), isReshapingNessecary));
		citySettings.addPreference(customCity);
		if(customCity.isChecked())
		{
			citiesList.setEnabled(false);
			countriesList.setEnabled(false);
		}
		}
		EditTextPreference timeZone=new EditTextPreference(this);
		timeZone.setKey(Keys.TIME_ZONE_KEY);
		timeZone.setTitle(reshapeText(getString(R.string.time_zone_title), isReshapingNessecary));
		timeZone.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
		timeZone.setDefaultValue("0");
		timeZone.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if(((String)newValue).equals(""))
				{	PreferenceManager.getDefaultSharedPreferences(Wizard.this)
					.edit().putString(preference.getKey(), "0").commit();
					return false;
				}
				else
					return true;
			}
		});
		citySettings.addPreference(timeZone);
		PreferenceCategory calculationSettings=new PreferenceCategory(this);
		calculationSettings.setTitle(reshapeText(getString(R.string.calculation_settings_title), isReshapingNessecary));
		root.addPreference(calculationSettings);
		ListPreference organization=new ListPreference(this);
		organization.setKey(Keys.ORGANIZATION_KEY);
		organization.setTitle(reshapeText(getString(R.string.organization_settings_title), isReshapingNessecary));
		organization.setSummary(reshapeText(getString(R.string.organization_settings_summary), isReshapingNessecary));
		organization.setEntries(reshapeText(getResources().getStringArray(R.array.organizations), isReshapingNessecary));
		organization.setEntryValues(R.array.organizations_int);
		organization.setDefaultValue("1");
		calculationSettings.addPreference(organization);
		ListPreference asrMadhab=new ListPreference(this);
		asrMadhab.setKey(Keys.ASR_MADHAB_KEY);
		asrMadhab.setTitle(reshapeText(getString(R.string.asr_madhab_title), isReshapingNessecary));
		asrMadhab.setSummary(reshapeText(getString(R.string.asr_madhab_summary), isReshapingNessecary));
		asrMadhab.setEntries(reshapeText(getResources().getStringArray(R.array.asr_madhab), isReshapingNessecary));
		asrMadhab.setEntryValues(R.array.asr_madhab_int);
		asrMadhab.setDefaultValue("0");
		calculationSettings.addPreference(asrMadhab);
		return root;
	}
	
	private boolean hasLocationSupport()
	{
		LocationManager manager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
		List<String> list=manager.getAllProviders();
		if(manager==null||list.isEmpty())
			return false;
		return true;
	}
	
	private class OnCountryChangedListener implements OnPreferenceChangeListener
	{
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			DbAdapter dbAdapter=new DbAdapter(Wizard.this);
			dbAdapter.open();
			String[] cities=dbAdapter.getCities((String)newValue);
			citiesList.setEntries(cities);
			citiesList.setEntryValues(cities);
			dbAdapter.close();
			return true;
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
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
				DbAdapter dbAdapter=new DbAdapter(Wizard.this);
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
	
	private class OnCustomCityPreferenceClickListener implements OnPreferenceClickListener
	{
		public boolean onPreferenceClick(Preference preference) {
			if(((CheckBoxPreference)preference).isChecked())
			{	
				showDialog(WAITING_GPS);
				controller=new LocationController(Wizard.this, Wizard.this);
				controller.requestUpdates();
			}
			return true;
		}
	}
	
	//OnLocationListener methods
	public void onLocationChanged(Location location) {
		if (isDialogPreviouslyShown) {
			dismissDialog(WAITING_GPS);
			prefs = new Bundle();
			prefs.putDouble("longitude", location.getLongitude());
			prefs.putDouble("latitude", location.getLatitude());
			prefs.putDouble("altitude", location.getAltitude());
			showDialog(CONFIRM_LOCATION);
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
	
	//performing the first scheduling using the parameters collected
	@Override
	protected void onPause() {
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
		if(!prefs.getBoolean("Event_scheduled", false))
	        { 
	        	EventsHandler handler=new EventsHandler(this);
	        	handler.scheduleNextEvent(-1, -1); //not defining the next prayer nor the next event
	        }
		super.onPause();
	}
}