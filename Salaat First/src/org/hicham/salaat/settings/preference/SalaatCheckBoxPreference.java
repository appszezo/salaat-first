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

import org.holoeverywhere.preference.CheckBoxPreference;
import org.hicham.salaat.R;
import org.holoeverywhere.widget.TextView;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.View;

public class SalaatCheckBoxPreference extends CheckBoxPreference {

	public SalaatCheckBoxPreference(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
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
	}
}
