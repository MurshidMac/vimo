package vimo.service;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.Uri;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.UUID;

import vimo.impl.GBDevice;
import vimo.model.Alarm;
import vimo.model.CalendarEventSpec;
import vimo.model.CallSpec;
import vimo.model.CannedMessagesSpec;
import vimo.model.MusicSpec;
import vimo.model.MusicStateSpec;
import vimo.model.NotificationSpec;
import vimo.model.WeatherSpec;

/**
 * Wraps another device support instance and supports busy-checking and throttling of events.
 */
public class ServiceDeviceSupport implements DeviceSupport {

    enum Flags {
        THROTTLING,
        BUSY_CHECKING,
    }

    private static final Logger LOG = LoggerFactory.getLogger(ServiceDeviceSupport.class);

    private static final long THROTTLING_THRESHOLD = 1000; // throttle multiple events in between one second
    private final DeviceSupport delegate;

    private long lastNotificationTime = 0;
    private String lastNotificationKind;
    private final EnumSet<Flags> flags;

    public ServiceDeviceSupport(DeviceSupport delegate, EnumSet<Flags> flags) {
        this.delegate = delegate;
        this.flags = flags;
    }

    @Override
    public void setContext(GBDevice gbDevice, BluetoothAdapter btAdapter, Context context) {
        delegate.setContext(gbDevice, btAdapter, context);
    }

    @Override
    public boolean isConnected() {
        return delegate.isConnected();
    }

    @Override
    public boolean connect() {
        return delegate.connect();
    }

    @Override
    public void setAutoReconnect(boolean enable) {
        delegate.setAutoReconnect(enable);
    }

    @Override
    public boolean getAutoReconnect() {
        return delegate.getAutoReconnect();
    }

    @Override
    public void dispose() {
        delegate.dispose();
    }

    @Override
    public GBDevice getDevice() {
        return delegate.getDevice();
    }

    @Override
    public BluetoothAdapter getBluetoothAdapter() {
        return delegate.getBluetoothAdapter();
    }

    @Override
    public Context getContext() {
        return delegate.getContext();
    }

    @Override
    public boolean useAutoConnect() {
        return delegate.useAutoConnect();
    }

    @Override
    public void pair() {
        delegate.pair();
    }

    private boolean checkBusy(String notificationKind) {
        if (!flags.contains(Flags.BUSY_CHECKING)) {
            return false;
        }
        if (getDevice().isBusy()) {
            LOG.info("Ignoring " + notificationKind + " because we're busy with " + getDevice().getBusyTask());
            return true;
        }
        return false;
    }

