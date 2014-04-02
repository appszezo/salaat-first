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

package org.hicham.salaat.ui.dialogs;

import static org.arabic.ArabicUtilities.reshapeSentence;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;

import android.os.Bundle;
import android.text.Html;

public class FeaturesDialog extends LinkifiedAlertDialog {
	
	
	public static final String FEATURES_KEY = "features_key";
	private String title;
	private String[] features;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments().containsKey(FEATURES_KEY))
		{
				features=getArguments().getStringArray(FEATURES_KEY);
				title = features[0];
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder adbuilder = new AlertDialog.Builder(
				getSupportActivity());
		if(title!=null)
			adbuilder.setTitle(reshapeSentence(title));
		StringBuffer message=new StringBuffer();
		for(int i=1; i<features.length; i++)
		{
			message.append("<p>"+reshapeSentence(features[i])+"<br /></p>");
		}
		adbuilder.setMessage(Html.fromHtml(message.toString()));
		adbuilder.setPositiveButton("OK", null);
		Dialog dialog = adbuilder.create();
		return dialog;
	}
}
