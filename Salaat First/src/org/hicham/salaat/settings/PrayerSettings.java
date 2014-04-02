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

import org.hicham.salaat.R;
import org.hicham.salaat.SalaatFirstApplication;
import org.hicham.salaat.settings.Keys.DefaultValues;
import org.hicham.salaat.settings.preference.AdhanListPreference;
import org.hicham.salaat.settings.preference.NumberPickerPreferenceForSalaatFirst;
import org.hicham.salaat.settings.preference.SalaatCheckBoxPreference;
import org.hicham.salaat.settings.preference.SeekBarPreference;
import org.holoeverywhere.preference.CheckBoxPreference;
import org.holoeverywhere.preference.PreferenceActivity;
import org.holoeverywhere.preference.PreferenceScreen;

import android.content.Intent;
import android.os.Bundle;

public class PrayerSettings extends PreferenceActivity {

	private AdhanListPreference sounds;

	private int prayer;
	private String prayerName;
	private SeekBarPreference siltentModeDelay;
	private SeekBarPreference silentModeDuration;

	private void createPreferenceHierarchy() {
		PreferenceScreen root = getPreferenceScreen();
		CheckBoxPreference showingAdhan = new SalaatCheckBoxPreference(this);
		showingAdhan.setKey(Keys.SHOW_ADHAN_KEY + prayer);
		showingAdhan.setDefaultValue(DefaultValues.SHOW_ADHAN);
		showingAdhan
				.setTitle(reshapeSentence(R.string.show_adhan_activity_title));
		root.addPreference(showingAdhan);
		// Adhan sound settings
		sounds = new AdhanListPreference(this);
		sounds.setKey(Keys.ADHAN_SOUND_KEY + prayer);
		sounds.setSecondaryKey(Keys.ADHAN_SOUND_URI_KEY + prayer);
		sounds.setEntries(reshapeText(getResources().getStringArray(
				R.array.sounds_entries)));
		sounds.setEntryValues(R.array.sounds_entryValues);
		sounds.setDefaultValue(DefaultValues.ADHAN_SOUND);
		sounds.setTitle(reshapeSentence(R.string.choosing_adhan_sound_title));
		root.addPreference(sounds);


		if (SalaatFirstApplication.prefs.getBoolean(Keys.GLOBAL_ACTIVATING_SILENT_KEY,
				DefaultValues.GLOBAL_ACTIVATING_SILENT)) {
			CheckBoxPreference activatingSilent = new SalaatCheckBoxPreference(this);
			activatingSilent.setKey(Keys.ACTIVATING_SILENT_KEY + prayer);
			activatingSilent.setDefaultValue(DefaultValues.ACTIVATING_SILENT);
			activatingSilent
					.setTitle(reshapeSentence(R.string.activating_silent_title));
			root.addPreference(activatingSilent);
			siltentModeDelay = new SeekBarPreference(this);
			siltentModeDelay.setMessage(reshapeSentence(R.string.silent_mode_start));
			siltentModeDelay.setSuffix(reshapeSentence(R.string.after_adhan));
			siltentModeDelay.setMax(15);
			siltentModeDelay.setDefaultValue(DefaultValues.DELAY_BEFORE_SILENT);
			siltentModeDelay.setKey(Keys.DELAY_BEFORE_SILENT_KEY + prayer);
			siltentModeDelay
					.setTitle(reshapeSentence(R.string.silent_mode_start_title));
			siltentModeDelay
					.setSummary(reshapeSentence(R.string.silent_mode_start_summary));
			root.addPreference(siltentModeDelay);
			siltentModeDelay.setDependency(Keys.ACTIVATING_SILENT_KEY + prayer);
			silentModeDuration = new SeekBarPreference(this);
			silentModeDuration.setMessage(reshapeSentence(R.string.silent_mode_duration));
			silentModeDuration.setMax(40);
			silentModeDuration.setDefaultValue(DefaultValues.SILENT_MODE_DURATION);
			silentModeDuration.setKey(Keys.SILENT_MODE_DURATION_KEY + prayer);
			silentModeDuration
					.setTitle(reshapeSentence(R.string.silent_mode_duration_title));
			silentModeDuration
					.setSummary(reshapeSentence(R.string.silent_mode_duration_summary));
			root.addPreference(silentModeDuration);
			silentModeDuration.setDependency(Keys.ACTIVATING_SILENT_KEY
					+ prayer);
		}

		// personnalising time

		NumberPickerPreferenceForSalaatFirst offsetPreference = new NumberPickerPreferenceForSalaatFirst(
				this);
		offsetPreference
				.setTitle(reshapeSentence(R.string.customizing_prayer_time_title));
		offsetPreference.setKey(Keys.TIME_OFFSET_KEY + prayer);
		offsetPreference.setDefaultValue(DefaultValues.TIME_OFFSET);
		root.addPreference(offsetPreference);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		((SalaatFirstApplication) getSupportApplication()).refreshLanguage();

		Intent intent = getIntent();
		prayer = intent.getIntExtra(Keys.PRAYER_INTENT_KEY, 0);
		prayerName = SalaatFirstApplication.getPrayerName(prayer);
		setTitle(prayerName);
		//getSupportActionBar().setTitle(prayerName);
		setPreferenceScreen(getPreferenceManager().createPreferenceScreen(this));
		createPreferenceHierarchy();
	}
}
