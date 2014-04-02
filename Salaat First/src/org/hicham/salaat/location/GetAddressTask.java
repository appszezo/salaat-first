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

package org.hicham.salaat.location;

import java.util.List;
import java.util.Locale;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.hicham.salaat.SalaatFirstApplication;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;

/**
 * A subclass of AsyncTask that calls getFromLocation() in the background. The
 * class definition has these generic types: Location - A Location object
 * containing the current location. Void - indicates that progress units are not
 * used String - An address passed to onPostExecute()
 */
public class GetAddressTask extends AsyncTask<Double, Void, String> {
	private class GetAddressTimer extends CountDownTimer {

		public GetAddressTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			Log.i(SalaatFirstApplication.TAG,"GetAddressTask timer expired");
			GetAddressTask.this.cancel(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub

		}
	}
	private static final DefaultHttpClient HTTP_CLIENT = new DefaultHttpClient();
	private Context mContext;
	private AddressTaskListener mListener;
	private boolean mIsNeedTimer;

	private Locale mLocale;

	public GetAddressTask(Context context, AddressTaskListener listener,
			boolean isNeedTimer, Locale locale) {
		super();
		mContext = context;
		mListener = listener;
		mIsNeedTimer = isNeedTimer;
		mLocale = locale;
	}

	/**
	 * Get a Geocoder instance, get the latitude and longitude look up the
	 * address, and return it
	 * 
	 * @params params latitude, longitude
	 * @return A string containing the address of the current location, or an
	 *         empty string if no address can be found, or an error message
	 */
	@Override
	protected String doInBackground(Double... params) {

		Geocoder geocoder = new Geocoder(mContext, mLocale);
		// Get the current location from the input parameter list
		double latitude = params[0];
		double longitude = params[1];
		// Create a list to contain the result address
		List<Address> addresses = null;
		try {
			/*
			 * Return 1 address.
			 */
			addresses = geocoder.getFromLocation(latitude, longitude, 1);
		} catch (Exception e) {

		}
		// If the reverse geocode returned an address
		if (addresses != null && addresses.size() > 0 && addresses.get(0).getLocality()!=null) {
			// Get the first address
			Address address = addresses.get(0);
			/*
			 * Format the first line of address (if available), city, and
			 * country name.
			 */
			String addressText = address.getLocality();
			Log.i(SalaatFirstApplication.TAG,"get city name using GeoCoder, addressText="+addressText);
			// Return the text
			return addressText;
		} else {
			Log.i(SalaatFirstApplication.TAG,"trying fetching using google maps api");
			return fetchNameUsingGoogleMap(latitude, longitude);
		}
	}

	private String fetchNameUsingGoogleMap(double latitude, double longitude) {
		String googleMapUrl = "http://maps.googleapis.com/maps/api/geocode/json?latlng="
				+ latitude
				+ ","
				+ longitude
				+ "&sensor=false&language="
				+ mLocale.getLanguage();

		try {
			JSONObject googleMapResponse = new JSONObject(
					HTTP_CLIENT.execute(new HttpGet(googleMapUrl),
							new BasicResponseHandler()));

			// many nested loops.. not great -> use expression instead
			// loop among all results
			JSONArray results = (JSONArray) googleMapResponse.get("results");
			for (int i = 0; i < results.length(); i++) {
				// loop among all addresses within this result
				JSONObject result = results.getJSONObject(i);
				if (result.has("address_components")) {
					JSONArray addressComponents = result
							.getJSONArray("address_components");
					// loop among all address component to find a 'locality' or
					// 'sublocality'
					for (int j = 0; j < addressComponents.length(); j++) {
						JSONObject addressComponent = addressComponents
								.getJSONObject(j);
						if (result.has("types")) {
							JSONArray types = addressComponent
									.getJSONArray("types");

							// search for locality
							String cityName = null;

							for (int k = 0; k < types.length(); k++) {
								if ("locality".equals(types.getString(k))
										&& cityName == null) {
									if (addressComponent.has("long_name")) {
										cityName = addressComponent
												.getString("long_name");
									} else if (addressComponent
											.has("short_name")) {
										if (cityName == null)
											cityName = addressComponent
													.getString("short_name");
									}
								}

							}
							if (cityName != null) {
								Log.i(SalaatFirstApplication.TAG,"cityName="+cityName);
								return cityName;
							}
						}
					}
				}
			}
		} catch (Exception ignored) {
			ignored.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onCancelled(String result) {
		Log.i(SalaatFirstApplication.TAG,"Task cancelled, onCancelled");
		mListener.onFailure();
	}

	/**
	 * A method that's called once doInBackground() completes. Turn off the
	 * indeterminate activity indicator and set the text of the UI element that
	 * shows the address. If the lookup failed, display the error message.
	 */
	@Override
	protected void onPostExecute(String address) {
		if (address != null)
			mListener.publishResult(address);
		else
			mListener.onFailure();
	}

	@Override
	protected void onPreExecute() {
		if (mIsNeedTimer) {
			new GetAddressTimer(30 * 1000, 30 * 1000).start();
		}
	}
}
