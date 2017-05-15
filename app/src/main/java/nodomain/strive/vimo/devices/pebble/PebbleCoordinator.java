package nodomain.strive.vimo.devices.pebble;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import de.greenrobot.dao.query.QueryBuilder;
import nodomain.strive.vimo.GBApplication;
import nodomain.strive.vimo.GBException;
import nodomain.freeyourgadget.vimo.R;
import nodomain.strive.vimo.activities.appmanager.AppManagerActivity;
import nodomain.strive.vimo.devices.AbstractDeviceCoordinator;
import nodomain.strive.vimo.devices.InstallHandler;
import nodomain.strive.vimo.devices.SampleProvider;
import nodomain.strive.vimo.entities.AbstractActivitySample;
import nodomain.strive.vimo.entities.DaoSession;
import nodomain.strive.vimo.entities.Device;
import nodomain.strive.vimo.entities.PebbleHealthActivityOverlayDao;
import nodomain.strive.vimo.entities.PebbleHealthActivitySampleDao;
import nodomain.strive.vimo.entities.PebbleMisfitSampleDao;
import nodomain.strive.vimo.entities.PebbleMorpheuzSampleDao;
import nodomain.strive.vimo.impl.GBDevice;
import nodomain.strive.vimo.impl.GBDeviceCandidate;
import nodomain.strive.vimo.model.DeviceType;
import nodomain.strive.vimo.util.PebbleUtils;
import nodomain.strive.vimo.util.Prefs;

public class PebbleCoordinator extends AbstractDeviceCoordinator {
    public PebbleCoordinator() {
    }

    @Override
    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        String name = candidate.getDevice().getName();
        if (name != null && name.startsWith("Pebble")) {
            return DeviceType.PEBBLE;
        }
        return DeviceType.UNKNOWN;
    }

    @Override
    public DeviceType getDeviceType() {
        return DeviceType.PEBBLE;
    }

    @Override
    public Class<? extends Activity> getPairingActivity() {
        return PebblePairingActivity.class;
    }

    @Override
    public Class<? extends Activity> getPrimaryActivity() {
        return AppManagerActivity.class;
    }

    @Override
    protected void deleteDevice(GBDevice gbDevice, Device device, DaoSession session) throws GBException {
        Long deviceId = device.getId();

        QueryBuilder<?> qb = null;
        qb.where(PebbleHealthActivitySampleDao.Properties.DeviceId.eq(deviceId)).buildDelete().executeDeleteWithoutDetachingEntities();
        //qb = session.getPebbleHealthActivityOverlayDao().queryBuilder();
        qb.where(PebbleHealthActivityOverlayDao.Properties.DeviceId.eq(deviceId)).buildDelete().executeDeleteWithoutDetachingEntities();
        //qb = session.getPebbleMisfitSampleDao().queryBuilder();
        qb.where(PebbleMisfitSampleDao.Properties.DeviceId.eq(deviceId)).buildDelete().executeDeleteWithoutDetachingEntities();
        //qb = session.getPebbleMorpheuzSampleDao().queryBuilder();
        qb.where(PebbleMorpheuzSampleDao.Properties.DeviceId.eq(deviceId)).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    @Override
    public SampleProvider<? extends AbstractActivitySample> getSampleProvider(GBDevice device, DaoSession session) {
        Prefs prefs = GBApplication.getPrefs();
        int activityTracker = prefs.getInt("pebble_activitytracker", SampleProvider.PROVIDER_PEBBLE_HEALTH);
        switch (activityTracker) {
            case SampleProvider.PROVIDER_PEBBLE_HEALTH:
                return new PebbleHealthSampleProvider(device, session);
            case SampleProvider.PROVIDER_PEBBLE_MISFIT:
                return new PebbleMisfitSampleProvider(device, session);
            case SampleProvider.PROVIDER_PEBBLE_MORPHEUZ:
                return new PebbleMorpheuzSampleProvider(device, session);
            default:
                return new PebbleHealthSampleProvider(device, session);
        }
    }

    @Override
    public InstallHandler findInstallHandler(Uri uri, Context context) {
        PBWInstallHandler installHandler = new PBWInstallHandler(uri, context);
        return installHandler.isValid() ? installHandler : null;
    }

    @Override
    public boolean supportsActivityDataFetching() {
        return false;
    }

    @Override
    public boolean supportsActivityTracking() {
        return true;
    }

    @Override
    public boolean supportsScreenshots() {
        return true;
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
        return PebbleUtils.hasHRM(device.getModel());
    }

    @Override
    public int getTapString() {
        return R.string.tap_connected_device_for_app_mananger;
    }

    @Override
    public String getManufacturer() {
        return "Pebble";
    }

    @Override
    public boolean supportsAppsManagement() {
        return true;
    }

    @Override
    public Class<? extends Activity> getAppsManagementActivity() {
        return AppManagerActivity.class;
    }
}
