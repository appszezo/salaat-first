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

package org.hicham.salaat.media;

import java.io.IOException;

import org.hicham.salaat.AdhanActivity;
import org.hicham.salaat.PrayerTimesActivity;
import org.hicham.salaat.settings.Keys;

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
	private int currentNotificationVolume=-1;
	private int lastVolumeSet=-1;

	
	public MediaHandler(Context context) {
		this.context=context;
		mediaPlayer=new MediaPlayer();
		prefs=PreferenceManager.getDefaultSharedPreferences(context);
		lastVolumeSet=prefs.getInt(Keys.VOLUME_KEY, 15);

		//final float volume = (float) (1 - (Math.log(AdhanActivity.MAX_VOLUME - soundVolume) / Math.log(AdhanActivity.MAX_VOLUME)));
		//mediaPlayer.setVolume(soundVolume, soundVolume);
		audioManager=(AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		currentNotificationVolume=audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
		int maxStreamVolume=audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
		if(lastVolumeSet!=0&&(((double)lastVolumeSet/(double)AdhanActivity.MAX_VOLUME)*(double)maxStreamVolume)<1)
			lastVolumeSet=3;
		audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, (int)(((double)lastVolumeSet/(double)AdhanActivity.MAX_VOLUME)*(double)maxStreamVolume), 0);
	}
	/**
	 * 
	 * @param uri uri to be played, null if you want using the default sound
	 */
	public void playSound(Uri uri)
	{
		if(uri==null)
		{
			uri=Uri.parse(prefs.getString(Keys.ADHAN_SOUND_URI_KEY, "android.resource://org.hicham.salaat/raw/makkah"));
		}
		try
		{
			if(mediaPlayer.isPlaying())
		{
			mediaPlayer.stop();
		}
			mediaPlayer.reset();
		}
		catch(IllegalStateException e)
		{
			Log.e(PrayerTimesActivity.TAG, "MediaPlayer object not initialized");
		}
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_SYSTEM);
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
	}
	
	public void stopSound() {
		try
		{
			if(mediaPlayer.isPlaying())
		{
			mediaPlayer.stop();
		}
		mediaPlayer.release();
		}
		catch(IllegalStateException e)
		{
			Log.w(PrayerTimesActivity.TAG, "MediaPlayer object not initialized");
		}
		if(currentNotificationVolume!=-1)
			audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, currentNotificationVolume, 0);
	}
	
	public int getLastVolumeSet()
	{
		return lastVolumeSet;
	}
	
	public void updateVolume(int volume)
	{
		lastVolumeSet=volume;
		audioManager=(AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		//currentNotificationVolume=audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
		int maxStreamVolume=audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
		if(volume!=0&&(((double)volume/(double)AdhanActivity.MAX_VOLUME)*(double)maxStreamVolume)<1)
			volume=3;
		audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, (int)(((double)volume/(double)AdhanActivity.MAX_VOLUME)*(double)maxStreamVolume), 0);
	}
	
	public void cancelVolume()
	{
		if(currentNotificationVolume!=-1)
			audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, currentNotificationVolume, 0);
	}
	
	public MediaPlayer getMediaPlayer()
	{
		return mediaPlayer;
	}
}
