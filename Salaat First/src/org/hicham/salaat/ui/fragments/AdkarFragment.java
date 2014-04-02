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
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.ImageButton;
import org.holoeverywhere.widget.TextView;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class AdkarFragment extends Fragment {

	private View rootView;
	private TextView dikrText;
	private TextView dikrNumber;
	private ImageButton rightButton;
	private ImageButton leftButton;
	private String[] adkarArray;
	private int currentDikr;
	private int numberOfAdkar;
	
	public static final String ADHKAR_ARRAY_ID="adkhar_id";
	private static final String CURRENT_DIKR="current";
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if(savedInstanceState!=null)
		{
			currentDikr=savedInstanceState.getInt("current_dikr");
		}
		adkarArray=getResources().getStringArray(getArguments().getInt(ADHKAR_ARRAY_ID));
		numberOfAdkar=adkarArray.length;
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView=inflater.inflate(R.layout.adkar_layout, container, false);
		dikrText=(TextView) rootView.findViewById(R.id.dikr_text);
		dikrNumber=(TextView) rootView.findViewById(R.id.dikr);
		rightButton=(ImageButton) rootView.findViewById(R.id.right);
		leftButton=(ImageButton) rootView.findViewById(R.id.left);
		Typeface tf=Typeface.createFromAsset(getActivity().getAssets(), "DroidNaskh-Regular.ttf");
		dikrText.setTypeface(tf);
		if(currentDikr==0)
			rightButton.setEnabled(false);
		
		rightButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showPreviousDikr();
			}
		});
		
		leftButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showNextDikr();
			}
		});
		return rootView;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		updateUi();
		System.out.println("onStart fragment "+getSupportActionBar().getNavigationMode());
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		if(outState==null)
			outState=new Bundle();
		outState.putInt(CURRENT_DIKR, currentDikr);
		super.onSaveInstanceState(outState);
	}
	
	private void showNextDikr()
	{
		if(currentDikr<numberOfAdkar-1)
		{
			currentDikr++;
			rightButton.setEnabled(true);
		}
		if(currentDikr==numberOfAdkar-1)
		{
			leftButton.setEnabled(false);
		}
		updateUi();
	}
	
	private void showPreviousDikr()
	{
		if(currentDikr>0)
		{
			currentDikr--;
			leftButton.setEnabled(true);
		}
		if(currentDikr==0)
		{
			rightButton.setEnabled(false);
		}
		updateUi();
	}
	
	private void updateUi()
	{
		String dikr=reshapeSentence(adkarArray[currentDikr]);
		dikrText.setText(Html.fromHtml(dikr));	
		dikrNumber.setText((currentDikr+1)+"/"+numberOfAdkar);
	}
}
