package vimo.service.devices.miband;

import android.support.annotation.NonNull;

import vimo.devices.miband.MiBandConst;
import vimo.impl.GBDevice;

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
