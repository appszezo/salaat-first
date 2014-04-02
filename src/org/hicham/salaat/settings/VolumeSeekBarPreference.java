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

import org.hicham.salaat.PrayerTimesActivity;
import org.hicham.salaat.media.MediaHandler;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.util.Log;
import android.widget.SeekBar;

public class VolumeSeekBarPreference extends SeekBarPreference{
	
	
	MediaHandler mediaHandler;
	public VolumeSeekBarPreference(Context context) {
		super(context);
	}
	
	@Override
	protected void onPrepareDialogBuilder(Builder builder) {
		super.onPrepareDialogBuilder(builder);
		mediaHandler=new MediaHandler(getContext());
	}
	@Override
	public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
		super.onProgressChanged(seek, value, fromTouch);
		//play sound
		if(fromTouch)
		{
			mediaHandler.updateVolume(value);
			mediaHandler.playSound(null);
		}
	}
	
	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		mediaHandler.stopSound();
	}
	
}
