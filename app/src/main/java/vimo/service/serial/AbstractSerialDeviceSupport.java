package vimo.service.serial;

import java.util.UUID;

import vimo.deviceevents.GBDeviceEvent;
import vimo.deviceevents.GBDeviceEventSendBytes;
import vimo.devices.EventHandler;
import vimo.model.CalendarEventSpec;
import vimo.model.CallSpec;
import vimo.model.CannedMessagesSpec;
import vimo.model.MusicSpec;
import vimo.model.MusicStateSpec;
import vimo.model.NotificationSpec;
import vimo.model.WeatherSpec;
import vimo.service.AbstractDeviceSupport;

/**
 * An abstract base class for devices speaking a serial protocol, like via
 * an rfcomm bluetooth socket or a TCP socket.
 * <p/>
 * This class uses two helper classes to deal with that:
 * - GBDeviceIoThread, which creates and maintains the actual socket connection and implements the transport layer
 * - GBDeviceProtocol, which implements the encoding and decoding of messages, i.e. the actual device specific protocol
 * <p/>
 * Note that these two classes need to be implemented in a device specific way.
 * <p/>
 * This implementation implements all methods of {@link EventHandler}, calls the {@link GBDeviceProtocol device protocol}
 * to create the device specific message for the respective events and sends them to the device via {@link #sendToDevice(byte[])}.
 */
public abstract class AbstractSerialDeviceSupport extends AbstractDeviceSupport {
    private GBDeviceProtocol gbDeviceProtocol;
    protected GBDeviceIoThread gbDeviceIOThread;

    /**
     * Factory method to create the device specific GBDeviceProtocol instance to be used.
     */
    protected abstract GBDeviceProtocol createDeviceProtocol();

    /**
     * Factory method to create the device specific GBDeviceIoThread instance to be used.
     */
    protected abstract GBDeviceIoThread createDeviceIOThread();

    @Override
    public void dispose() {
        // currently only one thread allowed
        if (gbDeviceIOThread != null) {
            gbDeviceIOThread.quit();
            gbDeviceIOThread.interrupt();
            gbDeviceIOThread = null;
        }
    }

    @Override
    public void pair() {
        // Default implementation does no manual pairing, use the Android
        // pairing dialog instead.
    }

    /**
     * Lazily creates and returns the GBDeviceProtocol instance to be used.
     */
    protected synchronized GBDeviceProtocol getDeviceProtocol() {
        if (gbDeviceProtocol == null) {
            gbDeviceProtocol = createDeviceProtocol();
        }
        return gbDeviceProtocol;
    }

    /**
     * Lazily creates and returns the GBDeviceIoThread instance to be used.
     */
    public synchronized GBDeviceIoThread getDeviceIOThread() {
        if (gbDeviceIOThread == null) {
            gbDeviceIOThread = createDeviceIOThread();
        }
        return gbDeviceIOThread;
    }

    /**
     * Sends the given message to the device. This implementation delegates the
     * writing to the {@link #getDeviceIOThread device io thread}
     *
     * @param bytes the message to send to the device
     */
    private void sendToDevice(byte[] bytes) {
        if (bytes != null && gbDeviceIOThread != null) {
            gbDeviceIOThread.write(bytes);
        }
    }

    private void handleGBDeviceEvent(GBDeviceEventSendBytes sendBytes) {
        sendToDevice(sendBytes.encodedBytes);
    }

    @Override
    public void evaluateGBDeviceEvent(GBDeviceEvent deviceEvent) {
        if (deviceEvent instanceof GBDeviceEventSendBytes) {
            handleGBDeviceEvent((GBDeviceEventSendBytes) deviceEvent);
            return;
        }
        super.evaluateGBDeviceEvent(deviceEvent);
    }

    @Override
    public void onNotification(NotificationSpec notificationSpec) {
        byte[] bytes = gbDeviceProtocol.encodeNotification(notificationSpec);
        sendToDevice(bytes);
    }

    @Override
    public void onDeleteNotification(int id) {
        byte[] bytes = gbDeviceProtocol.encodeDeleteNotification(id);
        sendToDevice(bytes);
    }

    @Override
    public void onSetTime() {
        byte[] bytes = gbDeviceProtocol.encodeSetTime();
        sendToDevice(bytes);
    }

