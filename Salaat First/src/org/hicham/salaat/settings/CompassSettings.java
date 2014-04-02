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

package org.hicham.salaat.settings;

import static org.arabic.ArabicUtilities.reshapeSentence;

import org.hicham.salaat.R;
import org.hicham.salaat.SalaatFirstApplication;
import org.hicham.salaat.settings.Keys.DefaultValues;
import org.hicham.salaat.settings.preference.SalaatCheckBoxPreference;
import org.holoeverywhere.preference.CheckBoxPreference;
import org.holoeverywhere.preference.Preference;
import org.holoeverywhere.preference.Preference.OnPreferenceChangeListener;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.PreferenceScreen;
import org.holoeverywhere.preference.SharedPreferences;

import android.os.Bundle;

public class CompassSettings extends CustomPreferenceActivity {

	private void createPreferenceHierarchy() {
		PreferenceScreen root = getPreferenceScreen();
		CheckBoxPreference usingGps = new SalaatCheckBoxPreference(this);
		usingGps.setTitle(reshapeSentence(R.string.using_gps_name));
		usingGps.setKey(Keys.GPS_FOR_COMPASS_KEY);
		usingGps.setDefaultValue(DefaultValues.GPS_FOR_COMPASS);
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean isGpsOn = pref.getBoolean(Keys.GPS_FOR_COMPASS_KEY, true);
		if (isGpsOn)
			usingGps.setSummary(reshapeSentence(R.string.using_gps_description_gps_on));
		else
			usingGps.setSummary(reshapeSentence(R.string.using_gps_description_gps_off));
		usingGps.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				Boolean value = (Boolean) newValue;
				if (value)
					preference
							.setSummary(reshapeSentence(R.string.using_gps_description_gps_on));
				else
					preference
							.setSummary(reshapeSentence(R.string.using_gps_description_gps_off));
				return true;
			}
		});
		root.addPreference(usingGps);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((SalaatFirstApplication) getSupportApplication()).refreshLanguage();
		getSupportActionBar().setTitle(
				reshapeSentence(R.string.compass_settings));

		setPreferenceScreen(getPreferenceManager().createPreferenceScreen(this));
		createPreferenceHierarchy();
	}
}
