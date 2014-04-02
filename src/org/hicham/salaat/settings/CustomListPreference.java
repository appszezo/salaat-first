/** This file is part of Salaat First.
 *
 *   Licensed under the Creative Commons Attribution-NonCommercial 4.0 International Public License;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at:
 *   
 *   http://creativecommons.org/licenses/by-nc/4.0/legalcode
 *
*
*	@author Hicham BOUSHABA 2012 <hicham.boushaba@gmail.com>
*	
*/

package org.hicham.salaat.settings;

import java.io.IOException;
import static com.ahmadiv.dari.DariGlyphUtils.reshapeText;

import org.hicham.salaat.AdhanActivity;
import org.hicham.salaat.PrayerTimesActivity;
import org.hicham.salaat.R;
import org.hicham.salaat.media.MediaHandler;

import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.preference.PreferenceManager.OnActivityResultListener;
import android.util.Log;

public class CustomListPreference extends ListPreference{

	private int mClickedDialogEntryIndex;
	private MediaHandler mediaHandler;
	private Uri chosenUri;

	public CustomListPreference(Context context) {
		super(context);
	}
	
	@Override
	protected void onPrepareDialogBuilder(Builder builder) {
		super.onPrepareDialogBuilder(builder);
		mediaHandler=new MediaHandler(getContext());
		if (super.getEntries() == null || super.getEntryValues() == null) {
			throw new IllegalStateException(
					"ListPreference requires an entries array and an entryValues array.");
		}

		mClickedDialogEntryIndex = findIndexOfValue(getValue());
		CharSequence[] entries=new CharSequence[getEntries().length+1];
		for(int i=0;i<entries.length-1;i++)
		{
			entries[i]=getEntries()[i];
		}
		entries[entries.length-1]=reshapeText(getContext().getString(R.string.browse), PrayerTimesActivity.isReshapingNessecary);
		builder.setSingleChoiceItems(entries, mClickedDialogEntryIndex,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						// setting the selected item
						mClickedDialogEntryIndex = which;

						// playing the adhan sound
						if (which <= getEntryValues().length-1) {
								String value = getEntryValues()[which]
										.toString();
								chosenUri=getUri(value);
								mediaHandler.playSound(chosenUri);
						}
						else
						{
							mediaHandler.stopSound();
							dialog.dismiss();
							callChangeListener("");
						}
					}
				});

		builder.setPositiveButton("Ok", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				CustomListPreference.this.onClick(dialog,
						DialogInterface.BUTTON_POSITIVE);
				dialog.dismiss();
			}
		});
	}

	private Uri getUri(String sound) {
		Class rawClass = R.raw.class;
		int res = 0;
		try {
			res = (Integer) (rawClass.getField(sound).get(null));
		} catch (IllegalArgumentException e1) {
			Log.e(PrayerTimesActivity.TAG,
					e1.getClass().getName() + " " + e1.getMessage());
			return null;
		} catch (SecurityException e1) {
			Log.e(PrayerTimesActivity.TAG,
					e1.getClass().getName() + " " + e1.getMessage());
			return null;
		} catch (IllegalAccessException e1) {
			Log.e(PrayerTimesActivity.TAG,
					e1.getClass().getName() + " " + e1.getMessage());
			return null;
		} catch (NoSuchFieldException e1) {
			Log.e(PrayerTimesActivity.TAG,
					e1.getClass().getName() + " " + e1.getMessage());
			return null;
		}
		return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
				+ "://"
				+ getContext().getResources()
						.getResourcePackageName(res)
				+ '/'
				+ getContext().getResources().getResourceTypeName(res)
				+ '/'
				+ getContext().getResources().getResourceEntryName(res));
	}


	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (mediaHandler != null) {
			mediaHandler.stopSound();
		}
		
		super.onDialogClosed(positiveResult);

		if (positiveResult && mClickedDialogEntryIndex >= 0
				&& getEntryValues() != null&&chosenUri!=null) {
			String value = getEntryValues()[mClickedDialogEntryIndex]
					.toString();
			if (callChangeListener(value)) {
				setValue(value);
				getSharedPreferences().edit().putString(Keys.ADHAN_SOUND_URI_KEY, chosenUri.toString()).commit();
			}
		}
	}
}
