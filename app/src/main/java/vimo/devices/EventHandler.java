package vimo.devices;

import android.net.Uri;

import java.util.ArrayList;
import java.util.UUID;

import vimo.model.Alarm;
import vimo.model.CalendarEventSpec;
import vimo.model.CallSpec;
import vimo.model.CannedMessagesSpec;
import vimo.model.MusicSpec;
import vimo.model.MusicStateSpec;
import vimo.model.NotificationSpec;
import vimo.model.WeatherSpec;

/**
 * Specifies all events that Gadgetbridge intends to send to the gadget device.
 * Implementations can decide to ignore events that they do not support.
 * Implementations need to send/encode event to the connected device.
 */
public interface EventHandler {
    void onNotification(NotificationSpec notificationSpec);

    void onDeleteNotification(int id);

    void onSetTime();

    void onSetAlarms(ArrayList<? extends Alarm> alarms);

    void onSetCallState(CallSpec callSpec);

    void onSetCannedMessages(CannedMessagesSpec cannedMessagesSpec);

    void onSetMusicState(MusicStateSpec stateSpec);

    void onSetMusicInfo(MusicSpec musicSpec);

    void onEnableRealtimeSteps(boolean enable);

    void onInstallApp(Uri uri);

    void onAppInfoReq();

    void onAppStart(UUID uuid, boolean start);

    void onAppDelete(UUID uuid);

    void onAppConfiguration(UUID appUuid, String config);

    void onAppReorder(UUID uuids[]);

    void onFetchActivityData();

    void onReboot();

    void onHeartRateTest();

    void onEnableRealtimeHeartRateMeasurement(boolean enable);

    void onFindDevice(boolean start);

    void onSetConstantVibration(int integer);

    void onScreenshotReq();

    void onEnableHeartRateSleepSupport(boolean enable);

    void onAddCalendarEvent(CalendarEventSpec calendarEventSpec);

    void onDeleteCalendarEvent(byte type, long id);

    /**
     * Sets the given option in the device, typically with values from the preferences.
     * The config name is device specific.
     * @param config the device specific option to set on the device
     */
    void onSendConfiguration(String config);

    void onTestNewFunction();

    void onSendWeather(WeatherSpec weatherSpec);
}
