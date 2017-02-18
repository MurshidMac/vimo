package nodomain.freeyourgadget.gadgetbridge.service.devices.pebble;

import android.util.Pair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEvent;
import nodomain.freeyourgadget.gadgetbridge.devices.pebble.PebbleColor;
import nodomain.freeyourgadget.gadgetbridge.model.Weather;
import ru.gelin.android.weather.notification.ParcelableWeather2;

class AppMessageHandlerPebStyle extends AppMessageHandler {
    public static final int KEY_AMPM_TEXT = 21;
    public static final int KEY_BLUETOOTH_ALERT = 2;
    public static final int KEY_BLUETOOTH_ICON = 20;
    public static final int KEY_CITY_NAME = 9;
    public static final int KEY_COLOR_SELECTION = 15;
    public static final int KEY_JSREADY = 6;
    public static final int KEY_JS_TIMEZONE_OFFSET = 13;
    public static final int KEY_LOCATION_SERVICE = 7;
    public static final int KEY_MAIN_BG_COLOR = 16;
    public static final int KEY_MAIN_CLOCK = 0;
    public static final int KEY_MAIN_COLOR = 17;
    public static final int KEY_SECONDARY_INFO_TYPE = 10;
    public static final int KEY_SECOND_HAND = 1;
    public static final int KEY_SIDEBAR_BG_COLOR = 18;
    public static final int KEY_SIDEBAR_COLOR = 19;
    public static final int KEY_SIDEBAR_LOCATION = 14;
    public static final int KEY_TEMPERATURE_FORMAT = 8;
    public static final int KEY_TIMEZONE_NAME = 11;
    public static final int KEY_TIME_SEPARATOR = 12;
    public static final int KEY_WEATHER_CODE = 3;
    public static final int KEY_WEATHER_INTERVAL = 5;
    public static final int KEY_WEATHER_TEMP = 4;


    private static final Logger LOG = LoggerFactory.getLogger(AppMessageHandlerPebStyle.class);

    public AppMessageHandlerPebStyle(UUID uuid, PebbleProtocol pebbleProtocol) {
        super(uuid, pebbleProtocol);
    }

    private byte[] encodeAck() {
        byte[] ackMessage = mPebbleProtocol.encodeApplicationMessageAck(mUUID, mPebbleProtocol.last_id);
        ByteBuffer buf = ByteBuffer.allocate(ackMessage.length);
        buf.put(ackMessage);
        return buf.array();
    }

    private byte[] encodePebStyleConfig() {
        ArrayList<Pair<Integer, Object>> pairs = new ArrayList<>();
        //settings that give good legibility on pebble time
        pairs.add(new Pair<>(KEY_SECOND_HAND, (Object) 0)); //1 enabled
        pairs.add(new Pair<>(KEY_BLUETOOTH_ALERT, (Object) 0)); //1 silent, 2 weak, up to 5
        pairs.add(new Pair<>(KEY_TEMPERATURE_FORMAT, (Object) 1)); //0 fahrenheit
        pairs.add(new Pair<>(KEY_LOCATION_SERVICE, (Object) 2)); //0 auto, 1 manual
        pairs.add(new Pair<>(KEY_SIDEBAR_LOCATION, (Object) 1)); //0 right
        pairs.add(new Pair<>(KEY_COLOR_SELECTION, (Object) 1)); //1 custom
        pairs.add(new Pair<>(KEY_MAIN_COLOR, (Object) PebbleColor.Black));
        pairs.add(new Pair<>(KEY_MAIN_BG_COLOR, (Object) PebbleColor.White));
        pairs.add(new Pair<>(KEY_SIDEBAR_BG_COLOR, (Object) PebbleColor.MediumSpringGreen));

        //DIGITAL settings
       /*
        pairs.add(new Pair<>(KEY_MAIN_CLOCK, (Object) 1)); //0 analog
        pairs.add(new Pair<>(KEY_SECONDARY_INFO_TYPE, (Object) 3)); //1 time, 2 location
        */
        //ANALOG + DIGITAL settings
        pairs.add(new Pair<>(KEY_MAIN_CLOCK, (Object) 0)); //0 analog, 1 digital
        pairs.add(new Pair<>(KEY_SECONDARY_INFO_TYPE, (Object) 1)); //1 time, 2 location


        //WEATHER
        ParcelableWeather2 weather = Weather.getInstance().getWeather2();
        if (weather != null) {
            //comment the same key in the general section above!
            pairs.add(new Pair<>(KEY_LOCATION_SERVICE, (Object) 0)); //0 auto, 1 manual
            pairs.add(new Pair<>(KEY_WEATHER_CODE, (Object) Weather.mapToYahooCondition(weather.currentConditionCode)));
            pairs.add(new Pair<>(KEY_WEATHER_TEMP, (Object) (weather.currentTemp - 273)));
        }

        byte[] testMessage = mPebbleProtocol.encodeApplicationMessagePush(PebbleProtocol.ENDPOINT_APPLICATIONMESSAGE, mUUID, pairs);


        ByteBuffer buf = ByteBuffer.allocate(testMessage.length);

        // encode ack and put in front of push message (hack for acknowledging the last message)
        buf.put(testMessage);

        return buf.array();
    }


    @Override
    public GBDeviceEvent[] handleMessage(ArrayList<Pair<Integer, Object>> pairs) {
        return null;
        /*
        GBDeviceEventSendBytes sendBytes = new GBDeviceEventSendBytes();
        ByteBuffer buf = ByteBuffer.allocate(encodeAck().length + encodePebStyleConfig().length);
        buf.put(encodeAck());
        buf.put(encodePebStyleConfig());
        sendBytes.encodedBytes = buf.array();
        return new GBDeviceEvent[]{sendBytes};
        */
    }

    @Override
    public GBDeviceEvent[] onAppStart() {
        return null;
        /*
        GBDeviceEventSendBytes sendBytes = new GBDeviceEventSendBytes();
        sendBytes.encodedBytes = encodePebStyleConfig();
        return new GBDeviceEvent[]{sendBytes};
        */
    }
}
