package nodomain.freeyourgadget.vimo.activities.appmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import nodomain.freeyourgadget.vimo.impl.DeviceApp;

public class AppManagerInstalledWatchfaces extends ABAppManager {

    @Override
    protected List<DeviceApp> categoryAppSystem() {
        List<DeviceApp> systemWatchfaces = new ArrayList<>();
        systemWatchfaces.add(new DeviceApp(UUID.fromString("8f3c8686-31a1-4f5f-91f5-01600c9bdc59"), "Tic Toc (System)", "Pebble Inc.", "", DeviceApp.Type.WATCHFACE_SYSTEM));
        systemWatchfaces.add(new DeviceApp(UUID.fromString("3af858c3-16cb-4561-91e7-f1ad2df8725f"), "Kickstart (System)", "Pebble Inc.", "", DeviceApp.Type.WATCHFACE_SYSTEM));
        return systemWatchfaces;
    }

    @Override
    protected boolean checkCache() {
        return false;
    }

    @Override
    protected String getSortName() {
        return mGBDevice.getAddress() + ".watchfaces";
    }

    @Override
    protected void onChangedAppOrder() {
        super.onChangedAppOrder();
        sendOrderToDevice(mGBDevice.getAddress() + ".watchapps");
    }

    @Override
    protected boolean appFilter(DeviceApp deviceApp) {
        if (deviceApp.getType() == DeviceApp.Type.WATCHFACE) {
            return true;
        }
        return false;
    }
}
