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

package org.hicham.salaat.calculating;


import static java.lang.Math.PI;
import static java.lang.Math.acos;
import static java.lang.Math.asin;
import static java.lang.Math.atan;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.floor;
import static java.lang.Math.round;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.SimpleTimeZone;

/**
 * Old calculator
 * @author hicham_boushaba
 *
 */
class PrayerTimesCalculator_ {

	public static enum Organization {
		MOROCCO(109, 107), // - Moroccon zenith for fajr and icha is 19°/17°
		// وزارة الأوقاف والشؤون الإسلامية بالمغرب
		UIS(108, 108), // - University of Islamic Science, Karachi 18º/18º
		// جامعة العلوم الإسلامية بكراتشى .
		ISNA(105, 105), // - Islamic Society of North America 15º/15º
		// الاتحاد الإسلامي بأمريكا الشمالية .
		WIL(108, 107), // - World Islamic League 18º/17º
		// رابطة العالم الإسلامي .
		EGOS(109.5, 107.5), // - Egyptian General Organization of surveying
							// 19.5º /17.5º
		// الهيئة العامة المصرية للمساحة .
		UQ(109, 90); // - Umm al-Qura kalender 19º for fajr, and maghrib+90min
						private double zenithDawn, zenithDusk;

		// for icha (+120min in ramadan)
		// جامعة أم القرى .
		Organization(double zenithDawn, double zenithDusk) {
			this.zenithDawn = zenithDawn;
			this.zenithDusk = zenithDusk;
		}

		public double getZenithDawn() {
			return zenithDawn;
		}

		public double getZenithDusk() {
			return zenithDusk;
		}
	}
	public static final int SHAFII = 0;

	public static final int HANAFI = 1;
	public static final int FAJR = 0;
	public static final int CHOROUK = 1;
	public static final int DHUHR = 2;
	public static final int ASR = 3;
	public static final int MAGHRIB = 4;

	public static final int ICHAA = 5;
	/*
	 * utilities methodes
	 */
	/**
	 * range reduce angle to 0..359
	 * 
	 * @param a
	 *            the angle
	 * @return the angle in the range 0..359
	 */
	static private double fixangle(double a) {

		a = a - (360 * (floor(a / 360.0)));

		a = a < 0 ? (a + 360) : a;

		return a;
	}
	/**
	 * range reduce hours to 0..23
	 * 
	 * @param a
	 *            the hour
	 * @return the hour in the range 0..23
	 */
	static private double fixhour(double a) {
		a = a - 24.0 * floor(a / 24.0);
		a = a < 0 ? (a + 24) : a;
		return a;
	}
	private double mLongitude = -6.8500000000000005;
	private double mLatitude = 34.016666666666666;
	private int mAsrMadhab;
	private double mDhuhr;
								private Organization mOrganization;
	private double mTimeZone;

	/*
	 * private double alfa, // Apparent Right Ascension of the Sun in Degree L0,
	 * // sun's mean Longitude ــــــــ خط طزل الشمس الوسطي المصحح للزيغان
	 * بالدرجات L_m, // moon's mean Longitude epsilon, // Obliquity Of
	 * Ecliptique in dergree teta, // ecliptique longitude in degree lambda, //
	 * Apparant longitude of the sun Ma_s, // Mean Anomaly in degree of the sun
	 * Ma_m, // Moon mean anomaly i_m, // Phase angle of moon D_m, // Mean
	 * ellogation of the moon Ect, // Eccentricity of Earth's orbit c, // Suns's
	 * equation of center v, // Suns True Anomaly omega, // Longitude of the
	 * ascending node of the moon's mean orbit on the ecliptique DeltaPhi, //
	 * Nutation in longitude
	 * 
	 * EqT, // Eqaution Of Time expressed in minutes of time
	 */

	private double mElev = 0; // level from sea, using this default value in

	// this version
	private double offsets[] = { 0, 0, 0, 0, 0, 0 };

	// ---astronomic fields
	private double Rv_s, // Sun's radius vector= distance from Earth to the Sun
							// given in astronomical units
			sunSemiD, // Sun semidiameter
			decl; // Sun' Declination

