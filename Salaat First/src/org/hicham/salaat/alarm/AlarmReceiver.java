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

package org.hicham.salaat.alarm;

import static org.arabic.ArabicUtilities.reshapeSentence;
import static org.hicham.salaat.SalaatFirstApplication.TAG;
import static org.hicham.salaat.SalaatFirstApplication.prefs;

import java.util.Calendar;

import org.hicham.salaat.R;
import org.hicham.salaat.SalaatFirstApplication;
import org.hicham.salaat.settings.Keys;
import org.hicham.salaat.settings.Keys.DefaultValues;
import org.hicham.salaat.ui.activities.AdhanActivity;
import org.hicham.salaat.ui.activities.AdkarActivity;
import org.hicham.salaat.ui.activities.MainActivity;
import org.hicham.salaat.ui.fragments.AdkarFragment;
import org.hicham.salaat.util.Utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ahmedsoliman.devel.jislamic.DayPrayers;

public class AlarmReceiver extends BroadcastReceiver {

	private Context context;
	private int nextPrayer;
	private int oldRingerMode = -1;
	private static final int NOTIFICATION_ID = 1;
	private static final int ADKAR_NOTIFICATION_ID=2;
	public static final String PRAYER_ID = "prayer_id";

	private void activateSilent() {
		EventsHandler handler = new EventsHandler(context);
		if (prefs.getBoolean(Keys.GLOBAL_ACTIVATING_SILENT_KEY, DefaultValues.GLOBAL_ACTIVATING_SILENT)
				&& prefs.getBoolean(Keys.ACTIVATING_SILENT_KEY + nextPrayer,
						DefaultValues.ACTIVATING_SILENT)) {
			Log.i(TAG, "silent");
			AudioManager audioManager = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
			oldRingerMode = audioManager.getRingerMode();
			int silentMode;
			if (prefs.getBoolean(Keys.VIBRATE_MODE_KEY, DefaultValues.VIBRATE_MODE))
				silentMode = AudioManager.RINGER_MODE_VIBRATE;
			else
				silentMode = AudioManager.RINGER_MODE_SILENT;
			audioManager.setRingerMode(silentMode);
			if (prefs.getBoolean(Keys.VIBRATING_DURING_SWITCHING_KEY, DefaultValues.VIBRATING_DURING_SWITCHING)) {
				Vibrator v = (Vibrator) context
						.getSystemService(Context.VIBRATOR_SERVICE);
				long[] pattern = { 0, 400, 200, 400 };
				v.vibrate(pattern, -1);
			}
			handler.setOldRingerMode(oldRingerMode);
			Intent intent=new Intent(context, RingerModeChangeListener.class);
			context.startService(intent);
		}
		handler.scheduleSilentDeactivation(nextPrayer);
	}

	private void deactivateSilent() {
		Log.i(TAG, "deactivate silent");
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		Log.i(TAG, "older ringer mode value: " + oldRingerMode);
		if (oldRingerMode != -1)// if oldRingerMode==1 this means that we didn't
								// activate silent
		{
			audioManager.setRingerMode(oldRingerMode);
			oldRingerMode = -1;
			
			if (prefs.getBoolean(Keys.VIBRATING_DURING_SWITCHING_KEY, DefaultValues.VIBRATING_DURING_SWITCHING)) {
				Vibrator v = (Vibrator) context
						.getSystemService(Context.VIBRATOR_SERVICE);
				long[] pattern = { 0, 400, 200, 400 };
				v.vibrate(pattern, -1);
			}
			Intent intent=new Intent(context, RingerModeChangeListener.class);
			context.stopService(intent);
		}
		EventsHandler handler = new EventsHandler(context);
		handler.scheduleNextPrayerEvent(false);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		nextPrayer = intent.getIntExtra(EventsHandler.PRAYER_KEY, 0);
		SalaatFirstApplication.getLastInstance().refreshLanguage();
		switch (intent.getIntExtra(EventsHandler.EVENT_TYPE_KEY, 0)) {
		case EventsHandler.SHOW_NOTIIFCATION:
			showPrayerNotification(nextPrayer);
			break;
		case EventsHandler.SHOW_ADHAN:
			showAdhanActivity(nextPrayer);
			break;
		case EventsHandler.ACTIVATING_SILENT:
			activateSilent();
			break;
		case EventsHandler.DEACTIVATING_SILENT:
			oldRingerMode = intent.getIntExtra("OldRingerMode", -1);
			deactivateSilent();
			break;
		case EventsHandler.SHOW_ADKAR_ASSABAH:
			showAdkarAssabahNotification();
			break;
		case EventsHandler.SHOW_ADKAR_ALMASSAE:
			showAdkarAlmassaeNotification();
			break;
		case EventsHandler.SHOW_ADKAR_ALNAWM:
			showAdkarAnnawmNotification();
			break;
		}
	}

