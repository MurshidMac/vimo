package nodomain.strive.vimo.service.btle.actions;

import nodomain.strive.vimo.service.btle.BtLEAction;

/**
 * An abstract non-BTLE action. It performs no bluetooth operation,
 * does not have a BluetoothGattCharacteristic instance and expects no result.
 */
public abstract class PlainAction extends BtLEAction {

    public PlainAction() {
        super(null);
    }

    @Override
    public boolean expectsResult() {
        return false;
    }

    @Override
    public String toString() {
        return getCreationTime() + ": " + getClass().getSimpleName();
    }
}
