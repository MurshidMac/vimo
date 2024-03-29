package vimo.service.devices.pebble;

import android.util.Pair;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

import vimo.deviceevents.GBDeviceEvent;
import vimo.deviceevents.GBDeviceEventSendBytes;
import vimo.model.Weather;
import vimo.model.WeatherSpec;

class AppMessageHandlerMarioTime extends AppMessageHandler {

    private static final int KEY_WEATHER_ICON_ID = 10;
    private static final int KEY_WEATHER_TEMPERATURE = 11;

    AppMessageHandlerMarioTime(UUID uuid, PebbleProtocol pebbleProtocol) {
        super(uuid, pebbleProtocol);
    }

    private byte[] encodeMarioWeatherMessage(WeatherSpec weatherSpec) {
        if (weatherSpec == null) {
            return null;
        }

        ArrayList<Pair<Integer, Object>> pairs = new ArrayList<>(2);
        pairs.add(new Pair<>(KEY_WEATHER_ICON_ID, (Object) (byte) 1));
        pairs.add(new Pair<>(KEY_WEATHER_TEMPERATURE, (Object) (byte) (weatherSpec.currentTemp - 273)));
        byte[] weatherMessage = mPebbleProtocol.encodeApplicationMessagePush(PebbleProtocol.ENDPOINT_APPLICATIONMESSAGE, mUUID, pairs);

        ByteBuffer buf = ByteBuffer.allocate(weatherMessage.length);

        buf.put(weatherMessage);

        return buf.array();
    }

    @Override
    public GBDeviceEvent[] onAppStart() {
        WeatherSpec weatherSpec = Weather.getInstance().getWeatherSpec();
        if (weatherSpec == null) {
            return new GBDeviceEvent[]{null};
        }
        GBDeviceEventSendBytes sendBytes = new GBDeviceEventSendBytes();
        sendBytes.encodedBytes = encodeMarioWeatherMessage(weatherSpec);
        return new GBDeviceEvent[]{sendBytes};
    }

    @Override
    public byte[] encodeUpdateWeather(WeatherSpec weatherSpec) {
        return encodeMarioWeatherMessage(weatherSpec);
    }
}
