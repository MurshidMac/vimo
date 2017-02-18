package nodomain.freeyourgadget.gadgetbridge.service.devices.miband2.operations;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBand2Service;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEOperation;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.btle.actions.SetDeviceStateAction;
import nodomain.freeyourgadget.gadgetbridge.service.devices.miband2.MiBand2Support;
import nodomain.freeyourgadget.gadgetbridge.util.GB;

public class InitOperation extends AbstractBTLEOperation<MiBand2Support> {
    private static final Logger LOG = LoggerFactory.getLogger(InitOperation.class);

    private final TransactionBuilder builder;
    private final boolean needsAuth;

    public InitOperation(boolean needsAuth, MiBand2Support support, TransactionBuilder builder) {
        super(support);
        this.needsAuth = needsAuth;
        this.builder = builder;
        builder.setGattCallback(this);
    }

    @Override
    protected void doPerform() throws IOException {
        getSupport().enableNotifications(builder, true);
        if (needsAuth) {
            builder.add(new SetDeviceStateAction(getDevice(), GBDevice.State.AUTHENTICATING, getContext()));
            // write key to miband2
            byte[] sendKey = org.apache.commons.lang3.ArrayUtils.addAll(new byte[]{MiBand2Service.AUTH_SEND_KEY, MiBand2Service.AUTH_BYTE}, getSecretKey());
            builder.write(getCharacteristic(MiBand2Service.UUID_CHARACTERISTIC_AUTH), sendKey);
        } else {
            builder.add(new SetDeviceStateAction(getDevice(), GBDevice.State.INITIALIZING, getContext()));
            // get random auth number
            builder.write(getCharacteristic(MiBand2Service.UUID_CHARACTERISTIC_AUTH), requestAuthNumber());
        }
    }

    private byte[] requestAuthNumber() {
        return new byte[]{MiBand2Service.AUTH_REQUEST_RANDOM_AUTH_NUMBER, MiBand2Service.AUTH_BYTE};
    }

    private byte[] getSecretKey() {
        return new byte[]{0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x40, 0x41, 0x42, 0x43, 0x44, 0x45};
    }

    @Override
    public TransactionBuilder performInitialized(String taskName) throws IOException {
        throw new UnsupportedOperationException("This IS the initialization class, you cannot call this method");
    }

    @Override
    public boolean onCharacteristicChanged(BluetoothGatt gatt,
                                           BluetoothGattCharacteristic characteristic) {
        UUID characteristicUUID = characteristic.getUuid();
        if (MiBand2Service.UUID_CHARACTERISTIC_AUTH.equals(characteristicUUID)) {
            try {
                byte[] value = characteristic.getValue();
                getSupport().logMessageContent(value);
                if (value[0] == MiBand2Service.AUTH_RESPONSE &&
                        value[1] == MiBand2Service.AUTH_SEND_KEY &&
                        value[2] == MiBand2Service.AUTH_SUCCESS) {
                    TransactionBuilder builder = createTransactionBuilder("Sending the secret key to the band");
                    builder.write(characteristic, requestAuthNumber());
                    getSupport().performImmediately(builder);
                } else if (value[0] == MiBand2Service.AUTH_RESPONSE &&
                        value[1] == MiBand2Service.AUTH_REQUEST_RANDOM_AUTH_NUMBER &&
                        value[2] == MiBand2Service.AUTH_SUCCESS) {
                    // md5??
                    byte[] eValue = handleAESAuth(value, getSecretKey());
                    byte[] responseValue = org.apache.commons.lang3.ArrayUtils.addAll(
                            new byte[]{MiBand2Service.AUTH_SEND_ENCRYPTED_AUTH_NUMBER, MiBand2Service.AUTH_BYTE}, eValue);

                    TransactionBuilder builder = createTransactionBuilder("Sending the encrypted random key to the band");
                    builder.write(characteristic, responseValue);
                    getSupport().setCurrentTimeWithService(builder);
                    getSupport().performImmediately(builder);
                } else if (value[0] == MiBand2Service.AUTH_RESPONSE &&
                        value[1] == MiBand2Service.AUTH_SEND_ENCRYPTED_AUTH_NUMBER &&
                        value[2] == MiBand2Service.AUTH_SUCCESS) {
                    TransactionBuilder builder = createTransactionBuilder("Authenticated, now initialize phase 2");
                    builder.add(new SetDeviceStateAction(getDevice(), GBDevice.State.INITIALIZING, getContext()));
                    getSupport().requestDeviceInfo(builder);
                    getSupport().phase2Initialize(builder);
                    getSupport().setInitialized(builder);
                    getSupport().performImmediately(builder);
                } else {
                    return super.onCharacteristicChanged(gatt, characteristic);
                }
            } catch (Exception e) {
                GB.toast(getContext(), "Error authenticating Mi Band 2", Toast.LENGTH_LONG, GB.ERROR, e);
            }
            return true;
        } else {
            LOG.info("Unhandled characteristic changed: " + characteristicUUID);
            return super.onCharacteristicChanged(gatt, characteristic);
        }
    }

    private TransactionBuilder createTransactionBuilder(String task) {
        TransactionBuilder builder = getSupport().createTransactionBuilder(task);
        builder.setGattCallback(this);
        return builder;
    }

    private byte[] getMD5(byte[] message) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        return md5.digest(message);
    }

    private byte[] handleAESAuth(byte[] value, byte[] secretKey) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
        byte[] mValue = Arrays.copyOfRange(value, 3, 19);
        Cipher ecipher = Cipher.getInstance("AES/ECB/NoPadding");
        SecretKeySpec newKey = new SecretKeySpec(secretKey, "AES");
        ecipher.init(Cipher.ENCRYPT_MODE, newKey);
        byte[] enc = ecipher.doFinal(mValue);
        return enc;
    }
}
