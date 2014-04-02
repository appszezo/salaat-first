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

package org.hicham.salaat.ui.activities;

import static org.arabic.ArabicUtilities.reshapeSentence;

import org.hicham.salaat.R;
import org.hicham.salaat.ui.fragments.AdkarFragment;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.TextView;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

public class AdkarActivity extends Activity{
	
	public static final String ADKAR_TITLE="adkar_title";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.adkar_activity_layout);
		Bundle extras=getIntent().getExtras();
		
		if(extras!=null&&extras.containsKey(ADKAR_TITLE))
		{
			((TextView)findViewById(R.id.adkar_title)).setText(reshapeSentence(extras.getInt(ADKAR_TITLE)));
		}
		
		if(extras!=null&&extras.containsKey(AdkarFragment.ADHKAR_ARRAY_ID))
		{
			AdkarFragment fragment=new AdkarFragment();
			fragment.setArguments(extras);
			FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
			ft.add(R.id.adkar_fragment, fragment).commit();		
		}	
	}
}
