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
import static org.hicham.salaat.SalaatFirstApplication.prefs;

import org.hicham.salaat.R;
import org.hicham.salaat.db.AhadithDatabaseHelper;
import org.hicham.salaat.db.AhadithDatabaseHelper.Hadith;
import org.hicham.salaat.util.CustomShareActionProvider;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.CheckBox;
import org.holoeverywhere.widget.TextView;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class HadithFragment extends Fragment {

	private static final String SHOW_HADITH_DIALOG_KEY = "show_hadith_key";
	private View rootView;
	private ShareActionProvider actionProvider;
	private Hadith hadith;
	private MenuItem shareMenuItem;
	private MenuItem alternateShareMenuItem;


	private Intent getShareIntent() {
		Intent share = new Intent(android.content.Intent.ACTION_SEND);
		share.setType("text/plain");
		// Add data to the intent, the receiving app will decide
		// what to do with it.
		share.putExtra(Intent.EXTRA_SUBJECT, reshapeSentence("حديث نبوي"));
		if (hadith != null)
			share.putExtra(Intent.EXTRA_TEXT, reshapeSentence(hadith.getText()));
		return share;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.hadith_layout, container, false);
		hadith = AhadithDatabaseHelper.getRandomHadith();
		Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
				"DroidNaskh-Regular.ttf");
		TextView hadithTextView = ((TextView) rootView
				.findViewById(R.id.hadith_text_field));
		hadithTextView.setText(reshapeSentence(hadith.getText()));
		hadithTextView.setTypeface(tf);
		TextView hadithReferenceTextView = ((TextView) rootView
				.findViewById(R.id.hadith_reference));
		// hadithReferenceTextView.setTypeface(tf);
		hadithReferenceTextView.setText(reshapeSentence(hadith.getReference()));
		return rootView;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 10:
			updateHadith();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		MenuItem refresh = menu.add(Menu.NONE, 10, Menu.NONE, "");
		MenuItemCompat.setShowAsAction(refresh, MenuItem.SHOW_AS_ACTION_ALWAYS);
		refresh.setIcon(R.drawable.ic_action_refresh);
		alternateShareMenuItem = menu.add(Menu.NONE, Menu.NONE, Menu.NONE, "");
		MenuItemCompat.setShowAsAction(alternateShareMenuItem,
				MenuItem.SHOW_AS_ACTION_ALWAYS);
		alternateShareMenuItem.setVisible(false);
		getMenuInflater().inflate(R.menu.action_bar_menu, menu);
		menu.findItem(R.id.action_settings).setTitle(
				reshapeSentence(R.string.settings_text));
		menu.findItem(R.id.action_about).setTitle(
				reshapeSentence(R.string.about));
		menu.findItem(R.id.action_quit).setTitle(
				reshapeSentence(R.string.quit_text));
		shareMenuItem = menu.findItem(R.id.action_share);
		actionProvider = (CustomShareActionProvider) MenuItemCompat
				.getActionProvider(shareMenuItem);
		actionProvider.setShareIntent(getShareIntent());
		if (prefs.getBoolean(SHOW_HADITH_DIALOG_KEY, true)) {
			shareMenuItem.setVisible(false);
			alternateShareMenuItem.setVisible(true);
			alternateShareMenuItem.setIcon(R.drawable.ic_share_animated);
			new Thread() {
				@Override
				public void run() {
					try {
						sleep(1000);

						((AnimationDrawable) alternateShareMenuItem.getIcon())
								.stop();
						((AnimationDrawable) alternateShareMenuItem.getIcon())
								.start();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
		}
		return;
	}

	@Override
	public void onStart() {
		if (prefs.getBoolean(SHOW_HADITH_DIALOG_KEY, true)) {
			AlertDialog.Builder adb = new AlertDialog.Builder(
					new ContextThemeWrapper(getSupportActivity(),
							R.style.Holo_Theme_Dialog_Light));
			View view =  getLayoutInflater().inflate(
					R.layout.dialog_with_not_show_option);
			adb.setView(view);
			((TextView) view.findViewById(R.id.text))
					.setText(reshapeSentence(R.string.share_hadith_dialog));
			((CheckBox) view.findViewById(R.id.checkbox))
					.setText(reshapeSentence(R.string.hadith_share_dialog_checkbox));

			adb.setPositiveButton("Ok", new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			adb.setOnDismissListener(new OnDismissListener() {

				public void onDismiss(DialogInterface dialog) {
					((AnimationDrawable) alternateShareMenuItem.getIcon())
							.stop();
					alternateShareMenuItem.setVisible(false);
					shareMenuItem.setVisible(true);
					CheckBox checkBox = (CheckBox) ((AlertDialog) dialog)
							.findViewById(R.id.checkbox);
					if (checkBox.isChecked())
						prefs.edit().putBoolean(SHOW_HADITH_DIALOG_KEY, false)
								.commit();
				}
			});

			adb.show();
			getSupportActivity().supportInvalidateOptionsMenu();
		}
		if (actionProvider != null) {
			actionProvider.setShareIntent(getShareIntent());
		}
		super.onStart();

	}

	private void updateHadith() {
		hadith = AhadithDatabaseHelper.getRandomHadith();
		((TextView) rootView.findViewById(R.id.hadith_text_field))
				.setText(reshapeSentence(hadith.getText()));
		((TextView) rootView.findViewById(R.id.hadith_reference))
				.setText(reshapeSentence(hadith.getReference()));
		actionProvider.setShareIntent(getShareIntent());
	}

}
