package vimo.service.btle.profiles.alertnotification;

import vimo.service.btle.AbstractBTLEDeviceSupport;
import vimo.service.btle.profiles.AbstractBleProfile;

public class AlertNotificationProfile<T extends AbstractBTLEDeviceSupport> extends AbstractBleProfile<T> {
    public AlertNotificationProfile(T support) {
        super(support);
    }


}
