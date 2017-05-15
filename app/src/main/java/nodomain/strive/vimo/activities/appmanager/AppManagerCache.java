package nodomain.strive.vimo.activities.appmanager;

import java.util.List;

import nodomain.strive.vimo.impl.DeviceApp;

public class AppManagerCache extends ABAppManager {
    @Override
    public void refreshList() {
        appList.clear();
        appList.addAll(getCachedApps(null));
    }

    @Override
    protected boolean checkCache() {
        return true;
    }

    @Override
    protected List<DeviceApp> categoryAppSystem() {
        return null;
    }

    @Override
    public String getSortName() {
        return "pbwcacheorder.txt";
    }

    @Override
    protected boolean appFilter(DeviceApp deviceApp) {
        return true;
    }
}
