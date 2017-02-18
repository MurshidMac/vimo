package nodomain.freeyourgadget.gadgetbridge.service.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

import nodomain.freeyourgadget.gadgetbridge.deviceevents.GBDeviceEventCallControl;

public class GBCallControlReceiver extends BroadcastReceiver {
    public static final String ACTION_CALLCONTROL = "nodomain.freeyourgadget.gadgetbridge.callcontrol";
    private static final Logger LOG = LoggerFactory.getLogger(GBCallControlReceiver.class);

    @Override
    public void onReceive(Context context, Intent intent) {
        GBDeviceEventCallControl.Event callCmd = GBDeviceEventCallControl.Event.values()[intent.getIntExtra("event", 0)];
        switch (callCmd) {
            case END:
            case START:
                try {
                    TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    Class clazz = Class.forName(telephonyManager.getClass().getName());
                    Method method = clazz.getDeclaredMethod("getITelephony");
                    method.setAccessible(true);
                    ITelephony telephonyService = (ITelephony) method.invoke(telephonyManager);
                    if (callCmd == GBDeviceEventCallControl.Event.END) {
                        telephonyService.endCall();
                    } else {
                        telephonyService.answerRingingCall();
                    }
                } catch (Exception e) {
                    LOG.warn("could not start or hangup call");
                }
                break;
            default:
        }
    }
}
