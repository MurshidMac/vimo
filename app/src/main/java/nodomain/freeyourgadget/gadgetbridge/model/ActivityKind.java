package nodomain.freeyourgadget.gadgetbridge.model;

import java.util.Arrays;

import nodomain.freeyourgadget.gadgetbridge.devices.SampleProvider;

public class ActivityKind {
    public static final int TYPE_NOT_MEASURED = -1;
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_ACTIVITY = 1;
    public static final int TYPE_LIGHT_SLEEP = 2;
    public static final int TYPE_DEEP_SLEEP = 4;
    public static final int TYPE_NOT_WORN = 8;

    public static final int TYPE_SLEEP = TYPE_LIGHT_SLEEP | TYPE_DEEP_SLEEP;
    public static final int TYPE_ALL = TYPE_ACTIVITY | TYPE_SLEEP | TYPE_NOT_WORN;

    public static int[] mapToDBActivityTypes(int types, SampleProvider provider) {
        int[] result = new int[3];
        int i = 0;
        if ((types & ActivityKind.TYPE_ACTIVITY) != 0) {
            result[i++] = provider.toRawActivityKind(TYPE_ACTIVITY);
        }
        if ((types & ActivityKind.TYPE_DEEP_SLEEP) != 0) {
            result[i++] = provider.toRawActivityKind(TYPE_DEEP_SLEEP);
        }
        if ((types & ActivityKind.TYPE_LIGHT_SLEEP) != 0) {
            result[i++] = provider.toRawActivityKind(TYPE_LIGHT_SLEEP);
        }
        if ((types & ActivityKind.TYPE_NOT_WORN) != 0) {
            result[i++] = provider.toRawActivityKind(TYPE_NOT_WORN);
        }
        return Arrays.copyOf(result, i);
    }

}
