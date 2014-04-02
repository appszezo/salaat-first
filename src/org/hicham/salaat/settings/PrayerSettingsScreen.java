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

import org.hicham.salaat.R;
import org.hicham.salaat.calculating.PrayerTimesCalculator;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public class PrayerSettingsScreen extends PreferenceActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setPreferenceScreen(createPreferenceHierarchy());
		setTitle(reshapeText(getString(R.string.prayer_settings_title), isReshapingNessecary));
	}
	
	private PreferenceScreen createPreferenceHierarchy()
	{
		PreferenceScreen root=getPreferenceManager().createPreferenceScreen(this);
		CheckBoxPreference activatingNotifications=new CheckBoxPreference(this);
		activatingNotifications.setTitle(reshapeText(getString(R.string.show_notification_title), isReshapingNessecary));
		activatingNotifications.setSummary(R.string.show_notification_summary);
		activatingNotifications.setKey(Keys.SHOW_NOTIFICATION_KEY);
		activatingNotifications.setDefaultValue(true);
		root.addPreference(activatingNotifications);
		Preference fajr=new Preference(this);
		fajr.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				Intent intent=new Intent(PrayerSettingsScreen.this, PrayerSettings.class);
				intent.putExtra(Keys.PRAYER_INTENT_KEY, PrayerTimesCalculator.FAJR);
				intent.putExtra(Keys.PRAYER_NAME_INTENT_KEY, getString(R.string.fajr_name));
				startActivity(intent);
				return true;
			}
		});
		fajr.setTitle(reshapeText(getString(R.string.fajr_name), isReshapingNessecary));
		root.addPreference(fajr);
		Preference dhuhr=new Preference(this);
		dhuhr.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				Intent intent=new Intent(PrayerSettingsScreen.this, PrayerSettings.class);
				intent.putExtra(Keys.PRAYER_INTENT_KEY, PrayerTimesCalculator.DHUHR);
				intent.putExtra(Keys.PRAYER_NAME_INTENT_KEY, getString(R.string.dhuhr_name));
				startActivity(intent);
				return true;
			}
		});
		dhuhr.setTitle(reshapeText(getString(R.string.dhuhr_name), isReshapingNessecary));
		root.addPreference(dhuhr);
		Preference asr=new Preference(this);
		asr.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				Intent intent=new Intent(PrayerSettingsScreen.this, PrayerSettings.class);
				intent.putExtra(Keys.PRAYER_INTENT_KEY, PrayerTimesCalculator.ASR);
				intent.putExtra(Keys.PRAYER_NAME_INTENT_KEY, getString(R.string.asr_name));
				startActivity(intent);
				return true;
			}
		});
		asr.setTitle(reshapeText(getString(R.string.asr_name), isReshapingNessecary));
		root.addPreference(asr);
		Preference maghrib=new Preference(this);
		maghrib.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				Intent intent=new Intent(PrayerSettingsScreen.this, PrayerSettings.class);
				intent.putExtra(Keys.PRAYER_INTENT_KEY, PrayerTimesCalculator.MAGHRIB);
				intent.putExtra(Keys.PRAYER_NAME_INTENT_KEY, getString(R.string.maghrib_name));
				startActivity(intent);
				return true;
			}
		});
		maghrib.setTitle(reshapeText(getString(R.string.maghrib_name), isReshapingNessecary));
		root.addPreference(maghrib);
		Preference ichaa=new Preference(this);
		ichaa.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				Intent intent=new Intent(PrayerSettingsScreen.this, PrayerSettings.class);
				intent.putExtra(Keys.PRAYER_INTENT_KEY, PrayerTimesCalculator.ICHAA);
				intent.putExtra(Keys.PRAYER_NAME_INTENT_KEY, getString(R.string.ishaa_name));
				startActivity(intent);
				return true;
			}
		});
		ichaa.setTitle(reshapeText(getString(R.string.ishaa_name), isReshapingNessecary));
		root.addPreference(ichaa);
		return root;
	}
	
}
