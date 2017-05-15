package nodomain.strive.vimo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import nodomain.freeyourgadget.vimo.R;
import nodomain.strive.vimo.impl.GBDevice;
import nodomain.strive.vimo.impl.GBDeviceCandidate;
import nodomain.strive.vimo.model.DeviceType;
import nodomain.strive.vimo.util.GB;
import nodomain.strive.vimo.model.DeviceType.*;

import static nodomain.strive.vimo.model.DeviceType.*;

/**
 * Adapter for displaying GBDeviceCandate instances.
 */
public class DeviceCandidateAdapter extends ArrayAdapter<GBDeviceCandidate> {

    private final Context context;

    public DeviceCandidateAdapter(Context context, List<GBDeviceCandidate> deviceCandidates) {
        super(context, 0, deviceCandidates);

        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        GBDeviceCandidate device = getItem(position);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.item_with_details, parent, false);
        }
        ImageView deviceImageView = (ImageView) view.findViewById(R.id.item_image);
        TextView deviceNameLabel = (TextView) view.findViewById(R.id.item_name);
        TextView deviceAddressLabel = (TextView) view.findViewById(R.id.item_details);

        String name = formatDeviceCandidate(device);
        deviceNameLabel.setText(name);
        deviceAddressLabel.setText(device.getMacAddress());

        switch (device.getDeviceType()) {
            case MIBAND:

            case MIBAND2:
                deviceImageView.setImageResource(R.drawable.ic_device_miband);
                break;
            default:
                deviceImageView.setImageResource(R.drawable.ic_launcher);
        }

        return view;
    }

    private String formatDeviceCandidate(GBDeviceCandidate device) {
        if (device.getRssi() > GBDevice.RSSI_UNKNOWN) {
            return context.getString(R.string.device_with_rssi, device.getName(), GB.formatRssi(device.getRssi()));
        }
        return device.getName();
    }
}
