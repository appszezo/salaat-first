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
import org.hicham.salaat.alarm.EventsHandler;
import org.hicham.salaat.calculating.PrayerTimesCalcluationUtils;
import org.hicham.salaat.settings.Keys.DefaultValues;
import org.hicham.salaat.settings.preference.SalaatSwitchScreenPreference;
import org.hicham.salaat.settings.preference.SeekBarPreference;
import org.hicham.salaat.settings.preference.TimePickerPreference;
import org.holoeverywhere.preference.Preference;
import org.holoeverywhere.preference.Preference.OnPreferenceClickListener;
import org.holoeverywhere.preference.PreferenceScreen;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.preference.SharedPreferences.OnSharedPreferenceChangeListener;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import ar.com.daidalos.afiledialog.FileChooserActivity;

public class AdkarSettings extends CustomPreferenceActivity implements OnSharedPreferenceChangeListener{

	
	private int NOTIFICATION_RINGTONE_PICK_CODE=2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle(
				reshapeSentence(R.string.adkar_notifications_title));

		// addPreferencesFromResource(R.xml.settings);
		setPreferenceScreen(getPreferenceManager().createPreferenceScreen(this));
		createPreferenceHierarchy();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		getDefaultSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	private void createPreferenceHierarchy() {
		PreferenceScreen root=getPreferenceScreen();
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
				
				
				Intent appIntent=new Intent(AdkarSettings.this, FileChooserActivity.class);
				appIntent.putExtra(FileChooserActivity.INPUT_USE_BACK_BUTTON_TO_NAVIGATE, true);
				appIntent.putExtra(FileChooserActivity.INPUT_REGEX_FILTER, ".*mp3|.*ogg|.*flac|.*wav|.*mid|.*3gp|.*aac|.*mp4|.*m4a|"
			     		+ ".*MP3|.*OGG|.*FLAC|.*WAV|.*MID|.*3GP|.*AAC|.*MP4|.*M4A");
				appIntent.putExtra(FileChooserActivity.INPUT_START_FOLDER, Environment.getExternalStorageDirectory().toString());
				appIntent.putExtra(FileChooserActivity.INPUT_SHOW_ONLY_SELECTABLE, true);
				appIntent.putExtra(FileChooserActivity.INPUT_AS_RINGTONE_MANAGER, true);
				appIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
				chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{appIntent});
				
				PackageManager pm = AdkarSettings.this.getPackageManager();

