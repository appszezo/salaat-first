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

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import com.michaelnovakjr.numberpicker.NumberPickerPreference;

public class PrayerSettings extends PreferenceActivity {
	
	
	private int prayer;
	private String prayerName;
	private SeekBarPreference beforeSilentTime;
	private SeekBarPreference afterSilentTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent intent=getIntent();
		prayer=intent.getIntExtra(Keys.PRAYER_INTENT_KEY, 0);
		prayerName=intent.getStringExtra(Keys.PRAYER_NAME_INTENT_KEY);
		setTitle(reshapeText(prayerName, isReshapingNessecary));
		setPreferenceScreen(createPreferenceHierarchy());
	}

	private PreferenceScreen createPreferenceHierarchy() {
		PreferenceScreen root=getPreferenceManager().createPreferenceScreen(this);
		CheckBoxPreference showingAdhan=new CheckBoxPreference(this);
		showingAdhan.setKey(Keys.SHOW_ADHAN_KEY+prayer);
		showingAdhan.setDefaultValue(true);
		showingAdhan.setTitle(reshapeText(getString(R.string.show_adhan_activity_title), isReshapingNessecary));
		root.addPreference(showingAdhan);
		CheckBoxPreference activatingSilent=new CheckBoxPreference(this);		
		activatingSilent.setKey(Keys.ACTIVATING_SILENT_KEY+prayer);
		activatingSilent.setDefaultValue(true);
		activatingSilent.setTitle(reshapeText(getString(R.string.activating_silent_title), isReshapingNessecary));
		activatingSilent.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if((Boolean)newValue)
				{
					beforeSilentTime.setEnabled(true);
					afterSilentTime.setEnabled(true);
				}
				else
				{
					beforeSilentTime.setEnabled(false);
					afterSilentTime.setEnabled(false);
				}
				return true;
			}
		});
		root.addPreference(activatingSilent);
		beforeSilentTime=new SeekBarPreference(this);
		beforeSilentTime.setMax(15);
		beforeSilentTime.setDefaultValue(5);
		beforeSilentTime.setKey(Keys.TIME_BEFORE_SILENT_KEY+prayer);
		beforeSilentTime.setTitle(reshapeText(getString(R.string.time_before_silent_title), isReshapingNessecary));
		beforeSilentTime.setSummary(reshapeText(getString(R.string.time_before_silent_summary), isReshapingNessecary));
		beforeSilentTime.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				int val=(Integer)newValue;
				if(val>afterSilentTime.getValue())
				{
					Toast.makeText(PrayerSettings.this, "Erreur! le temps de désactivation du mode silencieux ne peut être inferieur" +
							"au temps de son activation", Toast.LENGTH_SHORT).show();
					return false;
				}
				else
					return true;
			}
		});

		if(!activatingSilent.isChecked())
		{
			beforeSilentTime.setEnabled(false);
		}
		root.addPreference(beforeSilentTime);
		afterSilentTime=new SeekBarPreference(this);
		afterSilentTime.setMax(40);
		afterSilentTime.setDefaultValue(25);
		afterSilentTime.setKey(Keys.TIME_AFTER_SILENT_KEY+prayer);
		afterSilentTime.setTitle(reshapeText(getString(R.string.time_after_silent_title), isReshapingNessecary));
		afterSilentTime.setSummary(reshapeText(getString(R.string.time_after_silent_summary), isReshapingNessecary));
		afterSilentTime.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				int val=(Integer)newValue;
				if(val<beforeSilentTime.getValue())
				{	
					Toast.makeText(PrayerSettings.this, "Erreur! le temps de désactivation du mode silencieux ne peut être inferieur" +
							"au temps de son activation", Toast.LENGTH_SHORT).show();
					return false;
				}
				else
					return true;
			}
		});

		if(!activatingSilent.isChecked())
		{
			afterSilentTime.setEnabled(false);
		}
		root.addPreference(afterSilentTime);
		
		//personnalising time
		NumberPickerPreference offsetPreference=new NumberPickerPreference(this);
		offsetPreference.setTitle(reshapeText(getString(R.string.customizing_prayer_time_title), isReshapingNessecary));
		offsetPreference.setRange(-10, 10);
		offsetPreference.setKey(Keys.TIME_OFFSET_KEY+prayer);
		root.addPreference(offsetPreference);
		return root;
	}
}
