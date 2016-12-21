package com.swm.core;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.swm.heart.R;
import com.swm.heart.SplashActivity;
import com.swm.heart.SwmBaseActivity;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class DeviceDiscoverAndConnActivity extends SwmBaseActivity implements AdapterView.OnItemClickListener{
    private static final String  LOG_TAG = "Device";
    private BleDeviceAdapter mBleDeviceAdapter;
    private ListView mDeviceListView;
    private UIHandler mUIHandler;
    private TextView mBleActionTextView;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BluetoothDevice device = (BluetoothDevice) mBleDeviceAdapter.getItem(position);
        SwmDeviceController.getIns().connectDevice(device);
        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
        finish();
    }

    private class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SwmDeviceController.MSG_NEW_DEVICES:
                    Bundle data = msg.getData();
                    mBleDeviceAdapter.setDevices((BluetoothDevice[]) data.getParcelableArray(SwmDeviceController.DATA_DEVICES));
                    mDeviceListView.setVisibility(View.VISIBLE);
                    mBleActionTextView.setText(getString(R.string.swm_ble_choose_your_device));
                    break;
                case SwmDeviceController.MSG_DONE:
                    finish();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_device_discover_and_conn);

        mBleActionTextView = (TextView) findViewById(R.id.swm_ble_action_text_view);
        mDeviceListView = (ListView) findViewById(R.id.swm_ble_devices_list_view);
        mBleDeviceAdapter = new BleDeviceAdapter();
        mDeviceListView.setAdapter(mBleDeviceAdapter);
        mDeviceListView.setOnItemClickListener(this);
        mUIHandler = new UIHandler();

        SwmDeviceController.getIns().setIcon(findViewById(R.id.swm_ble_icon));
        SwmDeviceController.getIns().setUIHandler(mUIHandler);
        SwmDeviceController.getIns().startScan(false);
        mBleActionTextView.setText(getString(R.string.swm_ble_scan));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }


}
