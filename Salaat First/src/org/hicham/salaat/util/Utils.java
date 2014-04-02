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

import static org.arabic.ArabicUtilities.reshapeSentence;
import static org.hicham.salaat.SalaatFirstApplication.prefs;

import java.text.DecimalFormat;
import java.util.Calendar;

import org.hicham.salaat.R;
import org.hicham.salaat.SalaatFirstApplication;
import org.hicham.salaat.settings.Keys;
import org.hicham.salaat.settings.Keys.DefaultValues;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.TextView;

import android.graphics.Typeface;
import android.support.v4.text.BidiFormatter;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;

import com.ahmedsoliman.devel.jislamic.hijri.HijriCalendar;

public class Utils {

	public static String convertLatitudeToSecondsFormat(double latitude) {
		if (latitude < -180.0 || latitude > 180.0 || Double.isNaN(latitude)) {
			throw new IllegalArgumentException("latitude=" + latitude);
		}

		StringBuilder sb = new StringBuilder();
		double positivelatitude = latitude;
		// Handle negative values
		if (latitude < 0) {
			positivelatitude = -positivelatitude;
		}

		DecimalFormat df = new DecimalFormat("##");
		int degrees = (int) Math.floor(positivelatitude);
		sb.append(degrees);
		sb.append('°');
		positivelatitude -= degrees;
		positivelatitude *= 60.0;
		int minutes = (int) Math.floor(positivelatitude);
		sb.append(minutes);
		sb.append('\'');
		positivelatitude -= minutes;
		positivelatitude *= 60.0;

		sb.append(df.format(positivelatitude) + '"');
		sb.append(latitude < 0 ? "S" : "N");
		return sb.toString();
	}

	public static String convertLongitudeToSecondsFormat(double longitude) {
		if (longitude < -180.0 || longitude > 180.0 || Double.isNaN(longitude)) {
			throw new IllegalArgumentException("longitude=" + longitude);
		}

		StringBuilder sb = new StringBuilder();
		double positiveLongitude = longitude;
		// Handle negative values
		if (longitude < 0) {
			positiveLongitude = -positiveLongitude;
		}

		DecimalFormat df = new DecimalFormat("##");
		int degrees = (int) Math.floor(positiveLongitude);
		sb.append(degrees);
		sb.append('°');
		positiveLongitude -= degrees;
		positiveLongitude *= 60.0;
		int minutes = (int) Math.floor(positiveLongitude);
		sb.append(minutes);
		sb.append('\'');
		positiveLongitude -= minutes;
		positiveLongitude *= 60.0;

		sb.append(df.format(positiveLongitude) + '"');
		sb.append(longitude < 0 ? "W" : "E");
		return sb.toString();
	}
	
	public static void formatDateLayout(Calendar today, LinearLayout layout, float txtSize)
	{
		layout.removeAllViews();
		/*get the day name before the conversion*/
		String dayName=SalaatFirstApplication.getLastInstance().getResources().getStringArray(R.array.days)[today.get(Calendar.DAY_OF_WEEK)-1];
		
		HijriCalendar todayHijri=new HijriCalendar(today);
		String day=todayHijri.get(Calendar.DAY_OF_MONTH)+"";
		String month=SalaatFirstApplication.getLastInstance().getResources().getStringArray(R.array.months_hijri)[todayHijri.get(Calendar.MONTH)];
		String year=todayHijri.get(Calendar.YEAR)+"";
		Typeface tf=Typeface.createFromAsset(layout.getContext().getAssets(), "droidsans.ttf");
		int txtColor=layout.getResources().getColor(R.color.text_color);
		if(!prefs.getString(Keys.LANGUAGE_KEY, DefaultValues.LANGUAGE).contains("ar")||android.os.Build.VERSION.SDK_INT>=17)
		{
			/*if not arabic, or android version >= 4.2*/
			
			TextView view=new TextView(layout.getContext());
			view.setText(dayName + " " + day + " " + month + " " + " " + year);
			view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, txtSize);
			view.setEllipsize(TruncateAt.MARQUEE);
			view.setTextColor(txtColor);
			view.setSingleLine(true);
			if(prefs.getString(Keys.LANGUAGE_KEY, DefaultValues.LANGUAGE).contains("ar"))
			{
				/* if it's arabic, set the font*/
				view.setTypeface(tf);
			}
			layout.addView(view);
		}

		else
		{
			TextView yearView = new TextView(layout.getContext());
			yearView.setText(reshapeSentence(year) + " ");
			yearView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, txtSize);
			yearView.setEllipsize(TruncateAt.MARQUEE);
			yearView.setTextColor(txtColor);
			yearView.setSingleLine(true);
			TextView monthView = new TextView(layout.getContext());
			monthView.setText(reshapeSentence(month) + " ");
			monthView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, txtSize);
			monthView.setEllipsize(TruncateAt.MARQUEE);
			monthView.setTextColor(txtColor);

			monthView.setSingleLine(true);
			TextView dayView = new TextView(layout.getContext());
			dayView.setText(reshapeSentence(day) + " ");
			dayView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, txtSize);
			dayView.setEllipsize(TruncateAt.MARQUEE);
			dayView.setTextColor(txtColor);
 
			dayView.setSingleLine(true);
			TextView dayNameView = new TextView(layout.getContext());
			dayNameView.setText(reshapeSentence(dayName) + " ");
			dayNameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, txtSize);
			dayNameView.setEllipsize(TruncateAt.MARQUEE);
			dayNameView.setTextColor(txtColor);

			dayNameView.setSingleLine(true);

			yearView.setTypeface(tf);
			monthView.setTypeface(tf);
			yearView.setTypeface(tf);
			dayNameView.setTypeface(tf);
			
			layout.addView(yearView);
			layout.addView(monthView);
			layout.addView(dayView);
			layout.addView(dayNameView);
		}
		
	}

	public static String convertArabicNumberToStr(int n)
	{
		if(n>20)
		{
			throw new IllegalArgumentException("Number should be less than 20");
		}
		
		switch(n)
		{
		case 1:
			return "دقيقة";
		case 2:
			return "دقيقتين";
		case 3:
			return "ثلاث دقائق";
		case 4:
			return "أربع دقائق";
		case 5:
			return "خمس دقائق";
		case 6:
			return "ست دقائق";
		case 7:
			return "سبع دقائق";
		case 8:
			return "ثمان دقائق";
		case 9:
			return "تسع دقائق";
		case 10:
			return "عشر دقائق";
		case 11:
			return "أحد عشر دقيقة";
		case 12:
			return "اثنا عشر دقيقة";
		case 13:
			return "ثلاث عشر دقيقة";
		case 14:
			return "أربع عشر دقيقة";
		case 15:
			return "خمس عشر دقيقة";
		case 16:
			return "ست عشر دقيقة";
		case 17:
			return "سبع عشر دقيقة";
		case 18:
			return "ثمان عشر دقيقة";
		case 19:
			return "تسع عشر دقيقة";
		case 20:
			return "عشرين";
		}
		return null;
	}
	
}
