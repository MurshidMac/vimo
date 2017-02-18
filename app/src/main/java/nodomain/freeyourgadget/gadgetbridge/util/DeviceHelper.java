package nodomain.freeyourgadget.gadgetbridge.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.GBException;
import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.database.DBHandler;
import nodomain.freeyourgadget.gadgetbridge.database.DBHelper;
import nodomain.freeyourgadget.gadgetbridge.devices.DeviceCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.UnknownDeviceCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.hplus.HPlusCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.hplus.MakibesF68Coordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.liveview.LiveviewCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBand2Coordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandConst;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleCoordinator;
import nodomain.freeyourgadget.gadgetbridge.devices.vibratissimo.VibratissimoCoordinator;
import nodomain.freeyourgadget.gadgetbridge.entities.Device;
import nodomain.freeyourgadget.gadgetbridge.entities.DeviceAttributes;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;

public class DeviceHelper {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceHelper.class);

    private static final DeviceHelper instance = new DeviceHelper();

    public static DeviceHelper getInstance() {
        return instance;
    }

    // lazily created
    private List<DeviceCoordinator> coordinators;

    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        for (DeviceCoordinator coordinator : getAllCoordinators()) {
            DeviceType deviceType = coordinator.getSupportedType(candidate);
            if (deviceType.isSupported()) {
                return deviceType;
            }
        }
        return DeviceType.UNKNOWN;
    }

    public boolean getSupportedType(GBDevice device) {
        for (DeviceCoordinator coordinator : getAllCoordinators()) {
            if (coordinator.supports(device)) {
                return true;
            }
        }
        return false;
    }

    public GBDevice findAvailableDevice(String deviceAddress, Context context) {
        Set<GBDevice> availableDevices = getAvailableDevices(context);
        for (GBDevice availableDevice : availableDevices) {
            if (deviceAddress.equals(availableDevice.getAddress())) {
                return availableDevice;
            }
        }
        return null;
    }

    /**
     * Returns the list of all available devices that are supported by Gadgetbridge.
     * Note that no state is known about the returned devices. Even if one of those
     * devices is connected, it will report the default not-connected state.
     *
     * Clients interested in the "live" devices being managed should use the class
     * DeviceManager.
     * @param context
     * @return
     */
    public Set<GBDevice> getAvailableDevices(Context context) {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<GBDevice> availableDevices = new LinkedHashSet<GBDevice>();

        if (btAdapter == null) {
            GB.toast(context, context.getString(R.string.bluetooth_is_not_supported_), Toast.LENGTH_SHORT, GB.WARN);
        } else if (!btAdapter.isEnabled()) {
            GB.toast(context, context.getString(R.string.bluetooth_is_disabled_), Toast.LENGTH_SHORT, GB.WARN);
        }
        List<GBDevice> dbDevices = getDatabaseDevices();
        // these come first, as they have the most information already
        availableDevices.addAll(dbDevices);
        if (btAdapter != null) {
            List<GBDevice> bondedDevices = getBondedDevices(btAdapter);
            availableDevices.addAll(bondedDevices);
        }

        Prefs prefs = GBApplication.getPrefs();
        String miAddr = prefs.getString(MiBandConst.PREF_MIBAND_ADDRESS, "");
        if (miAddr.length() > 0) {
            GBDevice miDevice = new GBDevice(miAddr, "MI", DeviceType.MIBAND);
            availableDevices.add(miDevice);
        }

        String pebbleEmuAddr = prefs.getString("pebble_emu_addr", "");
        String pebbleEmuPort = prefs.getString("pebble_emu_port", "");
        if (pebbleEmuAddr.length() >= 7 && pebbleEmuPort.length() > 0) {
            GBDevice pebbleEmuDevice = new GBDevice(pebbleEmuAddr + ":" + pebbleEmuPort, "Pebble qemu", DeviceType.PEBBLE);
            availableDevices.add(pebbleEmuDevice);
        }
        return availableDevices;
    }

    public GBDevice toSupportedDevice(BluetoothDevice device) {
        GBDeviceCandidate candidate = new GBDeviceCandidate(device, GBDevice.RSSI_UNKNOWN, device.getUuids());
        return toSupportedDevice(candidate);
    }

    public GBDevice toSupportedDevice(GBDeviceCandidate candidate) {
        for (DeviceCoordinator coordinator : getAllCoordinators()) {
            if (coordinator.supports(candidate)) {
                return coordinator.createDevice(candidate);
            }
        }
        return null;
    }

    public DeviceCoordinator getCoordinator(GBDeviceCandidate device) {
        synchronized (this) {
            for (DeviceCoordinator coord : getAllCoordinators()) {
                if (coord.supports(device)) {
                    return coord;
                }
            }
        }
        return new UnknownDeviceCoordinator();
    }

    public DeviceCoordinator getCoordinator(GBDevice device) {
        synchronized (this) {
            for (DeviceCoordinator coord : getAllCoordinators()) {
                if (coord.supports(device)) {
                    return coord;
                }
            }
        }
        return new UnknownDeviceCoordinator();
    }

    public synchronized List<DeviceCoordinator> getAllCoordinators() {
        if (coordinators == null) {
            coordinators = createCoordinators();
        }
        return coordinators;
    }

    private List<DeviceCoordinator> createCoordinators() {
        List<DeviceCoordinator> result = new ArrayList<>();
        result.add(new MiBand2Coordinator()); // Note: MiBand2 must come before MiBand because detection is hacky, atm
        result.add(new MiBandCoordinator());
        result.add(new PebbleCoordinator());
        result.add(new VibratissimoCoordinator());
        result.add(new LiveviewCoordinator());
        result.add(new HPlusCoordinator());
        result.add(new MakibesF68Coordinator());

        return result;
    }

    private List<GBDevice> getDatabaseDevices() {
        List<GBDevice> result = new ArrayList<>();
        try (DBHandler lockHandler = GBApplication.acquireDB()) {
            List<Device> activeDevices = DBHelper.getActiveDevices(lockHandler.getDaoSession());
            for (Device dbDevice : activeDevices) {
                GBDevice gbDevice = toGBDevice(dbDevice);
                if (gbDevice != null && DeviceHelper.getInstance().getSupportedType(gbDevice)) {
                    result.add(gbDevice);
                }
            }
            return result;

        } catch (Exception e) {
            GB.toast("Error retrieving devices from database", Toast.LENGTH_SHORT, GB.ERROR);
            return Collections.emptyList();
        }
    }

    /**
     * Converts a known device from the database to a GBDevice.
     * Note: The device might not be supported anymore, so callers should verify that.
     * @param dbDevice
     * @return
     */
    public GBDevice toGBDevice(Device dbDevice) {
        DeviceType deviceType = DeviceType.fromKey(dbDevice.getType());
        GBDevice gbDevice = new GBDevice(dbDevice.getIdentifier(), dbDevice.getName(), deviceType);
        List<DeviceAttributes> deviceAttributesList = dbDevice.getDeviceAttributesList();
        if (deviceAttributesList.size() > 0) {
            gbDevice.setModel(dbDevice.getModel());
            DeviceAttributes attrs = deviceAttributesList.get(0);
            gbDevice.setFirmwareVersion(attrs.getFirmwareVersion1());
            gbDevice.setFirmwareVersion2(attrs.getFirmwareVersion2());
            gbDevice.setVolatileAddress(attrs.getVolatileIdentifier());
        }

        return gbDevice;
    }

    private List<GBDevice> getBondedDevices(BluetoothAdapter btAdapter) {
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        List<GBDevice> result = new ArrayList<>(pairedDevices.size());
        DeviceHelper deviceHelper = DeviceHelper.getInstance();
        for (BluetoothDevice pairedDevice : pairedDevices) {
            if (pairedDevice.getName() != null && (pairedDevice.getName().startsWith("Pebble-LE ") || pairedDevice.getName().startsWith("Pebble Time LE "))) {
                continue; // ignore LE Pebble (this is part of the main device now (volatileAddress)
            }
            GBDevice device = deviceHelper.toSupportedDevice(pairedDevice);
            if (device != null) {
                result.add(device);
            }
        }
        return result;
    }

    /**
     * Attempts to removing the bonding with the given device. Returns true
     * if bonding was supposedly successful and false if anything went wrong
     * @param device
     * @return
     */
    public boolean removeBond(GBDevice device) throws GBException {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            BluetoothDevice remoteDevice = defaultAdapter.getRemoteDevice(device.getAddress());
            if (remoteDevice != null) {
                try {
                    Method method = BluetoothDevice.class.getMethod("removeBond", (Class[]) null);
                    Object result = method.invoke(remoteDevice, (Object[]) null);
                    return Boolean.TRUE.equals(result);
                } catch (Exception e) {
                    throw new GBException("Error removing bond to device: " + device, e);
                }
            }
        }
        return false;
    }
}
