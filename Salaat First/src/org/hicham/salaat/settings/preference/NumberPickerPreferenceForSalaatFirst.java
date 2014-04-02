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

package org.hicham.salaat.settings.preference;

import org.holoeverywhere.preference.NumberPickerPreference;

import android.content.Context;

public class NumberPickerPreferenceForSalaatFirst extends
		NumberPickerPreference {

	private int maxValue = 10;
	private int minValue = -10;

	public NumberPickerPreferenceForSalaatFirst(Context context) {
		super(context);
		setMinValue(maxValue + minValue);
		setMaxValue(maxValue - minValue);
		//setDefaultValue((maxValue - minValue) / 2);
		String[] nums = new String[maxValue - minValue + 1];
		for (int i = minValue; i <= maxValue; i++)
			nums[i + maxValue] = "" + i;
		getNumberPicker().setDisplayedValues(nums);
	}
	
	public NumberPickerPreferenceForSalaatFirst(Context context, int minValue, int maxValue)
	{
		super(context);
		this.minValue=minValue;
		this.maxValue=maxValue;
		setMinValue(maxValue + minValue);
		setMaxValue(maxValue - minValue);
		//setDefaultValue((maxValue - minValue) / 2);
		String[] nums = new String[maxValue - minValue + 1];
		for (int i = minValue; i <= maxValue; i++)
			nums[i + maxValue] = "" + i;
		getNumberPicker().setDisplayedValues(nums);
	}

	@Override
	protected int getPersistedInt(int defaultReturnValue) {
		if (!shouldPersist()) {
			return defaultReturnValue;
		}
		int def = getPreferenceManager().getSharedPreferences().getInt(
				getKey(), 0);
		return def - minValue;
	}
	
	@Override
	public void setDefaultValue(Object defaultValue) {
		int value=(Integer)defaultValue;
		super.setDefaultValue(value - minValue);
	}

	@Override
	protected boolean persistInt(int value) {
		return super.persistInt(value + minValue);
	}
}
