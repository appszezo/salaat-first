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

package org.hicham.salaat.ui.activities;

import static org.arabic.ArabicUtilities.reshapeSentence;
import static org.hicham.salaat.SalaatFirstApplication.TAG;

import org.hicham.salaat.R;
import org.hicham.salaat.SalaatFirstApplication;
import org.hicham.salaat.alarm.AlarmReceiver;
import org.hicham.salaat.media.MediaHandler;
import org.hicham.salaat.settings.Keys;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.TextView;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.WindowManager.LayoutParams;

public class AdhanActivity extends Activity {
	public static final int MAX_VOLUME = 20;
	private MediaHandler mediaHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		SalaatFirstApplication.getLastInstance().refreshLanguage();
		setContentView(R.layout.adhan);
		// wake the phone and show the activity even when locked
		getWindow().addFlags(
				android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
						| LayoutParams.FLAG_TURN_SCREEN_ON
						| LayoutParams.FLAG_KEEP_SCREEN_ON);
		LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout);
		Display displayManager = getWindowManager().getDefaultDisplay();
		int width = displayManager.getWidth();
		int height = displayManager.getHeight();
		layout.setPadding(width / 4, height / 5, 0, 0);
		TextView textView1 = (TextView) findViewById(R.id.textView1);
		TextView textView2 = (TextView) findViewById(R.id.textView2);
		textView1.setText(reshapeSentence((R.string.adhan_activity_text)));
		int prayer = getIntent().getExtras().getInt(AlarmReceiver.PRAYER_ID);
		textView2.setText(SalaatFirstApplication
				.getPrayerName(prayer));

		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL
				&& audioManager.getMode() != AudioManager.MODE_IN_CALL
				&& audioManager.getMode() != AudioManager.MODE_RINGTONE) {
			
			mediaHandler = new MediaHandler(this);
			Uri uri = Uri.parse(SalaatFirstApplication.prefs.getString(Keys.ADHAN_SOUND_URI_KEY + prayer,
					"android.resource://" + this.getPackageName()
							+ "/raw/makkah"));
			mediaHandler.playSound(uri);
			listenToPhoneStates();/*register phone state listener*/
			mediaHandler.getMediaPlayer().setOnCompletionListener(
					new OnCompletionListener() {

						public void onCompletion(MediaPlayer mp) {
							try {
								Log.i(TAG, "playing completed");
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								Log.e(TAG, e.getMessage());
							}
							// close the activity
							Log.i(TAG, "Adhan playing completed");
							finish();
						}
					});
		} else {
			// show the activity for 30 seconds
			new Thread() {
				@Override
				public void run() {
					try {
						Thread.sleep(30 * 1000);
						finish();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			event.startTracking();
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			event.startTracking();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (mediaHandler != null)
				mediaHandler.stopSound();
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (mediaHandler != null)
				mediaHandler.stopSound();
			return true;
		}
		return super.onKeyLongPress(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (mediaHandler != null && mediaHandler.getVolumeConfigured() < 20)
				mediaHandler
						.updateVolume(mediaHandler.getVolumeConfigured() + 1);
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (mediaHandler != null && mediaHandler.getVolumeConfigured() > 0)
				mediaHandler
						.updateVolume(mediaHandler.getVolumeConfigured() - 1);
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onStop() {

		if (isFinishing() && mediaHandler != null)
		{
			mediaHandler.stopSound();
			mediaHandler.releasePlayerAndRestoreVolume();
		}
		if(MediaHandler.isAlarmSolo)
			((AudioManager)getSystemService(AUDIO_SERVICE)).setStreamMute(AudioManager.STREAM_ALARM, false);
		super.onStop();
	}
	
	private void listenToPhoneStates()
	{
		final TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String number) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
            			if (mediaHandler != null)
            				mediaHandler.stopSound();
            			if(MediaHandler.isAlarmSolo)
            				((AudioManager)getSystemService(AUDIO_SERVICE)).setStreamMute(AudioManager.STREAM_ALARM, false);
                    	break;
                }
            }
        };
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
	}
}