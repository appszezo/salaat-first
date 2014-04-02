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

import org.hicham.salaat.R;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.widget.TextView;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Gravity;

public class LinkifiedAlertDialog extends DialogFragment {

	public static final String TEXT_KEY = "text_key";
	private String text;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments().containsKey(TEXT_KEY))
				text = getArguments().getString(TEXT_KEY);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder adbuilder = new AlertDialog.Builder(
				getSupportActivity());
		if(text!=null)
			adbuilder.setMessage(text);
		adbuilder.setPositiveButton("OK", null);
		Dialog dialog = adbuilder.create();
		return dialog;
	}

	@Override
	public void onStart() {
		super.onStart();
		TextView message = (TextView) getDialog().findViewById(R.id.message);
		message.setTextAppearance(getActivity(), R.style.Holo_TextAppearance_Small_Light);
		message.setGravity(Gravity.CENTER);
		Linkify.addLinks(message, Linkify.WEB_URLS | Linkify.EMAIL_ADDRESSES);
		message.setMovementMethod(LinkMovementMethod.getInstance());

	}
}
