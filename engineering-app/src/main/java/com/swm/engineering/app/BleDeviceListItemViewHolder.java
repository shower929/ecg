package com.swm.engineering.app;

import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.widget.TextView;

/**
 * Created by yangzhenyu on 2016/9/22.
 */

public class BleDeviceListItemViewHolder {
    public final TextView mDeviceName;
    public final TextView mDeviceAddress;
    public final View mItemLayout;

    public BleDeviceListItemViewHolder(View group) {
        mDeviceName = (TextView) group.findViewById(R.id.swm_ble_device_name);
        mDeviceAddress = (TextView) group.findViewById(R.id.swm_ble_device_address);
        mItemLayout = group;
    }

    public void updateView(final BluetoothDevice device) {
        mDeviceName.setText(device.getName());
        mDeviceAddress.setText(device.getAddress());
    }
}
