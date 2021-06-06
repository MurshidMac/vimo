package vimo.activities.appmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.woxthebox.draglistview.DragListView;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import vimo.GBApplication;
import com.vimo.R;
import vimo.activities.ExternalPebbleJSActivity;
import vimo.adapter.GBDeviceAppAdapter;
import vimo.impl.DeviceApp;
import vimo.impl.GBDevice;
import vimo.model.DeviceService;
import vimo.service.devices.pebble.PebbleProtocol;
import vimo.util.FileUtils;
import vimo.util.PebbleUtils;

// Initial App Manager for the Project Vimo
// Extends the ab class fragments
public abstract class ABAppManager extends Fragment {
    // Refreshing the App List For New Devices
    public static final String ACTION_DEVICE_REFRESH_APPLIST = "nodomain.freeyourgadget.vimo.appmanager.action.refresh_applist";
    // Generating Loggers for Usage
    private static final Logger LOGGER = LoggerFactory.getLogger(ABAppManager.class);
    // AB List for the Devices we tend to Use
    protected abstract List<DeviceApp> categoryAppSystem();
    // Sorting the File
    protected abstract String getSortName();
    // checking for caches
    protected abstract boolean checkCache();
    // Filter app for the devices
    protected abstract boolean appFilter(DeviceApp deviceApp);

    protected void onChangedAppOrder() {
        List<UUID> uuidList = new ArrayList<>();
        for (DeviceApp deviceApp : mGBDeviceAppAdapter.getItemList()) {
            uuidList.add(deviceApp.getUUID());
        }
        AppManagerActivity.rewriteAppOrderFile(getSortName(), uuidList);
    }

    protected void refreshList() {
        appList.clear();
        ArrayList uuids = AppManagerActivity.getUuidsFromFile(getSortName());
        List<DeviceApp> systemApps = categoryAppSystem();
        boolean needsRewrite = false;
        for (DeviceApp systemApp : systemApps) {
            if (!uuids.contains(systemApp.getUUID())) {
                uuids.add(systemApp.getUUID());
                needsRewrite = true;
            }
        }
        if (needsRewrite) {
            AppManagerActivity.rewriteAppOrderFile(getSortName(), uuids);
        }
        appList.addAll(getCachedApps(uuids));
    }

