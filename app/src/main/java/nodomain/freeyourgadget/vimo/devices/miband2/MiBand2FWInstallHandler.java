package nodomain.freeyourgadget.vimo.devices.miband2;

import android.content.Context;
import android.net.Uri;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import nodomain.freeyourgadget.vimo.devices.miband.AbstractMiBandFWHelper;
import nodomain.freeyourgadget.vimo.devices.miband.AbstractMiBandFWInstallHandler;
import nodomain.freeyourgadget.vimo.impl.GBDevice;
import nodomain.freeyourgadget.vimo.model.DeviceType;

public class MiBand2FWInstallHandler extends AbstractMiBandFWInstallHandler {
    private static final Logger LOG = LoggerFactory.getLogger(MiBand2FWInstallHandler.class);

    public MiBand2FWInstallHandler(Uri uri, Context context) {
        super(uri, context);
    }

    @Override
    protected AbstractMiBandFWHelper createHelper(Uri uri, Context context) throws IOException {
        return new MiBand2FWHelper(uri, context);
    }

    @Override
    protected boolean isSupportedDeviceType(GBDevice device) {
        return device.getType() == DeviceType.MIBAND2;
    }
}
