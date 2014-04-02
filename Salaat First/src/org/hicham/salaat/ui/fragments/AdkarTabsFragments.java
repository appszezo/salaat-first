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

package org.hicham.salaat.ui.fragments;

import static org.arabic.ArabicUtilities.reshapeSentence;

import org.hicham.salaat.R;
import org.holoeverywhere.app.TabSwipeFragment;

import android.os.Bundle;
import android.view.View;

public class AdkarTabsFragments extends TabSwipeFragment{

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		//getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		System.out.println("onViewCreated "+getSupportActionBar().getNavigationMode());

	}
	
	@Override
	public void onStart() {
		super.onStart();
		System.out.println("onStart "+getSupportActionBar().getNavigationMode());
	}
	
	public void onHandleTabs() {
		addTab(reshapeSentence("أذكار الصباح"), AdkarFragment.class, makeBundle(R.array.adkar_assabah));
		addTab(reshapeSentence("أذكار المساء"), AdkarFragment.class, makeBundle(R.array.adkar_almassae));
		addTab(reshapeSentence("أذكار النوم"), AdkarFragment.class, makeBundle(R.array.adkar_annawm));
		
		System.out.println("onHandleTabs "+getSupportActionBar().getNavigationMode());
	}
	
	private Bundle makeBundle(int adkarId)
	{
		Bundle bundle=new Bundle();
		bundle.putInt(AdkarFragment.ADHKAR_ARRAY_ID, adkarId);
		return bundle;
	}
}
