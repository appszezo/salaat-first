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
import static org.arabic.ArabicUtilities.reshapeText;
import static org.hicham.salaat.SalaatFirstApplication.TAG;

import org.hicham.salaat.R;
import org.hicham.salaat.SalaatFirstApplication;
import org.hicham.salaat.settings.Keys.DefaultValues;
import org.hicham.salaat.settings.preference.NumberPickerPreferenceForSalaatFirst;
import org.hicham.salaat.settings.preference.SalaatCheckBoxPreference;
import org.hicham.salaat.ui.dialogs.FeaturesDialog;
import org.holoeverywhere.preference.CheckBoxPreference;
import org.holoeverywhere.preference.ListPreference;
import org.holoeverywhere.preference.Preference;
import org.holoeverywhere.preference.Preference.OnPreferenceChangeListener;
import org.holoeverywhere.preference.Preference.OnPreferenceClickListener;
import org.holoeverywhere.preference.PreferenceCategory;
import org.holoeverywhere.preference.PreferenceScreen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * general settings
 * 
 * @author Hicham_
 * 
 */
public class Settings extends CustomPreferenceActivity  {
	
	private ListPreference language;
	private CheckBoxPreference cancelAllAlarms;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((SalaatFirstApplication) getSupportApplication()).refreshLanguage();
		getSupportActionBar().setTitle(reshapeSentence(R.string.settings_text));
		
