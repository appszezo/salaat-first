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

package org.hicham.salaat.ui.fragments;

import static org.arabic.ArabicUtilities.reshapeSentence;
import static org.hicham.salaat.SalaatFirstApplication.prefs;

import java.util.Calendar;
import java.util.Locale;

import org.arabic.ArabicUtilities;
import org.hicham.salaat.R;
import org.hicham.salaat.SalaatFirstApplication;
import org.hicham.salaat.calculating.PrayerTimesCalcluationUtils;
import org.hicham.salaat.location.AddressTaskListener;
import org.hicham.salaat.location.GetAddressTask;
import org.hicham.salaat.settings.Keys;
import org.hicham.salaat.settings.Keys.DefaultValues;
import org.hicham.salaat.ui.dialogs.LocationSearchDialogFragment;
import org.hicham.salaat.util.Utils;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.LinearLayout;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ahmedsoliman.devel.jislamic.DayPrayers;
import com.ahmedsoliman.devel.jislamic.astro.Location;

public class PrayerTimesFragment extends Fragment implements
		AddressTaskListener {
	/**
	 * Setting the font to textviews passed in args
	 * 
	 * @param tf
	 * @param params
	 */
	public static void setLayoutFont(Typeface tf, TextView... params) {
		for (TextView tv : params) {
			tv.setTypeface(tf);
		}
	}
	View rootView;
	TextView sobhTextView;
	TextView choroukTextView;
	TextView dhuhrTextView;
	TextView asrTextView;
	TextView maghribTextView;
	TextView ichaaTextView;
	TextView sobhTextAr;
	private boolean isNearestLocationUsed=false;
	

	/**
	 * fix text size according to the actual screen size
	 */
	private void fixTextProperties() {
		RelativeLayout layout = (RelativeLayout) rootView
				.findViewById(R.id.mainLayout);
		Display displayManager = getActivity().getWindowManager()
				.getDefaultDisplay();
		int width = displayManager.getWidth();
		int height = displayManager.getHeight();
		final float scale = getResources().getDisplayMetrics().density;

		if (scale < 1) {
			layout.setPadding(0, height / 12, 5, 5);
		} else if (scale == 1) {
			layout.setPadding(0, height / 10, 5, 5);
		} else
			layout.setPadding(0, height / 8, 10, 10);

		double textSize = (width / scale) * 0.05;
		double bottomMargin = height * 0.03;
		TextView sobhTextAr = (TextView) rootView.findViewById(R.id.sobhtext);
		TextView sunriseTextAr = (TextView) rootView
				.findViewById(R.id.sunrisetext);
		TextView dohrTextAr = (TextView) rootView.findViewById(R.id.dohrtext);
		TextView asrTextAr = (TextView) rootView.findViewById(R.id.asrtext);
		TextView maghribTextAr = (TextView) rootView
				.findViewById(R.id.maghribtext);
		TextView ishaaTextAr = (TextView) rootView.findViewById(R.id.ishaatext);
		sobhTextAr.setText(reshapeSentence("الصبح"));
		sunriseTextAr.setText(reshapeSentence("الشروق"));
		dohrTextAr.setText(reshapeSentence("الظهر"));
		asrTextAr.setText(reshapeSentence("العصر"));
		maghribTextAr.setText(reshapeSentence("المغرب"));
		ishaaTextAr.setText(reshapeSentence("العشاء"));
		TextView sobhTextFr = (TextView) rootView.findViewById(R.id.sobhtextfr);
		TextView sunriseTextFr = (TextView) rootView
				.findViewById(R.id.sunrisetextfr);
		TextView dohrTextFr = (TextView) rootView.findViewById(R.id.dohrtextfr);
		TextView asrTextFr = (TextView) rootView.findViewById(R.id.asrtextfr);
		TextView maghribTextFr = (TextView) rootView
				.findViewById(R.id.maghribtextfr);
		TextView ishaaTextFr = (TextView) rootView
				.findViewById(R.id.ishaatextfr);
		// set text size
		sobhTextAr.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float) textSize);
		sunriseTextAr
				.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float) textSize);
		dohrTextAr.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float) textSize);
		asrTextAr.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float) textSize);
		maghribTextAr
				.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float) textSize);
		ishaaTextAr.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float) textSize);
		sobhTextFr.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float) textSize);
		sunriseTextFr
				.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float) textSize);
		dohrTextFr.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float) textSize);
		asrTextFr.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float) textSize);
		maghribTextFr
				.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float) textSize);
		ishaaTextFr.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float) textSize);
		sobhTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float) textSize);
		choroukTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,
				(float) textSize);
		dhuhrTextView
				.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float) textSize);
		asrTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float) textSize);
		maghribTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,
				(float) textSize);
		ichaaTextView
				.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float) textSize);
		((TextView) rootView.findViewById(R.id.cityName)).setTextSize(
				TypedValue.COMPLEX_UNIT_DIP, (float) textSize);		
		// set margins
		TableRow.LayoutParams textFrLayoutParams = (TableRow.LayoutParams) sobhTextFr
				.getLayoutParams();
		textFrLayoutParams.bottomMargin = (int) (bottomMargin);
		sobhTextFr.setLayoutParams(textFrLayoutParams);
		sunriseTextFr.setLayoutParams(textFrLayoutParams);
		dohrTextFr.setLayoutParams(textFrLayoutParams);
		asrTextFr.setLayoutParams(textFrLayoutParams);
		maghribTextFr.setLayoutParams(textFrLayoutParams);
		ishaaTextFr.setLayoutParams(textFrLayoutParams);

		textFrLayoutParams.leftMargin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 10, getResources()
						.getDisplayMetrics());
		textFrLayoutParams.rightMargin = textFrLayoutParams.leftMargin;
		sobhTextView.setLayoutParams(textFrLayoutParams);
		choroukTextView.setLayoutParams(textFrLayoutParams);
		dhuhrTextView.setLayoutParams(textFrLayoutParams);
		asrTextView.setLayoutParams(textFrLayoutParams);
		maghribTextView.setLayoutParams(textFrLayoutParams);
		ichaaTextView.setLayoutParams(textFrLayoutParams);

		Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
				"droidsans.ttf");
		setLayoutFont(tf, sobhTextAr, sunriseTextAr, dohrTextAr, asrTextAr,
				maghribTextAr, ishaaTextAr);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.prayer_times, container, false);
		sobhTextView = (TextView) rootView.findViewById(R.id.sobh);
		choroukTextView = (TextView) rootView.findViewById(R.id.sunrise);
		dhuhrTextView = (TextView) rootView.findViewById(R.id.dohr);
		asrTextView = (TextView) rootView.findViewById(R.id.asr);
		maghribTextView = (TextView) rootView.findViewById(R.id.maghrib);
		ichaaTextView = (TextView) rootView.findViewById(R.id.ishaa);

		// initializing the text
		fixTextProperties();
		LinearLayout date=((LinearLayout) rootView.findViewById(R.id.date));
		Utils.formatDateLayout(Calendar.getInstance(), date, asrTextView.getTextSize()/getResources().getDisplayMetrics().density*3f/4f);
		populateFields();
		return rootView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(SalaatFirstApplication.isConfigurationChanged)
		{
			populateFields();
			SalaatFirstApplication.isConfigurationChanged=false;
		}
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		MenuItem refreshLocation = menu.add(Menu.NONE, 15, Menu.NONE, reshapeSentence(R.string.refresh_gps));
		MenuItemCompat.setShowAsAction(refreshLocation, MenuItem.SHOW_AS_ACTION_ALWAYS);
		refreshLocation.setIcon(R.drawable.ic_action_location_found);

		getMenuInflater().inflate(R.menu.action_bar_menu, menu);
		menu.findItem(R.id.action_settings).setTitle(
				reshapeSentence(R.string.settings_text));
		menu.findItem(R.id.action_about).setTitle(
				reshapeSentence(R.string.about));
		menu.findItem(R.id.action_quit).setTitle(
				reshapeSentence(R.string.quit_text));
		super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 15:
			updateLocation();
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void updateLocation() {
		LocationSearchDialogFragment dialog=new LocationSearchDialogFragment();
		dialog.show(getSupportActivity());
	}

	public void onFailure() {
		if(!isNearestLocationUsed)
		{
			String name=prefs.getString(Keys.CITY_KEY, DefaultValues.CITY);
			if(!name.equals("custom"))
				return;
			Location nearestLocation=SalaatFirstApplication.dBAdapter.getCustomNearestLocation();
			new GetAddressTask(getSupportApplication(), this, false, new Locale(prefs.getString(Keys.LANGUAGE_KEY, DefaultValues.LANGUAGE)))
			.execute(nearestLocation.getDegreeLat(), nearestLocation.getDegreeLong());
			isNearestLocationUsed=true;
		}
	}


	public void populateFields() {

		String cityNameFormatted = prefs
				.getString(Keys.CITY_NAME_FORMATTED, "");
		if (!cityNameFormatted.equals("")) {
			setCityName(cityNameFormatted);
		} else {
			String city = prefs.getString(Keys.CITY_KEY, DefaultValues.CITY);
			if (!city.equals("custom"))
				setCityName(city);
			else
				setCityName("");
			Locale locale = new Locale(prefs.getString(Keys.LANGUAGE_KEY, DefaultValues.LANGUAGE));
			Location loc = SalaatFirstApplication.dBAdapter.getLocation(city);
			new GetAddressTask(getSupportApplication(), this, false, locale)
					.execute(loc.getDegreeLat(), loc.getDegreeLong());
		}
		
		
		DayPrayers prayers = PrayerTimesCalcluationUtils
				.getCurrentPrayerTimes();
		sobhTextView.setText(prayers.fajr().toString());
		choroukTextView.setText(prayers.shuruq().toString());
		dhuhrTextView.setText(prayers.duhr().toString());
		asrTextView.setText(prayers.assr().toString());
		maghribTextView.setText(prayers.maghrib().toString());
		ichaaTextView.setText(prayers.ishaa().toString());
	}

	public void publishResult(String cityName) {
		setCityName(cityName);
		prefs.edit().putString(Keys.CITY_NAME_FORMATTED, cityName).commit();
	}

	private void setCityName(String city) {
		TextView textView = ((TextView) rootView.findViewById(R.id.cityName));
		if (ArabicUtilities.hasArabicLetters(city)) {
			Typeface tf = Typeface.createFromAsset(getSupportActivity()
					.getAssets(), "droidsans.ttf");
			textView.setTypeface(tf);
			textView.setText(reshapeSentence(city));
		} else {
			textView.setTypeface(null, Typeface.BOLD);
			textView.setText(city);
		}
	}

	public void onLocationConfirmed() {
		prefs.edit().putBoolean(Keys.USE_AUTOMATIC_LOCATION_KEY, true).commit();
		populateFields();
	}
}