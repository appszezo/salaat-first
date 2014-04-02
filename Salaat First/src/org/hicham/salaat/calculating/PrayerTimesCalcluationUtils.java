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

package org.hicham.salaat.calculating;

import static java.lang.Math.round;
import static org.hicham.salaat.SalaatFirstApplication.TAG;
import static org.hicham.salaat.SalaatFirstApplication.dBAdapter;
import static org.hicham.salaat.SalaatFirstApplication.prefs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;

import org.hicham.salaat.SalaatFirstApplication;
import org.hicham.salaat.settings.Keys;
import org.hicham.salaat.settings.Keys.DefaultValues;

import android.util.Log;

import com.ahmedsoliman.devel.jislamic.DayPrayers;
import com.ahmedsoliman.devel.jislamic.JIslamic;
import com.ahmedsoliman.devel.jislamic.Mathhab;
import com.ahmedsoliman.devel.jislamic.Method;
import com.ahmedsoliman.devel.jislamic.astro.Location;

public class PrayerTimesCalcluationUtils {

	/**
	 * converting the hour to a string having the forme: HH:mm
	 * 
	 * @param time
	 * @return
	 */
	public static String floatToStr(double time) {
		Calendar _Time = Calendar.getInstance();
		DateFormat formatter = new SimpleDateFormat("HH:mm");
		SimpleTimeZone timeZone = new SimpleTimeZone(0, "");
		formatter.setTimeZone(timeZone);
		_Time.setTimeInMillis(round(time * 1000));
		
		return formatter.format(_Time.getTime()).toString();
	}

	private static JIslamic getCalculator(final Location loc,
			final Method method) {
		int asrMadhab = Integer.parseInt(prefs.getString(Keys.ASR_MADHAB_KEY,
				DefaultValues.ASR_MADHAB));
			method.setMathhab(asrMadhab == 0 ? Mathhab.SHAAFI : Mathhab.HANAFI);
		
		double[] offsets = new double[6];
		for (int i = 0; i < 6; i++) {
			offsets[i] = (prefs.getInt(Keys.TIME_OFFSET_KEY + i, DefaultValues.TIME_OFFSET));
		}
		method.setUserOffsets(offsets);
		double timeZone = Double.parseDouble(prefs.getString(
				Keys.TIME_ZONE_KEY, DefaultValues.TIME_ZONE));
		loc.setGmtDiff(timeZone);
		boolean dst=prefs.getBoolean(Keys.USE_DST_MODE_KEY, DefaultValues.USE_DST_MODE);
		if(dst)
			loc.setDst(1);
		return new JIslamic(loc, method);
	}

	/**
	 * Return Prayer Times using current application settings, used in most
	 * calculations
	 * 
	 * @return {@link DayPrayers}
	 */

	public static DayPrayers getCurrentPrayerTimes() {
		String city = prefs.getString(Keys.CITY_KEY, DefaultValues.CITY);
		Log.i(TAG, "City selected: " + city);
		Location loc = dBAdapter.getLocation(city);
		return getPrayerTimes(loc, (GregorianCalendar) Calendar.getInstance());
	}

	public static JIslamic getDefaultCalculator() {
		String city = prefs.getString(Keys.CITY_KEY, DefaultValues.CITY);
		Log.i(TAG, "City selected: " + city);
		Location loc = dBAdapter.getLocation(city);
		Method method = getMethod();
		int asrMadhab = Integer.parseInt(prefs.getString(Keys.ASR_MADHAB_KEY,
				DefaultValues.ASR_MADHAB));
			method.setMathhab(asrMadhab == 0 ? Mathhab.SHAAFI : Mathhab.HANAFI);
		return getCalculator(loc, method);
	}

	private static Method getMethod() {
		String s = SalaatFirstApplication.prefs.getString(
				Keys.ORGANIZATION_KEY, DefaultValues.ORGANIZATION);
		int i = Integer.parseInt(s);
		switch (i) {
		case 1:
			return Method.MOROCCO;
		case 2:
			return Method.KARACHI_HANAF;
		case 3:
			return Method.NORTH_AMERICA;
		case 4:
			return Method.MUSLIM_LEAGUE;
		case 5:
			return Method.EGYPT_SURVEY;
		case 6:
			return Method.UMM_ALQURRA;
		default:
			return Method.MOROCCO;
		}
	}

	/**
	 * Return Prayer Times in specified {@link Location} and date, it uses the
	 * current {@link Mathhab} specifid in the parameters
	 * 
	 * @param loc
	 *            Location used to calculate prayer times, the timeZone and
	 *            gmtDiff will be pulled from the settings.
	 * @param cal
	 *            Date to use
	 * @return {@link DayPrayers}
	 */

	public static DayPrayers getPrayerTimes(final Location loc,
			final GregorianCalendar cal) {
		Method method = getMethod();
		int asrMadhab = Integer.parseInt(prefs.getString(Keys.ASR_MADHAB_KEY,
				DefaultValues.ASR_MADHAB));
			method.setMathhab(asrMadhab == 0 ? Mathhab.SHAAFI : Mathhab.HANAFI);
		return getPrayerTimesUsingMethod(loc, cal, method);
	}

	/**
	 * Return Prayer Times in specified Location, Date and Method
	 * 
	 * @param loc
	 *            Location used to calculate prayer times, the timeZone and
	 *            gmtDiff will be pulled from the settings.
	 * @param cal
	 *            Date to use
	 * @param method
	 *            Method to use
	 * @return {@link DayPrayers}
	 */
	public static DayPrayers getPrayerTimesUsingMethod(final Location loc,
			final GregorianCalendar cal, final Method method) {
		JIslamic calculator = getCalculator(loc, method);
		DayPrayers prayers = calculator.getPrayerTimes(cal);
		return prayers;
	}
}
