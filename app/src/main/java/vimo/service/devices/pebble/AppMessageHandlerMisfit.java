package vimo.service.devices.pebble;

import android.util.Pair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.UUID;

import vimo.GBApplication;
import vimo.database.DBHandler;
import vimo.database.DBHelper;
import vimo.deviceevents.GBDeviceEvent;
import vimo.deviceevents.GBDeviceEventSendBytes;
import vimo.devices.pebble.PebbleMisfitSampleProvider;
import vimo.entities.PebbleMisfitSample;
import vimo.impl.GBDevice;
import vimo.util.Prefs;

class AppMessageHandlerMisfit extends AppMessageHandler {

    private static final int KEY_SLEEPGOAL = 1;
    private static final int KEY_STEP_ROGRESS = 2;
    private static final int KEY_SLEEP_PROGRESS = 3;
    private static final int KEY_VERSION = 4;
    private static final int KEY_SYNC = 5;
    private static final int KEY_INCOMING_DATA_BEGIN = 6;
    private static final int KEY_INCOMING_DATA = 7;
    private static final int KEY_INCOMING_DATA_END = 8;
    private static final int KEY_SYNC_RESULT = 9;

    private static final Logger LOG = LoggerFactory.getLogger(AppMessageHandlerMisfit.class);

    AppMessageHandlerMisfit(UUID uuid, PebbleProtocol pebbleProtocol) {
        super(uuid, pebbleProtocol);
    }

    @Override
    public boolean isEnabled() {
        Prefs prefs = GBApplication.getPrefs();
        return prefs.getBoolean("pebble_sync_misfit", true);
    }

    @Override
    public GBDeviceEvent[] handleMessage(ArrayList<Pair<Integer, Object>> pairs) {
        GBDevice device = getDevice();
        for (Pair<Integer, Object> pair : pairs) {
            switch (pair.first) {
                case KEY_INCOMING_DATA_BEGIN:
                    LOG.info("incoming data start");
                    break;
                case KEY_INCOMING_DATA_END:
                    LOG.info("incoming data end");
                    break;
                case KEY_INCOMING_DATA:
                    byte[] data = (byte[]) pair.second;
                    ByteBuffer buf = ByteBuffer.wrap(data);
                    buf.order(ByteOrder.LITTLE_ENDIAN);
                    int timestamp = buf.getInt();
                    int key = buf.getInt();
                    int samples = (data.length - 8) / 2;
                    if (samples <= 0) {
                        break;
                    }

                    if (mPebbleProtocol.mFwMajor < 3) {
                        timestamp -= SimpleTimeZone.getDefault().getOffset(timestamp * 1000L) / 1000;
                    }
                    Date startDate = new Date((long) timestamp * 1000L);
                    Date endDate = new Date((long) (timestamp + samples * 60) * 1000L);
                    LOG.info("got data from " + startDate + " to " + endDate);

                    int totalSteps = 0;
                    PebbleMisfitSample[] misfitSamples = new PebbleMisfitSample[samples];
                    try (DBHandler db = GBApplication.acquireDB()) {
                        PebbleMisfitSampleProvider sampleProvider = new PebbleMisfitSampleProvider(device, db.getDaoSession());
                        Long userId = DBHelper.getUser(db.getDaoSession()).getId();
                        Long deviceId = DBHelper.getDevice(getDevice(), db.getDaoSession()).getId();
                        for (int i = 0; i < samples; i++) {
                            short sample = buf.getShort();
                            misfitSamples[i] = new PebbleMisfitSample(timestamp + i * 60, deviceId, userId, sample & 0xffff);
                            misfitSamples[i].setProvider(sampleProvider);
                            int steps = misfitSamples[i].getSteps();
                            totalSteps += steps;
                            LOG.info("got steps for sample " + i + " : " + steps + "(" + Integer.toHexString(sample & 0xffff) + ")");

                        }
                        LOG.info("total steps for above period: " + totalSteps);

                        sampleProvider.addGBActivitySamples(misfitSamples);
                    } catch (Exception e) {
                        LOG.error("Error acquiring database", e);
                        return null;
                    }
                    break;
                default:
                    LOG.info("unhandled key: " + pair.first);
                    break;
            }
        }

        // always ack
        GBDeviceEventSendBytes sendBytesAck = new GBDeviceEventSendBytes();
        sendBytesAck.encodedBytes = mPebbleProtocol.encodeApplicationMessageAck(mUUID, mPebbleProtocol.last_id);

        return new GBDeviceEvent[]{sendBytesAck};
    }
}
