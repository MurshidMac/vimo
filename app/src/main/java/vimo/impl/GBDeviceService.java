package vimo.impl;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.UUID;

import vimo.model.Alarm;
import vimo.model.CalendarEventSpec;
import vimo.model.CallSpec;
import vimo.model.CannedMessagesSpec;
import vimo.model.DeviceService;
import vimo.model.MusicSpec;
import vimo.model.MusicStateSpec;
import vimo.model.NotificationSpec;
import vimo.model.WeatherSpec;
import vimo.service.DeviceCommunicationService;
import vimo.util.LanguageUtils;

import static vimo.util.JavaExtensions.coalesce;

public class GBDeviceService implements DeviceService {
    protected final Context mContext;
    private final Class<? extends Service> mServiceClass;
    private final String[] transliterationExtras = new String[]{
            EXTRA_NOTIFICATION_PHONENUMBER,
            EXTRA_NOTIFICATION_SENDER,
            EXTRA_NOTIFICATION_SUBJECT,
            EXTRA_NOTIFICATION_TITLE,
            EXTRA_NOTIFICATION_BODY,
            EXTRA_NOTIFICATION_SOURCENAME,
            EXTRA_CALL_PHONENUMBER,
            EXTRA_CALL_DISPLAYNAME,
            EXTRA_MUSIC_ARTIST,
            EXTRA_MUSIC_ALBUM,
            EXTRA_MUSIC_TRACK,
            EXTRA_CALENDAREVENT_TITLE,
            EXTRA_CALENDAREVENT_DESCRIPTION
    };

    public GBDeviceService(Context context) {
        mContext = context;
        mServiceClass = DeviceCommunicationService.class;
    }

    protected Intent createIntent() {
        return new Intent(mContext, mServiceClass);
    }

    protected void invokeService(Intent intent) {
        if(LanguageUtils.transliterate()){
            for (String extra: transliterationExtras) {
                if (intent.hasExtra(extra)){
                    intent.putExtra(extra, LanguageUtils.transliterate(intent.getStringExtra(extra)));
                }
            }
        }

        mContext.startService(intent);
    }

    protected void stopService(Intent intent) {
        mContext.stopService(intent);
    }

    @Override
    public void start() {
        Intent intent = createIntent().setAction(ACTION_START);
        invokeService(intent);
    }

    @Override
    public void connect() {
        connect(null, false);
    }

    @Override
    public void connect(@Nullable GBDevice device) {
        connect(device, false);
    }

        @Override
    public void connect(@Nullable GBDevice device, boolean performPair) {
        Intent intent = createIntent().setAction(ACTION_CONNECT)
                .putExtra(GBDevice.EXTRA_DEVICE, device)
                .putExtra(EXTRA_PERFORM_PAIR, performPair);
        invokeService(intent);
    }

    @Override
    public void disconnect() {
        Intent intent = createIntent().setAction(ACTION_DISCONNECT);
        invokeService(intent);
    }

    @Override
    public void quit() {
        Intent intent = createIntent();
        stopService(intent);
    }

    @Override
    public void requestDeviceInfo() {
        Intent intent = createIntent().setAction(ACTION_REQUEST_DEVICEINFO);
        invokeService(intent);
    }

    @Override
    public void onNotification(NotificationSpec notificationSpec) {
        Intent intent = createIntent().setAction(ACTION_NOTIFICATION)
                .putExtra(EXTRA_NOTIFICATION_FLAGS, notificationSpec.flags)
                .putExtra(EXTRA_NOTIFICATION_PHONENUMBER, notificationSpec.phoneNumber)
                .putExtra(EXTRA_NOTIFICATION_SENDER, coalesce(notificationSpec.sender, getContactDisplayNameByNumber(notificationSpec.phoneNumber)))
                .putExtra(EXTRA_NOTIFICATION_SUBJECT, notificationSpec.subject)
                .putExtra(EXTRA_NOTIFICATION_TITLE, notificationSpec.title)
                .putExtra(EXTRA_NOTIFICATION_BODY, notificationSpec.body)
                .putExtra(EXTRA_NOTIFICATION_ID, notificationSpec.id)
                .putExtra(EXTRA_NOTIFICATION_TYPE, notificationSpec.type)
                .putExtra(EXTRA_NOTIFICATION_SOURCENAME, notificationSpec.sourceName);
        invokeService(intent);
    }

    @Override
    public void onDeleteNotification(int id) {
        Intent intent = createIntent().setAction(ACTION_DELETE_NOTIFICATION)
                .putExtra(EXTRA_NOTIFICATION_ID, id);
        invokeService(intent);

    }

    @Override
    public void onSetTime() {
        Intent intent = createIntent().setAction(ACTION_SETTIME);
        invokeService(intent);
    }

