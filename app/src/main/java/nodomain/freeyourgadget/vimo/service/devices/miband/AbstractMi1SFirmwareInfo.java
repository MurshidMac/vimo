package nodomain.freeyourgadget.vimo.service.devices.miband;

import android.support.annotation.NonNull;

import nodomain.freeyourgadget.vimo.devices.miband.MiBandConst;
import nodomain.freeyourgadget.vimo.impl.GBDevice;

public abstract class AbstractMi1SFirmwareInfo extends AbstractMiFirmwareInfo {

    public AbstractMi1SFirmwareInfo(@NonNull byte[] wholeFirmwareBytes) {
        super(wholeFirmwareBytes);
    }

    @Override
    public boolean isGenerallyCompatibleWith(GBDevice device) {
        return MiBandConst.MI_1S.equals(device.getModel());
    }

    @Override
    public boolean isSingleMiBandFirmware() {
        return false;
    }
}
