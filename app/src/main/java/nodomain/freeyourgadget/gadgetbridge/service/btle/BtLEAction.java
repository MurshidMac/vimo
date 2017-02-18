package nodomain.freeyourgadget.gadgetbridge.service.btle;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import java.util.Date;

import nodomain.freeyourgadget.gadgetbridge.util.DateTimeUtils;

/**
 * The Bluedroid implementation only allows performing one GATT request at a time.
 * As they are asynchronous anyway, we encapsulate every GATT request (read and write)
 * inside a runnable action.
 * <p/>
 * These actions are then executed one after another, ensuring that every action's result
 * has been posted before invoking the next action.
 */
public abstract class BtLEAction {
    private final BluetoothGattCharacteristic characteristic;
    private final long creationTimestamp;

    public BtLEAction(BluetoothGattCharacteristic characteristic) {
        this.characteristic = characteristic;
        creationTimestamp = System.currentTimeMillis();
    }

    /**
     * Returns true if this action expects an (async) result which must
     * be waited for, before continuing with other actions.
     * <p/>
     * This is needed because the current Bluedroid stack can only deal
     * with one single bluetooth operation at a time.
     */
    public abstract boolean expectsResult();

    /**
     * Executes this action, e.g. reads or write a GATT characteristic.
     *
     * @param gatt the characteristic to manipulate, or null if none.
     * @return true if the action was successful, false otherwise
     */
    public abstract boolean run(BluetoothGatt gatt);

    /**
     * Returns the GATT characteristic being read/written/...
     *
     * @return the GATT characteristic, or <code>null</code>
     */
    public BluetoothGattCharacteristic getCharacteristic() {
        return characteristic;
    }

    protected String getCreationTime() {
        return DateTimeUtils.formatDateTime(new Date(creationTimestamp));
    }

    public String toString() {
        BluetoothGattCharacteristic characteristic = getCharacteristic();
        String uuid = characteristic == null ? "(null)" : characteristic.getUuid().toString();
        return getCreationTime() + ": " + getClass().getSimpleName() + " on characteristic: " + uuid;
    }
}
