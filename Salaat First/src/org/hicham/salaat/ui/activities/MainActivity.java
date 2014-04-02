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
import static org.hicham.salaat.SalaatFirstApplication.TAG;
import static org.hicham.salaat.SalaatFirstApplication.prefs;

import org.hicham.salaat.R;
import org.hicham.salaat.SalaatFirstApplication;
import org.hicham.salaat.settings.CitySettings;
import org.hicham.salaat.settings.Keys;
import org.hicham.salaat.settings.Keys.DefaultValues;
import org.hicham.salaat.settings.Settings;
import org.hicham.salaat.settings.wizard.WizardWelcome;
import org.hicham.salaat.ui.dialogs.DialogWithNotShowOption;
import org.hicham.salaat.ui.dialogs.FeaturesDialog;
import org.hicham.salaat.ui.dialogs.LocationConfirmDialogFragment.LocationConfirmDialogListener;
import org.hicham.salaat.ui.fragments.AdkarTabsFragments;
import org.hicham.salaat.ui.fragments.DatePrayerTimesFragment;
import org.hicham.salaat.ui.fragments.HadithFragment;
import org.hicham.salaat.ui.fragments.PrayerTimesFragment;
import org.hicham.salaat.ui.fragments.QiblaFragment;
import org.hicham.salaat.util.CustomShareActionProvider;
import org.hicham.salaat.util.UpdateCheckAsyncTask;
import org.holoeverywhere.addon.AddonSlider;
import org.holoeverywhere.addon.Addons;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.slider.SliderMenu;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

@Addons(AddonSlider.class)
public class MainActivity extends Activity implements LocationConfirmDialogListener {
	ShareActionProvider actionProvider;
	UpdateCheckAsyncTask updateVersionCheckTask;
	public static final String FIRST_RUN_KEY="firstrun_version_2";
	
	public AddonSlider.AddonSliderA addonSlider() {
		return addon(AddonSlider.class);
	}

	private Intent getShareIntent() {
		Intent share = new Intent(android.content.Intent.ACTION_SEND);
		share.setType("text/plain");
		// Add data to the intent, the receiving app will decide
		// what to do with it.
		share.putExtra(Intent.EXTRA_SUBJECT, "Salaat First");
		share.putExtra(Intent.EXTRA_TEXT,
				"https://play.google.com/store/apps/details?id=org.hicham.salaat");
		return share;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/* schedule alarms as the city is changed */
		SalaatFirstApplication.getLastInstance()
				.scheduleNextPrayerNotification();
		Intent intent = getIntent();
		finish();
		startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * if the city configured in the settings doesn't exist, start the city
		 * settings activity
		 */
		if (SalaatFirstApplication.dBAdapter.getLocation(prefs.getString(
				Keys.CITY_KEY, DefaultValues.CITY)) == null) {
			prefs.edit().putString(Keys.CITY_KEY, DefaultValues.CITY).commit();
			Intent intent = new Intent(getBaseContext(), CitySettings.class);
			startActivityForResult(intent, 0);
			return;
		}

		((SalaatFirstApplication) getApplication()).refreshLanguage();

		// ------
		// show the wizard in first execution
		//TODO update the wizzard
		if (prefs.getBoolean(FIRST_RUN_KEY, true)) {
			Log.i(TAG, "first run");
			Intent intent = new Intent(getBaseContext(), WizardWelcome.class);
			startActivity(intent);
		}



		AddonSlider.AddonSliderA addon = addonSlider();
		final SliderMenu sliderMenu = addon
				.obtainDefaultSliderMenu(R.layout.menu);
		final Resources res = getResources();
		sliderMenu
				.add(reshapeSentence(R.string.prayer_times),
						PrayerTimesFragment.class)
				.setIcon(res.getDrawable(R.drawable.ic_prayer_times))
				.setBackgroundColor(
						res.getColor(R.color.drawer_item_background))
				.setSelectionHandlerColor(
						res.getColor(R.color.drawer_item_selection_handler))
				.setTag("prayer-times");
		sliderMenu
				.add(reshapeSentence(R.string.qibla), QiblaFragment.class)
				.setIcon(res.getDrawable(R.drawable.ic_compass))
				.setBackgroundColor(
						res.getColor(R.color.drawer_item_background))
				.setSelectionHandlerColor(
						res.getColor(R.color.drawer_item_selection_handler));
		sliderMenu
				.add(reshapeSentence(R.string.hadith_slider_item),
						HadithFragment.class)
				.setIcon(res.getDrawable(R.drawable.ic_book))
				.setBackgroundColor(
						res.getColor(R.color.drawer_item_background))
				.setSelectionHandlerColor(
						res.getColor(R.color.drawer_item_selection_handler));
		sliderMenu
				.add(reshapeSentence(R.string.date_prayertimes_slider_item),
						DatePrayerTimesFragment.class)
				.setIcon(res.getDrawable(R.drawable.ic_calendar))
				.setBackgroundColor(
						res.getColor(R.color.drawer_item_background))
				.setSelectionHandlerColor(
						res.getColor(R.color.drawer_item_selection_handler));
		
		sliderMenu
				.add(reshapeSentence(R.string.adkar_slider_item), AdkarTabsFragments.class)
				.setIcon(res.getDrawable(R.drawable.ic_adkar))
				.setBackgroundColor(
						res.getColor(R.color.drawer_item_background))
				.setSelectionHandlerColor(
						res.getColor(R.color.drawer_item_selection_handler));

		
		getSupportActionBar().setTitle("Salaat First");
		// getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_background));

		sliderMenu.setHandleHomeKey(true);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		addonSlider().setOverlayActionBar(false);
		updateVersionCheckTask=new UpdateCheckAsyncTask(this);
		updateVersionCheckTask.checkVersion();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.three_dots_item:
			System.out.println(item.hasSubMenu());
			System.out.println(item.getSubMenu().size());
			break;

		case R.id.action_settings:
			showSettings();
			break;
		case R.id.action_quit:
			finish();
			break;
		case R.id.action_about:
			showAboutActivity();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		getMenuInflater().inflate(R.menu.action_bar_menu, menu);
		menu.findItem(R.id.action_settings).setTitle(
				reshapeSentence(R.string.settings_text));
		menu.findItem(R.id.action_about).setTitle(
				reshapeSentence(R.string.about));
		menu.findItem(R.id.action_quit).setTitle(
				reshapeSentence(R.string.quit_text));
		MenuItem shareItem = menu.findItem(R.id.action_share);
		actionProvider = (CustomShareActionProvider) MenuItemCompat
				.getActionProvider(shareItem);
		actionProvider.setShareIntent(getShareIntent());
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (SalaatFirstApplication.isLanguageChanged) {
			Intent intent = getIntent();
			finish();
			startActivity(intent);
			SalaatFirstApplication.isLanguageChanged= false;
			return;
		}
		if(!prefs.getBoolean(FIRST_RUN_KEY, true)&&prefs.getBoolean("version_2.0_new_features_show", true))
		{
			showNewFeaturesDialog();
		}
		System.gc();
	}
	