    @Override
    public void onSetCallState(CallSpec callSpec) {
        byte[] bytes = gbDeviceProtocol.encodeSetCallState(callSpec.number, callSpec.name, callSpec.command);
        sendToDevice(bytes);
    }

    @Override
    public void onSetCannedMessages(CannedMessagesSpec cannedMessagesSpec) {
        byte[] bytes = gbDeviceProtocol.encodeSetCannedMessages(cannedMessagesSpec);
        sendToDevice(bytes);
    }

    @Override
    public void onSetMusicState(MusicStateSpec stateSpec) {
        byte[] bytes = gbDeviceProtocol.encodeSetMusicState(stateSpec.state, stateSpec.position, stateSpec.playRate, stateSpec.shuffle, stateSpec.repeat);
        sendToDevice(bytes);
    }

    @Override
    public void onSetMusicInfo(MusicSpec musicSpec) {
        byte[] bytes = gbDeviceProtocol.encodeSetMusicInfo(musicSpec.artist, musicSpec.album, musicSpec.track, musicSpec.duration, musicSpec.trackCount, musicSpec.trackNr);
        sendToDevice(bytes);
    }

    @Override
    public void onAppInfoReq() {
        byte[] bytes = gbDeviceProtocol.encodeAppInfoReq();
        sendToDevice(bytes);
    }

    @Override
    public void onAppStart(UUID uuid, boolean start) {
        byte[] bytes = gbDeviceProtocol.encodeAppStart(uuid, start);
        sendToDevice(bytes);
    }

    @Override
    public void onAppDelete(UUID uuid) {
        byte[] bytes = gbDeviceProtocol.encodeAppDelete(uuid);
        sendToDevice(bytes);
    }

    @Override
    public void onAppReorder(UUID[] uuids) {
        byte[] bytes = gbDeviceProtocol.encodeAppReorder(uuids);
        sendToDevice(bytes);
    }

    @Override
    public void onFetchActivityData() {
        byte[] bytes = gbDeviceProtocol.encodeSynchronizeActivityData();
        sendToDevice(bytes);
    }

    @Override
    public void onReboot() {
        byte[] bytes = gbDeviceProtocol.encodeReboot();
        sendToDevice(bytes);
    }

    @Override
    public void onFindDevice(boolean start) {
        byte[] bytes = gbDeviceProtocol.encodeFindDevice(start);
        sendToDevice(bytes);
    }

    @Override
    public void onScreenshotReq() {
        byte[] bytes = gbDeviceProtocol.encodeScreenshotReq();
        sendToDevice(bytes);
    }

    @Override
    public void onEnableRealtimeSteps(boolean enable) {
        byte[] bytes = gbDeviceProtocol.encodeEnableRealtimeSteps(enable);
        sendToDevice(bytes);
    }

    @Override
    public void onEnableHeartRateSleepSupport(boolean enable) {
        byte[] bytes = gbDeviceProtocol.encodeEnableHeartRateSleepSupport(enable);
        sendToDevice(bytes);
    }

    @Override
    public void onEnableRealtimeHeartRateMeasurement(boolean enable) {
        byte[] bytes = gbDeviceProtocol.encodeEnableRealtimeHeartRateMeasurement(enable);
        sendToDevice(bytes);
    }

    @Override
    public void onAddCalendarEvent(CalendarEventSpec calendarEventSpec) {
        byte[] bytes = gbDeviceProtocol.encodeAddCalendarEvent(calendarEventSpec);
        sendToDevice(bytes);
    }

    @Override
    public void onDeleteCalendarEvent(byte type, long id) {
        byte[] bytes = gbDeviceProtocol.encodeDeleteCalendarEvent(type, id);
        sendToDevice(bytes);
    }

    @Override
    public void onSendConfiguration(String config) {
        byte[] bytes = gbDeviceProtocol.encodeSendConfiguration(config);
        sendToDevice(bytes);
    }

    @Override
    public void onTestNewFunction() {
        byte[] bytes = gbDeviceProtocol.encodeTestNewFunction();
        sendToDevice(bytes);
    }

    @Override
    public void onSendWeather(WeatherSpec weatherSpec) {
        byte[] bytes = gbDeviceProtocol.encodeSendWeather(weatherSpec);
        sendToDevice(bytes);
    }
}
