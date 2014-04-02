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

package org.hicham.salaat.settings.wizard;

import static org.arabic.ArabicUtilities.reshapeSentence;
import static org.arabic.ArabicUtilities.reshapeText;

import org.hicham.salaat.R;
import org.hicham.salaat.SalaatFirstApplication;
import org.hicham.salaat.settings.CustomPreferenceActivity;
import org.hicham.salaat.settings.Keys;
import org.hicham.salaat.settings.Keys.DefaultValues;
import org.holoeverywhere.preference.ListPreference;
import org.holoeverywhere.preference.PreferenceScreen;

import android.os.Bundle;

/**
 * general settings
 * 
 * @author Hicham_
 * 
 */
public class PrayerCalculationSettings extends CustomPreferenceActivity  {
	
	
	
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
		PreferenceScreen calculationSettings = getPreferenceScreen();
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
	}

}
