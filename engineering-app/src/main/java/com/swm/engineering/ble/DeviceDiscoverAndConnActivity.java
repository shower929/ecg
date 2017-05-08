package com.swm.engineering.ble;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.swm.engineering.MainActivity;
import com.swm.engineering.app.R;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class DeviceDiscoverAndConnActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private static final String  LOG_TAG = "Device";
    private BleDeviceAdapter mBleDeviceAdapter;
    private ListView mDeviceListView;
    private UIHandler mUIHandler;
    private TextView mBleActionTextView;
    private BleManager manager;
    public static final int MSG_NEW_DEVICES = 1;
    public static final int MSG_DONE = 2;
    public static final String DATA_DEVICES = "data_devices";

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BluetoothDevice device = (BluetoothDevice) mBleDeviceAdapter.getItem(position);
        manager.connectDevice(device);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_NEW_DEVICES:
                    Bundle data = msg.getData();
                    mBleDeviceAdapter.setDevices((BluetoothDevice[]) data.getParcelableArray(DATA_DEVICES));
                    mDeviceListView.setVisibility(View.VISIBLE);
                    mBleActionTextView.setText(getString(R.string.swm_ble_choose_your_device));
                    break;
                case MSG_DONE:
                    finish();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = new BleManager(this);

        setContentView(R.layout.activity_device_discover_and_conn);

        mBleActionTextView = (TextView) findViewById(R.id.swm_ble_action_text_view);
        mDeviceListView = (ListView) findViewById(R.id.swm_ble_devices_list_view);
        mBleDeviceAdapter = new BleDeviceAdapter();
        mDeviceListView.setAdapter(mBleDeviceAdapter);
        mDeviceListView.setOnItemClickListener(this);
        mUIHandler = new UIHandler();
        manager.setIcon(findViewById(R.id.swm_ble_icon));
        manager.setUIHandler(mUIHandler);

        manager.startScan(false, null, new BleManagerCallback() {
            @Override
            public void onDeviceFound(BluetoothDevice device) {

            }

            @Override
            public void onListDevice(BluetoothDevice[] devices) {
                Message msg = mUIHandler.obtainMessage(MSG_NEW_DEVICES);
                Bundle data = new Bundle();
                data.putParcelableArray(DATA_DEVICES, devices);
                msg.setData(data);
                mUIHandler.sendMessage(msg);
            }
        });

        mBleActionTextView.setText(getString(R.string.swm_ble_scan));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

}
