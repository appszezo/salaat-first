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

package org.hicham.salaat.settings;

public class Keys {

	public static final String ORGANIZATION_KEY = "organization";
	public static final String ASR_MADHAB_KEY = "asr_madhab";
	public static final String COUNTRY_KEY = "country";
	public static final String CITY_KEY = "city";
	public static final String SHOW_ADHAN_KEY = "show_adhan";
	public static final String SHOW_NOTIFICATION_KEY = "show_notification";
	public static final String DELAY_BEFORE_SILENT_KEY = "time_before_silent_";
	public static final String SILENT_MODE_DURATION_KEY = "time_after_silent_";
	public static final String ACTIVATING_SILENT_KEY = "activating_silent";
	public static final String LANGUAGE_KEY = "language";
	public static final String TIME_ZONE_KEY = "time_zone";
	public static final String STARTING_SERVICE_ON_BOOT_COMPLETE_KEY = "starting_service_on_boot_complete";
	public static final String ADHAN_SOUND_KEY = "adhan_sound";
	public static final String ADHAN_SOUND_URI_KEY = "adhan_sound_uri";
	public static final String TIME_OFFSET_KEY = "time_offset__";
	public static final String VOLUME_KEY = "volume";
	public static final String PRAYER_INTENT_KEY = "prayer";
	public final static String GPS_FOR_COMPASS_KEY = "gps_key";
	public static final String NOTIFICATION_TIME_KEY = "notification_key";
	public static final String VIBRATE_MODE_KEY = "vibration_mode_";
	public static final String USE_AUTOMATIC_LOCATION_KEY = "use_automatic_location";
	public static final String CITY_NAME_FORMATTED = "city_name_formatted";
	public static final String CANCEL_ALL_ALARMS_KEY = "cancel_all_application_alarms";
	public static final String NOTIFICATION_TONE_KEY = "notification_tone";
	public static final String GLOBAL_ACTIVATING_SILENT_KEY = "global_activating_silent";
	public static final String VIBRATING_DURING_SWITCHING_KEY = "vibrating_during_silent_switching";
	public static final String USE_DST_MODE_KEY = "use_dst_mode";
	public static final String HIJRI_CALENDAR_OFFSET_KEY = "hijri_calendar_offset";
	public static final String USE_CUSTOM_SETTINGS_FOR_JUMUA_KEY = "use_custom_settings_for_jumua";
	public static final String ADKAR_SABAH_NOTIFICATION_KEY = "show_adkar_sabah";
	public static final String ADKAR_SABAH_TIME_KEY = "adkar_sabah_time";
	public static final String ADKAR_NOTIFICATION_TONE_KEY = "adkar_notification_tone";
	public static final String LOCATION_REFRESH_MODE_KEY = "location_refresh_mode";
	public static final String LOCATION_REFRESH_FREQUENCY_KEY = "location_refresh_frequency";
	public static final String ADKAR_ALMASSAE_NOTIFICATION_KEY = "show_adkar_almassae";
	public static final String ADKAR_ALMASSAE_TIME_KEY = "adkar_almassae_time";
	public static final String ADKAR_ANNAWM_NOTIFICATION_KEY = "show_adkar_annawm";
	public static final String ADKAR_ANNAWM_TIME_KEY = "adkar_annawm_time";


	
	public class DefaultValues {
		public static final String LANGUAGE="en";
		public static final String ORGANIZATION="1";
		public static final String ASR_MADHAB="0";
		public static final String COUNTRY="Morocco";
		public static final String CITY="Rabat et Salé";
		public static final boolean SHOW_ADHAN=true;
		public static final boolean SHOW_NOTIFICATION=true;
		public static final int DELAY_BEFORE_SILENT=5;
		public static final int SILENT_MODE_DURATION=25;
		public static final boolean ACTIVATING_SILENT=true;
		public static final String TIME_ZONE="0";
		public static final boolean STARTING_SERVICE_ON_BOOT_COMPLETE=true;
		public static final String ADHAN_SOUND="makkah";
		public static final int TIME_OFFSET=0;
		public static final int VOLUME=15;
		public static final boolean GPS_FOR_COMPASS=false;
		public static final int NOTIFICATION_TIME=5;
		public static final boolean VIBRATE_MODE=false;
		public static final boolean USE_AUTOMATIC_LOCATION=false;
		public static final boolean CANCEL_ALL_ALARMS=false;
		public static final String NOTIFICATION_TONE="content://settings/system/notification_sound";
		public static final boolean GLOBAL_ACTIVATING_SILENT=false;
		public static final boolean VIBRATING_DURING_SWITCHING=false;
		public static final boolean USE_DST_MODE=false;
		public static final int HIJRI_CALENDAR_OFFSET = 0;
		public static final boolean USE_CUSTOM_SETTINGS_FOR_JUMUA = true;
		public static final int DELAY_BEFORE_SILENT_JUMUA = 15;
		public static final int SILENT_MODE_DURATION_JUMUA = 50;
		public static final boolean ADKAR_NOTIFICATIONS = true;
		public static final int ADKAR_SABAH_TIME = 30;
		public static final String LOCATION_REFRESH_MODE = "manual";
		public static final int ADKAR_ALMASSAE_TIME = 15;
		public static final long ADKAR_ANNAWM_TIME = 23*3600*1000;
		public static final String LOCATION_REFRESH_FREQUENCY = "30";
	}
}