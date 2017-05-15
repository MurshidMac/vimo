package nodomain.strive.vimo.service.devices.liveview;

import android.net.Uri;

import java.util.ArrayList;
import java.util.UUID;

import nodomain.strive.vimo.model.Alarm;
import nodomain.strive.vimo.model.CalendarEventSpec;
import nodomain.strive.vimo.model.CallSpec;
import nodomain.strive.vimo.model.MusicSpec;
import nodomain.strive.vimo.model.MusicStateSpec;
import nodomain.strive.vimo.model.NotificationSpec;
import nodomain.strive.vimo.service.serial.AbstractSerialDeviceSupport;
import nodomain.strive.vimo.service.serial.GBDeviceIoThread;
import nodomain.strive.vimo.service.serial.GBDeviceProtocol;

public class LiveviewSupport extends AbstractSerialDeviceSupport {

    @Override
    public boolean connect() {
        getDeviceIOThread().start();
        return true;
    }

    @Override
    protected GBDeviceProtocol createDeviceProtocol() {
        return new LiveviewProtocol(getDevice());
    }

    @Override
    protected GBDeviceIoThread createDeviceIOThread() {
        return new LiveviewIoThread(getDevice(), getContext(), getDeviceProtocol(), LiveviewSupport.this, getBluetoothAdapter());
    }

    @Override
    public boolean useAutoConnect() {
        return false;
    }

    @Override
    public void onInstallApp(Uri uri) {
        //nothing to do ATM
    }

    @Override
    public void onAppConfiguration(UUID uuid, String config) {
        //nothing to do ATM
    }

    @Override
    public void onHeartRateTest() {
        //nothing to do ATM
    }

    @Override
    public void onSetConstantVibration(int intensity) {
        //nothing to do ATM
    }

    @Override
    public synchronized LiveviewIoThread getDeviceIOThread() {
        return (LiveviewIoThread) super.getDeviceIOThread();
    }

    @Override
    public void onNotification(NotificationSpec notificationSpec) {
        super.onNotification(notificationSpec);
    }

    @Override
    public void onSetCallState(CallSpec callSpec) {
        //nothing to do ATM
    }

    @Override
    public void onSetMusicState(MusicStateSpec musicStateSpec) {
        //nothing to do ATM
    }

    @Override
    public void onSetMusicInfo(MusicSpec musicSpec) {
        //nothing to do ATM
    }


    @Override
    public void onSetAlarms(ArrayList<? extends Alarm> alarms) {
        //nothing to do ATM
    }

    @Override
    public void onAddCalendarEvent(CalendarEventSpec calendarEventSpec) {
        //nothing to do ATM
    }

    @Override
    public void onDeleteCalendarEvent(byte type, long id) {
        //nothing to do ATM
    }

    @Override
    public void onTestNewFunction() {
        //nothing to do ATM
    }
}
