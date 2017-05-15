package nodomain.strive.vimo.devices.hplus;

/*
* @author João Paulo Barraca &lt;jpbarraca@gmail.com&gt;
*/


import android.support.annotation.NonNull;

import nodomain.strive.vimo.impl.GBDeviceCandidate;
import nodomain.strive.vimo.model.DeviceType;

/**
 * Pseudo Coordinator for the Makibes F68, a sub type of the HPLUS devices
 */
public class MakibesF68Coordinator extends HPlusCoordinator {

    @NonNull
    @Override
    public DeviceType getSupportedType(GBDeviceCandidate candidate) {
        String name = candidate.getDevice().getName();
        if(name != null && name.startsWith("SPORT")){
            return DeviceType.MAKIBESF68;
        }

        return DeviceType.UNKNOWN;
    }

    @Override
    public DeviceType getDeviceType() {
        return DeviceType.MAKIBESF68;
    }

    @Override
    public String getManufacturer() {
        return "Makibes";
    }

}
