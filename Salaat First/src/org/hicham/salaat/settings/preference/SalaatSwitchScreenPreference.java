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

package org.hicham.salaat.settings.preference;

import static org.arabic.ArabicUtilities.reshapeSentence;

import org.hicham.salaat.SalaatFirstApplication;
import org.hicham.salaat.settings.Keys;
import org.hicham.salaat.settings.Keys.DefaultValues;
import org.hicham.salaat.R;
import org.holoeverywhere.preference.SwitchScreenPreference;
import org.holoeverywhere.widget.TextView;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.View;

public class SalaatSwitchScreenPreference extends SwitchScreenPreference {

	public SalaatSwitchScreenPreference(Context context) {
		super(context);
		setSwitchTextOff(reshapeSentence(getSwitchTextOff().toString()));
		setSwitchTextOn(reshapeSentence(getSwitchTextOn().toString()));
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setEllipsize(TruncateAt.MARQUEE);
		title.setMarqueeRepeatLimit(-1);
		title.setHorizontallyScrolling(true);
		title.setSingleLine(true);
		title.setSelected(true);

		if (SalaatFirstApplication.prefs.getString(Keys.LANGUAGE_KEY, DefaultValues.LANGUAGE)
				.contains("ar"))
			;// title.setGravity(Gravity.RIGHT);

	}

}