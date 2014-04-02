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

import org.hicham.salaat.R;
import org.hicham.salaat.SalaatFirstApplication;
import org.hicham.salaat.settings.Keys.DefaultValues;
import org.hicham.salaat.settings.preference.SalaatCheckBoxPreference;
import org.hicham.salaat.settings.preference.SalaatSwitchScreenPreference;
import org.hicham.salaat.settings.preference.VolumeSeekBarPreference;
import org.holoeverywhere.preference.CheckBoxPreference;
import org.holoeverywhere.preference.NumberPickerPreference;
import org.holoeverywhere.preference.Preference;
import org.holoeverywhere.preference.Preference.OnPreferenceClickListener;
import org.holoeverywhere.preference.PreferenceCategory;
import org.holoeverywhere.preference.PreferenceScreen;
import org.holoeverywhere.preference.SwitchScreenPreference;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import ar.com.daidalos.afiledialog.FileChooserActivity;

import com.ahmedsoliman.devel.jislamic.DayPrayers;

public class PrayerSettingsScreen extends CustomPreferenceActivity {
	
	private static final int NOTIFICATION_RINGTONE_PICK_CODE=1;

	private void createPreferenceHierarchy() {
		PreferenceScreen root = getPreferenceScreen();
		PreferenceCategory globalSettings = new PreferenceCategory(this);
		globalSettings
				.setTitle(reshapeSentence(R.string.prayer_global_settings_title));
		root.addPreference(globalSettings);
		SwitchScreenPreference activatingNotifications = new SalaatSwitchScreenPreference(
				this);
		activatingNotifications
				.setTitle(reshapeSentence(R.string.show_notification_title));
		// activatingNotifications.setTitle(R.string.show_notification_summary);
		activatingNotifications.setKey(Keys.SHOW_NOTIFICATION_KEY);
		activatingNotifications.setDefaultValue(DefaultValues.SHOW_NOTIFICATION);

		globalSettings.addPreference(activatingNotifications);

		/* build the activatingNotifications screen */
		NumberPickerPreference notificationDelay = new NumberPickerPreference(
				this);
		notificationDelay.setKey(Keys.NOTIFICATION_TIME_KEY);
		notificationDelay
				.setTitle(reshapeSentence(R.string.notification_delay_title));
		notificationDelay
				.setSummary(reshapeSentence(R.string.notification_delay_summary));
		notificationDelay.setDefaultValue(DefaultValues.NOTIFICATION_TIME);
		notificationDelay.setMaxValue(20);
		notificationDelay.setMinValue(1);
		activatingNotifications.addPreference(notificationDelay);
		notificationDelay.setDependency(Keys.SHOW_NOTIFICATION_KEY);
		Preference notificationTone = new Preference(this);
		notificationTone
				.setTitle(reshapeSentence(R.string.notification_tone_title));
		
		notificationTone.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {

				Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, reshapeSentence(R.string.notification_tone_title));
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(getDefaultSharedPreferences().getString(Keys.NOTIFICATION_TONE_KEY, DefaultValues.NOTIFICATION_TONE)));
				Intent chooserIntent = Intent.createChooser(intent, reshapeSentence(R.string.notification_tone_title));
				
				
				Intent appIntent=new Intent(PrayerSettingsScreen.this, FileChooserActivity.class);
				appIntent.putExtra(FileChooserActivity.INPUT_USE_BACK_BUTTON_TO_NAVIGATE, true);
				appIntent.putExtra(FileChooserActivity.INPUT_REGEX_FILTER, ".*mp3|.*ogg|.*flac|.*wav|.*mid|.*3gp|.*aac|.*mp4|.*m4a|"
			     		+ ".*MP3|.*OGG|.*FLAC|.*WAV|.*MID|.*3GP|.*AAC|.*MP4|.*M4A");
				appIntent.putExtra(FileChooserActivity.INPUT_START_FOLDER, Environment.getExternalStorageDirectory().toString());
				appIntent.putExtra(FileChooserActivity.INPUT_SHOW_ONLY_SELECTABLE, true);
				appIntent.putExtra(FileChooserActivity.INPUT_AS_RINGTONE_MANAGER, true);
				appIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
				chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{appIntent});
				
				PackageManager pm = PrayerSettingsScreen.this.getPackageManager();

				if(!pm.queryIntentActivities(intent, 0).isEmpty())		
					PrayerSettingsScreen.this.startActivityForResult(chooserIntent, NOTIFICATION_RINGTONE_PICK_CODE);
				else
					PrayerSettingsScreen.this.startActivityForResult(appIntent, NOTIFICATION_RINGTONE_PICK_CODE);
				return true;
			}
		});
		
		activatingNotifications.addPreference(notificationTone);
		notificationTone.setDependency(Keys.SHOW_NOTIFICATION_KEY);
		/*------------------------------*/

		/* setting the volume */
		VolumeSeekBarPreference volume = new VolumeSeekBarPreference(this);
		volume.setMax(20);
		volume.setDefaultValue(DefaultValues.VOLUME);
		volume.setKey(Keys.VOLUME_KEY);
		volume.setTitle(reshapeSentence(R.string.volume));
		globalSettings.addPreference(volume);
		/*-------------------*/

		/* activating silent parameters */
		SwitchScreenPreference silentSwitch = new SalaatSwitchScreenPreference(
				this);
		silentSwitch.setKey(Keys.GLOBAL_ACTIVATING_SILENT_KEY);
		silentSwitch
				.setTitle(reshapeSentence(R.string.global_silent_activation_title));
		silentSwitch.setDefaultValue(DefaultValues.GLOBAL_ACTIVATING_SILENT);
		globalSettings.addPreference(silentSwitch);
		CheckBoxPreference vibrateMode = new SalaatCheckBoxPreference(this);
		vibrateMode.setKey(Keys.VIBRATE_MODE_KEY);
		vibrateMode.setDefaultValue(DefaultValues.VIBRATE_MODE);
		vibrateMode.setTitle(reshapeSentence(R.string.vibrate_mode_title));
		silentSwitch.addPreference(vibrateMode);
		vibrateMode.setDependency(Keys.GLOBAL_ACTIVATING_SILENT_KEY);
		CheckBoxPreference vibrateDuringSwitch = new SalaatCheckBoxPreference(this);
		vibrateDuringSwitch.setKey(Keys.VIBRATING_DURING_SWITCHING_KEY);
		vibrateDuringSwitch
				.setTitle(reshapeSentence(R.string.vibrate_during_silent_activation_title));
		vibrateDuringSwitch.setDefaultValue(DefaultValues.VIBRATING_DURING_SWITCHING);
		silentSwitch.addPreference(vibrateDuringSwitch);
		vibrateDuringSwitch.setDependency(Keys.GLOBAL_ACTIVATING_SILENT_KEY);

		/* Prayer specific settings */
		PreferenceCategory specificSettings = new PreferenceCategory(this);
		specificSettings
				.setTitle(reshapeSentence(R.string.prayer_specic_settings_title));
		root.addPreference(specificSettings);
		Preference fajr = new Preference(this);
		fajr.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(PrayerSettingsScreen.this,
						PrayerSettings.class);
				intent.putExtra(Keys.PRAYER_INTENT_KEY, DayPrayers.FAJR);
				startActivity(intent);
				return true;
			}
		});
		fajr.setTitle(reshapeSentence(R.string.fajr_name));
		specificSettings.addPreference(fajr);
		Preference dhuhr = new Preference(this);
		dhuhr.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(PrayerSettingsScreen.this,
						PrayerSettings.class);
				intent.putExtra(Keys.PRAYER_INTENT_KEY, DayPrayers.DHUHR);
				startActivity(intent);
				return true;
			}
		});
		dhuhr.setTitle(reshapeSentence(R.string.dhuhr_name));
		specificSettings.addPreference(dhuhr);
		Preference asr = new Preference(this);
		asr.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(PrayerSettingsScreen.this,
						PrayerSettings.class);
				intent.putExtra(Keys.PRAYER_INTENT_KEY, DayPrayers.ASR);
				startActivity(intent);
				return true;
			}
		});
		asr.setTitle(reshapeSentence(R.string.asr_name));
		specificSettings.addPreference(asr);
		Preference maghrib = new Preference(this);
		maghrib.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(PrayerSettingsScreen.this,
						PrayerSettings.class);
				intent.putExtra(Keys.PRAYER_INTENT_KEY, DayPrayers.MAGHRIB);
				startActivity(intent);
				return true;
			}
		});
		maghrib.setTitle(reshapeSentence(R.string.maghrib_name));
		specificSettings.addPreference(maghrib);
		Preference ichaa = new Preference(this);
		ichaa.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(PrayerSettingsScreen.this,
						PrayerSettings.class);
				intent.putExtra(Keys.PRAYER_INTENT_KEY, DayPrayers.ICHAA);
				startActivity(intent);
				return true;
			}
		});
		ichaa.setTitle(reshapeSentence(R.string.ishaa_name));
		specificSettings.addPreference(ichaa);
		
		Preference jumua = new Preference(this);
		jumua.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(PrayerSettingsScreen.this,
						FridayPrayerSettings.class);
				intent.putExtra(Keys.PRAYER_INTENT_KEY, DayPrayers.JUMUA);
				startActivity(intent);
				return true;
			}
		});
		jumua.setTitle(reshapeSentence(R.string.jumua_name));
		specificSettings.addPreference(jumua);

	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==NOTIFICATION_RINGTONE_PICK_CODE && resultCode==RESULT_OK)
		{
	          Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
	          if(uri!=null)
	          {
	        	  getDefaultSharedPreferences().edit().putString(Keys.NOTIFICATION_TONE_KEY, uri.toString()).commit();
	          }
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((SalaatFirstApplication) getSupportApplication()).refreshLanguage();

		getSupportActionBar().setTitle(
				reshapeSentence(R.string.prayer_settings_title));
		setPreferenceScreen(getPreferenceManager().createPreferenceScreen(this));
		createPreferenceHierarchy();
		setTitle(reshapeSentence(R.string.prayer_settings_title));
	}

}
