package nodomain.freeyourgadget.vimo.model;

import java.util.Date;

public interface ValidByDate {
    Date getValidFromUTC();
    Date getValidToUTC();
}
