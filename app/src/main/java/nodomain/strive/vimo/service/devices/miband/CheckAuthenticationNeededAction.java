package nodomain.strive.vimo.service.devices.miband;

import nodomain.strive.vimo.impl.GBDevice;
import nodomain.strive.vimo.service.btle.actions.AbortTransactionAction;

public class CheckAuthenticationNeededAction extends AbortTransactionAction {
    private final GBDevice mDevice;

    public CheckAuthenticationNeededAction(GBDevice device) {
        super();
        mDevice = device;
    }

    @Override
    protected boolean shouldAbort() {
        // the state is set in MiBandSupport.handleNotificationNotif()
        switch (mDevice.getState()) {
            case AUTHENTICATION_REQUIRED: // fall through
            case AUTHENTICATING:
                return true; // abort the whole thing
            default:
                return false;
        }
    }
}
