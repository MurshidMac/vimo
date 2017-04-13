package nodomain.freeyourgadget.vimo.deviceevents;

import nodomain.freeyourgadget.vimo.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.R;

public class GBDeviceEventVersionInfo extends GBDeviceEvent {
    public String fwVersion = GBApplication.getContext().getString(R.string.n_a);
    public String hwVersion = GBApplication.getContext().getString(R.string.n_a);
}
