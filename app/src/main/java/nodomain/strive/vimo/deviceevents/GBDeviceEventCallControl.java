package nodomain.strive.vimo.deviceevents;


public class GBDeviceEventCallControl extends GBDeviceEvent {
    public Event event = Event.UNKNOWN;

    public enum Event {
        UNKNOWN,
        ACCEPT,
        END,
        INCOMING,
        OUTGOING,
        REJECT,
        START,
    }
}
