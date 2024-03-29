package vimo.impl;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class DeviceApp {
    // Device Uses the
    private final String name;
    private final String creator;
    private final String version;
    private final UUID uuid;
    private final Type type;
    private final boolean inCache;
    private boolean isOnDevice;
    private final boolean configurable;


    public DeviceApp(UUID uuid, String name, String creator, String version, Type type) {
        this.uuid = uuid;
        this.name = name;
        this.creator = creator;
        this.version = version;
        this.type = type;
        //FIXME: do not assume
        this.inCache = false;
        this.configurable = false;
        this.isOnDevice = false;
    }

    public DeviceApp(JSONObject json, boolean configurable) {
        UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        String name = "";
        String creator = "";
        String version = "";
        Type type = Type.UNKNOWN;

        try {
            uuid = UUID.fromString(json.getString("uuid"));
            name = json.getString("name");
            creator = json.getString("creator");
            version = json.getString("version");
            type = Type.valueOf(json.getString("type"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.uuid = uuid;
        this.name = name;
        this.creator = creator;
        this.version = version;
        this.type = type;
        //FIXME: do not assume
        this.inCache = true;
        this.configurable = configurable;
    }

    public void setOnDevice(boolean isOnDevice) {
        this.isOnDevice = isOnDevice;
    }

    public boolean isInCache() {
        return inCache;
    }

    public boolean isOnDevice() {
        return isOnDevice;
    }

    public String getName() {
        return name;
    }

    public String getCreator() {
        return creator;
    }

    public String getVersion() {
        return version;
    }

    public UUID getUUID() {
        return uuid;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        UNKNOWN,
        WATCHFACE,
        WATCHFACE_SYSTEM,
        APP_GENERIC,
        APP_ACTIVITYTRACKER,
        APP_SYSTEM,
    }

    public JSONObject getJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("uuid", uuid.toString());
            json.put("name", name);
            json.put("creator", creator);
            json.put("version", version);
            json.put("type", type.name());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public boolean isConfigurable() {
        return configurable;
    }
}
