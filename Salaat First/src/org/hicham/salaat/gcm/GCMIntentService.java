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

package org.hicham.salaat.gcm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.hicham.salaat.R;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

/**
 * Intent which is handling GCM messages and registrations. <br/>
 * <br/>
 * {@link GCMIntentService#onMessage} will handle message from GCM4Public server
 * - show up a notification and vibrate the phone.
 * {@link GCMIntentService#onRegistered} will send the registrationId and
 * SENDER_ID constant to the server.<br/>
 * <br/>
 * In order this class to work, don't forget to copy the gcm.jar file to libs
 * folder.
 * 
 * @author Vilius Kraujutis
 * @since 2012-12-01
 */
public class GCMIntentService extends GCMBaseIntentService {
	protected static final String SENDER_ID = "583720506659";
	public static final String MESSAGE_KEY = "message";
	public static final String TITLE_KEY = "title";
	public static final int GCM_NOTIFICATION_ID = 2;

	private static void makeVibration(Context context) {
		// Get instance of Vibrator from current Context
		Vibrator v = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);

		// Vibrate for 30 milliseconds
		v.vibrate(30);
	}

	public static void registerAtGCM(Context context) {
		GCMRegistrar.checkDevice(context);
		GCMRegistrar.checkManifest(context);
		final String regId = GCMRegistrar.getRegistrationId(context);
		if (regId.equals("")) {
			GCMRegistrar.register(context, SENDER_ID);
		} else {
			Log.v(TAG, "Already registered: " + regId);
		}
	}

	@Override
	protected String[] getSenderIds(Context context) {
		return new String[] { SENDER_ID };
	}

	@Override
	protected void onError(Context context, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onMessage(Context context, Intent messageIntent) {
		makeVibration(context);
		Log.d(TAG, "onMessage: " + messageIntent);
		Bundle extras = messageIntent.getExtras();

		String title = extras.getString("title");
		String message = "";
		try {
			message = URLDecoder.decode(extras.getString("message"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String url = extras.getString("url");

		showNotification(title, message, url);
	}

	@Override
	protected void onRegistered(Context arg0, String registrationId) {
		// registrationId is something like this:
		// APA91bH6fqNq7-MmMdDaQLcegqa8vbqoPXcvqwso_owIFaUR794cl0gmRJr3n_nQEPqUwfR_HvxERUgQvVKXPN3HQoTt5_k8BMmeeWunKHsg8dBCxvMcIM0K6YndMX2DU4ne3STyOFRJjkeBynXL19yy7Dqn53UbNA
		Log.d(TAG, "onRegistered: " + registrationId);
		registerGCMClient(registrationId, SENDER_ID);
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
	}

	/**
	 * This is called when application first time registers for the GCM.<br/>
	 * <br/>
	 * This method registers on the opensource GCM4Public server
	 * 
	 * @param registrationId
	 * @param senderId
	 */
	private void registerGCMClient(String registrationId, String senderId) {
		String osVersion = "osVersion=" + android.os.Build.VERSION.RELEASE;
		String url = "http://salaat-first-gcm.appspot.com/registergcmclient?registrationId="
				+ registrationId + "&" + osVersion;
		Log.d(TAG, url);
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		try {
			httpclient.execute(httpget);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showNotification(String title, String message, String url) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.icon).setContentTitle(title)
				.setContentText(message);
		Intent notificationIntent = new Intent(getBaseContext(),
				PushNotificationDialog.class);
		notificationIntent.putExtra(MESSAGE_KEY, message);
		notificationIntent.putExtra(TITLE_KEY, title);
		PendingIntent contentIntent = PendingIntent.getActivity(
				getBaseContext(), 0, notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		mBuilder.setContentIntent(contentIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(GCM_NOTIFICATION_ID, mBuilder.build());
	}
}