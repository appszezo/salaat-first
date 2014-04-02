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
import static org.hicham.salaat.SalaatFirstApplication.dBAdapter;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.hicham.salaat.SalaatFirstApplication;
import org.hicham.salaat.R;
import org.hicham.salaat.calculating.PrayerTimesCalcluationUtils;
import org.hicham.salaat.settings.Keys;
import org.hicham.salaat.settings.Keys.DefaultValues;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.AdapterView;
import org.holoeverywhere.widget.AdapterView.OnItemSelectedListener;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.Spinner;
import org.holoeverywhere.widget.datetimepicker.date.DatePickerDialog;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteCursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ahmedsoliman.devel.jislamic.DayPrayers;
import com.ahmedsoliman.devel.jislamic.astro.Location;

public class DatePrayerTimesFragment extends Fragment implements
		DatePickerDialog.OnDateSetListener {
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
	private String cityName;

	private Calendar date;

	/**
	 * fix text size according to the actual screen size
	 */
	private void fixTextProperties() {
		System.out.println("density "
				+ getResources().getDisplayMetrics().density);
		System.out.println("dpi " + getResources().getDisplayMetrics().xdpi);
		System.out
				.println("Screen size "
						+ (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK));
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

		double textSize = (width / scale) * 0.06;
		double bottomMargin = height * 0.03;
		// double leftAndRightMargins=
		System.out.println("textSize " + textSize + " width " + width
				+ " scale " + scale);
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

		// set margins
		LinearLayout.LayoutParams textFrLayoutParams = (LinearLayout.LayoutParams) sobhTextFr
				.getLayoutParams();
		textFrLayoutParams.bottomMargin = (int) (bottomMargin);
		sobhTextFr.setLayoutParams(textFrLayoutParams);
		sunriseTextFr.setLayoutParams(textFrLayoutParams);
		dohrTextFr.setLayoutParams(textFrLayoutParams);
		asrTextFr.setLayoutParams(textFrLayoutParams);
		maghribTextFr.setLayoutParams(textFrLayoutParams);
		ishaaTextFr.setLayoutParams(textFrLayoutParams);

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
		rootView = inflater.inflate(R.layout.date_prayer_times, container,
				false);
		// MainActivity.validateLanguage(getActivity());
		sobhTextView = (TextView) rootView.findViewById(R.id.sobh);
		choroukTextView = (TextView) rootView.findViewById(R.id.sunrise);
		dhuhrTextView = (TextView) rootView.findViewById(R.id.dohr);
		asrTextView = (TextView) rootView.findViewById(R.id.asr);
		maghribTextView = (TextView) rootView.findViewById(R.id.maghrib);
		ichaaTextView = (TextView) rootView.findViewById(R.id.ishaa);
		rootView.setVisibility(View.INVISIBLE);
		DatePickerDialog datePickerDialog = new DatePickerDialog();
		date = Calendar.getInstance();
		// datePickerDialog.initialize(this, );
		datePickerDialog.setDate(date.get(Calendar.YEAR),
				date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
		datePickerDialog.setOnDateSetListener(this);
		datePickerDialog.show(getSupportActivity());
		// initializing the text
		fixTextProperties();

		Button dateButton = (Button) rootView.findViewById(R.id.date);
		dateButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				DatePickerDialog datePickerDialog = new DatePickerDialog();
				datePickerDialog.setDate(date.get(Calendar.YEAR),
						date.get(Calendar.MONTH),
						date.get(Calendar.DAY_OF_MONTH));
				datePickerDialog
						.setOnDateSetListener(DatePrayerTimesFragment.this);
				datePickerDialog.show(getSupportActivity());
			}
		});

		return rootView;
	}

	public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear,
			int dayOfMonth) {
		rootView.setVisibility(View.VISIBLE);
		((SalaatFirstApplication) getSupportApplication()).refreshLanguage();
		date.set(year, monthOfYear, dayOfMonth);
		Button dateButton = (Button) rootView.findViewById(R.id.date);
		dateButton.setText(DateFormat.format("dd/MM/yyyy", date));
		updateTimes();
	}

	@Override
	public void onStart() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		SimpleCursorAdapter citiesCursorAdapter = dBAdapter
				.getCitiesAsCursorAdapter(pref.getString(Keys.COUNTRY_KEY,
						DefaultValues.COUNTRY));
		List<String> cities = Arrays.asList(dBAdapter.getCities(pref.getString(
				Keys.COUNTRY_KEY, "Morocco")));
		cityName = pref.getString(Keys.CITY_KEY, DefaultValues.CITY);
		Spinner spinner = (Spinner) rootView.findViewById(R.id.cityName);
		spinner.setAdapter(citiesCursorAdapter);
		if(!cityName.equals("custom"))
			spinner.setSelection(cities.indexOf(cityName));
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				SQLiteCursor cursor = (SQLiteCursor) parent
						.getItemAtPosition(position);
				String selectedCity = cursor.getString(cursor
						.getColumnIndex("name"));
				if (!cityName.equals(selectedCity)) {
					cityName = selectedCity;
					updateTimes();
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		super.onStart();
	}

	public void updateTimes() {
		Location loc = SalaatFirstApplication.dBAdapter.getLocation(cityName);
		GregorianCalendar cal = (GregorianCalendar) date;
		DayPrayers prayers = PrayerTimesCalcluationUtils.getPrayerTimes(loc,
				cal);
		sobhTextView.setText(prayers.fajr().toString());
		choroukTextView.setText(prayers.shuruq().toString());
		dhuhrTextView.setText(prayers.duhr().toString());
		asrTextView.setText(prayers.assr().toString());
		maghribTextView.setText(prayers.maghrib().toString());
		ichaaTextView.setText(prayers.ishaa().toString());

	}

}