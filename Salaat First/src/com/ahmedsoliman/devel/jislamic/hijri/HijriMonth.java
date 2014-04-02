package com.ahmedsoliman.devel.jislamic.hijri;

public enum HijriMonth {

	MUHARRAM(1), SAFAR(2), RABI_AL_AWAL(3), RABI_AL_TANI(4), JUMADA_AL_AWAL(5), JUMADA_AL_THANI(
			6), RAJAB(7), SHABAAN(8), RAMADAN(9), SHAWWAL(10), DHU_AL_QIDAH(11), DHU_AL_HIJJA(
			12);
	public static HijriMonth fromNumber(int num) {
		switch (num) {
		case 1:
			return HijriMonth.MUHARRAM;
		case 2:
			return HijriMonth.SAFAR;
		case 3:
			return HijriMonth.RABI_AL_AWAL;
		case 4:
			return HijriMonth.RABI_AL_TANI;
		case 5:
			return HijriMonth.JUMADA_AL_AWAL;
		case 6:
			return HijriMonth.JUMADA_AL_THANI;
		case 7:
			return HijriMonth.RAJAB;
		case 8:
			return HijriMonth.SHABAAN;
		case 9:
			return HijriMonth.RAMADAN;
		case 10:
			return HijriMonth.SHAWWAL;
		case 11:
			return HijriMonth.DHU_AL_QIDAH;
		case 12:
			return HijriMonth.DHU_AL_HIJJA;
		}
		return null;
	}

	private int num;

	HijriMonth(int num) {
		this.num = num;
	}

	public int getMonthAsNumber() {
		return num;
	}

	@Override
	public String toString() {
		switch (this) {
		case MUHARRAM:
			return "Muharram";
		case SAFAR:
			return "Safar";
		case RABI_AL_AWAL:
			return "Rabi' al-awwal";
		case RABI_AL_TANI:
			return "Rabi' al-thani";
		case JUMADA_AL_AWAL:
			return "Jumada al-awwal";
		case JUMADA_AL_THANI:
			return "Jumada al-thani";
		case RAJAB:
			return "Rajab";
		case SHABAAN:
			return "Sha'aban";
		case RAMADAN:
			return "Ramadan";
		case SHAWWAL:
			return "Shawwal";
		case DHU_AL_QIDAH:
			return "Dhu al-Qi'dah";
		case DHU_AL_HIJJA:
			return "Dhu al-Hijjah";
		}
		return null;
	}
}
