package vimo.devices.miband;

import android.annotation.TargetApi;
import android.app.Activity;
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

import de.greenrobot.dao.query.QueryBuilder;
import vimo.GBApplication;
import vimo.GBException;
import nodomain.freeyourgadget.gadgetbridge.R;
import vimo.activities.charts.ChartsActivity;
import vimo.devices.AbstractDeviceCoordinator;
import vimo.devices.InstallHandler;
import vimo.devices.SampleProvider;
import vimo.entities.AbstractActivitySample;
import vimo.entities.DaoSession;
import vimo.entities.Device;
import vimo.entities.MiBandActivitySampleDao;
import vimo.impl.GBDevice;
import vimo.impl.GBDeviceCandidate;
import vimo.model.ActivityUser;
import vimo.model.DeviceType;
import vimo.util.Prefs;

public class MiBandCoordinator extends AbstractDeviceCoordinator {
    private static final Logger LOG = LoggerFactory.getLogger(MiBandCoordinator.class);

    public MiBandCoordinator() {
    }

    @NonNull
    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Collection<? extends ScanFilter> createBLEScanFilters() {
        ParcelUuid mi1Service = new ParcelUuid(MiBandService.UUID_SERVICE_MIBAND_SERVICE);
        ScanFilter filter = new ScanFilter.Builder().setServiceUuid(mi1Service).build();
        return Collections.singletonList(filter);
    }

    @NonNull
    @Override
    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        String macAddress = candidate.getMacAddress().toUpperCase();
        if (macAddress.startsWith(MiBandService.MAC_ADDRESS_FILTER_1_1A)
                || macAddress.startsWith(MiBandService.MAC_ADDRESS_FILTER_1S)) {
            return DeviceType.MIBAND;
        }
        if (candidate.supportsService(MiBandService.UUID_SERVICE_MIBAND_SERVICE)
                && !candidate.supportsService(MiBandService.UUID_SERVICE_MIBAND2_SERVICE)) {
            return DeviceType.MIBAND;
        }
        // and a heuristic
        try {
            BluetoothDevice device = candidate.getDevice();
            if (isHealthWearable(device)) {
                String name = device.getName();
                if (name != null && name.toUpperCase().startsWith(MiBandConst.MI_GENERAL_NAME_PREFIX.toUpperCase())) {
                    return DeviceType.MIBAND;
                }
            }
        } catch (Exception ex) {
            LOG.error("unable to check device support", ex);
        }
        return DeviceType.UNKNOWN;
    }

    @Override
    protected void deleteDevice(GBDevice gbDevice, Device device, DaoSession session) throws GBException {
        Long deviceId = device.getId();
        QueryBuilder<?> qb = session.getMiBandActivitySampleDao().queryBuilder();
        qb.where(MiBandActivitySampleDao.Properties.DeviceId.eq(deviceId)).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    @Override
    public DeviceType getDeviceType() {
        return DeviceType.MIBAND;
    }

    @Override
    public Class<? extends Activity> getPairingActivity() {
        return MiBandPairingActivity.class;
    }

    @Override
    public Class<? extends Activity> getPrimaryActivity() {
        return ChartsActivity.class;
    }

    @Override
    public SampleProvider<? extends AbstractActivitySample> getSampleProvider(GBDevice device, DaoSession session) {
        return new MiBandSampleProvider(device, session);
    }

    @Override
    public InstallHandler findInstallHandler(Uri uri, Context context) {
        MiBandFWInstallHandler handler = new MiBandFWInstallHandler(uri, context);
        return handler.isValid() ? handler : null;
    }

    @Override
    public boolean supportsActivityDataFetching() {
        return true;
    }

    @Override
    public boolean supportsScreenshots() {
        return false;
    }

    @Override
    public boolean supportsAlarmConfiguration() {
        return true;
    }

    @Override
    public boolean supportsSmartWakeup(GBDevice device) {
        return true;
    }

    @Override
    public boolean supportsActivityTracking() {
        return true;
    }

    @Override
    public int getTapString() {
        return R.string.tap_connected_device_for_activity;
    }

    @Override
    public String getManufacturer() {
        return "Xiaomi";
    }

    @Override
    public boolean supportsAppsManagement() {
        return false;
    }

    @Override
    public Class<? extends Activity> getAppsManagementActivity() {
        return null;
    }

    public static boolean hasValidUserInfo() {
        String dummyMacAddress = MiBandService.MAC_ADDRESS_FILTER_1_1A + ":00:00:00";
        try {
            UserInfo userInfo = getConfiguredUserInfo(dummyMacAddress);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    /**
     * Returns the configured user info, or, if that is not available or invalid,
     * a default user info.
     *
     * @param miBandAddress
     */
    public static UserInfo getAnyUserInfo(String miBandAddress) {
        try {
            return getConfiguredUserInfo(miBandAddress);
        } catch (Exception ex) {
            LOG.error("Error creating user info from settings, using default user instead: " + ex);
            return UserInfo.getDefault(miBandAddress);
        }
    }

    /**
     * Returns the user info from the user configured data in the preferences.
     *
     * @param miBandAddress
     * @throws IllegalArgumentException when the user info can not be created
     */
    public static UserInfo getConfiguredUserInfo(String miBandAddress) throws IllegalArgumentException {
        ActivityUser activityUser = new ActivityUser();
        Prefs prefs = GBApplication.getPrefs();

        UserInfo info = UserInfo.create(
                miBandAddress,
                prefs.getString(MiBandConst.PREF_USER_ALIAS, null),
                activityUser.getGender(),
                activityUser.getAge(),
                activityUser.getHeightCm(),
                activityUser.getWeightKg(),
                0
        );
        return info;
    }

    public static int getWearLocation(String miBandAddress) throws IllegalArgumentException {
        int location = 0; //left hand
        Prefs prefs = GBApplication.getPrefs();
        if ("right".equals(prefs.getString(MiBandConst.PREF_MIBAND_WEARSIDE, "left"))) {
            location = 1; // right hand
        }
        return location;
    }

	public static int getDeviceTimeOffsetHours() throws IllegalArgumentException {
		Prefs prefs = GBApplication.getPrefs();
		return prefs.getInt(MiBandConst.PREF_MIBAND_DEVICE_TIME_OFFSET_HOURS, 0);
	}

    public static boolean getHeartrateSleepSupport(String miBandAddress) throws IllegalArgumentException {
        Prefs prefs = GBApplication.getPrefs();
        return prefs.getBoolean(MiBandConst.PREF_MIBAND_USE_HR_FOR_SLEEP_DETECTION, false);
    }

    public static int getFitnessGoal(String miBandAddress) throws IllegalArgumentException {
        Prefs prefs = GBApplication.getPrefs();
        return prefs.getInt(MiBandConst.PREF_MIBAND_FITNESS_GOAL, 10000);
    }

    public static int getReservedAlarmSlots(String miBandAddress) throws IllegalArgumentException {
        Prefs prefs = GBApplication.getPrefs();
        return prefs.getInt(MiBandConst.PREF_MIBAND_RESERVE_ALARM_FOR_CALENDAR, 0);
    }

    @Override
    public boolean supportsHeartRateMeasurement(GBDevice device) {
        String hwVersion = device.getModel();
        return isMi1S(hwVersion) || isMiPro(hwVersion);
    }

    private boolean isMi1S(String hardwareVersion) {
        return MiBandConst.MI_1S.equals(hardwareVersion);
    }

    private boolean isMiPro(String hardwareVersion) {
        return MiBandConst.MI_PRO.equals(hardwareVersion);
    }
}