    @Override
    public void onSetAlarms(ArrayList<? extends Alarm> alarms) {
        Intent intent = createIntent().setAction(ACTION_SET_ALARMS)
                .putParcelableArrayListExtra(EXTRA_ALARMS, alarms);
        invokeService(intent);
    }

    @Override
    public void onSetCallState(CallSpec callSpec) {
        Intent intent = createIntent().setAction(ACTION_CALLSTATE)
                .putExtra(EXTRA_CALL_PHONENUMBER, callSpec.number)
                .putExtra(EXTRA_CALL_DISPLAYNAME, coalesce(callSpec.name, getContactDisplayNameByNumber(callSpec.number)))
                .putExtra(EXTRA_CALL_COMMAND, callSpec.command);
        invokeService(intent);
    }

    @Override
    public void onSetCannedMessages(CannedMessagesSpec cannedMessagesSpec) {
        Intent intent = createIntent().setAction(ACTION_SETCANNEDMESSAGES)
                .putExtra(EXTRA_CANNEDMESSAGES_TYPE, cannedMessagesSpec.type)
                .putExtra(EXTRA_CANNEDMESSAGES, cannedMessagesSpec.cannedMessages);
        invokeService(intent);
    }

    @Override
    public void onSetMusicState(MusicStateSpec stateSpec) {
        Intent intent = createIntent().setAction(ACTION_SETMUSICSTATE)
                .putExtra(EXTRA_MUSIC_REPEAT, stateSpec.repeat)
                .putExtra(EXTRA_MUSIC_RATE, stateSpec.playRate)
                .putExtra(EXTRA_MUSIC_STATE, stateSpec.state)
                .putExtra(EXTRA_MUSIC_SHUFFLE, stateSpec.shuffle)
                .putExtra(EXTRA_MUSIC_POSITION, stateSpec.position);
        invokeService(intent);
    }

    @Override
    public void onSetMusicInfo(MusicSpec musicSpec) {
        Intent intent = createIntent().setAction(ACTION_SETMUSICINFO)
                .putExtra(EXTRA_MUSIC_ARTIST, musicSpec.artist)
                .putExtra(EXTRA_MUSIC_ALBUM, musicSpec.album)
                .putExtra(EXTRA_MUSIC_TRACK, musicSpec.track)
                .putExtra(EXTRA_MUSIC_DURATION, musicSpec.duration)
                .putExtra(EXTRA_MUSIC_TRACKCOUNT, musicSpec.trackCount)
                .putExtra(EXTRA_MUSIC_TRACKNR, musicSpec.trackNr);
        invokeService(intent);
    }

    @Override
    public void onInstallApp(Uri uri) {
        Intent intent = createIntent().setAction(ACTION_INSTALL)
                .putExtra(EXTRA_URI, uri);
        invokeService(intent);
    }

    @Override
    public void onAppInfoReq() {
        Intent intent = createIntent().setAction(ACTION_REQUEST_APPINFO);
        invokeService(intent);
    }

    @Override
    public void onAppStart(UUID uuid, boolean start) {
        Intent intent = createIntent().setAction(ACTION_STARTAPP)
                .putExtra(EXTRA_APP_UUID, uuid)
                .putExtra(EXTRA_APP_START, start);
        invokeService(intent);
    }

    @Override
    public void onAppDelete(UUID uuid) {
        Intent intent = createIntent().setAction(ACTION_DELETEAPP)
                .putExtra(EXTRA_APP_UUID, uuid);
        invokeService(intent);
    }

    @Override
    public void onAppConfiguration(UUID uuid, String config) {
        Intent intent = createIntent().setAction(ACTION_APP_CONFIGURE)
                .putExtra(EXTRA_APP_UUID, uuid)
                .putExtra(EXTRA_APP_CONFIG, config);
        invokeService(intent);
    }

    @Override
    public void onAppReorder(UUID[] uuids) {
        Intent intent = createIntent().setAction(ACTION_APP_REORDER)
                .putExtra(EXTRA_APP_UUID, uuids);
        invokeService(intent);
    }

    @Override
    public void onFetchActivityData() {
        Intent intent = createIntent().setAction(ACTION_FETCH_ACTIVITY_DATA);
        invokeService(intent);
    }

    @Override
    public void onReboot() {
        Intent intent = createIntent().setAction(ACTION_REBOOT);
        invokeService(intent);
    }

    @Override
    public void onHeartRateTest() {
        Intent intent = createIntent().setAction(ACTION_HEARTRATE_TEST);
        invokeService(intent);
    }

    @Override
    public void onFindDevice(boolean start) {
        Intent intent = createIntent().setAction(ACTION_FIND_DEVICE)
                .putExtra(EXTRA_FIND_START, start);
        invokeService(intent);
    }

