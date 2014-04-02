/** This file is part of Salaat First.
 *
 *   Licensed under the Creative Commons Attribution-NonCommercial 4.0 International Public License;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at:
 *   
 *   http://creativecommons.org/licenses/by-nc/4.0/legalcode
 *
 *	@author Hicham BOUSHABA 2012 <hicham.boushaba@gmail.com>
 *	
 */
package org.hicham.salaat;


import static com.ahmadiv.dari.DariGlyphUtils.reshapeText;
import static org.hicham.salaat.PrayerTimesActivity.isReshapingNessecary;

import java.util.Locale;

import org.hicham.alarm.AlarmReceiver;
import org.hicham.salaat.settings.Keys;

import com.farsitel.qiblacompass.activities.QiblaActivity;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class MainActivity extends TabActivity{
	public void onCreate(Bundle savedInstanceState) {
        if(getResources().getDisplayMetrics().ydpi<=120)
    		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        validateLanguage(this.getBaseContext());
        
        TabHost tabHost = getTabHost();
        
        TabSpec prayerSpec = tabHost.newTabSpec("Prayer_times");
        // setting Title and Icon for the Tab
        prayerSpec.setIndicator(reshapeText(getString(R.string.prayer_times), isReshapingNessecary), getResources().getDrawable(R.drawable.prayer_times_icon));
        Intent prayersIntent = new Intent(this, PrayerTimesActivity.class);
        prayerSpec.setContent(prayersIntent);
        
        TabSpec qiblaSpec = tabHost.newTabSpec("Qibla");
        // setting Title and Icon for the Tab
        qiblaSpec.setIndicator(reshapeText(getString(R.string.qibla), isReshapingNessecary), getResources().getDrawable(R.drawable.compass));
        Intent qiblaIntent = new Intent(this, QiblaActivity.class);
        qiblaSpec.setContent(qiblaIntent);
                
        tabHost.addTab(prayerSpec);
        tabHost.addTab(qiblaSpec);

        }

public static void validateLanguage(Context context)
{
	SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(context);
    String language=pref.getString(Keys.LANGUAGE_KEY, "en");
    Locale locale=new Locale(language);
    Locale.setDefault(locale);
    Configuration config=new Configuration();
    config.locale=locale;
    context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
		if(language.equalsIgnoreCase("ar_MA"))
			//determine if language needs reshaping
			isReshapingNessecary=true;
		else
			isReshapingNessecary=false;

}
}
