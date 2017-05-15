package nodomain.strive.vimo.service.btle.profiles.alertnotification;

import nodomain.strive.vimo.service.btle.AbstractBTLEDeviceSupport;
import nodomain.strive.vimo.service.btle.profiles.AbstractBleProfile;

public class AlertNotificationProfile<T extends AbstractBTLEDeviceSupport> extends AbstractBleProfile<T> {
    public AlertNotificationProfile(T support) {
        super(support);
    }


}