    @Override
    public void onSetConstantVibration(int intensity) {
        Intent intent = createIntent().setAction(ACTION_SET_CONSTANT_VIBRATION)
                .putExtra(EXTRA_VIBRATION_INTENSITY, intensity);
        invokeService(intent);
    }

    @Override
    public void onScreenshotReq() {
        Intent intent = createIntent().setAction(ACTION_REQUEST_SCREENSHOT);
        invokeService(intent);
    }

    @Override
    public void onEnableRealtimeSteps(boolean enable) {
        Intent intent = createIntent().setAction(ACTION_ENABLE_REALTIME_STEPS)
                .putExtra(EXTRA_BOOLEAN_ENABLE, enable);
        invokeService(intent);
    }

    @Override
    public void onEnableHeartRateSleepSupport(boolean enable) {
        Intent intent = createIntent().setAction(ACTION_ENABLE_HEARTRATE_SLEEP_SUPPORT)
                .putExtra(EXTRA_BOOLEAN_ENABLE, enable);
        invokeService(intent);
    }

    @Override
    public void onEnableRealtimeHeartRateMeasurement(boolean enable) {
        Intent intent = createIntent().setAction(ACTION_ENABLE_REALTIME_HEARTRATE_MEASUREMENT)
                .putExtra(EXTRA_BOOLEAN_ENABLE, enable);
        invokeService(intent);
    }

    @Override
    public void onAddCalendarEvent(CalendarEventSpec calendarEventSpec) {
        Intent intent = createIntent().setAction(ACTION_ADD_CALENDAREVENT)
                .putExtra(EXTRA_CALENDAREVENT_ID, calendarEventSpec.id)
                .putExtra(EXTRA_CALENDAREVENT_TYPE, calendarEventSpec.type)
                .putExtra(EXTRA_CALENDAREVENT_TIMESTAMP, calendarEventSpec.timestamp)
                .putExtra(EXTRA_CALENDAREVENT_DURATION, calendarEventSpec.durationInSeconds)
                .putExtra(EXTRA_CALENDAREVENT_TITLE, calendarEventSpec.title)
                .putExtra(EXTRA_CALENDAREVENT_DESCRIPTION, calendarEventSpec.description);
        invokeService(intent);
    }

    @Override
    public void onDeleteCalendarEvent(byte type, long id) {
        Intent intent = createIntent().setAction(ACTION_DELETE_CALENDAREVENT)
                .putExtra(EXTRA_CALENDAREVENT_TYPE, type)
                .putExtra(EXTRA_CALENDAREVENT_ID, id);
        invokeService(intent);
    }

    @Override
    public void onSendConfiguration(String config) {
        Intent intent = createIntent().setAction(ACTION_SEND_CONFIGURATION)
                .putExtra(EXTRA_CONFIG, config);
        invokeService(intent);
    }

    @Override
    public void onTestNewFunction() {
        Intent intent = createIntent().setAction(ACTION_TEST_NEW_FUNCTION);
        invokeService(intent);
    }

    @Override
    public void onSendWeather(WeatherSpec weatherSpec) {
        Intent intent = createIntent().setAction(ACTION_SEND_WEATHER)
                .putExtra(EXTRA_WEATHER_TIMESTAMP, weatherSpec.timestamp)
                .putExtra(EXTRA_WEATHER_LOCATION, weatherSpec.location)
                .putExtra(EXTRA_WEATHER_CURRENTTEMP, weatherSpec.currentTemp)
                .putExtra(EXTRA_WEATHER_CURRENTCONDITIONCODE, weatherSpec.currentConditionCode)
                .putExtra(EXTRA_WEATHER_CURRENTCONDITION, weatherSpec.currentCondition)
                .putExtra(EXTRA_WEATHER_TODAYMAXTEMP, weatherSpec.todayMaxTemp)
                .putExtra(EXTRA_WEATHER_TODAYMINTEMP, weatherSpec.todayMinTemp)
                .putExtra(EXTRA_WEATHER_TOMORROWMAXTEMP, weatherSpec.tomorrowMaxTemp)
                .putExtra(EXTRA_WEATHER_TOMORROWMINTEMP, weatherSpec.tomorrowMinTemp)
                .putExtra(EXTRA_WEATHER_TOMORROWCONDITIONCODE, weatherSpec.tomorrowConditionCode);
        invokeService(intent);
    }

    /**
     * Returns contact DisplayName by call number
     * @param number contact number
     * @return contact DisplayName, if found it
     */
    private String getContactDisplayNameByNumber(String number) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String name = number;

        if (number == null || number.equals("")) {
            return name;
        }

        try (Cursor contactLookup = mContext.getContentResolver().query(uri, null, null, null, null)) {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            }
        } catch (SecurityException e) {
            // ignore, just return name below
        }

        return name;
    }
}