	private void showAdkarAssabahNotification() {
		buildAdkarNotification(R.array.adkar_assabah, R.string.adkar_assabah);
	}
	
	private void showAdkarAlmassaeNotification() {
		buildAdkarNotification(R.array.adkar_almassae, R.string.adkar_almassae);
	}
	
	private void showAdkarAnnawmNotification() {
		buildAdkarNotification(R.array.adkar_annawm, R.string.adkar_annawm);
	}
	
	
	private void buildAdkarNotification(int adkar_array, int adkar_title)
	{
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = R.drawable.notificationicon;
		String contentTitle=reshapeSentence(adkar_title);
		String contentText=reshapeSentence("حان موعد قراءة")+" "+reshapeSentence(adkar_title);
		NotificationCompat.Builder notificationBuilder=new NotificationCompat.Builder(context)
		.setSmallIcon(icon)
		.setContentTitle(contentTitle)
		.setContentText(contentText);
		notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
		notificationBuilder.setAutoCancel(true);
		notificationBuilder.setOnlyAlertOnce(true);
		Uri uri=Uri.parse(prefs.getString(Keys.ADKAR_NOTIFICATION_TONE_KEY, DefaultValues.NOTIFICATION_TONE));

		notificationBuilder.setSound(uri);
				
		Intent notificationIntent = new Intent(context, AdkarActivity.class);
		notificationIntent.putExtra(AdkarFragment.ADHKAR_ARRAY_ID, adkar_array);
		notificationIntent.putExtra(AdkarActivity.ADKAR_TITLE, adkar_title);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, PendingIntent.FLAG_ONE_SHOT);
	
		notificationBuilder.setContentIntent(contentIntent);
		
		notificationManager.notify(ADKAR_NOTIFICATION_ID, notificationBuilder.build());
	}

	private void showAdhanActivity(int prayer) {
		Log.i(TAG, "Adhan: Prayer=" + prayer);
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(NOTIFICATION_ID);
		if (prefs.getBoolean(Keys.SHOW_ADHAN_KEY + prayer, DefaultValues.SHOW_ADHAN)) {
			Intent intent = new Intent(context, AdhanActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_NO_USER_ACTION);
			intent.putExtra(PRAYER_ID, prayer);
			context.startActivity(intent);
		}
		EventsHandler handler = new EventsHandler(context);
		int lastAdhan=Integer.parseInt(prayer+""+Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
		handler.getAlarmPreferences().edit().putInt(EventsHandler.LAST_ADHAN_KEY, lastAdhan).commit();
		if(prayer!=DayPrayers.JUMUA)
			/*if it's Jumua, the silent activation was scheduled before adhan*/
			handler.scheduleSilentActivation(nextPrayer);
	}

	private void showPrayerNotification(int prayer) {
		Log.i(TAG, "notification: Prayer=" + prayer);
		if (prefs.getBoolean(Keys.SHOW_NOTIFICATION_KEY, DefaultValues.SHOW_NOTIFICATION)) {
			NotificationManager notificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			int icon = R.drawable.notificationicon;
			int notificationDelay=prefs.getInt(Keys.NOTIFICATION_TIME_KEY, DefaultValues.NOTIFICATION_TIME);
			StringBuilder contentText = new StringBuilder();
			contentText.append(reshapeSentence(R.string.notification_text1));
			
			
			if(prefs.getString(Keys.LANGUAGE_KEY, DefaultValues.LANGUAGE).contains("ar"))
			{
				contentText.append(" "+Utils.convertArabicNumberToStr(notificationDelay)+" ");
			}
			else
			{
				contentText.append(" "+notificationDelay+" ");
			}
			contentText.append(reshapeSentence(R.string.notification_text2));

			
			CharSequence contentTitle = SalaatFirstApplication
					.getPrayerName(prayer);

			NotificationCompat.Builder notificationBuilder=new NotificationCompat.Builder(context)
				.setSmallIcon(icon)
				.setContentTitle(contentTitle)
				.setContentText(contentText);
			notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
			notificationBuilder.setAutoCancel(true);
			notificationBuilder.setOnlyAlertOnce(true);
			Uri uri=Uri.parse(prefs.getString(Keys.NOTIFICATION_TONE_KEY, DefaultValues.NOTIFICATION_TONE));

			notificationBuilder.setSound(uri);
						
			Intent notificationIntent = new Intent(context, MainActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					notificationIntent, 0);
			
			notificationBuilder.setContentIntent(contentIntent);
			
			notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
		}
		EventsHandler handler = new EventsHandler(context);
		handler.scheduleNextPrayerAdhan(nextPrayer);
	}
	
}
