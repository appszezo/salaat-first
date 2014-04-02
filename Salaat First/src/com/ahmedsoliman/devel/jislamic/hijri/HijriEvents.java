package com.ahmedsoliman.devel.jislamic.hijri;

import java.util.Calendar;

public class HijriEvents {
	public static class HijriEventClass implements Comparable<HijriCalendar> {
		HijriEventName event;
		boolean extended;
		int day;
		HijriMonth month;

		public HijriEventClass(int day, HijriMonth month,
				HijriEventName evName, boolean extended) {
			this.day = day;
			this.month = month;
			this.event = evName;
			this.extended = extended;

		}

		public HijriEventClass(int day, int month, HijriEventName evName,
				boolean extended) {
			this(day, HijriMonth.fromNumber(month), evName, extended);
		}

		public int compareTo(HijriCalendar o) {
			if (month.getMonthAsNumber() > o.get(Calendar.MONTH)
					|| month.getMonthAsNumber() < o.get(Calendar.MONTH)) {
				return month.getMonthAsNumber() - o.get(Calendar.MONTH);
			} else {
				// months are equal, check days.
				return day - o.get(Calendar.DAY_OF_MONTH);
			}
		}

		@Override
		public boolean equals(Object obj) {
			return false;
		};

		public String getDescription() {
			return event.getDescription();
		}
	}

	public static HijriEventClass[] events = {
			new HijriEventClass(1, 1, HijriEventName.ISLAMIC_NEW_YEAR, false),
			new HijriEventClass(15, 1, HijriEventName.QADISIAH, true),
			new HijriEventClass(10, 1, HijriEventName.AASHURA, false),
			new HijriEventClass(10, 2,
					HijriEventName.OMAR_IBN_ABDEL_AZIZ_KHILAFA, true),
			new HijriEventClass(4, 3, HijriEventName.START_OF_ISLAMIC_CALENDAR,
					true),
			new HijriEventClass(12, 3, HijriEventName.BIRTH_OF_MOHAMMED_PBUH,
					false),
			new HijriEventClass(20, 3,
					HijriEventName.LIBERATION_OF_BAIT_ALMAQDES, true),
			new HijriEventClass(25, 4, HijriEventName.BATTLE_OF_HITTEEN, true),
			new HijriEventClass(5, 5, HijriEventName.BATTLE_OF_MUATTAH, true),
			new HijriEventClass(27, 7, HijriEventName.ALISRAA_WA_ALMIRAAJ,
					false),
			new HijriEventClass(27, 7,
					HijriEventName.SALAHUDDIN_LIBERATE_BAIT_ALMAQDES, true),
			new HijriEventClass(1, 9, HijriEventName.FIRST_DAY_OF_RAMADAN,
					false),
			new HijriEventClass(17, 9, HijriEventName.BATTLE_OF_BADR, true),
			new HijriEventClass(21, 9, HijriEventName.LIBERATION_OF_MAKKAH,
					true),
			new HijriEventClass(1, 10, HijriEventName.FIRST_DAY_OF_EID_ELFETR,
					false),
			new HijriEventClass(6, 10, HijriEventName.BATTLE_OF_UHUD, true),
			new HijriEventClass(10, 10, HijriEventName.BATTLE_OF_HUNAIN, true),
			new HijriEventClass(8, 12,
					HijriEventName.FIRST_DAY_OF_HAJJ_TO_MAKKAH_DAY1, false),
			new HijriEventClass(9, 12, HijriEventName.DAY_OF_ARAFAH, false),
			new HijriEventClass(10, 12, HijriEventName.FIRST_DAY_OF_EID_ALADHA,
					false) };

	public static HijriEventClass getEventDate(HijriEventName event) {
		for (HijriEventClass ev : events) {
			if (ev.event == event)
				return ev;
		}
		return null;
	}

	public static HijriEventClass[] getEvents() {
		return events;
	}
}
