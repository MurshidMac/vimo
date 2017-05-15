package nodomain.strive.vimo.activities.charts;

import android.view.ViewGroup;

import java.util.Date;

import nodomain.strive.vimo.impl.GBDevice;

public interface ChartsHost {
    String DATE_PREV = ChartsActivity.class.getName().concat(".date_prev");
    String DATE_NEXT = ChartsActivity.class.getName().concat(".date_next");
    String REFRESH = ChartsActivity.class.getName().concat(".refresh");

    GBDevice getDevice();

    void setStartDate(Date startDate);

    void setEndDate(Date endDate);

    Date getStartDate();

    Date getEndDate();

    void setDateInfo(String dateInfo);

    ViewGroup getDateBar();
}
