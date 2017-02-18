package nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.deviceinfo;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattCharacteristic;
import nodomain.freeyourgadget.gadgetbridge.service.btle.GattService;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.btle.profiles.AbstractBleProfile;

public class DeviceInfoProfile<T extends AbstractBTLEDeviceSupport> extends AbstractBleProfile {
    private static final Logger LOG = LoggerFactory.getLogger(DeviceInfoProfile.class);

    private static final String ACTION_PREFIX = DeviceInfoProfile.class.getName() + "_";

    public static final String ACTION_DEVICE_INFO = ACTION_PREFIX + "DEVICE_INFO";
    public static final String EXTRA_DEVICE_INFO = "DEVICE_INFO";

    public static final UUID SERVICE_UUID = GattService.UUID_SERVICE_DEVICE_INFORMATION;

    public static final UUID UUID_CHARACTERISTIC_MANUFACTURER_NAME_STRING = GattCharacteristic.UUID_CHARACTERISTIC_MANUFACTURER_NAME_STRING;
    public static final UUID UUID_CHARACTERISTIC_MODEL_NUMBER_STRING = GattCharacteristic.UUID_CHARACTERISTIC_MODEL_NUMBER_STRING;
    public static final UUID UUID_CHARACTERISTIC_SERIAL_NUMBER_STRING = GattCharacteristic.UUID_CHARACTERISTIC_SERIAL_NUMBER_STRING;
    public static final UUID UUID_CHARACTERISTIC_HARDWARE_REVISION_STRING = GattCharacteristic.UUID_CHARACTERISTIC_HARDWARE_REVISION_STRING;
    public static final UUID UUID_CHARACTERISTIC_FIRMWARE_REVISION_STRING = GattCharacteristic.UUID_CHARACTERISTIC_FIRMWARE_REVISION_STRING;
    public static final UUID UUID_CHARACTERISTIC_SOFTWARE_REVISION_STRING = GattCharacteristic.UUID_CHARACTERISTIC_SOFTWARE_REVISION_STRING;
    public static final UUID UUID_CHARACTERISTIC_SYSTEM_ID = GattCharacteristic.UUID_CHARACTERISTIC_SYSTEM_ID;
    public static final UUID UUID_CHARACTERISTIC_IEEE_11073_20601_REGULATORY_CERTIFICATION_DATA_LIST = GattCharacteristic.UUID_CHARACTERISTIC_IEEE_11073_20601_REGULATORY_CERTIFICATION_DATA_LIST;
    public static final UUID UUID_CHARACTERISTIC_PNP_ID = GattCharacteristic.UUID_CHARACTERISTIC_PNP_ID;
    private final DeviceInfo deviceInfo = new DeviceInfo();

    public DeviceInfoProfile(T support) {
        super(support);
    }

    public void requestDeviceInfo(TransactionBuilder builder) {
        builder.read(getCharacteristic(UUID_CHARACTERISTIC_MANUFACTURER_NAME_STRING))
                .read(getCharacteristic(UUID_CHARACTERISTIC_MODEL_NUMBER_STRING))
                .read(getCharacteristic(UUID_CHARACTERISTIC_SERIAL_NUMBER_STRING))
                .read(getCharacteristic(UUID_CHARACTERISTIC_HARDWARE_REVISION_STRING))
                .read(getCharacteristic(UUID_CHARACTERISTIC_FIRMWARE_REVISION_STRING))
                .read(getCharacteristic(UUID_CHARACTERISTIC_SOFTWARE_REVISION_STRING))
                .read(getCharacteristic(UUID_CHARACTERISTIC_SYSTEM_ID))
                .read(getCharacteristic(UUID_CHARACTERISTIC_IEEE_11073_20601_REGULATORY_CERTIFICATION_DATA_LIST))
                .read(getCharacteristic(UUID_CHARACTERISTIC_PNP_ID));
    }

