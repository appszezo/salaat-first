package com.ahmedsoliman.devel.jislamic.astro;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class SimpleDate {
	int day;

	int month;

	int year;

	/**
	 * TODO
	 * 
	 * @param gCalendar
	 */
	public SimpleDate(GregorianCalendar gCalendar) {
		this.day = gCalendar.get(Calendar.DATE);
		this.month = gCalendar.get(Calendar.MONTH) + 1;
		this.year = gCalendar.get(Calendar.YEAR);
	}

	/**
	 * TODO
	 * 
	 * @param day
	 * @param month
	 * @param year
	 */
	public SimpleDate(int day, int month, int year) {
		this.day = day;
		this.month = month;
		this.year = year;
	}

	public SimpleDate copy() {
		return new SimpleDate(day, month, year);
	}

	public int getDay() {
		return day;
	}

	public int getMonth() {
		return month;
	}

	public int getYear() {
		return year;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public void setYear(int year) {
		this.year = year;
	}

}
