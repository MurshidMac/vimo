package vimo.service.devices.pebble;

import java.util.UUID;

import vimo.GBApplication;
import vimo.impl.GBDevice;
import vimo.util.Prefs;

abstract class DatalogSessionPebbleHealth extends DatalogSession {

    private final GBDevice mDevice;

    DatalogSessionPebbleHealth(byte id, UUID uuid, int tag, byte itemType, short itemSize, GBDevice device) {
        super(id, uuid, tag, itemType, itemSize);
        mDevice = device;
    }

    public GBDevice getDevice() {
        return mDevice;
    }

    boolean isPebbleHealthEnabled() {
        Prefs prefs = GBApplication.getPrefs();
        return prefs.getBoolean("pebble_sync_health", true);
    }

    boolean storePebbleHealthRawRecord() {
        Prefs prefs = GBApplication.getPrefs();
        return prefs.getBoolean("pebble_health_store_raw", true);
    }
}