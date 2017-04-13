package nodomain.freeyourgadget.vimo.activities;

import nodomain.freeyourgadget.vimo.model.ItemWithDetails;

public interface InstallActivity {
    void setInfoText(String text);

    void setInstallEnabled(boolean enable);

    void clearInstallItems();

    void setInstallItem(ItemWithDetails item);
}
