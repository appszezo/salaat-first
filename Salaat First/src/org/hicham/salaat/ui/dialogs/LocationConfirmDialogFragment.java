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

import org.hicham.salaat.SalaatFirstApplication;
import org.hicham.salaat.R;
import org.hicham.salaat.settings.Keys;
import org.hicham.salaat.util.Utils;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import com.ahmedsoliman.devel.jislamic.astro.Location;

public class LocationConfirmDialogFragment extends DialogFragment {

	/*
	 * The activity that creates an instance of this dialog fragment must
	 * implement this interface in order to receive event callbacks. Each method
	 * passes the DialogFragment in case the host needs to query it.
	 */
	public interface LocationConfirmDialogListener {
		public void onLocationConfirmed();
	}

	public static final String LOCATION_NAME = "location_name";

	public static final String LONGITUDE = "longitude";

	public static final String LATITUDE = "latitude";

	public static final String ALTITUDE = "altitude";

	public static final String IS_CUSTOM_CITY = "is_cusotm_city";

	public static final String COUNTRY_NAME = "country";

	private LocationConfirmDialogListener mListener;
	private Location mLocation;
	private boolean mIsCustomCity;
	private String mCountryName;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			mListener = (LocationConfirmDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement LocationCOnfirmDialogListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String locationName = getArguments().getString(LOCATION_NAME);
		double longitude = getArguments().getDouble(LONGITUDE);
		double latitude = getArguments().getDouble(LATITUDE);
		double altitude = getArguments().getDouble(ALTITUDE);
		mCountryName = getArguments().getString(COUNTRY_NAME);
		mLocation = new Location(locationName, latitude, longitude);
		mLocation.setSeaLevel(altitude);
		mIsCustomCity = getArguments().getBoolean(IS_CUSTOM_CITY);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder adbuilder = new AlertDialog.Builder(
				getSupportActivity());
		adbuilder
				.setMessage(reshapeSentence(R.string.confirming_gps_data)
						+ "\n"
						+ (mIsCustomCity ? "" : mLocation.getName())
						+ "\n"
						+ Utils.convertLatitudeToSecondsFormat(mLocation
								.getDegreeLat())
						+ "\t"
						+ Utils.convertLongitudeToSecondsFormat(mLocation
								.getDegreeLong()));
		adbuilder.setPositiveButton(reshapeSentence(R.string.confirm),
				new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						if (mIsCustomCity) {
							SalaatFirstApplication.dBAdapter.setCustomCity(
									mLocation.getDegreeLong(),
									mLocation.getDegreeLat(),
									mLocation.getSeaLevel(), mCountryName);
							SalaatFirstApplication.prefs.edit()
									.putString(Keys.CITY_KEY, "custom")
									.commit();
							SalaatFirstApplication.prefs.edit().putString(
									Keys.CITY_NAME_FORMATTED, "");
							SalaatFirstApplication.prefs.edit()
							.putString(Keys.COUNTRY_KEY, mCountryName)
							.commit();
							SalaatFirstApplication.getLastInstance().onSharedPreferenceChanged(SalaatFirstApplication.prefs, Keys.CITY_KEY);
						} else {
							SalaatFirstApplication.prefs
									.edit()
									.putString(Keys.CITY_KEY,
											mLocation.getName()).commit();
							SalaatFirstApplication.prefs.edit()
									.putString(Keys.COUNTRY_KEY, mCountryName)
									.commit();
						}
						dialog.dismiss();
						mListener.onLocationConfirmed();
					}
				});
		adbuilder.setNegativeButton(reshapeSentence(R.string.cancel),
				new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		Dialog dialog = adbuilder.create();
		return dialog;
	}

	@Override
	public void onStart() {
		super.onStart();
		getDialog().setCanceledOnTouchOutside(false);
		TextView message = (TextView) getDialog().findViewById(R.id.message);
		message.setGravity(Gravity.CENTER);
	}
}
