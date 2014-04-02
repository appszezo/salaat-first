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

import java.util.Locale;

import org.hicham.salaat.PrayerTimesActivity;
import org.hicham.salaat.R;
import org.hicham.salaat.calculating.PrayerTimesCalculator.Organization;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

/**
 * general settings
 * @author Hicham_
 *
 */
public class Settings extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	private class OnCityPreferenceClickListener implements OnPreferenceClickListener
	{
		public boolean onPreferenceClick(Preference preference) {
			Intent intent=new Intent(Settings.this,CitySettings.class);
			startActivity(intent);
			return true;
		}
	}

	private ListPreference language;
	private MediaPlayer mediaPlayer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//addPreferencesFromResource(R.xml.settings);
		setPreferenceScreen(createPreferenceHierarchy());
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	

	private PreferenceScreen createPreferenceHierarchy()
	{
		//calculation settings
		PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
		PreferenceScreen calculationSettings=getPreferenceManager().createPreferenceScreen(this);
		String s=reshapeText(getString(R.string.calculation_settings_title),isReshapingNessecary);
		calculationSettings.setTitle(s);
		calculationSettings.setSummary(reshapeText(getString(R.string.calculation_settings_sumary), isReshapingNessecary));
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
		
		//city settings
		PreferenceScreen citySettings=getPreferenceManager().createPreferenceScreen(this);
		citySettings.setTitle(reshapeText(getString(R.string.city_settings_title), isReshapingNessecary));
		root.addPreference(citySettings);
		citySettings.setOnPreferenceClickListener(new OnCityPreferenceClickListener());
		//prayers settings
		Preference prayerPreference=new Preference(this);
		prayerPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				Intent intent=new Intent(Settings.this,PrayerSettingsScreen.class);
				startActivity(intent);
				return true;
			}
		});
		prayerPreference.setTitle(reshapeText(getString(R.string.prayer_settings_title),isReshapingNessecary));
		prayerPreference.setSummary(reshapeText(getString(R.string.prayer_settings_summary),isReshapingNessecary));
		root.addPreference(prayerPreference);
		
		//Adhan sound settings
		Preference adhanSoundPreference=new Preference(this);
		adhanSoundPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {

				Intent intent=new Intent(Settings.this,AdhanSettingsActivity.class);
				startActivity(intent);
				return true;
			}
		});
		adhanSoundPreference.setTitle(reshapeText(getString(R.string.adhan_sound_settings),isReshapingNessecary));
		root.addPreference(adhanSoundPreference);
		//compass settings
		Preference compassPreference=new Preference(this);
		compassPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				Intent intent=new Intent(Settings.this,CompassSettings.class);
				startActivity(intent);
				return true;
			}
		});
		compassPreference.setTitle(reshapeText(getString(R.string.compass_settings),isReshapingNessecary));
		root.addPreference(compassPreference);
		//-------------------
		language=new ListPreference(this);
		language.setDefaultValue("en");
		language.setTitle(reshapeText(getString(R.string.changing_language_title), isReshapingNessecary));
		language.setKey(Keys.LANGUAGE_KEY);
		language.setEntries(reshapeText(getResources().getStringArray(R.array.languages), isReshapingNessecary));
		language.setEntryValues(R.array.languages_values);
		language.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				Log.i(PrayerTimesActivity.TAG, "Language changed: "+(String)newValue);
				Locale locale=new Locale((String)newValue);
				if(((String)newValue).equalsIgnoreCase("ar_MA"))
					{
					isReshapingNessecary=true;
					}
				else
					isReshapingNessecary=false;
				Locale.setDefault(locale);
				Configuration config=new Configuration();
				config.locale=locale;
				getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
				
				setPreferenceScreen(createPreferenceHierarchy());
				language.setValue(((String)newValue));
				return true;
			}
		});
		root.addPreference(language);

		CheckBoxPreference startingServiceOnBootComplete=new CheckBoxPreference(this);
		startingServiceOnBootComplete.setKey(Keys.STARTING_SERVICE_ON_BOOT_COMPLETE_KEY);
		startingServiceOnBootComplete.setDefaultValue(true);
		startingServiceOnBootComplete.setTitle(reshapeText(getString(R.string.starting_service_option_title), isReshapingNessecary));
		startingServiceOnBootComplete.setSummary(reshapeText(getString(R.string.starting_service_option_summary), isReshapingNessecary));
		root.addPreference(startingServiceOnBootComplete);
		return root;
	}
	
	public static Organization getOrganization(Context context)
	{
		String s=PreferenceManager.getDefaultSharedPreferences(context).getString(Keys.ORGANIZATION_KEY, "1");
		int i=Integer.parseInt(s);
		switch(i)
		{
		case 1:
			return Organization.MOROCCO;
		case 2:
			return Organization.UIS;
		case 3:
			return Organization.ISNA;
		case 4:
			return Organization.WIL;
		case 5:
			return Organization.EGOS;
		case 6:
			return Organization.UQ;
		default:
			return Organization.MOROCCO;
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
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mediaPlayer!=null)
		{
			mediaPlayer.stop();
			mediaPlayer.release();

		}
	}
}
