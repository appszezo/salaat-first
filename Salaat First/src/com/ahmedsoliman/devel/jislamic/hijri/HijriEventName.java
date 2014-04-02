package com.ahmedsoliman.devel.jislamic.hijri;

/*
 * {
 d   m     Event String
 {  1,  1, "Islamic New Year"},
 { 15,  1, "Battle of Qadisiah (14 A.H)"},
 { 10,  1, "Aashura"},
 { 10,  2, "Start of Omar ibn Abd Al-Aziz Khilafah (99 A.H)"},
 {  4,  3, "Start of Islamic calander by Omar Ibn Al-Khattab (16 A.H)"},
 { 12,  3, "Birth of the Prophet (PBUH)"},
 { 20,  3, "Liberation of Bait AL-Maqdis by Omar Ibn Al-Khattab (15 A.H)"},
 { 25,  4, "Battle of Hitteen (583 A.H)"},
 {  5,  5, "Battle of Muatah (8 A.H)"},
 { 27,  7, "Salahuddin liberates Bait Al-Maqdis from crusaders"},
 { 27,  7, "Al-Israa wa Al-Miaaraj"},
 {  1,  9, "First day of month-long Fasting"},
 { 17,  9, "Battle of Badr (2 A.H)"},
 { 21,  9, "Liberation of Makkah (8 A.H)"},
 { 21,  9, "Quran Revealed - day #1"},
 { 22,  9, "Quran Revealed - day #2"},
 { 23,  9, "Quran Revealed - day #3"},
 { 24,  9, "Quran Revealed - day #4"},
 { 25,  9, "Quran Revealed - day #5"},
 { 26,  9, "Quran Revealed - day #6"},
 { 27,  9, "Quran Revealed - day #7"},
 { 28,  9, "Quran Revealed - day #8"},
 { 29,  9, "Quran Revealed - day #9"},
 {  1, 10, "Eid Al-Fitr"},
 {  6, 10, "Battle of Uhud (3 A.H)"},
 { 10, 10, "Battle of Hunian (8 A.H)"},
 {  8, 12, "Hajj to Makkah - day #1"},
 {  9, 12, "Hajj to Makkah - day #2"},
 {  9, 12, "Day of Arafah"},
 { 10, 12, "Hajj to Makkah - day #3"},
 { 10, 12, "Eid Al-Adhaa - day #1"},
 { 11, 12, "Eid Al-Adhaa - day #2"},
 { 12, 12, "Eid Al-Adhaa - day #3"}
 */
public enum HijriEventName {
	ISLAMIC_NEW_YEAR, QADISIAH, AASHURA, OMAR_IBN_ABDEL_AZIZ_KHILAFA, START_OF_ISLAMIC_CALENDAR, BIRTH_OF_MOHAMMED_PBUH, LIBERATION_OF_BAIT_ALMAQDES, BATTLE_OF_HITTEEN, BATTLE_OF_MUATTAH, SALAHUDDIN_LIBERATE_BAIT_ALMAQDES, ALISRAA_WA_ALMIRAAJ, FIRST_DAY_OF_RAMADAN, BATTLE_OF_BADR, LIBERATION_OF_MAKKAH, FIRST_DAY_OF_EID_ELFETR, BATTLE_OF_UHUD, BATTLE_OF_HUNAIN, FIRST_DAY_OF_HAJJ_TO_MAKKAH_DAY1, DAY_OF_ARAFAH, FIRST_DAY_OF_EID_ALADHA;

	public String getDescription() {
		switch (this) {
		case ISLAMIC_NEW_YEAR:
			return "Islamic New Year";
		case QADISIAH:
			return "Battle of Qadisiah (14 A.H)";
		case AASHURA:
			return "Aashura";
		case OMAR_IBN_ABDEL_AZIZ_KHILAFA:
			return "Start of Omar ibn Abd Al-Aziz Khilafah (99 A.H)";
		case START_OF_ISLAMIC_CALENDAR:
			return "Start of Islamic calander by Omar Ibn Al-Khattab (16 A.H)";
		case BIRTH_OF_MOHAMMED_PBUH:
			return "Birth of the Prophet (PBUH)";
		case LIBERATION_OF_BAIT_ALMAQDES:
			return "Liberation of Bait AL-Maqdis by Omar Ibn Al-Khattab (15 A.H)";
		case BATTLE_OF_HITTEEN:
			return "Battle of Hitteen (583 A.H)";
		case BATTLE_OF_MUATTAH:
			return "Battle of Muatah (8 A.H)";
		case SALAHUDDIN_LIBERATE_BAIT_ALMAQDES:
			return "Salahuddin liberates Bait Al-Maqdis from crusaders";
		case ALISRAA_WA_ALMIRAAJ:
			return "Al-Israa wa Al-Miaaraj";
		case FIRST_DAY_OF_RAMADAN:
			return "First day of Ramadan (month-long Fasting)";
		case BATTLE_OF_BADR:
			return "Battle of Badr (2 A.H)";
		case LIBERATION_OF_MAKKAH:
			return "Liberation of Makkah (8 A.H)";
		case FIRST_DAY_OF_EID_ELFETR:
			return "Eid Al-Fitr";
		case BATTLE_OF_UHUD:
			return "Battle of Uhud (3 A.H)";
		case BATTLE_OF_HUNAIN:
			return "Battle of Hunian (8 A.H)";
		case FIRST_DAY_OF_HAJJ_TO_MAKKAH_DAY1:
			return "First Day Of Hajj to Makkah (Bait ALLAH Al-Haraam)";
		case DAY_OF_ARAFAH:
			return "Day of Arafah";
		case FIRST_DAY_OF_EID_ALADHA:
			return "Eid Al-Adha";
		default:
			return "Unknown";
		}
	}
}