    @Override
    public boolean onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            UUID charUuid = characteristic.getUuid();
            if (charUuid.equals(UUID_CHARACTERISTIC_MANUFACTURER_NAME_STRING)) {
                handleManufacturerName(gatt, characteristic);
                return true;
            } else if (charUuid.equals(UUID_CHARACTERISTIC_MODEL_NUMBER_STRING)) {
                handleModelNumber(gatt, characteristic);
                return true;
            } else if (charUuid.equals(UUID_CHARACTERISTIC_SERIAL_NUMBER_STRING)) {
                handleSerialNumber(gatt, characteristic);
                return true;
            } else if (charUuid.equals(UUID_CHARACTERISTIC_HARDWARE_REVISION_STRING)) {
                handleHardwareRevision(gatt, characteristic);
                return true;
            } else if (charUuid.equals(UUID_CHARACTERISTIC_FIRMWARE_REVISION_STRING)) {
                handleFirmwareRevision(gatt, characteristic);
                return true;
            } else if (charUuid.equals(UUID_CHARACTERISTIC_SOFTWARE_REVISION_STRING)) {
                handleSoftwareRevision(gatt, characteristic);
                return true;
            } else if (charUuid.equals(UUID_CHARACTERISTIC_SYSTEM_ID)) {
                handleSystemId(gatt, characteristic);
                return true;
            } else if (charUuid.equals(UUID_CHARACTERISTIC_IEEE_11073_20601_REGULATORY_CERTIFICATION_DATA_LIST)) {
                handleRegulatoryCertificationData(gatt, characteristic);
                return true;
            } else if (charUuid.equals(UUID_CHARACTERISTIC_PNP_ID)) {
                handlePnpId(gatt, characteristic);
                return true;
            } else {
                LOG.info("Unexpected onCharacteristicRead: " + GattCharacteristic.toString(characteristic));
            }
        } else {
            LOG.warn("error reading from characteristic:" + GattCharacteristic.toString(characteristic));
        }
        return false;
    }


    private void handleManufacturerName(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        String name = characteristic.getStringValue(0);
        deviceInfo.setManufacturerName(name);
        notify(createIntent(deviceInfo));
    }

    private void handleModelNumber(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        String modelNumber = characteristic.getStringValue(0);
        deviceInfo.setModelNumber(modelNumber);
        notify(createIntent(deviceInfo));
    }
    private void handleSerialNumber(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        String serialNumber = characteristic.getStringValue(0);
        deviceInfo.setSerialNumber(serialNumber);
        notify(createIntent(deviceInfo));
    }

    private void handleHardwareRevision(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        String hardwareRevision = characteristic.getStringValue(0);
        deviceInfo.setHardwareRevision(hardwareRevision);
        notify(createIntent(deviceInfo));
    }

    private void handleFirmwareRevision(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        String firmwareRevision = characteristic.getStringValue(0);
        deviceInfo.setFirmwareRevision(firmwareRevision);
        notify(createIntent(deviceInfo));
    }

    private void handleSoftwareRevision(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        String softwareRevision = characteristic.getStringValue(0);
        deviceInfo.setSoftwareRevision(softwareRevision);
        notify(createIntent(deviceInfo));
    }

    private void handleSystemId(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        String systemId = characteristic.getStringValue(0);
        deviceInfo.setSystemId(systemId);
        notify(createIntent(deviceInfo));
    }

    private void handleRegulatoryCertificationData(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        // TODO: regulatory certification data list not supported yet
//        String regulatoryCertificationData = characteristic.getStringValue(0);
//        deviceInfo.setRegulatoryCertificationDataList(regulatoryCertificationData);
//        notify(createIntent(deviceInfo));
    }

    private void handlePnpId(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        byte[] value = characteristic.getValue();
        if (value.length == 7) {
//            int vendorSource
//
//            deviceInfo.setPnpId(pnpId);
            notify(createIntent(deviceInfo));
        } else {
            // TODO: LOG warning
        }
    }

    private Intent createIntent(DeviceInfo deviceInfo) {
        Intent intent = new Intent(ACTION_DEVICE_INFO);
        intent.putExtra(EXTRA_DEVICE_INFO, deviceInfo); // TODO: broadcast a clone of the info
        return intent;
    }

}
