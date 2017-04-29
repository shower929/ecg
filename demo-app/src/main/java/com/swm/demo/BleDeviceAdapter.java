package com.swm.demo;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by yangzhenyu on 2016/9/22.
 */

public class BleDeviceAdapter extends BaseAdapter {
    private BluetoothDevice[] mDevices;

    @Override
    public int getCount() {
        return mDevices == null ? 0 : mDevices.length;
    }

    @Override
    public Object getItem(int position) {
        return mDevices[position];
    }

    @Override
    public long getItemId(int position) {
        return mDevices[position].hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.swm_ble_device_list_item, null, false);
        }
        BluetoothDevice device = (BluetoothDevice) getItem(position);
        BleDeviceListItemViewHolder holder = (BleDeviceListItemViewHolder) view.getTag();
        if (holder == null) {
            holder = new BleDeviceListItemViewHolder(view);
        }
        holder.updateView(device);
        view.setTag(holder);
        view.setTag(view.getId(), device);
        return view;
    }

    public void setDevices(BluetoothDevice[] devices) {
        mDevices = devices;
        notifyDataSetChanged();
    }

}
