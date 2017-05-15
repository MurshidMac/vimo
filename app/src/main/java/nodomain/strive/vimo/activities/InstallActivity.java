package nodomain.strive.vimo.activities;

import nodomain.strive.vimo.model.ItemWithDetails;

public interface InstallActivity {
    void setInfoText(String text);

    void setInstallEnabled(boolean enable);

    void clearInstallItems();

    void setInstallItem(ItemWithDetails item);
}
