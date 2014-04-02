package com.ahmedsoliman.devel.jislamic.hijri;

public enum HijriDay {

	AHAD(1), ETHNAIN(2), THULATHA(3), ARBAA(4), KHAMIS(5), JUMAA(6), SABT(7);
	public static HijriDay fromNumber(int num) {
		switch (num) {
		case 1:
			return HijriDay.AHAD;
		case 2:
			return HijriDay.ETHNAIN;
		case 3:
			return HijriDay.THULATHA;
		case 4:
			return HijriDay.ARBAA;
		case 5:
			return HijriDay.KHAMIS;
		case 6:
			return HijriDay.JUMAA;
		case 7:
			return HijriDay.SABT;
		}
		return null;
	}

	private int num;

	HijriDay(int num) {
		this.num = num;
	}

	public int getDayAsNumber() {
		return num;
	}

	@Override
	public String toString() {
		switch (this) {
		case AHAD:
			return "Ahad";
		case ETHNAIN:
			return "Ethnain";
		case THULATHA:
			return "Thulatha";
		case ARBAA:
			return "Arbaa";
		case KHAMIS:
			return "Khamis";
		case JUMAA:
			return "Jumaa";
		case SABT:
			return "Sabt";
		}
		return null;
	}

}
