package nodomain.strive.vimo.service.btle.profiles;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;
import java.util.UUID;

import nodomain.strive.vimo.impl.GBDevice;
import nodomain.strive.vimo.service.btle.AbstractBTLEDeviceSupport;
import nodomain.strive.vimo.service.btle.AbstractGattCallback;
import nodomain.strive.vimo.service.btle.BtLEQueue;
import nodomain.strive.vimo.service.btle.TransactionBuilder;
import nodomain.strive.vimo.service.btle.GattCharacteristic;
import nodomain.strive.vimo.service.btle.GattService;

/**
 * Base class for all BLE profiles, with things that all impplementations are
 * expected to use.
 *
 * Instances are used in the context of a concrete AbstractBTLEDeviceSupport instance,
 * i.e. a concrete device.
 *
 * @see GattService
 * @see GattCharacteristic
 * @see https://www.bluetooth.com/specifications/assigned-numbers
 */
public abstract class AbstractBleProfile<T extends AbstractBTLEDeviceSupport> extends AbstractGattCallback {
    private final T mSupport;

    public AbstractBleProfile(T support) {
        this.mSupport = support;
    }

    /**
     * All notifications should be sent through this methods to make them testable.
     * @param intent the intent to broadcast
     */
    protected void notify(Intent intent) {
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }

    /**
     * Delegates to the DeviceSupport instance and additionally sets this instance as the Gatt
     * callback for the transaction.
     *
     * @param taskName
     * @return
     * @throws IOException
     */
    public TransactionBuilder performInitialized(String taskName) throws IOException {
        TransactionBuilder builder = mSupport.performInitialized(taskName);
        builder.setGattCallback(this);
        return builder;
    }

    public Context getContext() {
        return mSupport.getContext();
    }

    protected GBDevice getDevice() {
        return mSupport.getDevice();
    }

    protected BluetoothGattCharacteristic getCharacteristic(UUID uuid) {
        return mSupport.getCharacteristic(uuid);
    }

    protected BtLEQueue getQueue() {
        return mSupport.getQueue();
    }

}
