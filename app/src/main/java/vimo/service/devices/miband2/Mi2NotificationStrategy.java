package vimo.service.devices.miband2;

import android.bluetooth.BluetoothGattCharacteristic;

import vimo.devices.miband.VibrationProfile;
import vimo.service.btle.AbstractBTLEDeviceSupport;
import vimo.service.btle.BtLEAction;
import vimo.service.btle.GattCharacteristic;
import vimo.service.btle.TransactionBuilder;
import vimo.service.devices.miband.V2NotificationStrategy;

public class Mi2NotificationStrategy extends V2NotificationStrategy {

    public Mi2NotificationStrategy(AbstractBTLEDeviceSupport support) {
        super(support);
    }

    @Override
    protected void sendCustomNotification(VibrationProfile vibrationProfile, BtLEAction extraAction, TransactionBuilder builder) {
        //use the new alert characteristic
        BluetoothGattCharacteristic alert = getSupport().getCharacteristic(GattCharacteristic.UUID_CHARACTERISTIC_ALERT_LEVEL);
        for (short i = 0; i < vibrationProfile.getRepeat(); i++) {
            int[] onOffSequence = vibrationProfile.getOnOffSequence();
            for (int j = 0; j < onOffSequence.length; j++) {
                int on = onOffSequence[j];
                on = Math.min(500, on); // longer than 500ms is not possible
                builder.write(alert, new byte[]{(byte) vibrationProfile.getAlertLevel()});
                builder.wait(on);
                builder.write(alert, new byte[]{GattCharacteristic.NO_ALERT});

                if (++j < onOffSequence.length) {
                    int off = Math.max(onOffSequence[j], 25); // wait at least 25ms
                    builder.wait(off);
                }

                if (extraAction != null) {
                    builder.add(extraAction);
                }
            }
        }
    }

    @Override
    public void sendCustomNotification(VibrationProfile vibrationProfile, int flashTimes, int flashColour, int originalColour, long flashDuration, BtLEAction extraAction, TransactionBuilder builder) {
        // all other parameters are unfortunately not supported anymore ;-(
        sendCustomNotification(vibrationProfile, extraAction, builder);
    }
}
