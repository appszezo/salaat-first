/** This file is part of Salaat First.
 *
 *   Licensed under the Creative Commons Attribution-NonCommercial 4.0 International Public License;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at:
 *   
 *   http://creativecommons.org/licenses/by-nc/4.0/legalcode
 *
*
*	@author Hicham BOUSHABA 2011 <hicham.boushaba@gmail.com>
*	
*/

package org.hicham.salaat;

import static com.ahmadiv.dari.DariGlyphUtils.reshapeText;

import java.util.Calendar;

import org.hicham.alarm.AlarmReceiver;
import org.hicham.alarm.EventsHandler;
import org.hicham.salaat.calculating.PrayerTimesCalculator;
import org.hicham.salaat.db.DbAdapter;
import org.hicham.salaat.settings.Keys;
import org.hicham.salaat.settings.Settings;
import org.hicham.salaat.settings.Wizard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.webkit.WebSettings.TextSize;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class PrayerTimesActivity extends Activity {
	public static final String TAG="org.hicham.salaat";
	PrayerTimesCalculator calculator;
	TextView sobhTextView;
	TextView choroukTextView;
	TextView dhuhrTextView;
	TextView asrTextView;
	TextView maghribTextView;
	TextView ichaaTextView;
	TextView sobhTextAr;
	public static boolean isReshapingNessecary;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prayer_times);
        MainActivity.validateLanguage(this.getBaseContext());
        sobhTextView=(TextView) findViewById(R.id.sobh);
        choroukTextView=(TextView) findViewById(R.id.sunrise);
        dhuhrTextView=(TextView) findViewById(R.id.dohr);
        asrTextView=(TextView) findViewById(R.id.asr);
        maghribTextView=(TextView) findViewById(R.id.maghrib);
        ichaaTextView=(TextView) findViewById(R.id.ishaa);
        
        fixTextProperties();
        //initializing the text
      
        //for versions 1.0.2b and the next one
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //------
        //letting for showing wizard configuration

        if (prefs.getBoolean("firstrun", true)) {
            Log.i(TAG, "first run");
        	prefs.edit().putBoolean("firstrun", false).commit();
        	Intent intent=new Intent(this, Wizard.class);
        	startActivity(intent);
        	return;
        }
               
        //schedule event
        if(!prefs.getBoolean("Event_scheduled", false))
        { 
        	EventsHandler handler=new EventsHandler(this);
        	handler.scheduleNextEvent(-1, -1); //not defining the next prayer nor the next event
        }
    }
        
    
	/**
	 * Called every time the menu button is pressed
	 * we use this for updating the menu when language is changed 
	 */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	menu.clear();
    	MenuItem settings=menu.add(0,1,0,reshapeText(getString(R.string.settings_text),isReshapingNessecary));
    	settings.setIcon(android.R.drawable.ic_menu_manage);
    	MenuItem about=menu.add(0,3,0,reshapeText(getString(R.string.about),isReshapingNessecary));
    	about.setIcon(android.R.drawable.ic_dialog_info);
    	MenuItem quitter=menu.add(0,2,0,reshapeText(getString(R.string.quit_text),isReshapingNessecary));
    	quitter.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// TODO Auto-generated method stub
    	switch(item.getItemId())
    	{
    	case 1:
    		showSettings();
    		break;
    	case 2:
    		quit();
    		break;
    	case 3:
    		showAboutActivity();
    		break;
    	}
    	return super.onOptionsItemSelected(item);
    }
   
    /**
     * Show the about activity
     */
    private void showAboutActivity() {
    	Intent intent=new Intent(this, AboutActivity.class);
    	startActivity(intent);
	}
    
    /**
     * Show Settings activity
     */
	private void showSettings() {
		Intent intent=new Intent(this, Settings.class);
		startActivity(intent);
		
	}
	
	/**
	 * Quitting the application, this is called when button quit is pressed
	 * User can stop the service from this one
	 */
	private void quit() {
		AlertDialog.Builder adb=new AlertDialog.Builder(this);
		adb.setMessage(reshapeText(getString(R.string.quit_message), isReshapingNessecary));
		adb.setPositiveButton(reshapeText(getString(R.string.quit_text), isReshapingNessecary), new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				new EventsHandler(PrayerTimesActivity.this).cancelAlarm();
				finish();
			}
		});
		adb.setNegativeButton(reshapeText(getString(R.string.minimize_text), isReshapingNessecary), new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				//don't do nothing
				finish();
			}
		});
		adb.show();
	}
	
	/**
	 * Setting the font to textviews passed in args
	 * @param tf
	 * @param params
	 */
	public static void setLayoutFont(Typeface tf, TextView...params) {
	    for (TextView tv : params) {
	        tv.setTypeface(tf);
	    }
	}
	
	/**
	 * fix text size according to the actual screen size
	 */
	private void fixTextProperties()
	{
		System.out.println("density "+getResources().getDisplayMetrics().density);
		System.out.println("dpi "+getResources().getDisplayMetrics().xdpi);
		System.out.println("Screen size "+(getResources().getConfiguration().screenLayout& 
			    Configuration.SCREENLAYOUT_SIZE_MASK));
		RelativeLayout layout=(RelativeLayout)findViewById(R.id.mainLayout);
        Display displayManager=getWindowManager().getDefaultDisplay();
        int width=displayManager.getWidth();
        int height=displayManager.getHeight();
        final float scale = getResources().getDisplayMetrics().density;

        if(scale<1)
            {
        		layout.setPadding(0, height/12, 5, 5);
        		
            }
        else if(scale==1)
        {
    		layout.setPadding(0, height/10, 5, 5);
        }
        else
        	layout.setPadding(0, height/8, 10, 10);	
                
        double textSize=(width/scale)*0.05;
        double bottomMargin=height*0.03;
        //double leftAndRightMargins=
        System.out.println("textSize "+textSize+" width "+width+" scale "+scale);
        TextView sobhTextAr=(TextView)findViewById(R.id.sobhtext);
        TextView sunriseTextAr=(TextView)findViewById(R.id.sunrisetext);
        TextView dohrTextAr=(TextView)findViewById(R.id.dohrtext);
        TextView asrTextAr=(TextView)findViewById(R.id.asrtext);
        TextView maghribTextAr=(TextView)findViewById(R.id.maghribtext);
        TextView ishaaTextAr=(TextView)findViewById(R.id.ishaatext);
        sobhTextAr.setText(reshapeText("الصبح", true));
        sunriseTextAr.setText(reshapeText("الشروق", true));
        dohrTextAr.setText(reshapeText("الظهر", true));
        asrTextAr.setText(reshapeText("العصر", true));
        maghribTextAr.setText(reshapeText("المغرب", true));
        ishaaTextAr.setText(reshapeText("العشاء", true));
        TextView sobhTextFr=(TextView)findViewById(R.id.sobhtextfr);
        TextView sunriseTextFr=(TextView)findViewById(R.id.sunrisetextfr);
        TextView dohrTextFr=(TextView)findViewById(R.id.dohrtextfr);
        TextView asrTextFr=(TextView)findViewById(R.id.asrtextfr);
        TextView maghribTextFr=(TextView)findViewById(R.id.maghribtextfr);
        TextView ishaaTextFr=(TextView)findViewById(R.id.ishaatextfr);
        //set text size
        sobhTextAr.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float)textSize);
        sunriseTextAr.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float)textSize);
        dohrTextAr.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float)textSize);
        asrTextAr.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float)textSize);
        maghribTextAr.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float)textSize);
        ishaaTextAr.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float)textSize);
        sobhTextFr.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float)textSize);
        sunriseTextFr.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float)textSize);
        dohrTextFr.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float)textSize);
        asrTextFr.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float)textSize);
        maghribTextFr.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float)textSize);
        ishaaTextFr.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float)textSize);
        sobhTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float)textSize);
        choroukTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float)textSize);
        dhuhrTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float)textSize);
        asrTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float)textSize);
        maghribTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float)textSize);
        ichaaTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float)textSize);
        ((TextView)findViewById(R.id.cityName)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float)textSize);
        
        //set margins
        LinearLayout.LayoutParams textArLayoutParams=(LinearLayout.LayoutParams) sobhTextAr.getLayoutParams();
        textArLayoutParams.bottomMargin=(int) (bottomMargin-2*scale);
        sobhTextAr.setLayoutParams(textArLayoutParams);
        sunriseTextAr.setLayoutParams(textArLayoutParams);
       	dohrTextAr.setLayoutParams(textArLayoutParams);
        asrTextAr.setLayoutParams(textArLayoutParams);
        maghribTextAr.setLayoutParams(textArLayoutParams);
        ishaaTextAr.setLayoutParams(textArLayoutParams);
        LinearLayout.LayoutParams textFrLayoutParams=(LinearLayout.LayoutParams) sobhTextFr.getLayoutParams();
        textFrLayoutParams.bottomMargin=(int) (bottomMargin);
        sobhTextFr.setLayoutParams(textFrLayoutParams);
        sunriseTextFr.setLayoutParams(textFrLayoutParams);
        dohrTextFr.setLayoutParams(textFrLayoutParams);
        asrTextFr.setLayoutParams(textFrLayoutParams);
        maghribTextFr.setLayoutParams(textFrLayoutParams);
        ishaaTextFr.setLayoutParams(textFrLayoutParams);
        
        sobhTextView.setLayoutParams(textFrLayoutParams);
        choroukTextView.setLayoutParams(textFrLayoutParams);
        dhuhrTextView.setLayoutParams(textFrLayoutParams);
        asrTextView.setLayoutParams(textFrLayoutParams);
        maghribTextView.setLayoutParams(textFrLayoutParams);
        ichaaTextView.setLayoutParams(textFrLayoutParams);

        Typeface tf = Typeface.createFromAsset(getAssets(), "droidsans.ttf");
        setLayoutFont(tf, sobhTextAr,sunriseTextAr,dohrTextAr,asrTextAr,maghribTextAr,ishaaTextAr);

	}
	
	@Override
    protected void onStart() {
    	super.onStart();
    	MainActivity.validateLanguage(this.getBaseContext());
    	SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(this);
    	int asrMadhab=Integer.parseInt(pref.getString(Keys.ASR_MADHAB_KEY, "0"));
    	boolean isCustomCitySelected=pref.getBoolean(Keys.CUSTOM_CITY_KEY, false);  	
    	double timeZone=Double.parseDouble(pref.getString(Keys.TIME_ZONE_KEY, "0"));
    	String city;
    	if(isCustomCitySelected)
    		{
    		city="custom";
        	((TextView)findViewById(R.id.cityName)).setVisibility(View.INVISIBLE);
    		}
    	else
    		{
    			city=pref.getString(Keys.CITY_KEY, "Rabat et Salé");
    			((TextView)findViewById(R.id.cityName)).setText(city);
    		}
    	Log.i(TAG, "City selected: "+city);
    	DbAdapter dbAdapter=new DbAdapter(this);
    	dbAdapter.open();
    	double[] coordonnees=dbAdapter.getLocation(city); //{latitude, longitude,altitude}
    	double[] offsets=new double[6];
    	for(int i=0;i<6;i++)
    	{
    		offsets[i]=(double)(pref.getInt(Keys.TIME_OFFSET_KEY+i, 0))/60;
    	}
    	calculator=new PrayerTimesCalculator(coordonnees[1], coordonnees[0], Settings.getOrganization(this), timeZone, asrMadhab, coordonnees[2], offsets);
    	calculator.performCalculs(Calendar.getInstance());
    	String[] times=calculator.getPrayerTimesStr();
    	sobhTextView.setText(times[PrayerTimesCalculator.FAJR]);
    	choroukTextView.setText(times[PrayerTimesCalculator.CHOROUK]);
    	dhuhrTextView.setText(times[PrayerTimesCalculator.DHUHR]);
    	asrTextView.setText(times[PrayerTimesCalculator.ASR]);
    	maghribTextView.setText(times[PrayerTimesCalculator.MAGHRIB]);
    	ichaaTextView.setText(times[PrayerTimesCalculator.ICHAA]);
    	dbAdapter.close();
    }
}