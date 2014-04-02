/** This file is part of Salaat First.
 *
 *   Salaat First is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *   
 *   Salaat First is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *   
 *   You should have received a copy of the GNU General Public License
 *   along with Salaat First.  If not, see <http://www.gnu.org/licenses/>.
 *
 *	@author Hicham BOUSHABA 2014 <hicham.boushaba@gmail.com>
 *	
 */

package org.hicham.salaat.settings.preference;

import org.hicham.salaat.media.MediaHandler;
import org.holoeverywhere.app.AlertDialog.Builder;
import org.holoeverywhere.widget.SeekBar;

import android.content.Context;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class VolumeSeekBarPreference extends SeekBarPreference {

	MediaHandler mediaHandler;

	public VolumeSeekBarPreference(Context context) {
		super(context);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		mediaHandler.stopSound();
		mediaHandler.releasePlayerAndRestoreVolume();
	}

	@Override
	protected void onPrepareDialogBuilder(Builder builder) {
		super.onPrepareDialogBuilder(builder);
		mediaHandler = new MediaHandler(getContext());
	}

	@Override
	public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
		super.onProgressChanged(seek, value, fromTouch);
		// play sound
		if (fromTouch) {
			mediaHandler.updateVolume(value);
			if (!mediaHandler.getMediaPlayer().isPlaying())
				{
					mediaHandler.playSound(null);
					listenToPhoneStates();
				}
			
		}
	}
	
	@Override
	protected void prepareUi(int n) {
		mValueText.setText(""+n);
	}
	
	private void listenToPhoneStates()
	{
		final TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String number) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
            			if (mediaHandler != null)
            				mediaHandler.stopSound();
            			if(MediaHandler.isAlarmSolo)
            				((AudioManager)getContext().getSystemService(Context.AUDIO_SERVICE)).setStreamMute(AudioManager.STREAM_ALARM, false);
                    	break;
                }
            }
        };
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
	}

}
