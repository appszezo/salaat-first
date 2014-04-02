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

import org.hicham.salaat.PrayerTimesActivity;
import org.hicham.salaat.R;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;

public class AdhanSettingsActivity extends PreferenceActivity{
	private static final int PICKFILE_RESULT_CODE = 1;
	private ListPreference sounds;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PreferenceScreen screen=getPreferenceManager().createPreferenceScreen(this);
		//choosing the adhan sound
		sounds=new CustomListPreference(this);
		sounds.setKey(Keys.ADHAN_SOUND_KEY);
		sounds.setEntries(reshapeText(getResources().getStringArray(R.array.sounds_entries),isReshapingNessecary));
		sounds.setEntryValues(R.array.sounds_entryValues);
		sounds.setDefaultValue("makkah");
		sounds.setTitle(reshapeText(getString(R.string.choosing_adhan_sound_title), isReshapingNessecary));
		screen.addPreference(sounds);
		sounds.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				String value=(String) newValue;
				if(value.equals(""))
				{
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.setType("audio/*");
					startActivityForResult(intent, PICKFILE_RESULT_CODE);	
					return false;
				}
				else
					return true;
			}
		});
		
		//setting the volume
		VolumeSeekBarPreference volume=new VolumeSeekBarPreference(this);
		volume.setMax(20);
		volume.setDefaultValue(15);
		volume.setKey(Keys.VOLUME_KEY);
		volume.setTitle(reshapeText(getString(R.string.volume), isReshapingNessecary));
		screen.addPreference(volume);
		
		setPreferenceScreen(screen);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode)
		{
		case PICKFILE_RESULT_CODE:
			if (resultCode == RESULT_OK) {
				String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(data.getData().getLastPathSegment());
				String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
				if(mimetype==null)
				{
					ContentResolver cr=getContentResolver();
					mimetype=cr.getType(data.getData());
				}
				if(mimetype==null||!mimetype.contains("audio"))
				{
					Toast.makeText(this, "The selected file isn't an audio file", Toast.LENGTH_SHORT).show();
					return;
				}
				Log.i(PrayerTimesActivity.TAG, mimetype);
				String FileName = data.getData().getLastPathSegment();
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(this);
				Editor editor = prefs.edit();
				sounds.setValue("");
				editor.putString(Keys.ADHAN_SOUND_URI_KEY, data.getData().toString());
				editor.commit();
				Toast.makeText(this, FileName + " "+ reshapeText(getString(R.string.file_selected), isReshapingNessecary), Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}
}
