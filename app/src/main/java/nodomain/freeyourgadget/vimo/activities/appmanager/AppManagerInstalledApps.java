package nodomain.freeyourgadget.vimo.activities.appmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import nodomain.freeyourgadget.vimo.impl.DeviceApp;
import nodomain.freeyourgadget.vimo.service.devices.pebble.PebbleProtocol;
import nodomain.freeyourgadget.vimo.util.PebbleUtils;

public class AppManagerInstalledApps extends ABAppManager {

    @Override
    protected List<DeviceApp> categoryAppSystem() {
        List<DeviceApp> systemApps = new ArrayList<>();
        //systemApps.add(new DeviceApp(UUID.fromString("4dab81a6-d2fc-458a-992c-7a1f3b96a970"), "Sports (System)", "Pebble Inc.", "", DeviceApp.Type.APP_SYSTEM));
        //systemApps.add(new DeviceApp(UUID.fromString("cf1e816a-9db0-4511-bbb8-f60c48ca8fac"), "Golf (System)", "Pebble Inc.", "", DeviceApp.Type.APP_SYSTEM));
        systemApps.add(new DeviceApp(UUID.fromString("1f03293d-47af-4f28-b960-f2b02a6dd757"), "Music (System)", "Pebble Inc.", "", DeviceApp.Type.APP_SYSTEM));
        systemApps.add(new DeviceApp(PebbleProtocol.UUID_NOTIFICATIONS, "Notifications (System)", "Pebble Inc.", "", DeviceApp.Type.APP_SYSTEM));
        systemApps.add(new DeviceApp(UUID.fromString("67a32d95-ef69-46d4-a0b9-854cc62f97f9"), "Alarms (System)", "Pebble Inc.", "", DeviceApp.Type.APP_SYSTEM));
        systemApps.add(new DeviceApp(UUID.fromString("18e443ce-38fd-47c8-84d5-6d0c775fbe55"), "Watchfaces (System)", "Pebble Inc.", "", DeviceApp.Type.APP_SYSTEM));

        if (mGBDevice != null) {
            if (PebbleUtils.hasHealth(mGBDevice.getModel())) {
                systemApps.add(new DeviceApp(UUID.fromString("0863fc6a-66c5-4f62-ab8a-82ed00a98b5d"), "Send Text (System)", "Pebble Inc.", "", DeviceApp.Type.APP_SYSTEM));
                systemApps.add(new DeviceApp(PebbleProtocol.UUID_PEBBLE_HEALTH, "Health (System)", "Pebble Inc.", "", DeviceApp.Type.APP_SYSTEM));
            }
            if (PebbleUtils.hasHRM(mGBDevice.getModel())) {
                systemApps.add(new DeviceApp(PebbleProtocol.UUID_WORKOUT, "Workout (System)", "Pebble Inc.", "", DeviceApp.Type.APP_SYSTEM));
            }
            if (PebbleUtils.getFwMajor(mGBDevice.getFirmwareVersion()) >= 4) {
                systemApps.add(new DeviceApp(PebbleProtocol.UUID_WEATHER, "Weather (System)", "Pebble Inc.", "", DeviceApp.Type.APP_SYSTEM));
            }
        }

        return systemApps;
    }

    @Override
    protected boolean checkCache() {
        return false;
    }

    @Override
    protected String getSortName() {
        return mGBDevice.getAddress() + ".watchapps";
    }

    @Override
    protected void onChangedAppOrder() {
        super.onChangedAppOrder();
        sendOrderToDevice(mGBDevice.getAddress() + ".watchfaces");
    }

    @Override
    protected boolean appFilter(DeviceApp deviceApp) {
        return deviceApp.getType() == DeviceApp.Type.APP_ACTIVITYTRACKER || deviceApp.getType() == DeviceApp.Type.APP_GENERIC;
    }
}
