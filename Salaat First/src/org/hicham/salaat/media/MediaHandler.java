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

package org.hicham.salaat.media;

import static org.hicham.salaat.SalaatFirstApplication.TAG;

import java.io.IOException;

import org.hicham.salaat.settings.Keys;
import org.hicham.salaat.settings.Keys.DefaultValues;
import org.hicham.salaat.ui.activities.AdhanActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

public class MediaHandler {

	private Context context;
	private MediaPlayer mediaPlayer;
	private AudioManager audioManager;
	private SharedPreferences prefs;
	private int originalVolume = -1;
	private int volumeConfigured = -1;
	public static boolean isAlarmSolo=false;

	public MediaHandler(Context context) {
		this.context = context;
		mediaPlayer = new MediaPlayer();
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		volumeConfigured = prefs.getInt(Keys.VOLUME_KEY, DefaultValues.VOLUME);

		// final float volume = (float) (1 - (Math.log(AdhanActivity.MAX_VOLUME
		// - soundVolume) / Math.log(AdhanActivity.MAX_VOLUME)));
		// mediaPlayer.setVolume(soundVolume, soundVolume);
		audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		originalVolume = audioManager
				.getStreamVolume(AudioManager.STREAM_ALARM);
		int maxStreamVolume = audioManager
				.getStreamMaxVolume(AudioManager.STREAM_ALARM);
		if (volumeConfigured != 0
				&& (((double) volumeConfigured / (double) AdhanActivity.MAX_VOLUME) * maxStreamVolume) < 1)
			volumeConfigured = 3;
		audioManager
				.setStreamVolume(
						AudioManager.STREAM_ALARM,
						(int) (((double) volumeConfigured / (double) AdhanActivity.MAX_VOLUME) * maxStreamVolume),
						0);
	}

	public void cancelVolume() {
		if (originalVolume != -1)
			audioManager.setStreamVolume(AudioManager.STREAM_ALARM,
					originalVolume, 0);
	}

	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

	public int getVolumeConfigured() {
		return volumeConfigured;
	}

	/**
	 * 
	 * @param uri
	 *            uri to be played, null if you want using the default sound
	 */
	public void playSound(Uri uri) {
		if (uri == null) {
			uri = Uri.parse(prefs.getString(Keys.ADHAN_SOUND_URI_KEY,
					"android.resource://" + context.getPackageName()
							+ "/raw/makkah"));
		}
		try {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
			}
			mediaPlayer.reset();
		} catch (IllegalStateException e) {
			Log.e(TAG, "MediaPlayer object not initialized");
		}
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
		if(!isAlarmSolo)
			{
				audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
				isAlarmSolo=true;
			}
		try {
			mediaPlayer.setDataSource(context, uri);
			mediaPlayer.prepare();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mediaPlayer.start();
		System.out.println("start playing");
	}

	public void stopSound() {
		System.out.println("stop playing");
		try {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
			}
		} catch (IllegalStateException e) {
			Log.e(TAG, "MediaPlayer object not initialized");
		}
		if(isAlarmSolo)
			{
				audioManager.setStreamSolo(AudioManager.STREAM_ALARM, false);
				isAlarmSolo=false;
			}
	}
	
	public void releasePlayerAndRestoreVolume()
	{
		try
		{
			mediaPlayer.release();
		} catch (IllegalStateException e) {
			Log.e(TAG, "MediaPlayer object not initialized");
		}
		
		cancelVolume();
	}

	public void updateVolume(int volume) {
		volumeConfigured = volume;
		audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		// currentNotificationVolume=audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
		int maxStreamVolume = audioManager
				.getStreamMaxVolume(AudioManager.STREAM_ALARM);
		if (volume != 0
				&& (((double) volume / (double) AdhanActivity.MAX_VOLUME) * maxStreamVolume) < 1)
			/*
			 * in this case, the volume is !=0, but the given value will be
			 * null, so adjust it to the lower possible value 1
			 */
			audioManager.setStreamVolume(AudioManager.STREAM_ALARM, 1, 0);
		else
			audioManager
					.setStreamVolume(
							AudioManager.STREAM_ALARM,
							(int) (((double) volume / (double) AdhanActivity.MAX_VOLUME) * maxStreamVolume),
							0);
	}
}
