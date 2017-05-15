package nodomain.strive.vimo.devices.vibratissimo;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import nodomain.strive.vimo.GBException;
import nodomain.freeyourgadget.vimo.R;      // R for Data mining
import nodomain.strive.vimo.activities.VibrationActivity;
import nodomain.strive.vimo.devices.AbstractDeviceCoordinator;
import nodomain.strive.vimo.devices.InstallHandler;
import nodomain.strive.vimo.devices.SampleProvider;
import nodomain.strive.vimo.entities.DaoSession;
import nodomain.strive.vimo.entities.Device;
import nodomain.strive.vimo.impl.GBDevice;
import nodomain.strive.vimo.impl.GBDeviceCandidate;
import nodomain.strive.vimo.model.ActivitySample;
import nodomain.strive.vimo.model.DeviceType;

//
public class VibratissimoCoordinator extends AbstractDeviceCoordinator {
    @Override
    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        String name = candidate.getDevice().getName();
        if (name != null && name.startsWith("Vibratissimo")) {
            return DeviceType.VIBRATISSIMO;
        }
        return DeviceType.UNKNOWN;
    }

    @Override
    public DeviceType getDeviceType() {
        return DeviceType.VIBRATISSIMO;
    }

    @Override
    public Class<? extends Activity> getPairingActivity() {
        return null;
    }

    @Override
    public Class<? extends Activity> getPrimaryActivity() {
        return VibrationActivity.class;
    }

    @Override
    public InstallHandler findInstallHandler(Uri uri, Context context) {
        return null;
    }

    @Override
    public boolean supportsActivityDataFetching() {
        return false;
    }

    @Override
    public boolean supportsActivityTracking() {
        return false;
    }

    @Override
    public SampleProvider<? extends ActivitySample> getSampleProvider(GBDevice device, DaoSession session) {
        return null;
    }

    @Override
    public boolean supportsScreenshots() {
        return false;
    }

    @Override
    public boolean supportsAlarmConfiguration() {
        return false;
    }

    @Override
    public boolean supportsSmartWakeup(GBDevice device) {
        return false;
    }

    @Override
    public boolean supportsHeartRateMeasurement(GBDevice device) {
        return false;
    }

    @Override
    public int getTapString() {
        return R.string.tap_connected_device_for_vibration;
    }

    @Override
    public String getManufacturer() {
        return "Amor AG";
    }

    @Override
    public boolean supportsAppsManagement() {
        return false;
    }

    @Override
    public Class<? extends Activity> getAppsManagementActivity() {
        return null;
    }

    @Override
    protected void deleteDevice(@NonNull GBDevice gbDevice, @NonNull Device device, @NonNull DaoSession session) throws GBException {
        // nothing to delete, yet
    }
}