	@Override
	protected void onStop() {
		super.onStop();
	}

	/**
	 * Show the about activity
	 */
	private void showAboutActivity() {
		Intent intent = new Intent(getBaseContext(), AboutActivity.class);
		startActivity(intent);
	}

/*	private void showBetaNoticeAlertDialog() {
		String versionName = "1.9.0b";
		try {
			versionName = getPackageManager().getPackageInfo(getPackageName(),
					0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DialogWithNotShowOption dialog = new DialogWithNotShowOption();
		Bundle args = new Bundle();
		args.putString(
				DialogWithNotShowOption.TEXT_KEY,
				"Vous utiliser actuellement la version "
						+ versionName
						+ ".\n"
						+ "C'est une version bêta pour la préparation de la version 2.0, elle peut contenir quelques bugs à régler.\n"
						+ "Si vous voulez contribuer au développement de l'application, merci d'utiliser cette version et de reporter les bugs rencontrés, sinon veuillez telecharger "
						+ "la version stable 1.2.6 ici: "
						+ "http://sourceforge.net/projects/salaat-first/files/Salaat_First_1.2.6.apk/download");
		args.putString(DialogWithNotShowOption.SHARED_PREFERENCE_KEY,
				"version_beta_accept2");
		dialog.setArguments(args);
		dialog.show(this);
	}
*/
	/**
	 * Show Settings activity
	 */
	private void showSettings() {
		Intent intent = new Intent(getBaseContext(), Settings.class);
		startActivity(intent);

	}
	

	public void onLocationConfirmed() {
		/*generally, the visible fragment here should be PrayerTimesFragment*/
		try{
		PrayerTimesFragment prayerTimesFragment=(PrayerTimesFragment) getSupportFragmentManager().findFragmentByTag("prayer-times");
		prayerTimesFragment.onLocationConfirmed();
		}
		catch(Exception e)
		{
			/*something wrong, do nothing*/
		}
	}
	
	private void showNewFeaturesDialog() {
		String[] features=getResources().getStringArray(R.array.new_features);
		FeaturesDialog dialog=new FeaturesDialog();
		Bundle args=new Bundle();
		args.putStringArray(FeaturesDialog.FEATURES_KEY, features);
		dialog.setArguments(args);
		dialog.show(this);
		prefs.edit().putBoolean("version_2.0_new_features_show", false).commit();
	}
	

}
