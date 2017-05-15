package nodomain.strive.vimo.service.devices.vibratissimo;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.UUID;

import nodomain.strive.vimo.deviceevents.GBDeviceEventBatteryInfo;
import nodomain.strive.vimo.deviceevents.GBDeviceEventVersionInfo;
import nodomain.strive.vimo.impl.GBDevice;
import nodomain.strive.vimo.model.Alarm;
import nodomain.strive.vimo.model.CalendarEventSpec;
import nodomain.strive.vimo.model.CallSpec;
import nodomain.strive.vimo.model.CannedMessagesSpec;
import nodomain.strive.vimo.model.MusicSpec;
import nodomain.strive.vimo.model.MusicStateSpec;
import nodomain.strive.vimo.model.NotificationSpec;
import nodomain.strive.vimo.model.WeatherSpec;
import nodomain.strive.vimo.service.btle.AbstractBTLEDeviceSupport;
import nodomain.strive.vimo.service.btle.GattService;
import nodomain.strive.vimo.service.btle.TransactionBuilder;
import nodomain.strive.vimo.service.btle.actions.SetDeviceStateAction;
import nodomain.strive.vimo.service.btle.profiles.battery.BatteryInfoProfile;
import nodomain.strive.vimo.service.btle.profiles.deviceinfo.DeviceInfoProfile;
import nodomain.strive.vimo.service.btle.profiles.battery.BatteryInfo;
import nodomain.strive.vimo.service.btle.profiles.deviceinfo.DeviceInfo;

public class VibratissimoSupport extends AbstractBTLEDeviceSupport {

