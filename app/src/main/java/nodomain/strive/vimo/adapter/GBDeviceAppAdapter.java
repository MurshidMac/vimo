package nodomain.strive.vimo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.woxthebox.draglistview.DragItemAdapter;

import java.util.List;
import java.util.UUID;

import nodomain.strive.vimo.GBApplication;
import nodomain.freeyourgadget.vimo.R;
import nodomain.strive.vimo.activities.appmanager.ABAppManager;
import nodomain.strive.vimo.impl.DeviceApp;

/**
 * Adapter for displaying DeviceApp instances.
 */

public class GBDeviceAppAdapter extends DragItemAdapter<DeviceApp, GBDeviceAppAdapter.ViewHolder> {

    private final int mLayoutId;
    private final int mGrabHandleId;
    private final Context mContext;
    private final ABAppManager mParentFragment;

    public GBDeviceAppAdapter(List<DeviceApp> list, int layoutId, int grabHandleId, Context context, ABAppManager parentFragment) {
        super(true); // longpress
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mContext = context;
        mParentFragment = parentFragment;
        setHasStableIds(true);
        setItemList(list);
    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).getUUID().getLeastSignificantBits();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        DeviceApp deviceApp = mItemList.get(position);


        holder.mDeviceAppVersionAuthorLabel.setText(GBApplication.getContext().getString(R.string.appversion_by_creator, deviceApp.getVersion(), deviceApp.getCreator()));
        // FIXME: replace with small icons
        String appNameLabelText = deviceApp.getName();
        holder.mDeviceAppNameLabel.setText(appNameLabelText);

        switch (deviceApp.getType()) {
            case APP_GENERIC:
                holder.mDeviceImageView.setImageResource(R.drawable.ic_watchapp);
                break;
            case APP_ACTIVITYTRACKER:
                holder.mDeviceImageView.setImageResource(R.drawable.ic_activitytracker);
                break;
            case APP_SYSTEM:
                holder.mDeviceImageView.setImageResource(R.drawable.ic_systemapp);
                break;
            case WATCHFACE:
                holder.mDeviceImageView.setImageResource(R.drawable.ic_watchface);
                break;
            default:
                holder.mDeviceImageView.setImageResource(R.drawable.ic_watchapp);
        }
    }

    public class ViewHolder extends DragItemAdapter<DeviceApp, GBDeviceAppAdapter.ViewHolder>.ViewHolder {
        TextView mDeviceAppVersionAuthorLabel;
        TextView mDeviceAppNameLabel;
        ImageView mDeviceImageView;

        public ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId);
            mDeviceAppVersionAuthorLabel = (TextView) itemView.findViewById(R.id.item_details);
            mDeviceAppNameLabel = (TextView) itemView.findViewById(R.id.item_name);
            mDeviceImageView = (ImageView) itemView.findViewById(R.id.item_image);
        }

        @Override
        public void onItemClicked(View view) {
            UUID uuid = mItemList.get(getAdapterPosition()).getUUID();
            GBApplication.deviceService().onAppStart(uuid, true);
        }

        @Override
        public boolean onItemLongClicked(View view) {
            return mParentFragment.openPopupMenu(view, getAdapterPosition());
        }
    }
}