		setPreferenceScreen(getPreferenceManager().createPreferenceScreen(this));
		createPreferenceHierarchy();
	}
	
	private void createPreferenceHierarchy() {
		// calculation settings
		PreferenceScreen root = getPreferenceScreen();
		cancelAllAlarms = new SalaatCheckBoxPreference(this);
		cancelAllAlarms.setDefaultValue(DefaultValues.CANCEL_ALL_ALARMS);
		cancelAllAlarms.setKey(Keys.CANCEL_ALL_ALARMS_KEY);
		cancelAllAlarms
				.setTitle(reshapeSentence(R.string.cancel_all_alarms_title));
		cancelAllAlarms
				.setSummary(reshapeSentence(R.string.cancel_all_alarms_summary));
		root.addPreference(cancelAllAlarms);
		PreferenceScreen calculationSettings = getPreferenceManager()
				.createPreferenceScreen(this);
		String s = reshapeSentence(R.string.calculation_settings_title);
		calculationSettings.setTitle(s);
		calculationSettings
				.setSummary(reshapeSentence(R.string.calculation_settings_sumary));
		root.addPreference(calculationSettings);
		ListPreference organization = new ListPreference(this);
		organization.setKey(Keys.ORGANIZATION_KEY);
		organization
				.setTitle(reshapeSentence(R.string.organization_settings_title));
		organization
				.setSummary(reshapeSentence(R.string.organization_settings_summary));
		organization.setEntries(reshapeText(getResources().getStringArray(
				R.array.organizations)));
		organization.setEntryValues(R.array.organizations_int);
		organization.setDefaultValue(DefaultValues.ORGANIZATION);
		calculationSettings.addPreference(organization);
		ListPreference asrMadhab = new ListPreference(this);
		asrMadhab.setKey(Keys.ASR_MADHAB_KEY);
		asrMadhab.setTitle(reshapeSentence(R.string.asr_madhab_title));
		asrMadhab.setSummary(reshapeSentence(R.string.asr_madhab_summary));
		asrMadhab.setEntries(reshapeText(getResources().getStringArray(
				R.array.asr_madhab)));
		asrMadhab.setEntryValues(R.array.asr_madhab_int);
		asrMadhab.setDefaultValue(DefaultValues.ASR_MADHAB);
		calculationSettings.addPreference(asrMadhab);

		/* city settings */
		PreferenceScreen citySettings = getPreferenceManager()
				.createPreferenceScreen(this);
		citySettings
				.setTitle(reshapeSentence(R.string.city_settings_title));
		root.addPreference(citySettings);
		citySettings
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					
						public boolean onPreferenceClick(Preference preference) {
							Intent intent = new Intent(Settings.this, CitySettings.class);
							startActivity(intent);
							return true;
						}
				});
		/* prayers settings */
		Preference prayerPreference = new Preference(this);
		prayerPreference
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					public boolean onPreferenceClick(Preference preference) {
						Intent intent = new Intent(Settings.this,
								PrayerSettingsScreen.class);
						startActivity(intent);
						return true;
					}
				});
		prayerPreference
				.setTitle(reshapeSentence(R.string.prayer_settings_title));
		prayerPreference
				.setSummary(reshapeSentence(R.string.prayer_settings_summary));
		root.addPreference(prayerPreference);
		
		
		/*Adkar notifications settings*/
		Preference adkarPreference = new Preference(this);
		adkarPreference
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					public boolean onPreferenceClick(Preference preference) {
						Intent intent = new Intent(Settings.this,
								AdkarSettings.class);
						startActivity(intent);
						return true;
					}
				});
		adkarPreference
				.setTitle(reshapeSentence(R.string.adkar_notifications_title));
		root.addPreference(adkarPreference);

		/*Hijri Calendar settings*/
		NumberPickerPreferenceForSalaatFirst hijriCalendarOffset=new NumberPickerPreferenceForSalaatFirst(this, -2, 2);
		hijriCalendarOffset.setKey(Keys.HIJRI_CALENDAR_OFFSET_KEY);
		hijriCalendarOffset.setDefaultValue(DefaultValues.HIJRI_CALENDAR_OFFSET);
		hijriCalendarOffset.setTitle(reshapeSentence(R.string.hijri_calendar_offset_title));
		hijriCalendarOffset.setSummary(reshapeSentence(R.string.hijri_calendar_offset_summary));
		root.addPreference(hijriCalendarOffset);
		
		/* compass settings */
		Preference compassPreference = new Preference(this);
		compassPreference
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					public boolean onPreferenceClick(Preference preference) {
						Intent intent = new Intent(Settings.this,
								CompassSettings.class);
						startActivity(intent);
						return true;
					}
				});
		compassPreference
				.setTitle(reshapeSentence(R.string.compass_settings));
		root.addPreference(compassPreference);
		// -------------------
		language = new ListPreference(this);
		language.setDefaultValue(DefaultValues.LANGUAGE);
		language.setTitle(reshapeSentence(R.string.changing_language_title));
		language.setKey(Keys.LANGUAGE_KEY);
		language.setEntries(reshapeText(getResources().getStringArray(
				R.array.languages)));
		language.setEntryValues(R.array.languages_values);
		language.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				Log.i(TAG, "Language changed: " + (String) newValue);
				language.setValue((String)newValue);
				//SalaatFirstApplication.getLastInstance().refreshLanguage();
				Intent intent=getIntent();
				finish();
				startActivity(intent);
				return true;
			}
		});

		root.addPreference(language);

		CheckBoxPreference startingServiceOnBootComplete = new SalaatCheckBoxPreference(
				this);
		startingServiceOnBootComplete
				.setKey(Keys.STARTING_SERVICE_ON_BOOT_COMPLETE_KEY);
		startingServiceOnBootComplete.setDefaultValue(DefaultValues.STARTING_SERVICE_ON_BOOT_COMPLETE);
		startingServiceOnBootComplete
				.setTitle(reshapeSentence(R.string.starting_service_option_title));
		startingServiceOnBootComplete
				.setSummary(reshapeSentence(R.string.starting_service_option_summary));
		root.addPreference(startingServiceOnBootComplete);
		
		/* Facebook */
		PreferenceCategory aboutCategory=new PreferenceCategory(this);
		aboutCategory.setTitle("");
		root.addPreference(aboutCategory);
		Preference newFeatures=new Preference(this);
		newFeatures.setTitle(reshapeSentence(R.string.new_features_title));
		newFeatures.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				String[] features=getResources().getStringArray(R.array.new_features);
				FeaturesDialog dialog=new FeaturesDialog();
				Bundle args=new Bundle();
				args.putStringArray(FeaturesDialog.FEATURES_KEY, features);
				dialog.setArguments(args);
				dialog.show(Settings.this);
				return true;
			}
		});
		aboutCategory.addPreference(newFeatures);
		Preference facebookButton = new Preference(getBaseContext());
		facebookButton
				.setTitle(reshapeSentence(R.string.salaat_first_on_facebook));
		facebookButton
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					public boolean onPreferenceClick(Preference preference) {
						Intent intent = null;
						try {
							getBaseContext().getPackageManager()
									.getPackageInfo("com.facebook.katana", 0);
							intent = new Intent(Intent.ACTION_VIEW, Uri
									.parse("fb://page/557214157719272"));
						} catch (Exception e) {
							intent = new Intent(
									Intent.ACTION_VIEW,
									Uri.parse("https://www.facebook.com/SalaatFirst"));
						}
						try{
						startActivity(intent);
						}/*bug fix*/
						catch(Exception e)
						{
						}
						return true;
					}
				});
		aboutCategory.addPreference(facebookButton);
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		if(language!=null&language.getEntry()!=null)/*bug fix*/
			language.setSummary(reshapeSentence(language.getEntry().toString()));
	}
	
	@Override
	protected void onPostCreate(Bundle sSavedInstanceState) {
		// TODO Auto-generated method stub
		super.onPostCreate(sSavedInstanceState);
	}

}
