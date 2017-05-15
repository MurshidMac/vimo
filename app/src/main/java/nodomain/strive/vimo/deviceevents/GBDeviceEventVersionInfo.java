package nodomain.strive.vimo.deviceevents;

import nodomain.strive.vimo.GBApplication;
import nodomain.freeyourgadget.vimo.R;

public class GBDeviceEventVersionInfo extends GBDeviceEvent {
    public String fwVersion = GBApplication.getContext().getString(R.string.n_a);
    public String hwVersion = GBApplication.getContext().getString(R.string.n_a);
}
