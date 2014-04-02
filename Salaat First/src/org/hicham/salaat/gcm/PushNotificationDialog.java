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

import org.hicham.salaat.ui.dialogs.LinkifiedAlertDialog;
import org.holoeverywhere.app.Activity;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

public class PushNotificationDialog extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		String message = getIntent().getExtras().getString(
				GCMIntentService.MESSAGE_KEY);
		showAlertDialog(message);
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(GCMIntentService.GCM_NOTIFICATION_ID);
	}

	private void showAlertDialog(String message) {
		LinkifiedAlertDialog dialog = new LinkifiedAlertDialog();
		Bundle args = new Bundle();
		args.putString(LinkifiedAlertDialog.TEXT_KEY, message);
		dialog.setArguments(args);
		dialog.show(this);
	}

}
