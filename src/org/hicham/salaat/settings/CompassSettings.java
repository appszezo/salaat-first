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

import org.hicham.salaat.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

public class CompassSettings extends PreferenceActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setPreferenceScreen(createPreferenceHierarchy());
	}

	private PreferenceScreen createPreferenceHierarchy() {
		PreferenceScreen root=getPreferenceManager().createPreferenceScreen(this);
		CheckBoxPreference usingGps=new CheckBoxPreference(this);
		usingGps.setTitle(reshapeText(getString(R.string.using_gps_name), isReshapingNessecary));
		usingGps.setKey(Keys.GPS_FOR_COMPASS_KEY);
		usingGps.setDefaultValue(true);
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);
		boolean isGpsOn=pref.getBoolean(Keys.GPS_FOR_COMPASS_KEY, true);
		if(isGpsOn)
			usingGps.setSummary(reshapeText(getString(R.string.using_gps_description_gps_on),isReshapingNessecary));
		else
			usingGps.setSummary(reshapeText(getString(R.string.using_gps_description_gps_off),isReshapingNessecary));
		usingGps.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				Boolean value=(Boolean)newValue;
				if(value)
					preference.setSummary(reshapeText(getString(R.string.using_gps_description_gps_on),isReshapingNessecary));
				else
					preference.setSummary(reshapeText(getString(R.string.using_gps_description_gps_off),isReshapingNessecary));
				return true;
			}
		});
		root.addPreference(usingGps);
		return root;
	}
}
