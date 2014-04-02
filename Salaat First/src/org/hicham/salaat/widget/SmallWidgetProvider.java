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

package org.hicham.salaat.widget;

import static org.hicham.salaat.SalaatFirstApplication.TAG;

import org.hicham.salaat.R;
import org.hicham.salaat.widget.service.SmallWidgetService;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class SmallWidgetProvider extends AppWidgetProvider {
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		Log.i(TAG, "Widget Provider onReceive"+action);
		super.onReceive(context, intent);
	}
	
	@Override
	public void onDisabled(Context context) {
		Log.i(TAG, "onDisabled()");
		Intent intent = new Intent(context, SmallWidgetService.class);
		context.stopService(intent);
		super.onDisabled(context);
	}

	

	
	
	@Override
	public void onEnabled(Context context) {
		Log.i(TAG, "onEnabled()");
		super.onEnabled(context);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.i(TAG, "onUpdate()");
		
		Intent serviceIntent = new Intent(context, SmallWidgetService.class);
		context.startService(serviceIntent);
		for (int i = 0; i < appWidgetIds.length; i++)
		{
			Intent intent = new Intent(context, SmallWidgetService.class);
			RemoteViews views = new RemoteViews(context.getPackageName(),
					R.layout.small_widget_layout);
			PendingIntent pi = PendingIntent.getService(context, SmallWidgetService.SERVICE_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			views.setOnClickPendingIntent(views.getLayoutId(), pi);
			appWidgetManager.updateAppWidget(appWidgetIds[i], views);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
}
