package nodomain.freeyourgadget.gadgetbridge.devices.miband;

import android.content.Context;
import android.net.Uri;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.activities.InstallActivity;
import nodomain.freeyourgadget.gadgetbridge.devices.InstallHandler;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.GenericItem;

public abstract class AbstractMiBandFWInstallHandler implements InstallHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractMiBandFWInstallHandler.class);

    private final Context mContext;
    private AbstractMiBandFWHelper helper;
    private String errorMessage;

    public AbstractMiBandFWInstallHandler(Uri uri, Context context) {
        mContext = context;

        try {
            helper = createHelper(uri, context);
        } catch (IOException e) {
            errorMessage = e.getMessage();
            LOG.warn(errorMessage, e);
        }
    }

    protected abstract AbstractMiBandFWHelper createHelper(Uri uri, Context context) throws IOException;


    @Override
    public void validateInstallation(InstallActivity installActivity, GBDevice device) {
        if (device.isBusy()) {
            installActivity.setInfoText(device.getBusyTask());
            installActivity.setInstallEnabled(false);
            return;
        }

        if (!isSupportedDeviceType(device) || !device.isInitialized()) {
            installActivity.setInfoText(mContext.getString(R.string.fwapp_install_device_not_ready));
            installActivity.setInstallEnabled(false);
            return;
        }

        try {
            helper.checkValid();
        } catch (IllegalArgumentException ex) {
            installActivity.setInfoText(ex.getLocalizedMessage());
            installActivity.setInstallEnabled(false);
            return;
        }

        GenericItem fwItem = new GenericItem(mContext.getString(R.string.miband_installhandler_miband_firmware, helper.getHumanFirmwareVersion()));
        fwItem.setIcon(R.drawable.ic_device_miband);

        if (!helper.isFirmwareGenerallyCompatibleWith(device)) {
            fwItem.setDetails(mContext.getString(R.string.miband_fwinstaller_incompatible_version));
            installActivity.setInfoText(mContext.getString(R.string.fwinstaller_firmware_not_compatible_to_device));
            installActivity.setInstallEnabled(false);
            return;
        }
        StringBuilder builder = new StringBuilder();
        if (helper.isSingleFirmware()) {
            builder.append(mContext.getString(R.string.fw_upgrade_notice, helper.getHumanFirmwareVersion()));
        } else {
            builder.append(mContext.getString(R.string.fw_multi_upgrade_notice, helper.getHumanFirmwareVersion(), helper.getHumanFirmwareVersion2()));
        }


        if (helper.isFirmwareWhitelisted()) {
            builder.append(" ").append(mContext.getString(R.string.miband_firmware_known));
            fwItem.setDetails(mContext.getString(R.string.miband_fwinstaller_compatible_version));
            // TODO: set a CHECK (OKAY) button
        } else {
            builder.append("  ").append(mContext.getString(R.string.miband_firmware_unknown_warning)).append(" \n\n")
                    .append(mContext.getString(R.string.miband_firmware_suggest_whitelist, helper.getFirmwareVersion()));
            fwItem.setDetails(mContext.getString(R.string.miband_fwinstaller_untested_version));
            // TODO: set a UNKNOWN (question mark) button
        }
        installActivity.setInfoText(builder.toString());
        installActivity.setInstallItem(fwItem);
        installActivity.setInstallEnabled(true);
    }

    protected abstract boolean isSupportedDeviceType(GBDevice device);

    @Override
    public void onStartInstall(GBDevice device) {

    }

    @Override
    public boolean isValid() {
        return helper != null;
    }
}