    private void refreshListFromPebble(Intent intent) {
        appList.clear();
        int appCount = intent.getIntExtra("app_count", 0);
        for (Integer i = 0; i < appCount; i++) {
            String appName = intent.getStringExtra("app_name" + i.toString());
            String appCreator = intent.getStringExtra("app_creator" + i.toString());
            UUID uuid = UUID.fromString(intent.getStringExtra("app_uuid" + i.toString()));
            DeviceApp.Type appType = DeviceApp.Type.values()[intent.getIntExtra("app_type" + i.toString(), 0)];

            DeviceApp app = new DeviceApp(uuid, appName, appCreator, "", appType);
            app.setOnDevice(true);
            if (appFilter(app)) {
                appList.add(app);
            }
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_DEVICE_REFRESH_APPLIST)) {
                if (intent.hasExtra("app_count")) {
                    LOGGER.info("got app info from pebble");
                    if (!checkCache()) {
                        LOGGER.info("will refresh list based on data from pebble");
                        refreshListFromPebble(intent);
                    }
                } else if (PebbleUtils.getFwMajor(mGBDevice.getFirmwareVersion()) >= 3 || checkCache()) {
                    refreshList();
                }
                mGBDeviceAppAdapter.notifyDataSetChanged();
            }
        }
    };

    private DragListView appListView;
    protected final List<DeviceApp> appList = new ArrayList<>();
    private GBDeviceAppAdapter mGBDeviceAppAdapter;
    protected GBDevice mGBDevice = null;

    protected List<DeviceApp> getCachedApps(List<UUID> uuids) {
        List<DeviceApp> cachedAppList = new ArrayList<>();
        File cachePath;
        try {
            cachePath = new File(FileUtils.getExternalFilesDir().getPath() + "/pbw-cache");
        } catch (IOException e) {
            LOGGER.warn("could not get external dir while reading pbw cache.");
            return cachedAppList;
        }

        File[] files;
        if (uuids == null) {
            files = cachePath.listFiles();
        } else {
            files = new File[uuids.size()];
            int index = 0;
            for (UUID uuid : uuids) {
                files[index++] = new File(uuid.toString() + ".pbw");
            }
        }
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".pbw")) {
                    String baseName = file.getName().substring(0, file.getName().length() - 4);
                    //metadata
                    File jsonFile = new File(cachePath, baseName + ".json");
                    //configuration
                    File configFile = new File(cachePath, baseName + "_config.js");
                    try {
                        String jsonstring = FileUtils.getStringFromFile(jsonFile);
                        JSONObject json = new JSONObject(jsonstring);
                        cachedAppList.add(new DeviceApp(json, configFile.exists()));
                    } catch (Exception e) {
                        LOGGER.info("could not read json file for " + baseName);
                        //FIXME: this is really ugly, if we do not find system uuids in pbw cache add them manually. Also duplicated code
                        switch (baseName) {
                            case "8f3c8686-31a1-4f5f-91f5-01600c9bdc59":
                                cachedAppList.add(new DeviceApp(UUID.fromString(baseName), "Tic Toc (System)", "Pebble Inc.", "", DeviceApp.Type.WATCHFACE_SYSTEM));
                                break;
                            case "1f03293d-47af-4f28-b960-f2b02a6dd757":
                                cachedAppList.add(new DeviceApp(UUID.fromString(baseName), "Music (System)", "Pebble Inc.", "", DeviceApp.Type.APP_SYSTEM));
                                break;
                            case "b2cae818-10f8-46df-ad2b-98ad2254a3c1":
                                cachedAppList.add(new DeviceApp(UUID.fromString(baseName), "Notifications (System)", "Pebble Inc.", "", DeviceApp.Type.APP_SYSTEM));
                                break;
                            case "67a32d95-ef69-46d4-a0b9-854cc62f97f9":
                                cachedAppList.add(new DeviceApp(UUID.fromString(baseName), "Alarms (System)", "Pebble Inc.", "", DeviceApp.Type.APP_SYSTEM));
                                break;
                            case "18e443ce-38fd-47c8-84d5-6d0c775fbe55":
                                cachedAppList.add(new DeviceApp(UUID.fromString(baseName), "Watchfaces (System)", "Pebble Inc.", "", DeviceApp.Type.APP_SYSTEM));
                                break;
                            case "0863fc6a-66c5-4f62-ab8a-82ed00a98b5d":
                                cachedAppList.add(new DeviceApp(UUID.fromString(baseName), "Send Text (System)", "Pebble Inc.", "", DeviceApp.Type.APP_SYSTEM));
                                break;
                        }
                        /*
                        else if (baseName.equals("4dab81a6-d2fc-458a-992c-7a1f3b96a970")) {
                            cachedAppList.add(new DeviceApp(UUID.fromString("4dab81a6-d2fc-458a-992c-7a1f3b96a970"), "Sports (System)", "Pebble Inc.", "", DeviceApp.Type.APP_SYSTEM));
                        } else if (baseName.equals("cf1e816a-9db0-4511-bbb8-f60c48ca8fac")) {
                            cachedAppList.add(new DeviceApp(UUID.fromString("cf1e816a-9db0-4511-bbb8-f60c48ca8fac"), "Golf (System)", "Pebble Inc.", "", DeviceApp.Type.APP_SYSTEM));
                        }
                        */
                        if (mGBDevice != null) {
                            if (PebbleUtils.hasHealth(mGBDevice.getModel())) {
                                if (baseName.equals(PebbleProtocol.UUID_PEBBLE_HEALTH.toString())) {
                                    cachedAppList.add(new DeviceApp(PebbleProtocol.UUID_PEBBLE_HEALTH, "Health (System)", "Pebble Inc.", "", DeviceApp.Type.APP_SYSTEM));
                                    continue;
                                }
                            }
                            if (PebbleUtils.hasHRM(mGBDevice.getModel())) {
                                if (baseName.equals(PebbleProtocol.UUID_WORKOUT.toString())) {
                                    cachedAppList.add(new DeviceApp(PebbleProtocol.UUID_WORKOUT, "Workout (System)", "Pebble Inc.", "", DeviceApp.Type.APP_SYSTEM));
                                    continue;
                                }
                            }
                            if (PebbleUtils.getFwMajor(mGBDevice.getFirmwareVersion()) >= 4) {
                                if (baseName.equals("3af858c3-16cb-4561-91e7-f1ad2df8725f")) {
                                    cachedAppList.add(new DeviceApp(UUID.fromString(baseName), "Kickstart (System)", "Pebble Inc.", "", DeviceApp.Type.WATCHFACE_SYSTEM));
                                }
                                if (baseName.equals(PebbleProtocol.UUID_WEATHER.toString())) {
                                    cachedAppList.add(new DeviceApp(PebbleProtocol.UUID_WEATHER, "Weather (System)", "Pebble Inc.", "", DeviceApp.Type.APP_SYSTEM));
                                }
                            }
                        }
                        if (uuids == null) {
                            cachedAppList.add(new DeviceApp(UUID.fromString(baseName), baseName, "N/A", "", DeviceApp.Type.UNKNOWN));
                        }
                    }
                }
            }
        }
        return cachedAppList;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mGBDevice = ((AppManagerActivity) getActivity()).getGBDevice();

        if (PebbleUtils.getFwMajor(mGBDevice.getFirmwareVersion()) < 3 && !checkCache()) {
            appListView.setDragEnabled(false);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_DEVICE_REFRESH_APPLIST);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, filter);

        if (PebbleUtils.getFwMajor(mGBDevice.getFirmwareVersion()) < 3) {
            GBApplication.deviceService().onAppInfoReq();
            if (checkCache()) {
                refreshList();
            }
        } else {
            refreshList();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_appmanager, container, false);

        appListView = (DragListView) (rootView.findViewById(R.id.appListView));
        appListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mGBDeviceAppAdapter = new GBDeviceAppAdapter(appList, R.layout.item_with_details, R.id.item_image, this.getContext(), this);
        appListView.setAdapter(mGBDeviceAppAdapter, false);
        appListView.setCanDragHorizontally(false);
        appListView.setDragListListener(new DragListView.DragListListener() {
            @Override
            public void onItemDragStarted(int position) {
            }

            @Override
            public void onItemDragging(int itemPosition, float x, float y) {
            }

            @Override
            public void onItemDragEnded(int fromPosition, int toPosition) {
                onChangedAppOrder();
            }
        });
        return rootView;
    }

    protected void sendOrderToDevice(String concatFilename) {
        ArrayList<UUID> uuids = new ArrayList<>();
        for (DeviceApp deviceApp : mGBDeviceAppAdapter.getItemList()) {
            uuids.add(deviceApp.getUUID());
        }
        if (concatFilename != null) {
            ArrayList<UUID> concatUuids = AppManagerActivity.getUuidsFromFile(concatFilename);
            uuids.addAll(concatUuids);
        }
        GBApplication.deviceService().onAppReorder(uuids.toArray(new UUID[uuids.size()]));
    }

    public boolean openPopupMenu(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.appmanager_context, popupMenu.getMenu());
        Menu menu = popupMenu.getMenu();
        final DeviceApp selectedApp = appList.get(position);

        if (!selectedApp.isInCache()) {
            menu.removeItem(R.id.appmanager_app_reinstall);
            menu.removeItem(R.id.appmanager_app_delete_cache);
        }
        if (!PebbleProtocol.UUID_PEBBLE_HEALTH.equals(selectedApp.getUUID())) {
            menu.removeItem(R.id.appmanager_health_activate);
            menu.removeItem(R.id.appmanager_health_deactivate);
        }
        if (!PebbleProtocol.UUID_WORKOUT.equals(selectedApp.getUUID())) {
            menu.removeItem(R.id.appmanager_hrm_activate);
            menu.removeItem(R.id.appmanager_hrm_deactivate);
        }
        if (!PebbleProtocol.UUID_WEATHER.equals(selectedApp.getUUID())) {
            menu.removeItem(R.id.appmanager_weather_activate);
            menu.removeItem(R.id.appmanager_weather_deactivate);
            menu.removeItem(R.id.appmanager_weather_install_provider);
        }
        if (selectedApp.getType() == DeviceApp.Type.APP_SYSTEM || selectedApp.getType() == DeviceApp.Type.WATCHFACE_SYSTEM) {
            menu.removeItem(R.id.appmanager_app_delete);
        }
        if (!selectedApp.isConfigurable()) {
            menu.removeItem(R.id.appmanager_app_configure);
        }

        if (PebbleProtocol.UUID_WEATHER.equals(selectedApp.getUUID())) {
            PackageManager pm = getActivity().getPackageManager();
            try {
                pm.getPackageInfo("ru.gelin.android.weather.notification", PackageManager.GET_ACTIVITIES);
                menu.removeItem(R.id.appmanager_weather_install_provider);
            } catch (PackageManager.NameNotFoundException e) {
                menu.removeItem(R.id.appmanager_weather_activate);
                menu.removeItem(R.id.appmanager_weather_deactivate);
            }
        }

        switch (selectedApp.getType()) {
            case WATCHFACE:
            case APP_GENERIC:
            case APP_ACTIVITYTRACKER:
                break;
            default:
                menu.removeItem(R.id.appmanager_app_openinstore);
        }
        //menu.setHeaderTitle(selectedApp.getName());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                                 public boolean onMenuItemClick(MenuItem item) {
                                                     return onContextItemSelected(item, selectedApp);
                                                 }
                                             }
        );

        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        popupMenu.show();
        return true;
    }

    public boolean onContextItemSelected(MenuItem item, DeviceApp selectedApp) {
        switch (item.getItemId()) {
            case R.id.appmanager_app_delete_cache:
                String baseName;
                try {
                    baseName = FileUtils.getExternalFilesDir().getPath() + "/pbw-cache/" + selectedApp.getUUID();
                } catch (IOException e) {
                    LOGGER.warn("could not get external dir while trying to access pbw cache.");
                    return true;
                }

                String[] suffixToDelete = new String[]{".pbw", ".json", "_config.js", "_preset.json"};

                for (String suffix : suffixToDelete) {
                    File fileToDelete = new File(baseName + suffix);
                    if (!fileToDelete.delete()) {
                        LOGGER.warn("could not delete file from pbw cache: " + fileToDelete.toString());
                    } else {
                        LOGGER.info("deleted file: " + fileToDelete.toString());
                    }
                }
                AppManagerActivity.deleteFromAppOrderFile("pbwcacheorder.txt", selectedApp.getUUID()); // FIXME: only if successful
                // fall through
            case R.id.appmanager_app_delete:
                if (PebbleUtils.getFwMajor(mGBDevice.getFirmwareVersion()) >= 3) {
                    AppManagerActivity.deleteFromAppOrderFile(mGBDevice.getAddress() + ".watchapps", selectedApp.getUUID()); // FIXME: only if successful
                    AppManagerActivity.deleteFromAppOrderFile(mGBDevice.getAddress() + ".watchfaces", selectedApp.getUUID()); // FIXME: only if successful
                    Intent refreshIntent = new Intent(ABAppManager.ACTION_DEVICE_REFRESH_APPLIST);
                    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(refreshIntent);
                }
                GBApplication.deviceService().onAppDelete(selectedApp.getUUID());
                return true;
            case R.id.appmanager_app_reinstall:
                File cachePath;
                try {
                    cachePath = new File(FileUtils.getExternalFilesDir().getPath() + "/pbw-cache/" + selectedApp.getUUID() + ".pbw");
                } catch (IOException e) {
                    LOGGER.warn("could not get external dir while trying to access pbw cache.");
                    return true;
                }
                GBApplication.deviceService().onInstallApp(Uri.fromFile(cachePath));
                return true;
            case R.id.appmanager_health_activate:
                GBApplication.deviceService().onInstallApp(Uri.parse("fake://health"));
                return true;
            case R.id.appmanager_hrm_activate:
                GBApplication.deviceService().onInstallApp(Uri.parse("fake://hrm"));
                return true;
            case R.id.appmanager_weather_activate:
                GBApplication.deviceService().onInstallApp(Uri.parse("fake://weather"));
                return true;
            case R.id.appmanager_health_deactivate:
            case R.id.appmanager_hrm_deactivate:
            case R.id.appmanager_weather_deactivate:
                GBApplication.deviceService().onAppDelete(selectedApp.getUUID());
                return true;
            case R.id.appmanager_weather_install_provider:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://f-droid.org/app/ru.gelin.android.weather.notification")));
                return true;
            case R.id.appmanager_app_configure:
                GBApplication.deviceService().onAppStart(selectedApp.getUUID(), true);

                Intent startIntent = new Intent(getContext().getApplicationContext(), ExternalPebbleJSActivity.class);
                startIntent.putExtra(DeviceService.EXTRA_APP_UUID, selectedApp.getUUID());
                startIntent.putExtra(GBDevice.EXTRA_DEVICE, mGBDevice);
                startActivity(startIntent);
                return true;
            case R.id.appmanager_app_openinstore:
                String url = "https://apps.getpebble.com/en_US/search/" + ((selectedApp.getType() == DeviceApp.Type.WATCHFACE) ? "watchfaces" : "watchapps") + "/1?query=" + selectedApp.getName() + "&dev_settings=true";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
