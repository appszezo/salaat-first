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
import org.hicham.salaat.location.CustomLocationListener;
import org.hicham.salaat.location.LocationController;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.ahmedsoliman.devel.jislamic.astro.Location;

public class LocationSearchDialogFragment extends DialogFragment implements
		CustomLocationListener {

	private static LocationController controller;

	public void gpsIsDisabled() {
		dismiss();
		Toast.makeText(getSupportActivity(),
				reshapeSentence(R.string.gps_disabled), Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		controller = new LocationController(getSupportActivity(), this);
		controller.requestUpdates();
		getSupportActivity().getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		ProgressDialog dialog = new ProgressDialog(getSupportActivity());
		dialog.setButton(DialogInterface.BUTTON_NEGATIVE,
				reshapeSentence(R.string.cancel), new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dismiss();
					}
				});
		dialog.setOnCancelListener(new OnCancelListener() {

			public void onCancel(DialogInterface dialog) {
				dismiss();
			}
		});
		dialog.setMessage(reshapeSentence(R.string.wait_gps));
		return dialog;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		if (controller != null) {
			controller.removeLocationUpdates();
			controller=null;
		}
		getSupportActivity().getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onDismiss(dialog);
	}

	@Override
	public void onStart() {
		super.onStart();
		getDialog().setCanceledOnTouchOutside(false);
	}

	public void sendLocation(Location location, boolean isCustomCity,
			String country) {
		Log.i(SalaatFirstApplication.TAG, "location "+location.getName()+" getted, waiting confirmation, Custom:"+isCustomCity);
		if (location != null) {
			dismissAllowingStateLoss();
			DialogFragment locationConfirmDialog = new LocationConfirmDialogFragment();
			Bundle args = new Bundle();
			args.putString(LocationConfirmDialogFragment.LOCATION_NAME,
					location.getName());
			args.putDouble(LocationConfirmDialogFragment.LONGITUDE,
					location.getDegreeLong());
			args.putDouble(LocationConfirmDialogFragment.LATITUDE,
					location.getDegreeLat());
			args.putDouble(LocationConfirmDialogFragment.ALTITUDE,
					location.getSeaLevel());
			args.putBoolean(LocationConfirmDialogFragment.IS_CUSTOM_CITY,
					isCustomCity);
			args.putString(LocationConfirmDialogFragment.COUNTRY_NAME,
					country);

			locationConfirmDialog.setArguments(args);
			try{
			locationConfirmDialog.show(getSupportActivity());
			}
			catch(IllegalStateException e)
			{
				/*Android support library bug!!*/
			}
		}
	}
}