    private static final Logger LOG = LoggerFactory.getLogger(VibratissimoSupport.class);
    private final DeviceInfoProfile<VibratissimoSupport> deviceInfoProfile;
    private final BatteryInfoProfile<VibratissimoSupport> batteryInfoProfile;
    private final GBDeviceEventVersionInfo versionCmd = new GBDeviceEventVersionInfo();
    private final GBDeviceEventBatteryInfo batteryCmd = new GBDeviceEventBatteryInfo();
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            if (s.equals(DeviceInfoProfile.ACTION_DEVICE_INFO)) {
                handleDeviceInfo((DeviceInfo) intent.getParcelableExtra(DeviceInfoProfile.EXTRA_DEVICE_INFO));
            } else if (s.equals(BatteryInfoProfile.ACTION_BATTERY_INFO)) {
                handleBatteryInfo((BatteryInfo) intent.getParcelableExtra(BatteryInfoProfile.EXTRA_BATTERY_INFO));
            }
        }
    };

    public VibratissimoSupport() {
        super(LOG);
        addSupportedService(GattService.UUID_SERVICE_GENERIC_ACCESS);
        addSupportedService(GattService.UUID_SERVICE_GENERIC_ATTRIBUTE);
        addSupportedService(GattService.UUID_SERVICE_DEVICE_INFORMATION);
        addSupportedService(GattService.UUID_SERVICE_BATTERY_SERVICE);
        addSupportedService(UUID.fromString("00001523-1212-efde-1523-785feabcd123"));

        deviceInfoProfile = new DeviceInfoProfile<>(this);
        batteryInfoProfile = new BatteryInfoProfile<>(this);
        addSupportedProfile(deviceInfoProfile);
        addSupportedProfile(batteryInfoProfile);

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getContext());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BatteryInfoProfile.ACTION_BATTERY_INFO);
        intentFilter.addAction(DeviceInfoProfile.ACTION_DEVICE_INFO);
        broadcastManager.registerReceiver(mReceiver, intentFilter);
    }

    private void handleBatteryInfo(BatteryInfo info) {
        batteryCmd.level = (short) info.getPercentCharged();
        handleGBDeviceEvent(batteryCmd);
    }

    @Override
    public void dispose() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getContext());
        broadcastManager.unregisterReceiver(mReceiver);
        super.dispose();
    }

    @Override
    protected TransactionBuilder initializeDevice(TransactionBuilder builder) {
        builder.add(new SetDeviceStateAction(getDevice(), GBDevice.State.INITIALIZING, getContext()));
        requestDeviceInfo(builder);
        setInitialized(builder);
        batteryInfoProfile.requestBatteryInfo(builder);
        return builder;
    }

    private void requestDeviceInfo(TransactionBuilder builder) {
        LOG.debug("Requesting Device Info!");
        deviceInfoProfile.requestDeviceInfo(builder);
    }

    private void setInitialized(TransactionBuilder builder) {
        builder.add(new SetDeviceStateAction(getDevice(), GBDevice.State.INITIALIZED, getContext()));
    }


    @Override
    public boolean useAutoConnect() {
        return true;
    }

    @Override
    public void pair() {

    }

    private void handleDeviceInfo(DeviceInfo info) {
        LOG.warn("Device info: " + info);
        versionCmd.hwVersion = info.getHardwareRevision();
        versionCmd.fwVersion = info.getFirmwareRevision();
        handleGBDeviceEvent(versionCmd);
    }

    @Override
    public void onNotification(NotificationSpec notificationSpec) {
    }

    @Override
    public void onDeleteNotification(int id) {

    }

    @Override
    public void onSetTime() {

    }

    @Override
    public void onSetAlarms(ArrayList<? extends Alarm> alarms) {

    }

    @Override
    public void onSetCallState(CallSpec callSpec) {

    }

    @Override
    public void onSetCannedMessages(CannedMessagesSpec cannedMessagesSpec) {

    }

    @Override
    public void onSetMusicState(MusicStateSpec stateSpec) {

    }

    @Override
    public void onSetMusicInfo(MusicSpec musicSpec) {

    }

    @Override
    public void onEnableRealtimeSteps(boolean enable) {

    }

    @Override
    public void onInstallApp(Uri uri) {

    }

    @Override
    public void onAppInfoReq() {

    }

    @Override
    public void onAppStart(UUID uuid, boolean start) {

    }

    @Override
    public void onAppDelete(UUID uuid) {

    }

    @Override
    public void onAppConfiguration(UUID appUuid, String config) {

    }

    @Override
    public void onAppReorder(UUID[] uuids) {

    }

    @Override
    public void onFetchActivityData() {

    }

    @Override
    public void onReboot() {

    }

    @Override
    public void onHeartRateTest() {

    }

    @Override
    public void onEnableRealtimeHeartRateMeasurement(boolean enable) {

    }

    @Override
    public void onFindDevice(boolean start) {
        onSetConstantVibration(start ? 0xff : 0x00);
    }

    @Override
    public void onSetConstantVibration(int intensity) {
        getQueue().clear();
        BluetoothGattCharacteristic characteristic2 = getCharacteristic(UUID.fromString("00001526-1212-efde-1523-785feabcd123"));
        BluetoothGattCharacteristic characteristic1 = getCharacteristic(UUID.fromString("00001524-1212-efde-1523-785feabcd123"));

        TransactionBuilder builder = new TransactionBuilder("vibration");
        builder.write(characteristic1, new byte[]{0x03, (byte) 0x80});

        builder.write(characteristic2, new byte[]{(byte) intensity, 0x00});
        builder.queue(getQueue());
    }

    @Override
    public void onScreenshotReq() {

    }

    @Override
    public void onEnableHeartRateSleepSupport(boolean enable) {

    }

    @Override
    public void onAddCalendarEvent(CalendarEventSpec calendarEventSpec) {

    }

    @Override
    public void onDeleteCalendarEvent(byte type, long id) {

    }


    @Override
    public boolean onCharacteristicChanged(BluetoothGatt gatt,
                                           BluetoothGattCharacteristic characteristic) {
        if (super.onCharacteristicChanged(gatt, characteristic)) {
            return true;
        }

        UUID characteristicUUID = characteristic.getUuid();
        LOG.info("Unhandled characteristic changed: " + characteristicUUID);
        return false;
    }

    @Override
    public boolean onCharacteristicRead(BluetoothGatt gatt,
                                        BluetoothGattCharacteristic characteristic, int status) {
        if (super.onCharacteristicRead(gatt, characteristic, status)) {
            return true;
        }
        UUID characteristicUUID = characteristic.getUuid();

        LOG.info("Unhandled characteristic read: " + characteristicUUID);
        return false;
    }

    @Override
    public void onSendConfiguration(String config) {

    }

    @Override
    public void onTestNewFunction() {

    }

    @Override
    public void onSendWeather(WeatherSpec weatherSpec) {

    }
}
