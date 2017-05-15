package nodomain.strive.vimo.devices.miband;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanFilter;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;

import nodomain.strive.vimo.GBApplication;
import nodomain.freeyourgadget.vimo.R;
import nodomain.strive.vimo.devices.InstallHandler;
import nodomain.strive.vimo.devices.SampleProvider;
import nodomain.strive.vimo.devices.miband2.MiBand2FWInstallHandler;
import nodomain.strive.vimo.entities.AbstractActivitySample;
import nodomain.strive.vimo.entities.DaoSession;
import nodomain.strive.vimo.impl.GBDevice;
import nodomain.strive.vimo.impl.GBDeviceCandidate;
import nodomain.strive.vimo.model.DeviceType;
import nodomain.strive.vimo.util.Prefs;

public class MiBand2Coordinator extends MiBandCoordinator {
    private static final Logger LOG = LoggerFactory.getLogger(MiBand2Coordinator.class);

    @Override
    public DeviceType getDeviceType() {
        return DeviceType.MIBAND2;
    }

    @NonNull
    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Collection<? extends ScanFilter> createBLEScanFilters() {
        ParcelUuid mi2Service = new ParcelUuid(MiBandService.UUID_SERVICE_MIBAND2_SERVICE);
        ScanFilter filter = new ScanFilter.Builder().setServiceUuid(mi2Service).build();
        return Collections.singletonList(filter);
    }

    @NonNull
    @Override
    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        if (candidate.supportsService(MiBand2Service.UUID_SERVICE_MIBAND2_SERVICE)) {
            return DeviceType.MIBAND2;
        }

        // and a heuristic for now
        try {
            BluetoothDevice device = candidate.getDevice();
//            if (isHealthWearable(device)) {
                String name = device.getName();
                if (name != null && name.equalsIgnoreCase(MiBandConst.MI_BAND2_NAME)) {
                    return DeviceType.MIBAND2;
                }
//            }
        } catch (Exception ex) {
            LOG.error("unable to check device support", ex);
        }
        return DeviceType.UNKNOWN;

    }

    @Override
    public boolean supportsHeartRateMeasurement(GBDevice device) {
        return true;
    }

    @Override
    public boolean supportsAlarmConfiguration() {
        return true;
    }

    @Override
    public boolean supportsActivityDataFetching() {
        return true;
    }

    @Override
    public SampleProvider<? extends AbstractActivitySample> getSampleProvider(GBDevice device, DaoSession session) {
        return new MiBand2SampleProvider(device, session);
    }

    public static DateTimeDisplay getDateDisplay(Context context) throws IllegalArgumentException {
        Prefs prefs = GBApplication.getPrefs();
        String dateFormatTime = context.getString(R.string.p_dateformat_time);
        if (dateFormatTime.equals(prefs.getString(MiBandConst.PREF_MI2_DATEFORMAT, dateFormatTime))) {
            return DateTimeDisplay.TIME;
        }
        return DateTimeDisplay.DATE_TIME;
    }

    public static boolean getActivateDisplayOnLiftWrist() {
        Prefs prefs = GBApplication.getPrefs();
        return prefs.getBoolean(MiBandConst.PREF_MI2_ACTIVATE_DISPLAY_ON_LIFT, true);
    }

    @Override
    public InstallHandler findInstallHandler(Uri uri, Context context) {
        MiBand2FWInstallHandler handler = new MiBand2FWInstallHandler(uri, context);
        return handler.isValid() ? handler : null;
    }

    @Override
    public boolean supportsSmartWakeup(GBDevice device) {
        return false;
    }
}
