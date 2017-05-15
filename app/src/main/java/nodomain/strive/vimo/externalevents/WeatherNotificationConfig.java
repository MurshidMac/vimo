package nodomain.strive.vimo.externalevents;

import android.os.Bundle;

import nodomain.freeyourgadget.vimo.R;
import nodomain.strive.vimo.activities.GBActivity;

public class WeatherNotificationConfig extends GBActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_notification);
    }
}