	public PrayerTimesCalculator_(double longitude, double latitude,
			Organization org, double timeZone, int asrMadhab, double level,
			double[] offsets) {
		mLongitude = longitude;
		mLatitude = latitude;
		mOrganization = org;
		mTimeZone = timeZone;
		mAsrMadhab = asrMadhab;
		mElev = level;
		this.offsets = offsets;
	}

	private double acot(double x) {
		if (x == 0)
			return PI / 2;
		return x > 0 ? atan(1 / x) : PI + atan(1 / x);
	}

	/**
	 * converting the hour to a string having the forme: HH:mm
	 * 
	 * @param __time
	 * @return
	 */
	public String floatToStr(double __time) {
		Calendar _Time = Calendar.getInstance();
		DateFormat formatter = new SimpleDateFormat("HH:mm");
		SimpleTimeZone timeZone = new SimpleTimeZone(0, "");
		formatter.setTimeZone(timeZone);
		_Time.setTimeInMillis(round(__time * 60 * 60 * 1000));
		return formatter.format(_Time.getTime()).toString();
	}

	/**
	 * return maghrib time call performCalculs before
	 * 
	 * @return maghrib time
	 */
	public double getAsrTime() {
		double a = asin(sin(mLatitude * PI / 180) * sin(decl * PI / 180)
				+ cos(mLatitude * PI / 180) * cos(decl * PI / 180))
				* 180 / PI;
		double Zenith_AfterNoon;
		if (mAsrMadhab == SHAFII)
			Zenith_AfterNoon = 90 - acot(1 + (1 / tan(a * PI / 180))) * 180
					/ PI; // ــــــــ :الفقه شافعي
		else
			Zenith_AfterNoon = 90 - acot(2 + (1 / tan(a * PI / 180))) * 180
					/ PI; // ــــــــ :الفقه حنفي
		return mDhuhr + hourAngle(Zenith_AfterNoon) / 15 + offsets[ASR];
	}

	/**
	 * return chorouk time call performCalculs before
	 * 
	 * @return chorouk time
	 */
	public double getChoroukTime() {
		int T = 25; // temperature in °C
		int P = 1010; // presion in millibar
		double h; // geometric altitude of the center of the sun
		h = -sunSemiD;
		double R; // refraction atmospherique
		R = 1 / tan((h + 7.31 / (h + 4.4)) * PI / 180);
		R = R - 0.06 * sin((14.7 * R + 13) * PI / 180);
		R = R * P / 1010 * 283.0 / (273 + T); // correction of temperature and
												// pression
		R = R / 60; // translate from minutes to degree
		double zenithSunRise = 90 + sunSemiD + R - 8.794 / (3600 * Rv_s)
				+ 0.035333 * sqrt(mElev);
		return mDhuhr - hourAngle(zenithSunRise) / 15;
	}

	/**
	 * return the dhuhr time call performCalculs before
	 * 
	 * @return dhuhr time
	 */
	public double getDhuhrTime() {
		if (mOrganization == Organization.MOROCCO)
			return mDhuhr + (double) 5 / 60 + offsets[DHUHR]; // adds 5 minutes
		return mDhuhr + offsets[DHUHR];
	}

	/**
	 * return fajr time call performCalculs before
	 * 
	 * @return fajr time
	 */
	public double getFajrTime() {
		double fajr = mDhuhr - hourAngle(mOrganization.getZenithDawn()) / 15
				+ offsets[FAJR];
		return fajr;
	}

	/**
	 * return ichaa time call performCalculs before
	 * 
	 * @return ichaa time
	 */
	public double getIchaaTime() {
		if (mOrganization == Organization.UQ)
			return getMaghribTime() + (double) 90 / 60 + offsets[ICHAA];
		else {
			double ichaa = mDhuhr + hourAngle(mOrganization.getZenithDusk())
					/ 15 + offsets[ICHAA];
			return ichaa;
		}
	}

