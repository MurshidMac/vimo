package vimo.externalevents;

import android.os.Bundle;

import nodomain.freeyourgadget.gadgetbridge.R;
import vimo.activities.GBActivity;

public class WeatherNotificationConfig extends GBActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_notification);
    }
}