    private boolean checkThrottle(String notificationKind) {
        if (!flags.contains(Flags.THROTTLING)) {
            return false;
        }
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastNotificationTime) < THROTTLING_THRESHOLD) {
            if (notificationKind != null && notificationKind.equals(lastNotificationKind)) {
                LOG.info("Ignoring " + notificationKind + " because of throttling threshold reached");
                return true;
            }
        }
        lastNotificationTime = currentTime;
        lastNotificationKind = notificationKind;
        return false;
    }

    @Override
    public void onNotification(NotificationSpec notificationSpec) {
        if (checkBusy("generic notification") || checkThrottle("generic notification")) {
            return;
        }
        delegate.onNotification(notificationSpec);
    }

    @Override
    public void onDeleteNotification(int id) {
        delegate.onDeleteNotification(id);
    }

    @Override
    public void onSetTime() {
        if (checkBusy("set time") || checkThrottle("set time")) {
            return;
        }
        delegate.onSetTime();
    }

    // No throttling for the other events

    @Override
    public void onSetCallState(CallSpec callSpec) {
        if (checkBusy("set call state")) {
            return;
        }
        delegate.onSetCallState(callSpec);
    }

    @Override
    public void onSetCannedMessages(CannedMessagesSpec cannedMessagesSpec) {
        if (checkBusy("set canned messages")) {
            return;
        }
        delegate.onSetCannedMessages(cannedMessagesSpec);
    }

    @Override
    public void onSetMusicState(MusicStateSpec stateSpec) {
        if (checkBusy("set music state")) {
            return;
        }
        delegate.onSetMusicState(stateSpec);
    }

    @Override
    public void onSetMusicInfo(MusicSpec musicSpec) {
        if (checkBusy("set music info")) {
            return;
        }
        delegate.onSetMusicInfo(musicSpec);
    }

    @Override
    public void onInstallApp(Uri uri) {
        if (checkBusy("install app")) {
            return;
        }
        delegate.onInstallApp(uri);
    }

    @Override
    public void onAppInfoReq() {
        if (checkBusy("app info request")) {
            return;
        }
        delegate.onAppInfoReq();
    }

    @Override
    public void onAppStart(UUID uuid, boolean start) {
        if (checkBusy("app start")) {
            return;
        }
        delegate.onAppStart(uuid, start);
    }

    @Override
    public void onAppDelete(UUID uuid) {
        if (checkBusy("app delete")) {
            return;
        }
        delegate.onAppDelete(uuid);
    }

    @Override
    public void onAppConfiguration(UUID uuid, String config) {
        if (checkBusy("app configuration")) {
            return;
        }
        delegate.onAppConfiguration(uuid, config);
    }

    @Override
    public void onAppReorder(UUID[] uuids) {
        if (checkBusy("app reorder")) {
            return;
        }
        delegate.onAppReorder(uuids);
    }

    @Override
    public void onFetchActivityData() {
        if (checkBusy("fetch activity data")) {
            return;
        }
        delegate.onFetchActivityData();
    }

    @Override
    public void onReboot() {
        if (checkBusy("reboot")) {
            return;
        }
        delegate.onReboot();
    }

    @Override
    public void onHeartRateTest() {
        if (checkBusy("heartrate")) {
            return;
        }
        delegate.onHeartRateTest();
    }

    @Override
    public void onFindDevice(boolean start) {
        if (checkBusy("find device")) {
            return;
        }
        delegate.onFindDevice(start);
    }

    @Override
    public void onSetConstantVibration(int intensity) {
        if (checkBusy("set constant vibration")) {
            return;
        }
        delegate.onSetConstantVibration(intensity);
    }

    @Override
    public void onScreenshotReq() {
        if (checkBusy("request screenshot")) {
            return;
        }
        delegate.onScreenshotReq();
    }

    @Override
    public void onSetAlarms(ArrayList<? extends Alarm> alarms) {
        if (checkBusy("set alarms")) {
            return;
        }
        delegate.onSetAlarms(alarms);
    }

    @Override
    public void onEnableRealtimeSteps(boolean enable) {
        if (checkBusy("enable realtime steps: " + enable)) {
            return;
        }
        delegate.onEnableRealtimeSteps(enable);
    }

    @Override
    public void onEnableHeartRateSleepSupport(boolean enable) {
        if (checkBusy("enable heartrate sleep support: " + enable)) {
            return;
        }
        delegate.onEnableHeartRateSleepSupport(enable);
    }

    @Override
    public void onEnableRealtimeHeartRateMeasurement(boolean enable) {
        if (checkBusy("enable realtime heart rate measurement: " + enable)) {
            return;
        }
        delegate.onEnableRealtimeHeartRateMeasurement(enable);
    }

    @Override
    public void onAddCalendarEvent(CalendarEventSpec calendarEventSpec) {
        if (checkBusy("add calendar event")) {
            return;
        }
        delegate.onAddCalendarEvent(calendarEventSpec);
    }

    @Override
    public void onDeleteCalendarEvent(byte type, long id) {
        if (checkBusy("delete calendar event")) {
            return;
        }
        delegate.onDeleteCalendarEvent(type, id);
    }

    @Override
    public void onSendConfiguration(String config) {
        if (checkBusy("send configuration: " + config)) {
            return;
        }
        delegate.onSendConfiguration(config);
    }

    @Override
    public void onTestNewFunction() {
        if (checkBusy("test new function event")) {
            return;
        }
        delegate.onTestNewFunction();
    }

    @Override
    public void onSendWeather(WeatherSpec weatherSpec) {
        if (checkBusy("send weather event")) {
            return;
        }
        delegate.onSendWeather(weatherSpec);
    }
}
