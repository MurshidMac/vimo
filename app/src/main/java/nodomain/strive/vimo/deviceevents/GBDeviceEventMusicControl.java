package nodomain.strive.vimo.deviceevents;


public class GBDeviceEventMusicControl extends GBDeviceEvent {
    public Event event = Event.UNKNOWN;

    public enum Event {
        UNKNOWN,
        PLAY,
        PAUSE,
        PLAYPAUSE,
        NEXT,
        PREVIOUS,
        VOLUMEUP,
        VOLUMEDOWN,
    }
}
