package com.ahmedsoliman.devel.jislamic;

import java.util.Calendar;
import java.util.Iterator;

/**
 * Encapsulates the list of prayers time and shuruq time
 */
public class DayPrayers {
	public static final int FAJR = 0;
	public static final int CHOROUK = 1;
	public static final int DHUHR = 2;
	public static final int ASR = 3;
	public static final int MAGHRIB = 4;
	public static final int ICHAA = 5;
	public static final int JUMUA=6;

	private Prayer[] prayers = new Prayer[6];

	public DayPrayers() {
		for (int i = 0; i < 6; i++) {
			prayers[i] = new Prayer();
		}
	}

	/**
	 * Assr time
	 * 
	 * @return assr prayer time
	 */
	public Prayer assr() {
		return prayers[3];
	}

	/**
	 * Thuhr time
	 * 
	 * @return thuhr prayer time
	 */
	public Prayer duhr() {
		return prayers[2];
	}

	/**
	 * Fajr prayer time
	 * 
	 * @return fajr prayer time
	 */
	public Prayer fajr() {
		return prayers[0];
	}

	private double getAsrTime() {
		return assr().getHour() * 3600 + assr().getMinute() * 60;
	}

	private double getDhuhrTime() {
		return duhr().getHour() * 3600 + duhr().getMinute() * 60;
	}

	private double getFajrTime() {
		return fajr().getHour() * 3600 + fajr().getMinute() * 60;
	}

	private double getIshaaTime() {
		return ishaa().getHour() * 3600 + ishaa().getMinute() * 60;
	}

	private double getMaghribTime() {
		return maghrib().getHour() * 3600 + maghrib().getMinute() * 60;
	}

	public int getNextPrayer() {
		Calendar cal = Calendar.getInstance();
		double now = cal.get(Calendar.HOUR_OF_DAY) * 3600
				+ cal.get(Calendar.MINUTE) * 60 + cal.get(Calendar.SECOND);
		if (getFajrTime() > now || getIshaaTime() < now) {
			return FAJR;
		} else if (getDhuhrTime() > now)
			return DHUHR;
		else if (getAsrTime() > now)
			return ASR;
		else if (getMaghribTime() > now)
			return MAGHRIB;
		else
			return ICHAA;

	}

	/**
	 * Get prayer list as an array
	 * 
	 * @return an array containing the list of prayer times including the
	 *         shuruq. The size of the array is 6.
	 */
	public Prayer[] getPrayers() {
		return prayers;
	}

	/**
	 * Ishaa time
	 * 
	 * @return ishaa time
	 */
	public Prayer ishaa() {
		return prayers[5];
	}

	/**
	 * Creates an iterator on the prayers
	 * 
	 * @return an iterator over the prayers
	 * @see #getPrayers()
	 */
	public Iterator<Prayer> iterator() {
		return new Iterator<Prayer>() {
			private int i = 0;

			public boolean hasNext() {
				if (i < 6)
					return true;
				return false;
			}

			public Prayer next() {
				return prayers[i++];
			}

			public void remove() {
				if (i > 0) {
					i--;
				}
			}

		};
	}

	/**
	 * Maghrib time
	 * 
	 * @return maghrib time
	 */
	public Prayer maghrib() {
		return prayers[4];
	}

	/**
	 * set all prayer calculation to extreme
	 * 
	 * @param extreme
	 *            extreme boolean value
	 */
	void setAllExtreme(boolean extreme) {
		for (int i = 0; i < 6; i++) {
			prayers[i].setExtreme(extreme);
		}
	}

	/**
	 * Shuruq time
	 * 
	 * @return shuruq time
	 */
	public Prayer shuruq() {
		return prayers[1];
	}

	/**
	 * convert prayer times to a string.
	 * 
	 * @return prayer times as a string. It contains 6 lines
	 */
	@Override
	public String toString() {
		String ret = "";
		for (int i = 0; i < 6; i++) {
			ret += prayers[i].toString() + "\n";
		}
		return ret;
	}
}
