package com.ahmedsoliman.devel.jislamic.hijri;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.hicham.salaat.SalaatFirstApplication;
import org.hicham.salaat.settings.Keys;
import org.hicham.salaat.settings.Keys.DefaultValues;

public class HijriCalendar extends Calendar {

	/* Absolute date of start of Islamic calendar (July 19, 622 Gregorian) */
	public final int HIJRI_MONTH = 142930;
	public final int HIJRI_DAY = 142931;
	public final int HIJRI_YEAR = 142939;

	public final int BH = 142940; // Before Hijrah
	public final int AH = 142941; // After Hijrah

	// public final int ERA = 142942;

	public HijriCalendar(final Calendar cal) {
		int offset=SalaatFirstApplication.prefs.getInt(Keys.HIJRI_CALENDAR_OFFSET_KEY, DefaultValues.HIJRI_CALENDAR_OFFSET);
		Calendar calCopy=(Calendar)cal.clone();
		calCopy.add(Calendar.DAY_OF_MONTH, offset);
		convertGregorianToHijri(calCopy);
	}

	public HijriCalendar(HijriDay day, HijriMonth month, int year) {
		set(year, month.getMonthAsNumber(), day.getDayAsNumber());
	}

	public HijriCalendar(int day, int month, int year) {
		set(year, month, day);
	}

	/**
	 * supports DAY_OF_MONTH only
	 */
	@Override
	public void add(int field, int amount) {
		if (field != DAY_OF_MONTH)
			return;
		set(field, internalGet(field) + amount);
	}

	/**
	 * compute the actual fields values. the actual implementation correct the
	 * difference if the value of day is more/less than the actual
	 * maximum/minimum values with a value less than 29. this is enough for the
	 * application implementation
	 */
	@Override
	protected void computeFields() {
		if (internalGet(DAY_OF_MONTH) < getMinimum(DAY_OF_MONTH)) {

			switch (internalGet(MONTH)) {
			case 0:
				set(MONTH, 11);
				set(YEAR, internalGet(YEAR) - 1);
				break;
			default:
				set(MONTH, internalGet(MONTH) - 1);
			}
			int totalDaysOfMonth = hijriDaysOfActualMonth();
			set(DAY_OF_MONTH, totalDaysOfMonth);
		}
		if (internalGet(DAY_OF_MONTH) > getActualMaximum(DAY_OF_MONTH)) {
			int diff = internalGet(DAY_OF_MONTH)
					- getActualMaximum(DAY_OF_MONTH);
			switch (internalGet(MONTH)) {
			case 11:
				set(MONTH, 0);
				set(YEAR, internalGet(YEAR) + 1);
				break;
			default:
				set(MONTH, internalGet(MONTH) + 1);
			}
			set(DAY_OF_MONTH, diff);
		}
	}

	@Override
	protected void computeTime() {
		// TODO Auto-generated method stub

	}

	protected void convertGregorianToHijri(Calendar cal) {
		HijriCalculator.sDate mydate = new HijriCalculator.sDate();
		HijriCalculator.h_date(mydate, cal.get(Calendar.DAY_OF_MONTH),
				cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
		set(DAY_OF_MONTH, mydate.day);
		set(MONTH, mydate.month - 1);
		set(YEAR, mydate.year);
	}

	@Override
	public int getActualMaximum(int field) {
		switch (field) {
		case DAY_OF_MONTH:
			return hijriDaysOfActualMonth();
		default:
			return super.getActualMaximum(field);
		}
	}

	@Override
	public int getGreatestMinimum(int field) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLeastMaximum(int field) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaximum(int field) {
		int ret = 0;
		switch (field) {
		case DAY_OF_MONTH:
			ret = 30;
			break;
		case DAY_OF_WEEK:
			ret = 7;
		case MONTH:
			ret = 11;
			break;
		case YEAR:
			ret = 9999;
			break;
		case ERA:
			ret = AH;
			break;
		}
		return ret;
	}

	@Override
	public int getMinimum(int field) {
		int ret = 0;
		switch (field) {
		case DAY_OF_MONTH:
		case DAY_OF_WEEK:
			ret = 1;
			break;
		case MONTH:
			ret = 0;
			break;
		case YEAR:
			ret = 0;
			break;
		case ERA:
			ret = BH;
		}
		return ret;
	}

	private int hijriDaysOfActualMonth() {
		return HijriCalculator.h_numdays(internalGet(MONTH) + 1,
				internalGet(YEAR));
	}

	@Override
	public void roll(int field, boolean up) {
		// TODO Auto-generated method stub

	}

	public Calendar toGregorianCalendar() {
		HijriCalculator.sDate mydate = new HijriCalculator.sDate();
		HijriCalculator.g_date(mydate, get(DAY_OF_MONTH), get(MONTH) + 1,
				get(YEAR));
		Calendar ret = new GregorianCalendar(mydate.year, mydate.month - 1,
				mydate.day);
		return ret;
	}
}
