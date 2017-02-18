package nodomain.freeyourgadget.gadgetbridge.service.devices.miband2.operations;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventDisplayMessage;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBand2Service;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BLETypeConversions;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.SetDeviceBusyAction;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.SetProgressAction;
import nodomain.freeyourgadget.gadgetbridge.service.devices.miband2.MiBand2Support;
import nodomain.freeyourgadget.gadgetbridge.service.devices.miband2.AbstractMiBand2Operation;
import nodomain.freeyourgadget.gadgetbridge.service.devices.miband2.Mi2FirmwareInfo;
import nodomain.freeyourgadget.gadgetbridge.devices.miband2.MiBand2FWHelper;
import nodomain.freeyourgadget.gadgetbridge.util.GB;
import nodomain.freeyourgadget.gadgetbridge.util.Prefs;

public class UpdateFirmwareOperation extends AbstractMiBand2Operation {
    private static final Logger LOG = LoggerFactory.getLogger(UpdateFirmwareOperation.class);

    private final Uri uri;
    private final BluetoothGattCharacteristic fwCControlChar;
    private final BluetoothGattCharacteristic fwCDataChar;
    final Prefs prefs = GBApplication.getPrefs();
    private Mi2FirmwareInfo firmwareInfo;

    public UpdateFirmwareOperation(Uri uri, MiBand2Support support) {
        super(support);
        this.uri = uri;
        fwCControlChar = getCharacteristic(MiBand2Service.UUID_CHARACTERISTIC_FIRMWARE);
        fwCDataChar = getCharacteristic(MiBand2Service.UUID_CHARACTERISTIC_FIRMWARE_DATA);
    }

    @Override
    protected void enableNeededNotifications(TransactionBuilder builder, boolean enable) {
        builder.notify(fwCControlChar, enable);
    }

    @Override
    protected void doPerform() throws IOException {
        MiBand2FWHelper mFwHelper = new MiBand2FWHelper(uri, getContext());

        firmwareInfo = mFwHelper.getFirmwareInfo();
        if (!firmwareInfo.isGenerallyCompatibleWith(getDevice())) {
            throw new IOException("Firmware is not compatible with the given device: " + getDevice().getAddress());
        }

        if (!sendFwInfo()) {
            displayMessage(getContext(), "Error sending firmware info, aborting.", Toast.LENGTH_LONG, GB.ERROR);
            done();
        }
        //the firmware will be sent by the notification listener if the band confirms that the metadata are ok.
    }

    private void done() {
        LOG.info("Operation done.");
        operationFinished();
        unsetBusy();
    }

    @Override
    public boolean onCharacteristicChanged(BluetoothGatt gatt,
                                           BluetoothGattCharacteristic characteristic) {
        UUID characteristicUUID = characteristic.getUuid();
        if (fwCControlChar.getUuid().equals(characteristicUUID)) {
            handleNotificationNotif(characteristic.getValue());
        } else {
            super.onCharacteristicChanged(gatt, characteristic);
        }
        return false;
    }

    /**
     * React to messages sent by the Mi Band to the MiBandService.UUID_CHARACTERISTIC_NOTIFICATION
     * characteristic,
     * These messages appear to be always 1 byte long, with values that are listed in MiBandService.
     * It is not excluded that there are further values which are still unknown.
     * <p/>
     * Upon receiving known values that request further action by GB, the appropriate method is called.
     *
     * @param value
     */
    private void handleNotificationNotif(byte[] value) {
        if (value.length != 3) {
            LOG.error("Notifications should be 3 bytes long.");
            getSupport().logMessageContent(value);
            return;
        }
        boolean success = value[2] == MiBand2Service.SUCCESS;

        if (value[0] == MiBand2Service.RESPONSE && success) {
            try {
                switch (value[1]) {
                    case MiBand2Service.COMMAND_FIRMWARE_INIT: {
                        sendFirmwareData(getFirmwareInfo());
                        break;
                    }
                    case MiBand2Service.COMMAND_FIRMWARE_START_DATA: {
                        sendChecksum(getFirmwareInfo());
                        break;
                    }
                    case MiBand2Service.COMMAND_FIRMWARE_CHECKSUM: {
                        sendApplyReboot(getFirmwareInfo());
                        break;
                    }
                    case MiBand2Service.COMMAND_FIRMWARE_APPLY_REBOOT: {
                        GB.updateInstallNotification(getContext().getString(R.string.updatefirmwareoperation_update_complete), false, 100, getContext());
//                    getSupport().onReboot();
                        done();
                        break;
                    }
                    default: {
                        LOG.error("Unexpected response during firmware update: ");
                        getSupport().logMessageContent(value);
                        displayMessage(getContext(), getContext().getString(R.string.updatefirmwareoperation_updateproblem_do_not_reboot), Toast.LENGTH_LONG, GB.ERROR);
                        done();
                        return;
                    }
                }
            } catch (Exception ex) {
                displayMessage(getContext(), getContext().getString(R.string.updatefirmwareoperation_updateproblem_do_not_reboot), Toast.LENGTH_LONG, GB.ERROR);
                done();
            }
        } else {
            LOG.error("Unexpected notification during firmware update: ");
            getSupport().logMessageContent(value);
            displayMessage(getContext(), getContext().getString(R.string.updatefirmwareoperation_metadata_updateproblem), Toast.LENGTH_LONG, GB.ERROR);
            done();
        }
    }
    private void displayMessage(Context context, String message, int duration, int severity) {
        getSupport().handleGBDeviceEvent(new GBDeviceEventDisplayMessage(message, duration, severity));
    }