	/**
	 * return maghrib time call performCalculs before
	 * 
	 * @return maghrib time
	 */
	public double getMaghribTime() {
		int T = 25; // temperature in °C
		int P = 1010; // presion in millibar
		double h = -sunSemiD;
		double R = 1 / tan((h + 7.31 / (h + 4.4)) * PI / 180);
		R = R - 0.06 * sin((14.7 * R + 13) * PI / 180);
		R = R * P / 1010 * 283.0 / (273 + T); // correction of temperature and
												// pression
		R = R / 60; // transform from minutes to degree

		double Zenith_SunSet = 90 + sunSemiD + R - 8.794 / (3600 * Rv_s)
				+ 0.035333 * sqrt(mElev);
		// (14.2)
		return mDhuhr + hourAngle(Zenith_SunSet) / 15 + offsets[MAGHRIB];
	}

	/**
	 * call performCalculs before
	 */
	public int getNextPrayer() {
		Calendar cal = Calendar.getInstance();
		double now = cal.get(Calendar.HOUR_OF_DAY)
				+ (double) cal.get(Calendar.MINUTE) / 60;
		if (fixhour(getFajrTime()) > now || fixhour(getIchaaTime()) < now) {
			return FAJR;
		} else if (fixhour(getDhuhrTime()) > now)
			return DHUHR;
		else if (fixhour(getAsrTime()) > now)
			return ASR;
		else if (fixhour(getMaghribTime()) > now)
			return MAGHRIB;
		else
			return ICHAA;
	}

	/**
	 * call performCalculs before
	 * 
	 * @param prayer
	 * @return
	 */
	public double getPrayerTime(int prayer) {
		return fixhour(getPrayerTimesFloat()[prayer]);
	}

	/**
	 * call performCalculs before
	 * 
	 * @param date
	 * @return
	 */
	public double[] getPrayerTimesFloat() {
		double[] times = new double[6];
		times[FAJR] = getFajrTime();
		times[CHOROUK] = getChoroukTime();
		times[DHUHR] = getDhuhrTime();
		times[ASR] = getAsrTime();
		times[MAGHRIB] = getMaghribTime();
		times[ICHAA] = getIchaaTime();
		return times;
	}

	/**
	 * call performCalculs before
	 * 
	 * @param date
	 * @return
	 */
	public String[] getPrayerTimesStr() {
		String[] str = new String[6];
		double[] times = getPrayerTimesFloat();
		for (int i = 0; i < 6; i++) {
			str[i] = floatToStr(times[i]);
		}
		return str;
	}

	private double hourAngle(double Z) {
		double cosH0 = ((cos(Z * PI / 180) - sin(mLatitude * PI / 180)
				* sin(decl * PI / 180)) / (cos(mLatitude * PI / 180) * cos(decl
				* PI / 180)));
		return acos(cosH0) * 180 / PI;
	}

	private double julianDate(int year, int month, int day) {

		if (month <= 2) {
			year -= 1;
			month += 12;
		}
		double A = floor(year / 100.0);

		double B = 2 - A + floor(A / 4.0);

		double JD = floor(365.25 * (year + 4716))
				+ floor(30.6001 * (month + 1)) + day + B - 1524.5;

		return JD;
	}

	public void performCalculs(Calendar cal) {
		double jd = julianDate(cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));

		double T = (jd - 2451545.0) / 365250; // T is the julian millennia of
												// 365250 ephemeris day from the
												// epoch J2000.0
		double T2 = T * T;
		double T3 = T2 * T;
		double T4 = T3 * T;
		double T5 = T4 * T;
		double L0 = (280 + 27.0 / 60 + 59.244 / 3600) + (1296027713.8 / 3600)
				* T // sun's
				// mean
				// Longitude
				// in
				// degree
				// (27.2)
				// (Page16,
				// ch
				// 2)
				+ (109.15 / 3600) * T2 + T3 / 49931 - T4 / 15299 - T5 / 1988000;
		L0 = fixangle(L0);// reducing L0 to less than 360°
		// Mean Anomaly of the sun in degree (45.3) (also chp 21 page 132)
		// Moon's mean Longitude in degree (45.1) (Page 17,Ch 2)
		double L_m = 218.3164591 + 4812678.8134236 * T - 0.13268 * T2 + T3
				/ 538.841 - T4 / 6519.4;
		L_m = fixangle(L_m);
		double Ma_s = 357.5291092 + 359990.502909 * T - 0.01559 * T2 - T3
				/ 24490;
		Ma_s = fixangle(Ma_s);

