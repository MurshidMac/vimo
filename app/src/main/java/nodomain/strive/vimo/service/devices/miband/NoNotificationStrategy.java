package nodomain.strive.vimo.service.devices.miband;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nodomain.strive.vimo.devices.miband.VibrationProfile;
import nodomain.strive.vimo.service.btle.BtLEAction;
import nodomain.strive.vimo.service.btle.TransactionBuilder;

/**
 * Does not do anything.
 */
public class NoNotificationStrategy implements NotificationStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(NoNotificationStrategy.class);

    @Override
    public void sendDefaultNotification(TransactionBuilder builder, BtLEAction extraAction) {
        LOG.info("dummy notification stragegy: default notification");
    }

    @Override
    public void sendCustomNotification(VibrationProfile vibrationProfile, int flashTimes, int flashColour, int originalColour, long flashDuration, BtLEAction extraAction, TransactionBuilder builder) {
        LOG.info("dummy notification stragegy: custom notification");
    }
}
