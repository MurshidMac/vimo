package vimo.activities.charts;

import vimo.entities.AbstractActivitySample;

public class TrailingActivitySample extends AbstractActivitySample {
    private int timestamp;
    private long userId;
    private long deviceId;

    @Override
    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public long getDeviceId() {
        return deviceId;
    }

    @Override
    public long getUserId() {
        return userId;
    }

    @Override
    public int getTimestamp() {
        return timestamp;
    }
}