    public boolean sendFwInfo() {
        try {
            TransactionBuilder builder = performInitialized("send firmware info");
//                getSupport().setLowLatency(builder);
            builder.add(new SetDeviceBusyAction(getDevice(), getContext().getString(R.string.updating_firmware), getContext()));
            int fwSize = getFirmwareInfo().getSize();
            byte[] sizeBytes = BLETypeConversions.fromUint24(fwSize);
            byte[] bytes = new byte[]{
                    MiBand2Service.COMMAND_FIRMWARE_INIT,
                    sizeBytes[0],
                    sizeBytes[1],
                    sizeBytes[2],
            };

            builder.write(fwCControlChar, bytes);
            builder.queue(getQueue());
            return true;
        } catch (IOException e) {
            LOG.error("Error sending firmware info: " + e.getLocalizedMessage(), e);
            return false;
        }
    }

    /**
     * Method that uploads a firmware (fwbytes) to the Mi Band.
     * The firmware has to be split into chunks of 20 bytes each, and periodically a COMMAND_SYNC command has to be issued to the Mi Band.
     * <p/>
     * The Mi Band will send a notification after receiving this data to confirm if the firmware looks good to it.
     *
     * @param info
     * @return whether the transfer succeeded or not. Only a BT layer exception will cause the transmission to fail.
     * @see MiBand2Support#handleNotificationNotif
     */
    private boolean sendFirmwareData(Mi2FirmwareInfo info) {
        byte[] fwbytes = info.getBytes();
        int len = fwbytes.length;
        final int packetLength = 20;
        int packets = len / packetLength;

        try {
            // going from 0 to len
            int firmwareProgress = 0;

            TransactionBuilder builder = performInitialized("send firmware packet");
            if (prefs.getBoolean("mi_low_latency_fw_update", true)) {
                getSupport().setLowLatency(builder);
            }
            builder.write(fwCControlChar, new byte[] { MiBand2Service.COMMAND_FIRMWARE_START_DATA });

            for (int i = 0; i < packets; i++) {
                byte[] fwChunk = Arrays.copyOfRange(fwbytes, i * packetLength, i * packetLength + packetLength);

                builder.write(fwCDataChar, fwChunk);
                firmwareProgress += packetLength;

                int progressPercent = (int) ((((float) firmwareProgress) / len) * 100);
                if ((i > 0) && (i % 100 == 0)) {
                    builder.write(fwCControlChar, new byte[]{MiBand2Service.COMMAND_FIRMWARE_UPDATE_SYNC});
                    builder.add(new SetProgressAction(getContext().getString(R.string.updatefirmwareoperation_update_in_progress), true, progressPercent, getContext()));
                }
            }

            if (firmwareProgress < len) {
                byte[] lastChunk = Arrays.copyOfRange(fwbytes, packets * packetLength, len);
                builder.write(fwCDataChar, lastChunk);
                firmwareProgress = len;
            }

            builder.write(fwCControlChar, new byte[]{MiBand2Service.COMMAND_FIRMWARE_UPDATE_SYNC});
            builder.queue(getQueue());

        } catch (IOException ex) {
            LOG.error("Unable to send fw to MI 2", ex);
            GB.updateInstallNotification(getContext().getString(R.string.updatefirmwareoperation_firmware_not_sent), false, 0, getContext());
            return false;
        }
        return true;
    }


    private void sendChecksum(Mi2FirmwareInfo firmwareInfo) throws IOException {
        TransactionBuilder builder = performInitialized("send firmware checksum");
        int crc16 = firmwareInfo.getCrc16();
        byte[] bytes = BLETypeConversions.fromUint16(crc16);
        builder.write(fwCControlChar, new byte[] {
                MiBand2Service.COMMAND_FIRMWARE_CHECKSUM,
                bytes[0],
                bytes[1],
        });
        builder.queue(getQueue());
    }

    private void sendApplyReboot(Mi2FirmwareInfo firmwareInfo) throws IOException {
        TransactionBuilder builder = performInitialized("send firmware apply/reboot");
        builder.write(fwCControlChar, new byte[] { MiBand2Service.COMMAND_FIRMWARE_APPLY_REBOOT });
        builder.queue(getQueue());
    }

    private Mi2FirmwareInfo getFirmwareInfo() {
        return firmwareInfo;
    }

    enum State {
        INITIAL,
        SEND_FW2,
        SEND_FW1,
        FINISHED,
        UNKNOWN
    }
}
