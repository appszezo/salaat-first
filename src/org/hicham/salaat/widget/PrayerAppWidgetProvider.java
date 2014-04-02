/** This file is part of Salaat First.
 *
 *   Licensed under the Creative Commons Attribution-NonCommercial 4.0 International Public License;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at:
 *   
 *   http://creativecommons.org/licenses/by-nc/4.0/legalcode
 *
*
*	@author Hicham BOUSHABA 2012 <hicham.boushaba@gmail.com>
*	
*/

package org.hicham.salaat.widget;

import org.hicham.salaat.PrayerTimesActivity;
import org.hicham.salaat.R;
import org.hicham.salaat.WidgetService;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class PrayerAppWidgetProvider extends AppWidgetProvider{
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.i(PrayerTimesActivity.TAG, "onUpdate()");
		Intent intent=new Intent(context, WidgetService.class);
		RemoteViews views=new RemoteViews(context.getPackageName(), R.layout.widgetlayout);
		PendingIntent pi=PendingIntent.getService(context, 0, intent, 0);
		views.setOnClickPendingIntent(views.getLayoutId(), pi);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        for(int i=0;i<appWidgetIds.length;i++)
        	manager.updateAppWidget(appWidgetIds[i], views);
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	
	@Override
	public void onEnabled(Context context) {
		Log.i(PrayerTimesActivity.TAG, "onEnabled()");
		Intent intent=new Intent(context, WidgetService.class);
		context.startService(intent);
		super.onEnabled(context);
	}
	
	@Override
	public void onDisabled(Context context) {
		Log.i(PrayerTimesActivity.TAG, "onDisabled()");
		Intent intent=new Intent(context, WidgetService.class);
		context.stopService(intent);
		super.onDisabled(context);
	}

}