				if(!pm.queryIntentActivities(intent, 0).isEmpty())		
					AdkarSettings.this.startActivityForResult(chooserIntent, NOTIFICATION_RINGTONE_PICK_CODE);
				else
					AdkarSettings.this.startActivityForResult(appIntent, NOTIFICATION_RINGTONE_PICK_CODE);
				return true;
			}
		});
		root.addPreference(notificationTone);
		SalaatSwitchScreenPreference adkarSabah=new SalaatSwitchScreenPreference(this);
		adkarSabah.setTitle(reshapeSentence(R.string.adkar_assabah_notification_title));
		adkarSabah.setSummary(reshapeSentence(R.string.adkar_assabah_notification_summary));
		adkarSabah.setKey(Keys.ADKAR_SABAH_NOTIFICATION_KEY);
		adkarSabah.setDefaultValue(DefaultValues.ADKAR_NOTIFICATIONS);
		
		root.addPreference(adkarSabah);
		
		SeekBarPreference adkarSabahTime=new SeekBarPreference(this);
		adkarSabahTime.setTitle(reshapeSentence(R.string.adkar_assabah_time_title));
		adkarSabahTime.setSummary(reshapeSentence(R.string.adkar_assabah_time_summary));
		adkarSabahTime.setKey(Keys.ADKAR_SABAH_TIME_KEY);
		adkarSabahTime.setDefaultValue(DefaultValues.ADKAR_SABAH_TIME);
		adkarSabahTime.setMax(60);
		adkarSabahTime.setSuffix(reshapeSentence(R.string.before_sunrise));
		adkarSabah.addPreference(adkarSabahTime);
		adkarSabahTime.setDependency(Keys.ADKAR_SABAH_NOTIFICATION_KEY);
		
		SalaatSwitchScreenPreference adkarAlmassae=new SalaatSwitchScreenPreference(this);
		adkarAlmassae.setTitle(reshapeSentence(R.string.adkar_almassae_notification_title));
		adkarAlmassae.setSummary(reshapeSentence(R.string.adkar_almassae_notification_summary));
		adkarAlmassae.setKey(Keys.ADKAR_ALMASSAE_NOTIFICATION_KEY);
		adkarAlmassae.setDefaultValue(DefaultValues.ADKAR_NOTIFICATIONS);
		
		root.addPreference(adkarAlmassae);
		
		SeekBarPreference adkarAlmassaeTime=new SeekBarPreference(this);
		adkarAlmassaeTime.setTitle(reshapeSentence(R.string.adkar_almassae_time_title));
		adkarAlmassaeTime.setSummary(reshapeSentence(R.string.adkar_almassae_time_summary));
		adkarAlmassaeTime.setKey(Keys.ADKAR_ALMASSAE_TIME_KEY);
		adkarAlmassaeTime.setDefaultValue(DefaultValues.ADKAR_ALMASSAE_TIME);
		adkarAlmassaeTime.setMax(60);
		adkarAlmassaeTime.setSuffix(reshapeSentence(R.string.before_maghrib));
		adkarAlmassae.addPreference(adkarAlmassaeTime);
		adkarAlmassaeTime.setDependency(Keys.ADKAR_ALMASSAE_NOTIFICATION_KEY);
		
		SalaatSwitchScreenPreference adkarAnnawm=new SalaatSwitchScreenPreference(this);
		adkarAnnawm.setTitle(reshapeSentence(R.string.adkar_annawm_notification_title));
		adkarAnnawm.setSummary(reshapeSentence(R.string.adkar_annawm_notification_summary));
		adkarAnnawm.setKey(Keys.ADKAR_ANNAWM_NOTIFICATION_KEY);
		adkarAnnawm.setDefaultValue(DefaultValues.ADKAR_NOTIFICATIONS);
		
		root.addPreference(adkarAnnawm);
		
		TimePickerPreference adkarAnnawmTime=new TimePickerPreference(this);
		adkarAnnawmTime.setTitle(reshapeSentence(R.string.adkar_annawm_time_title));
		adkarAnnawmTime.setDefaultValue(DefaultValues.ADKAR_ANNAWM_TIME);
		adkarAnnawmTime.setDefaultTime(DefaultValues.ADKAR_ANNAWM_TIME);
		adkarAnnawmTime.setKey(Keys.ADKAR_ANNAWM_TIME_KEY);
		adkarAnnawm.addPreference(adkarAnnawmTime);
		adkarAlmassaeTime.setDependency(Keys.ADKAR_ANNAWM_NOTIFICATION_KEY);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==NOTIFICATION_RINGTONE_PICK_CODE && resultCode==RESULT_OK)
		{
	          Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
	          if(uri!=null)
	          {
	        	  getDefaultSharedPreferences().edit().putString(Keys.ADKAR_NOTIFICATION_TONE_KEY, uri.toString()).commit();
	          }
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onStop() {
		getDefaultSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		super.onStop();
	}

	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		if(Keys.ADKAR_ANNAWM_NOTIFICATION_KEY.equals(key))
		{
			if(prefs.getBoolean(key, DefaultValues.ADKAR_NOTIFICATIONS))
			{
				EventsHandler handler=new EventsHandler(getApplicationContext());
				handler.scheduleAdkarEvents(-1, PrayerTimesCalcluationUtils.getCurrentPrayerTimes());
			}
			else
			{
				EventsHandler handler=new EventsHandler(getApplicationContext());
				handler.cancelAlarm(EventsHandler.SHOW_ADKAR_ALNAWM_REQUEST_CODE);
			}
		}
		if(Keys.ADKAR_ANNAWM_TIME_KEY.equals(key))
		{
			EventsHandler handler=new EventsHandler(getApplicationContext());
			handler.scheduleAdkarEvents(-1, PrayerTimesCalcluationUtils.getCurrentPrayerTimes());
		}
		
		if(Keys.ADKAR_SABAH_NOTIFICATION_KEY.equals(key))
		{
			if(prefs.getBoolean(key, DefaultValues.ADKAR_NOTIFICATIONS))
			{
				EventsHandler handler=new EventsHandler(getApplicationContext());
				handler.scheduleAdkarEvents(-1, PrayerTimesCalcluationUtils.getCurrentPrayerTimes());
			}
			else
			{
				EventsHandler handler=new EventsHandler(getApplicationContext());
				handler.cancelAlarm(EventsHandler.SHOW_ADKAR_ASSABAH_REQUEST_CODE);
			}
		}
		if(Keys.ADKAR_SABAH_TIME_KEY.equals(key))
		{
			EventsHandler handler=new EventsHandler(getApplicationContext());
			handler.scheduleAdkarEvents(-1, PrayerTimesCalcluationUtils.getCurrentPrayerTimes());
		}

		
		if(Keys.ADKAR_ALMASSAE_NOTIFICATION_KEY.equals(key))
		{
			if(prefs.getBoolean(key, DefaultValues.ADKAR_NOTIFICATIONS))
			{
				EventsHandler handler=new EventsHandler(getApplicationContext());
				handler.scheduleAdkarEvents(-1, PrayerTimesCalcluationUtils.getCurrentPrayerTimes());
			}
			else
			{
				EventsHandler handler=new EventsHandler(getApplicationContext());
				handler.cancelAlarm(EventsHandler.SHOW_ADKAR_ALMASSAE_REQUEST_CODE);
			}
		}
		if(Keys.ADKAR_ALMASSAE_TIME_KEY.equals(key))
		{
			EventsHandler handler=new EventsHandler(getApplicationContext());
			handler.scheduleAdkarEvents(-1, PrayerTimesCalcluationUtils.getCurrentPrayerTimes());
		}

	}

}
