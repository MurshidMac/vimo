package nodomain.freeyourgadget.gadgetbridge.devices.miband;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nodomain.freeyourgadget.gadgetbridge.util.Prefs;

public final class MiBandConst {
    private static final Logger LOG = LoggerFactory.getLogger(MiBandConst.class);

    public static final String PREF_USER_ALIAS = "mi_user_alias";
    public static final String PREF_MIBAND_WEARSIDE = "mi_wearside";
    public static final String PREF_MIBAND_ADDRESS = "development_miaddr";  // FIXME: should be prefixed mi_
    public static final String PREF_MIBAND_ALARMS = "mi_alarms";
    public static final String PREF_MIBAND_FITNESS_GOAL = "mi_fitness_goal";
    public static final String PREF_MIBAND_DONT_ACK_TRANSFER = "mi_dont_ack_transfer";
    public static final String PREF_MIBAND_RESERVE_ALARM_FOR_CALENDAR = "mi_reserve_alarm_calendar";
    public static final String PREF_MIBAND_USE_HR_FOR_SLEEP_DETECTION = "mi_hr_sleep_detection";
	public static final String PREF_MIBAND_DEVICE_TIME_OFFSET_HOURS = "mi_device_time_offset_hours";
	public static final String PREF_MI2_DATEFORMAT = "mi2_dateformat";
	public static final String PREF_MI2_ACTIVATE_DISPLAY_ON_LIFT = "mi2_activate_display_on_lift_wrist";
    public static final String PREF_MIBAND_SETUP_BT_PAIRING = "mi_setup_bt_pairing";


    public static final String ORIGIN_INCOMING_CALL = "incoming_call";
    public static final String MI_GENERAL_NAME_PREFIX = "MI";
    public static final String MI_BAND2_NAME = "MI Band 2";
    public static final String MI_1 = "1";
    public static final String MI_1A = "1A";
    public static final String MI_1S = "1S";
    public static final String MI_AMAZFIT = "Amazfit";
    public static final String MI_PRO = "2";

    public static int getNotificationPrefIntValue(String pref, String origin, Prefs prefs, int defaultValue) {
        String key = getNotificationPrefKey(pref, origin);
        return prefs.getInt(key, defaultValue);
    }

    public static String getNotificationPrefStringValue(String pref, String origin, Prefs prefs, String defaultValue) {
        String key = getNotificationPrefKey(pref, origin);
        return prefs.getString(key, defaultValue);
    }

    public static String getNotificationPrefKey(String pref, String origin) {
        return pref + '_' + origin;
    }

    public static final String VIBRATION_PROFILE = "mi_vibration_profile";
    public static final String VIBRATION_COUNT = "mi_vibration_count";
    public static final String VIBRATION_DURATION = "mi_vibration_duration";
    public static final String VIBRATION_PAUSE = "mi_vibration_pause";
    public static final String FLASH_COUNT = "mi_flash_count";
    public static final String FLASH_DURATION = "mi_flash_duration";
    public static final String FLASH_PAUSE = "mi_flash_pause";
    public static final String FLASH_COLOUR = "mi_flash_colour";
    public static final String FLASH_ORIGINAL_COLOUR = "mi_flash_original_colour";

    public static final String DEFAULT_VALUE_VIBRATION_PROFILE = "short";
    public static final int DEFAULT_VALUE_VIBRATION_COUNT = 3;
    public static final int DEFAULT_VALUE_VIBRATION_DURATION = 500; // ms
    public static final int DEFAULT_VALUE_VIBRATION_PAUSE = 500; // ms
    public static final int DEFAULT_VALUE_FLASH_COUNT = 10; // ms
    public static final int DEFAULT_VALUE_FLASH_DURATION = 500; // ms
    public static final int DEFAULT_VALUE_FLASH_PAUSE = 500; // ms
    public static final int DEFAULT_VALUE_FLASH_COLOUR = 1; // TODO: colour!
    public static final int DEFAULT_VALUE_FLASH_ORIGINAL_COLOUR = 1; // TODO: colour!
}