		// Obliquity Of Ecliptique in dergree (21.2)
		double epsilon = (23 + 26.0 / 60 + 21.448 / 3600) - 468.15 / 3600 * T
				- 0.059 / 3600 * T2 + 0.1813 / 3600 * T3;

		// Eccentricity of Earth's orbit (24.4)
		double Ect = 0.016708617 - 0.00042037 * T - 0.00001236 * T2;

		// Suns's equation of center (chp 24 Page 152)
		double c = (1.914600 - 0.04817 * T - 0.0014 * T2)
				* sin(Ma_s * PI / 180) + (0.019993 - 0.00101 * T)
				* sin(2 * Ma_s * PI / 180) + 0.000290
				* sin(3 * Ma_s * PI / 180);

		// ecliptique longitude in degree (chp 24 Page 152)
		// the sun's true longitude
		double teta = L0 + c;

		// Suns True Anomaly (chp 24 Page 152)
		double v = Ma_s + c;

		// Longitude of the ascending node of the moon's mean orbit on the
		// ecliptique
		// (chp 24 Page 152) and (Chap 21 page 132)
		double omega = 125.04452 - 19341.36261 * T + 0.20708 * T2 + T3 / 450;
		omega = fixangle(omega);

		// Apparant longitude of the sun (chp 24 Page 152)
		double lambda = teta - 0.00569 - 0.00478 * sin(omega * PI / 180);

		// Nutation in longitude (chp 21 Page 132)
		double DeltaPhi = -17.20 / 3600 * sin(omega * PI / 180) - 1.32 / 3600
				* sin(2 * L0 * PI / 180) - 0.23 / 3600
				* sin(2 * L_m * PI / 180) + 0.21 / 3600
				* sin(2 * omega * PI / 180);

		// correction for the Obliquity
		epsilon = epsilon + 0.00256 * cos(omega * PI / 180);

		// Apparent Right Ascension of the Sun in Degree
		// (24.6)
		double alfa = atan2(cos(epsilon * PI / 180) * sin(lambda * PI / 180),
				cos(lambda * PI / 180)) * 180 / PI;
		alfa = fixangle(alfa);

		// Apparent declination of the sun (24.7)
		decl = asin(sin(epsilon * PI / 180) * sin(lambda * PI / 180)) * 180
				/ PI;

		// Sun's radius vector= distance from Earth to the Sun given in
		// astronomical units
		Rv_s = 1.000001018 * (1 - sqrt(Ect) / (1 + Ect * cos(v * PI / 180))); // (24.5)

		// Sun semidiameter
		sunSemiD = (959.63 / 3600) / Rv_s; // equ 16'/60 = 0°.266666

		// Eqaution Of Time in minutes of time
		// (27.1)
		double EqT = L0 - 0.0057183 - alfa + DeltaPhi * cos(epsilon * PI / 180);
		// the factor *4 is to convert the eqt from degree(angle) to
		// minute(time)
		EqT = EqT * 4;
		System.out.println(EqT);
		mDhuhr = 12 + mTimeZone - mLongitude / 15 - EqT / 60;

	}

	/**
	 * @param mAsrMadhab
	 *            the mAsrMadhab to set
	 */
	public void setAsrMadhab(int mAsrMadhab) {
		this.mAsrMadhab = mAsrMadhab;
	}

	/**
	 * @param Elev
	 *            the Elev to set
	 */
	public void setElev(double mElev) {
		this.mElev = mElev;
	}

	/**
	 * @param mLatitude
	 *            the mLatitude to set
	 */
	public void setLatitude(double mLatitude) {
		this.mLatitude = mLatitude;
	}

	/**
	 * @param mLongitude
	 *            the Longitude to set
	 */
	public void setLongitude(double mLongitude) {
		this.mLongitude = mLongitude;
	}

	/**
	 * @param mOrganization
	 *            the Organization to set
	 */
	public void setOrganization(Organization mOrganization) {
		this.mOrganization = mOrganization;
	}

	/**
	 * @param mTimeZone
	 *            the mTimeZone to set
	 */
	public void setTimeZone(double mTimeZone) {
		this.mTimeZone = mTimeZone;
	}
}