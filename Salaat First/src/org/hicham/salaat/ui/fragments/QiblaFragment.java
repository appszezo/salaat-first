/*
 * Copyright (C) 2011 Iranian Supreme Council of ICT, The FarsiTel Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASICS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hicham.salaat.ui.fragments;

/*
 * Main activity of the Qibla Compass application. 
 * Written By: Majid Kalkatehchi
 * Email: majid@farsitel.com
 * 
 * Required files:
 * QiblaCompassManager.java
 * res/layout/main.xml
 * 
 * 
 */

import static org.hicham.salaat.SalaatFirstApplication.prefs;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.hicham.salaat.R;
import org.hicham.salaat.SalaatFirstApplication;
import org.hicham.salaat.db.DbAdapter;
import org.hicham.salaat.settings.Keys;
import org.hicham.salaat.settings.Keys.DefaultValues;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.FrameLayout;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.TextView;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.farsitel.qiblacompass.logic.QiblaCompassManager;
import com.farsitel.qiblacompass.util.ConcurrencyUtil;
import com.farsitel.qiblacompass.util.ConstantUtilInterface;

public class QiblaFragment extends Fragment implements AnimationListener,
		ConstantUtilInterface {
	private View rootView;
	// Current location that is set by QiblaManager
	public Location currentLocation = null;

	// These tow variable is usefull to compute the difference between new
	// angles and last angles.(To compute the rotation degree and also some
	// performance and smoothing behaviours that prevents the arrow to rotate
	// for very smal angles)
	private double lastQiblaAngle = 0;
	private double lastNorthAngle = 0;
	private double lastQiblaAngleFromN = 0;

	// This animation is used to rotate north and qibla images
	private RotateAnimation animation;

	private ImageView compassImageView;
	private ImageView qiblaImageView;
	// This class informs us about changes in qibla and north direction
	private final QiblaCompassManager qiblaManager = new QiblaCompassManager(
			this);

	// QiblaManager is talking to us about changes in angles through accessors
	// of this variable and a TimerTask repeatedly checks this
	// variable.(QiblaManager will not sent messages directly because of
	// syncronization of animations). Though the TimerTask will check if any
	// animation is in run mode, if there wasn't any animation, timerTask will
	// use new angles. There might be some angles that are lost but it will not
	// affect the results.
	private boolean angleSignaled = false;
	private Timer timer = null;

	// These tow variables are redundant now. but they can be usefull when
	// registering and unregistering services.
	public boolean isRegistered = false;
	public boolean isGPSRegistered = false;

	// TimerTask talks to us by sending messages about changes in direction
	// of north and Qibla
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			if (message.what == ROTATE_IMAGES_MESSAGE) {
				Bundle bundle = message.getData();
				// These are for us to know that if qibla direction is changed
				// or north direction is changed.
				boolean isQiblaChanged = bundle.getBoolean(IS_QIBLA_CHANGED);
				boolean isCompassChanged = bundle
						.getBoolean(IS_COMPASS_CHANGED);
				// These are the delta angles from north and qibla (first set to
				// zero and if they are changed in this message, we will update
				// them)
				double qiblaNewAngle = 0;
				double compassNewAngle = 0;
				if (isQiblaChanged)
					qiblaNewAngle = (Double) bundle.get(QIBLA_BUNDLE_DELTA_KEY);
				if (isCompassChanged) {
					compassNewAngle = (Double) bundle
							.get(COMPASS_BUNDLE_DELTA_KEY);
				}
				// This
				syncQiblaAndNorthArrow(compassNewAngle, qiblaNewAngle,
						isCompassChanged, isQiblaChanged);
				angleSignaled = false;
			}
		}

	};

	/*
	 * Stopping the timerTask (For example when activity is paused or stopped)
	 */
	private void cancelSchedule() {

		if (timer == null)
			return;
		// timer.cancel();
	}

	/*
	 * when user changes the GPS status off, any changes we must show the images
	 * and use last location for direction
	 */
	/*
	 * private void onGPSOff(Location defaultLocation) { currentLocation =
	 * defaultLocation; gpsLocationFound = false; requestForValidationOfQibla();
	 * }
	 */
	/*
	 * This method get us appropraite message string about latitude and
	 * longitude points
	 */
	private String getLocationForPrint(double latitude, double longitude) {
		String latEnd = "N";
		String longEnd = "E";
		if (latitude < 0) {
			latEnd = "N";
			latitude = -latitude;
		}
		if (longitude < 0) {
			longEnd = "W";
			longitude = -longitude;
		}

		int latDegree = (int) latitude;
		int longDegree = (int) longitude;
		double latMinDouble = (latitude - latDegree) * 60;
		int latMinute = (int) latMinDouble;
		// double latMinDouble = (latSecond * 3d / 5d);

		double longMinDouble = (longitude - longDegree) * 60;
		// double longMinDouble = (longSecond * 3d / 5d);
		int longMinute = (int) longMinDouble;

		return String.format("%d° %d' %s , %d° %d' %s", latDegree, latMinute,
				latEnd, longDegree, longMinute, longEnd);
		// return getString(R.string.geo_location_info);

	}

	/*
	 * This is actually a loop task that check for new angles when no animation
	 * is in run and then provide a Message for QiblaActivity. Please note that
	 * this class is running in another thread.
	 */
	private TimerTask getTimerTask() {
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {

				if (angleSignaled && !ConcurrencyUtil.isAnyAnimationOnRun()) {

					// numAnimationOnRun += 2;
					Map<String, Double> newAnglesMap = qiblaManager
							.fetchDeltaAngles();
					Double newNorthAngle = newAnglesMap
							.get(ConstantUtilInterface.NORTH_CHANGED_MAP_KEY);
					Double newQiblaAngle = newAnglesMap
							.get(ConstantUtilInterface.QIBLA_CHANGED_MAP_KEY);

					Message message = mHandler.obtainMessage();
					message.what = ROTATE_IMAGES_MESSAGE;
					Bundle b = new Bundle();
					if (newNorthAngle == null) {
						b.putBoolean(IS_COMPASS_CHANGED, false);
					} else {
						ConcurrencyUtil.incrementAnimation();
						b.putBoolean(IS_COMPASS_CHANGED, true);

						b.putDouble(COMPASS_BUNDLE_DELTA_KEY, newNorthAngle);
					}
					if (newQiblaAngle == null) {
						b.putBoolean(IS_QIBLA_CHANGED, false);

					} else {
						ConcurrencyUtil.incrementAnimation();
						b.putBoolean(IS_QIBLA_CHANGED, true);
						b.putDouble(QIBLA_BUNDLE_DELTA_KEY, newQiblaAngle);
					}

					message.setData(b);
					mHandler.sendMessage(message);
				} else if (ConcurrencyUtil.getNumAimationsOnRun() < 0) {
					Log.d(NAMAZ_LOG_TAG,
							" Number of animations are negetive numOfAnimation: "
									+ ConcurrencyUtil.getNumAimationsOnRun());
				}
			}
		};
		return timerTask;
	}

	public void onAnimationEnd(Animation animation) {
		if (ConcurrencyUtil.getNumAimationsOnRun() <= 0) {
			Log.d(NAMAZ_LOG_TAG,
					"An animation ended but no animation was on run!!!!!!!!!");
		} else {
			ConcurrencyUtil.decrementAnimation();
		}
		schedule();
	}

	/*
	 * When user changes the gps status to on mode. The QiblaImages must became
	 * unvisible and some screen texts must be changed. These changes will
	 * became permanent until the GPS device recieves location, or user set GPS
	 * to off.
	 */
	/*
	 * private void onInvalidateQible(String message) { // TextView textView =
	 * (TextView) // rootView.findViewById(R.id.location_text_line1); TextView
	 * textView = (TextView) rootView.findViewById(R.id.location_text_line2); //
	 * TextView textView3 = (TextView) //
	 * rootView.findViewById(R.id.location_text_line3);
	 * 
	 * textView.setText(""); textView.setVisibility(View.INVISIBLE);
	 * ((ImageView) rootView.findViewById(R.id.arrowImage))
	 * .setVisibility(View.INVISIBLE); ((ImageView)
	 * rootView.findViewById(R.id.compassImage)) .setVisibility(View.INVISIBLE);
	 * ((ImageView) rootView.findViewById(R.id.frameImage))
	 * .setVisibility(View.INVISIBLE); ((FrameLayout)
	 * rootView.findViewById(R.id.qiblaLayout)) .setVisibility(View.INVISIBLE);
	 * TextView textView3 = (TextView)
	 * rootView.findViewById(R.id.noLocationText); textView3.setText(message);
	 * ((LinearLayout) rootView.findViewById(R.id.noLocationLayout))
	 * .setVisibility(View.VISIBLE); ((LinearLayout)
	 * rootView.findViewById(R.id.textLayout)) .setVisibility(View.INVISIBLE);
	 * 
	 * }
	 */
	/*
	 * private void requestForValidationOfQibla() { // TextView textView =
	 * (TextView) // rootView.findViewById(R.id.location_text_line1); //
	 * TextView textView2 = (TextView)
	 * rootView.findViewById(R.id.location_text_line2); ImageView arrow =
	 * ((ImageView) rootView.findViewById(R.id.arrowImage)); ImageView compass =
	 * ((ImageView) rootView.findViewById(R.id.compassImage)); ImageView frame =
	 * ((ImageView) rootView.findViewById(R.id.frameImage)); FrameLayout
	 * qiblaFrame = ((FrameLayout) rootView.findViewById(R.id.qiblaLayout));
	 * 
	 * if (faceUp && (gpsLocationFound || currentLocation != null)) { //
	 * textView2.setVisibility(View.VISIBLE); //
	 * textView2.setText(location_line2); ((LinearLayout)
	 * rootView.findViewById(R.id.textLayout)) .setVisibility(View.VISIBLE);
	 * qiblaFrame.setVisibility(View.VISIBLE);
	 * arrow.setVisibility(View.VISIBLE); compass.setVisibility(View.VISIBLE);
	 * frame.setVisibility(View.VISIBLE); } else { if (!faceUp) {
	 * onScreenDown(); } else if (!(gpsLocationFound || currentLocation !=
	 * null)) { onGPSOn(); } } }
	 */
	/*
	 * private void onGPSOn() { gpsLocationFound = false;
	 * onInvalidateQible(getString(R.string.no_location_yet)); }
	 */

	// When new Locations are set in the class the information about the
	// location will be printed
	// private void setLocationText() {
	// TextView textView = (TextView)
	// rootView.findViewById(R.id.location_text_line1);
	// TextView textView2 = (TextView)
	// rootView.findViewById(R.id.location_text_line2);
	//
	// // textView.setText(getString(R.string.location_set));
	// textView2.setText(getLocationForPrint(currentLocation.getLatitude(),
	// currentLocation.getLongitude()));
	//
	// }

	/*
	 * Qible direction is set with the assumption of horizontal and up to ceil
	 * screen orientation. If the user changes these aligns, we wil notify
	 * him/her with messages.
	 */
	/*
	 * public void onScreenDown() { faceUp = false;
	 * onInvalidateQible(getString(R.string.screen_down_text)); }
	 */

	/*
	 * when user changes align of screen to horizontal and up to sky. The
	 * previously set messages will changes
	 */

	public void onAnimationRepeat(Animation animation) {
	}

	public void onAnimationStart(Animation animation) {
		cancelSchedule();

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		((SalaatFirstApplication) getSupportApplication()).refreshLanguage();

	}

	/** Called when the activity is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rootView = inflater.inflate(R.layout.qibla_compass, container, false);
		((SalaatFirstApplication) getSupportApplication()).refreshLanguage();
		// registering for listeners
		registerListeners();
		// Checking if the GPS is on or off. If it was on the default location
		// will be set and if its on, appropriate
		Context context = getActivity();
		if (prefs.getBoolean(Keys.GPS_FOR_COMPASS_KEY, DefaultValues.GPS_FOR_COMPASS) == true)
			registerForGPS();
		// onGPSOn();
		else
			useDefaultLocation();
		this.qiblaImageView = (ImageView) rootView
				.findViewById(R.id.arrowImage);
		this.compassImageView = (ImageView) rootView
				.findViewById(R.id.compassImage);
		return rootView;
	}

	/*
	 * QiblaManager will set new location of the device with this method. We
	 * will set appropriate me.ssages
	 */
	public void onNewLocation(Location location, boolean isFromGPS) {
		currentLocation = location;
		String locationName = "";
		if (isFromGPS) {
			Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
			List<Address> addresses = null;
			try {
				addresses = gcd.getFromLocation(location.getLatitude(),
						location.getLongitude(), 1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (addresses != null && addresses.size() > 0) {
				if (addresses.get(0).getLocality() != null)
					locationName = addresses.get(0).getLocality();
				if (addresses.get(0).getSubLocality() != null)
					locationName += " - " + addresses.get(0).getSubLocality();
			}

			this.setLocationText(
					locationName,
					getLocationForPrint(location.getLatitude(),
							location.getLongitude()));
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		ConcurrencyUtil.setToZero();
		ConcurrencyUtil.directionChangedLock.readLock();
		unregisterListeners();
	}

	@Override
	public void onResume() {
		super.onResume();
		registerListeners();
	}


	/*
	 * Registering for locationListener (When GPS is set on)
	 */
	private void registerForGPS() {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setSpeedRequired(false);
		criteria.setCostAllowed(true);
		LocationManager locationManager = ((LocationManager) getSystemService(Context.LOCATION_SERVICE));
		String provider = locationManager.getBestProvider(criteria, true);
		qiblaManager.setLocationFromGPS(true);
		if (provider != null) {
			locationManager.requestLocationUpdates(provider, MIN_LOCATION_TIME,
					MIN_LOCATION_DISTANCE, qiblaManager);
		}
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				MIN_LOCATION_TIME, MIN_LOCATION_DISTANCE, qiblaManager);
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, MIN_LOCATION_TIME,
				MIN_LOCATION_DISTANCE, qiblaManager);
		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location == null) {
			location = ((LocationManager) getSystemService(Context.LOCATION_SERVICE))
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
		if (location != null) {
			qiblaManager.onLocationChanged(location);
		}

	}

	/*
	 * Registering for all Listeners. LocationListener will be registered if and
	 * only if GPS status is on.
	 */
	private void registerListeners() {
		if (prefs.getBoolean(Keys.GPS_FOR_COMPASS_KEY, DefaultValues.GPS_FOR_COMPASS)) {
			registerForGPS();
		} else {
			useDefaultLocation();
		}
		SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		Sensor gsensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		Sensor msensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mSensorManager.registerListener(qiblaManager, gsensor,
				SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(qiblaManager, msensor,
				SensorManager.SENSOR_DELAY_GAME);
		schedule();
		isRegistered = true;

	}

	private double rotateImageView(double newAngle, double fromDegree,
			ImageView imageView) {

		float toDegree = new Double(newAngle % 360).floatValue();
		if (toDegree - fromDegree > 180)
			toDegree -= 360;
		if (toDegree - fromDegree < -180)
			toDegree += 360;

		double rotationDegree = fromDegree - toDegree;
		long duration = new Double(Math.abs(rotationDegree) * 4000 / 360)
				.longValue();
		FrameLayout frameLayout = (FrameLayout) rootView
				.findViewById(R.id.qiblaLayout);
		final int width = Math.abs(frameLayout.getRight()
				- frameLayout.getLeft());
		final int height = Math.abs(frameLayout.getBottom()
				- frameLayout.getTop());

		float pivotX = width / 2f;
		float pivotY = height / 2f;
		animation = new RotateAnimation(new Double(fromDegree).floatValue(),
				toDegree, pivotX, pivotY);
		animation.setRepeatCount(0);
		animation.setDuration(duration);
		animation.setInterpolator(new LinearInterpolator());
		animation.setFillEnabled(true);
		animation.setFillAfter(true);
		animation.setAnimationListener(this);
		Log.d(NAMAZ_LOG_TAG, "rotating image from degree:" + fromDegree
				+ " degree to rotate: " + rotationDegree + " ImageView: "
				+ imageView.getId());
		imageView.startAnimation(animation);
		return toDegree;

	}

	/*
	 * Running the TimerTask. (for example when application is started or became
	 * back from pause mode.)
	 */
	private void schedule() {

		if (timer == null) {
			timer = new Timer();
			this.timer.schedule(getTimerTask(), 0, 200);
		} else {
			timer.cancel();
			timer = new Timer();
			timer.schedule(getTimerTask(), 0, 200);
		}
	}

	public void setLocationText(String locationName, String locationCoordinates) {
		((TextView) rootView.findViewById(R.id.location_name))
				.setText(locationName);
		((TextView) rootView.findViewById(R.id.location))
				.setText(locationCoordinates);
	}

	public void signalForAngleChange() {
		this.angleSignaled = true;
	}

	/*
	 * This method synchronizes the Qibla and North arrow rotation.
	 */
	public void syncQiblaAndNorthArrow(double northNewAngle,
			double qiblaNewAngle, boolean northChanged, boolean qiblaChanged) {
		if (northChanged) {
			lastNorthAngle = rotateImageView(northNewAngle, lastNorthAngle,
					compassImageView);
			// if North is changed and our location are not changed(Though qibla
			// direction is not changed). Still we need to rotated Qibla arrow
			// to have the same difference between north and Qibla.
			if (qiblaChanged == false && qiblaNewAngle != 0) {
				lastQiblaAngleFromN = qiblaNewAngle;
				lastQiblaAngle = rotateImageView(qiblaNewAngle + northNewAngle,
						lastQiblaAngle, qiblaImageView);
			} else if (qiblaChanged == false && qiblaNewAngle == 0)

				lastQiblaAngle = rotateImageView(lastQiblaAngleFromN
						+ northNewAngle, lastQiblaAngle, qiblaImageView);

		}
		if (qiblaChanged) {
			lastQiblaAngleFromN = qiblaNewAngle;
			lastQiblaAngle = rotateImageView(qiblaNewAngle + lastNorthAngle,
					lastQiblaAngle, qiblaImageView);

		}
	}

	/*
	 * Unregistering from Location Listener (When GPS is set off)
	 */
	private void unregisterForGPS() {
		((LocationManager) getSystemService(Context.LOCATION_SERVICE))
				.removeUpdates(qiblaManager);

	}

	/*
	 * Unregistering every listeners
	 */
	private void unregisterListeners() {
		((LocationManager) getSystemService(Context.LOCATION_SERVICE))
				.removeUpdates(qiblaManager);

		((LocationManager) getSystemService(Context.LOCATION_SERVICE))
				.removeUpdates(qiblaManager);
		SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		Sensor gsensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		Sensor msensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mSensorManager.unregisterListener(qiblaManager, gsensor);
		mSensorManager.unregisterListener(qiblaManager, msensor);
		cancelSchedule();

	}

	private void useDefaultLocation() {

		String city = prefs.getString(Keys.CITY_KEY, DefaultValues.CITY);
		DbAdapter dbAdapter = new DbAdapter(
				SalaatFirstApplication.getLastInstance());
		dbAdapter.open();
		Location location = new Location("GPS");
		com.ahmedsoliman.devel.jislamic.astro.Location loc = dbAdapter
				.getLocation(city);
		location.setLatitude(loc.getDegreeLat());
		location.setLongitude(loc.getDegreeLong());
		qiblaManager.setLocationFromGPS(false);
		qiblaManager.onLocationChanged(location);
		this.setLocationText(city.equals("custom") ? "" : city,
				getLocationForPrint(loc.getDegreeLat(), loc.getDegreeLong()));
	}
}