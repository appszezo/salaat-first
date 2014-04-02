package com.ahmedsoliman.devel.jislamic;

import java.util.Calendar;

/**
 * This class encapsulates prayer times.
 */
public class Prayer {

	private int hour; /* prayer time hour */

	private int minute; /* prayer time minute */

	private int second; /* prayer time second */

	private boolean extreme; /*  */

	public Prayer() {

	}

	public Prayer(int hour, int minute, int second, boolean extreme) {
		super();
		this.hour = hour;
		this.minute = minute;
		this.second = second;
		this.extreme = extreme;
	}

	/**
	 * copy constructor
	 * 
	 * @return a copy of the current instance
	 */
	public Prayer copy() {
		return new Prayer(hour, minute, second, extreme);
	}

	public int getHour() {
		return hour;
	}

	public int getMinute() {
		return minute;
	}

	/**
	 * 
	 * @return prayer time in seconds
	 */
	public long getPrayerTime() {
		return hour * 3600 + minute * 60;
	}

	/**
	 * 
	 * @return prayer time
	 */
	public Calendar getPrayerTimeAsCalendar() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		cal.getTime();// force calculation of the Calendar object
		return cal;
	}

	public int getSecond() {
		return second;
	}

	/**
	 * @return Extreme calculation switch. The 'getPrayerTimes' function sets
	 *         this switch to true to indicate that this particular prayer time
	 *         has been calculated through extreme latitude methods and NOT by
	 *         conventional means of calculation.
	 */
	public boolean isExtreme() {
		return extreme;
	}

	public void setExtreme(boolean extreme) {
		this.extreme = extreme;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	@Override
	public String toString() {
		return String.format("%02d:%02d", hour, minute);
	}
}
