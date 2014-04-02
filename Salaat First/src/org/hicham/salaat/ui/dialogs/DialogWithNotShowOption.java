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

import org.hicham.salaat.R;
import org.hicham.salaat.SalaatFirstApplication;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.widget.CheckBox;
import org.holoeverywhere.widget.TextView;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;

public class DialogWithNotShowOption extends DialogFragment {

	public final static String SHARED_PREFERENCE_KEY = "key";
	public static final String TEXT_KEY = "text_key";
	public static final String CHECKBOX_KEY = "checkbox_key";
	private String text;
	private CheckBox checkBox;
	private String key;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		text = getArguments().getString(TEXT_KEY);
		key = getArguments().getString(SHARED_PREFERENCE_KEY);

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder adbuilder = new AlertDialog.Builder(
				getSupportActivity());
		View view =  getLayoutInflater().inflate(
				R.layout.dialog_with_not_show_option);
		adbuilder.setView(view);
		TextView textView = (TextView) view.findViewById(R.id.text);
		textView.setText(reshapeSentence(text));
		Linkify.addLinks(textView, Linkify.WEB_URLS | Linkify.EMAIL_ADDRESSES);
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		checkBox = (CheckBox) view.findViewById(R.id.checkbox);
		checkBox.setText(reshapeSentence(R.string.hadith_share_dialog_checkbox));
		adbuilder.setPositiveButton("Ok", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dismiss();
			}
		});
		adbuilder.setIcon(android.R.drawable.ic_dialog_info);
		return adbuilder.create();
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		if (checkBox.isChecked())
			SalaatFirstApplication.prefs.edit().putBoolean(key, true).commit();
	}
}
