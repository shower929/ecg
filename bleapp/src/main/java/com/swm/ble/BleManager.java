package com.swm.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.swm.sdk.DeviceCallback;
import com.swm.sdk.SwmConfig;
import com.swm.sdk.SwmDeviceModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangzhenyu on 2016/9/22.
 */
 public class BleManager {
    private static final String LOG_TAG = "Device";

    private Handler mUIHandler;
    private BleIconHandler mBleIconHandler;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mMyHandler;
    private static final int SCAN_PERIOD = 3 * 1000;
    private Runnable mTimer;
    private BluetoothAdapter.LeScanCallback mCallback;
    private ScanCallback mNewCallback;
    private static final String SWM_DEVICE = "CC2650 SensorTag";
    private Context mContext;


    private boolean mScanning = false;
    private boolean mAutoRescan = false;
    private BluetoothLeScanner mScanner;

    public BleManager(Context context) {
        mBleIconHandler = new BleIconHandler();
        mMyHandler = new Handler();
        mContext = context;
    }

    void connectDevice(BluetoothDevice device) {
        SwmDeviceModule.getIns().connectBle(mContext, device, new DeviceCallback() {
            @Override
            public void readConfig(SwmConfig config) {
                SharedPreferences pref = mContext.getSharedPreferences(SwmPref.PREF_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();

                if(config.hasSystemId())
                    editor.putString(SwmPref.SWM_SYSTEM_ID, config.systemId);

                if(config.hasModel())
                    editor.putString(SwmPref.SWM_MODEL_NUMBER, config.model);

                if(config.hasSerial())
                    editor.putString(SwmPref.SWM_SERIAL_NUMBER, config.serial);

                if(config.hasHardwareRevision())
                    editor.putString(SwmPref.SWM_HARDWARE_REVISION, config.hardwareRevision);

                if(config.hasFirmwareRevision())
                    editor.putString(SwmPref.SWM_FIRMWARE, config.firmwareRevision);

                if(config.hasSoftwareRevision())
                    editor.putString(SwmPref.SWM_SOFTWARE_REVISION, config.softwareRevision);

                if(config.hasManufactureName())
                    editor.putString(SwmPref.SWM_MANUFACTURE_NAME, config.manufacture);

                editor.apply();

            }
        });

        if(mScanning)
            stopScan();

        if (mAutoRescan)
            mAutoRescan = false;

        savePrefDevice(device);
        Log.i(LOG_TAG, "Connect to bluetooth device");

    }

    private void savePrefDevice(BluetoothDevice device) {
        SharedPreferences pref = mContext.getSharedPreferences(SwmPref.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(SwmPref.SWM_DEVICE_ADDRESS, device.getAddress());
        editor.commit();
    }

    void setIcon(View view) {
        mBleIconHandler.setIcon(view);
    }

    void setUIHandler(Handler handler) {
        mUIHandler = handler;
    }

    synchronized void startScan(boolean autoRescan, @Nullable final String address, @NonNull final BleManagerCallback callback) {
        if(mScanning)
            return;

        mScanning = true;
        mAutoRescan = autoRescan;
        final Map<String, BluetoothDevice> devices = new HashMap<String, BluetoothDevice>();
        mBleIconHandler.scanning();

        final BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (Build.VERSION.SDK_INT >= 21) {
            mNewCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);

                    BluetoothDevice device = result.getDevice();

                    if (TextUtils.equals(address, device.getAddress())) {
                        stopScan();
                        callback.onDeviceFound(device);
                    }

                    if (!devices.containsKey(device.getAddress()))
                        devices.put(device.getAddress(), device);
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    super.onBatchScanResults(results);
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                }
            };

            mScanner = mBluetoothAdapter.getBluetoothLeScanner();
            ScanSettings.Builder scanBuilder = new ScanSettings.Builder();
            scanBuilder.setScanMode(ScanSettings.SCAN_MODE_BALANCED);
            ScanSettings settings = scanBuilder.build();

            ScanFilter.Builder filterBuilder = new ScanFilter.Builder();
            filterBuilder.setDeviceName(SWM_DEVICE);
            ScanFilter filter = filterBuilder.build();
            List filters = new ArrayList();
            filters.add(filter);
            mScanner.startScan(filters, settings, mNewCallback);

        } else {
            mCallback = new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                    if(TextUtils.equals(SWM_DEVICE, device.getName())) {

                        if (TextUtils.equals(address, device.getAddress())) {
                            stopScan();
                            callback.onDeviceFound(device);
                        }

                        if (!devices.containsKey(device.getAddress())) {
                            devices.put(device.getAddress(), device);
                        }
                    }
                }
            };

            mBluetoothAdapter.startLeScan(mCallback);
        }

        if (mTimer != null) {
            mMyHandler.removeCallbacks(mTimer);
        }

        mTimer = new Runnable() {
            @Override
            public void run() {
                stopScan();
                callback.onListDevice(devices.values().toArray(new BluetoothDevice[devices.values().size()]));
                if (mAutoRescan)
                    startScan(mAutoRescan, address, callback);
            }
        };

        mMyHandler.postDelayed(mTimer, SCAN_PERIOD);
    }

    void connecting() {
        mBleIconHandler.connecting();
    }

    synchronized void stopScan() {
        if(!mScanning)
            return;

        mScanning = false;
        mBleIconHandler.idle();

        if (Build.VERSION.SDK_INT >= 21) {
            mScanner.stopScan(mNewCallback);
        } else {
            mBluetoothAdapter.stopLeScan(mCallback);
        }
        if (mTimer != null) {
            mMyHandler.removeCallbacks(mTimer);
        }
    }

    void removeListener() {
        SwmDeviceModule.getIns().removeListener();
    }

    void disconnect() {
        SwmDeviceModule.getIns().disconnect();
    }
}
