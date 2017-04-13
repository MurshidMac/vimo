package nodomain.freeyourgadget.vimo.service.btle.profiles.alertnotification;

import nodomain.freeyourgadget.vimo.service.btle.AbstractBTLEDeviceSupport;
import nodomain.freeyourgadget.vimo.service.btle.profiles.AbstractBleProfile;

public class AlertNotificationProfile<T extends AbstractBTLEDeviceSupport> extends AbstractBleProfile<T> {
    public AlertNotificationProfile(T support) {
        super(support);
    }


}
