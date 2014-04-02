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

package org.hicham.salaat;

import static com.ahmadiv.dari.DariGlyphUtils.reshapeText;

import org.hicham.alarm.AlarmReceiver;
import org.hicham.salaat.media.MediaHandler;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AdhanActivity extends Activity {
	public static final int MAX_VOLUME=20;
	private MediaHandler mediaHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MainActivity.validateLanguage(this);
		setContentView(R.layout.adhan);
		//wake the phone and show the activity even when locked
		getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|LayoutParams.FLAG_TURN_SCREEN_ON);
		LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout);
		Display displayManager = getWindowManager().getDefaultDisplay();
		int width = displayManager.getWidth();
		int height = displayManager.getHeight();
		layout.setPadding(width / 4, height / 5, 0, 0);
		TextView textView1 = (TextView) findViewById(R.id.textView1);
		TextView textView2 = (TextView) findViewById(R.id.textView2);
		textView1.setText(reshapeText(getString(R.string.adhan_activity_text),
				PrayerTimesActivity.isReshapingNessecary));
		String prayerName = getIntent().getExtras().getString(
				AlarmReceiver.PRAYER_NAME_KEY);
		textView2.setText(reshapeText(prayerName,
				PrayerTimesActivity.isReshapingNessecary));
		
		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL&&audioManager.getMode()!=AudioManager.MODE_IN_CALL) {
			mediaHandler=new MediaHandler(this);
			mediaHandler.playSound(null);
			mediaHandler.getMediaPlayer().setOnCompletionListener(new OnCompletionListener() {

				public void onCompletion(MediaPlayer mp) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						Log.e(PrayerTimesActivity.TAG, e.getMessage());
					}
					// close the activity
					Log.i(PrayerTimesActivity.TAG, "Adhan playing completed");
					finish();
				}
			});
		}
		else
		{
			//show the activity for 30 seconds
			new Thread()
			{
				public void run() {
					try {
						Thread.sleep(30*1000);
						finish();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
		}
	}
		
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
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		switch (event.getKeyCode()) {		
	    case KeyEvent.KEYCODE_VOLUME_UP:
	    	if(mediaHandler!=null&&mediaHandler.getLastVolumeSet()<20)
	    		mediaHandler.updateVolume(mediaHandler.getLastVolumeSet()+1);
	    	return true;
	    case KeyEvent.KEYCODE_VOLUME_DOWN:
	    	if(mediaHandler!=null&&mediaHandler.getLastVolumeSet()>0)
	    		mediaHandler.updateVolume(mediaHandler.getLastVolumeSet()-1);
	    	return true;
	    }
		return super.onKeyUp(keyCode, event);
	}
	
	@Override
	public boolean onKeyLongPress (int keyCode, KeyEvent event)
	{
		switch (event.getKeyCode()) {		
	    case KeyEvent.KEYCODE_VOLUME_UP:
	    	if(mediaHandler!=null)
	    		mediaHandler.stopSound();
	    	return true;
	    case KeyEvent.KEYCODE_VOLUME_DOWN:
	    	if(mediaHandler!=null)
	    		mediaHandler.stopSound();
	    	return true;
	    }
		return super.onKeyLongPress(keyCode, event);
	}
	

	@Override
	protected void onStop() {
		if (mediaHandler != null)
			mediaHandler.stopSound();
		super.onStop();
	}
}