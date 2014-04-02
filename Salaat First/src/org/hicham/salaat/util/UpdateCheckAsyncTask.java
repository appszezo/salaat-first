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

package org.hicham.salaat.util;

import static org.hicham.salaat.SalaatFirstApplication.TAG;
import static org.arabic.ArabicUtilities.reshapeSentence;



import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

import org.apache.http.util.ByteArrayBuffer;
import org.hicham.salaat.BuildConfig;
import org.hicham.salaat.R;
import org.hicham.salaat.SalaatFirstApplication;
import org.holoeverywhere.app.AlertDialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class UpdateCheckAsyncTask extends AsyncTask<Integer, Void, Boolean>{
	
	
	private static final String PREF_REMINDER_TIME = "update_remind_time";
	private static final String PREF_IGNORE_VERSION_CODE = "ignore_version_code";
	private static final String PREF_LAST_CHECK_TIME="last_check_time";
	private Activity activity;
	private int versionCode;
	
	public UpdateCheckAsyncTask(Activity activity) {
		this.activity=activity;
	}
	
	
	public void checkVersion() {
			long currentTimeStamp = System.currentTimeMillis();
			long reminderTimeStamp = getReminderTime();
				Log.v(TAG, "currentTimeStamp="+currentTimeStamp);
				Log.v(TAG, "reminderTimeStamp="+reminderTimeStamp);
			long lastCheckTime=getLastCheckTime();

			if(currentTimeStamp > (lastCheckTime + (24 * 60 * 60 * 1000)) && currentTimeStamp > reminderTimeStamp){
				// fire request to get update version content
					Log.v(TAG, "getting update content...");
				int currentVersion=getCurrentVersionCode();
				int ignoredVersion=getIgnoredVersionCode();
				execute(new Integer[]{currentVersion, ignoredVersion});
				setLastCheckTime();
			}
	}
	
	public int getCurrentVersionCode() {
		int currentVersionCode = 0;
		PackageInfo pInfo;
		try {
			pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
			currentVersionCode = pInfo.versionCode;
		} catch (NameNotFoundException e) {
			// return 0
		}
		return currentVersionCode;
	}

	
	private long getReminderTime() {
		return SalaatFirstApplication.prefs.getLong(PREF_REMINDER_TIME, 0);
	}
	
	private long getLastCheckTime() {
		return SalaatFirstApplication.prefs.getLong(PREF_LAST_CHECK_TIME, 0);
	}

	private void setLastCheckTime() {
		SalaatFirstApplication.prefs.edit().putLong(PREF_LAST_CHECK_TIME, System.currentTimeMillis()).commit();		
	}

	
	private void setReminderTime(long reminderTimeStamp) {
		SalaatFirstApplication.prefs.edit().putLong(PREF_REMINDER_TIME, reminderTimeStamp).commit();		
	}

	
	private void ignoreThisVersion() {
		SalaatFirstApplication.prefs.edit().putInt(PREF_IGNORE_VERSION_CODE, versionCode).commit();
	}
	
	
	public int getIgnoredVersionCode() {
		return SalaatFirstApplication.prefs.getInt(PREF_IGNORE_VERSION_CODE, 1);
	}

    private String getGooglePlayStoreUrl(){
    	String id = activity.getApplicationInfo().packageName; // current google play is using package name as id
    	return "market://details?id=" + id; 	
    }
    
    

	@Override
	protected Boolean doInBackground(Integer... params) {
		try{
			URL updateURL = new URL("https://dl.dropboxusercontent.com/u/40450073/lastversion");                
			URLConnection conn = updateURL.openConnection(); 
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			
			int current = 0;
			while((current = bis.read()) != -1){
				baf.append((byte)current);
			}

			/* Convert the Bytes read to a String. */
			final String s = new String(baf.toByteArray());         

			/* Get current Version Number */
			int curVersion = params[0];
			int newVersion = Integer.valueOf(s);
			Log.i(TAG, "current version: "+curVersion);
			Log.i(TAG, "new version: "+newVersion);
			/* Is a higher version than the current already out? */
			if (newVersion > curVersion) {
				if(params.length==2)
					{
						int ignoredVersionCode=params[1];
						if(newVersion==ignoredVersionCode)
							return false;
					}
				versionCode=newVersion;
				return true;
			}
			else
				return false;

		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		if(result)
		{
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);

				builder.setTitle(reshapeSentence(R.string.update_dialog_title));
				builder.setMessage(reshapeSentence(R.string.update_dialog_message));

					builder.setPositiveButton(reshapeSentence(R.string.update_dialog_updateNow_label), new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							String url=getGooglePlayStoreUrl();
							try{
								Uri uri = Uri.parse(url);
						    	Intent intent = new Intent(Intent.ACTION_VIEW, uri);	    	
						    	activity.startActivity(intent);
							}catch(Exception e){
								Log.e(TAG, "is update url correct?" + e );
							}
						}
					});
					builder.setNeutralButton(reshapeSentence(R.string.update_dialog_remindMe_label), new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							Calendar c = Calendar.getInstance(); 
							long currentTimeStamp = c.getTimeInMillis();

							c.add(Calendar.DAY_OF_MONTH, 2);
							long reminderTimeStamp = c.getTimeInMillis();

							if(BuildConfig.DEBUG){
								Log.v(TAG, "currentTimeStamp="+currentTimeStamp);
								Log.v(TAG, "reminderTimeStamp="+reminderTimeStamp);
							}

							setReminderTime(reminderTimeStamp);

						}

					});
					builder.setNegativeButton(reshapeSentence(R.string.update_dialog_ignore_label), new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							ignoreThisVersion();
						}
					});

				builder.setCancelable(true);

		        
		        AlertDialog dialog = builder.create();
		        if(activity != null && !activity.isFinishing()){
		        	dialog.show();
		        }
			}
		}
}
