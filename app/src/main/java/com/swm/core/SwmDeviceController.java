package com.swm.core;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.swm.device.SwmDeviceListener;
import com.swm.heart.BuildConfig;
import com.swm.heart.pref.SwmPref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by yangzhenyu on 2016/9/22.
 */
 public class SwmDeviceController {
    private static final String LOG_TAG = "Device";
    public static final int MSG_NEW_DEVICES = 1;
    public static final int MSG_DONE = 2;
    public static final String DATA_DEVICES = "data_devices";
    private Handler mUIHandler;
    private BleIconHandler mBleIconHandler;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mMyHandler;
    private static final int SCAN_PERIOD = 3 * 1000;
    private Runnable mTimer;
    private BluetoothAdapter.LeScanCallback mCallback;
    private ScanCallback mNewCallback;
    private static final String SWM_DEVICE = "CC2650 SensorTag";
    private String mPrefAddress;
    private static SwmDeviceController mSwmDeviceController;
    private Context mContext;
    private BluetoothDevice mDevice;
    private EcgBleProfile mEcgBleProfile;
    private MotionBleProfile mMotionBleProfile;
    private BluetoothGatt mBluetoothGatt;
    private LinkedBlockingQueue<BleRequest> mBleReqQueue;
    private boolean mRunning = true;
    private BleReqWorker mBleReqWorker;
    private SwmDeviceListener mListener;
    static boolean sConnected = false;
    private boolean mScanning = false;
    private boolean mAutoRescan = false;
    private BluetoothLeScanner mScanner;

    private class BleReqWorker extends Thread {
        private static final int IDLE = 0;
        private static final int DESCRIPTOR_WRITING = 1;
        private static final int CHARACTERISTIC_WRITING = 2;

        private int mState;
        private List<BleRequest> mPendingReqs;

        BleReqWorker() {
            mPendingReqs = new ArrayList<>();
        }

        @Override
        public void run() {
            super.run();
            for(;;) {
                if(mRunning) {
                    try {
                        BleRequest bleRequest = mBleReqQueue.take();
                        if (bleRequest.request == BleRequest.WRITE_DESCRIPTOR
                                || bleRequest.request == BleRequest.WRITE_CHARACTERISTIC){
                            if (mState != IDLE) {
                                mPendingReqs.add(bleRequest);
                                continue;
                            }

                            switch(bleRequest.request) {
                                case BleRequest.WRITE_DESCRIPTOR:
                                    if(mBluetoothGatt.writeDescriptor(bleRequest.descriptor))
                                        mState = DESCRIPTOR_WRITING;
                                    break;
                                case BleRequest.WRITE_CHARACTERISTIC:
                                    if(mBluetoothGatt.writeCharacteristic(bleRequest.characteristic))
                                        mState = CHARACTERISTIC_WRITING;
                                    break;
                            }
                        }

                        if (bleRequest.request == BleRequest.DESCRIPTOR_WRITE_DONE
                                && mState == DESCRIPTOR_WRITING) {
                            mState = IDLE;
                        }


                        if (bleRequest.request == BleRequest.CHARACTERISTIC_WRITE_DONE
                                && mState == CHARACTERISTIC_WRITING) {
                            mState = IDLE;
                        }

                        if(mState == IDLE) {
                            for(BleRequest pendingReq : mPendingReqs) {
                                mBleReqQueue.offer(pendingReq);
                            }
                            mPendingReqs.clear();
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        synchronized int getWorkingState() {
            return mState;
        }
    }

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.i(LOG_TAG, "Connect status: " + status + ", new state: " + newState);

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                if(!gatt.discoverServices())
                    Log.e(LOG_TAG, "Bluetooth device is busy");
                sConnected = true;
                if(mListener != null) {
                    mListener.onConnectStateChanged(SwmDeviceListener.CONNECTED);
                }
            }
            if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                sConnected = false;
                if (mListener != null) {
                    mListener.onConnectStateChanged(SwmDeviceListener.DISCONNECTED);
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            List<BluetoothGattService> services = gatt.getServices();
            for(BluetoothGattService service : services) {
                Log.d(LOG_TAG, "Service: " + service.getUuid().toString());
                if(service.getUuid().equals(EcgBleProfile.SERVICE)) {
                    mEcgBleProfile = new EcgBleProfile(service);
                    try {
                        mEcgBleProfile.enableNotification(gatt);
                        mEcgBleProfile.enableService();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if(!BuildConfig.ENGINEERING)
                    return;

                //if(service.getUuid().equals(MotionBleProfile.SERVICE)) {
                    //mMotionBleProfile = new MotionBleProfile(service);
                    //try {
                        //mMotionBleProfile.enableNotification(gatt);
                        //mMotionBleProfile.enableService();
                    //} catch (Exception e) {
                        //e.printStackTrace();
                   //}
                //}

            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            BleRequest.Builder builder = new BleRequest.Builder();
            builder.setRequest(BleRequest.CHARACTERISTIC_WRITE_DONE);
            mBleReqQueue.offer(builder.build());
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            SwmCore.getIns().onBleDataAvailable(BleData.from(characteristic));
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            BleRequest.Builder builder = new BleRequest.Builder();
            builder.setRequest(BleRequest.DESCRIPTOR_WRITE_DONE);
            mBleReqQueue.offer(builder.build());
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }
    };

    private SwmDeviceController(Context context) {
        mBleIconHandler = new BleIconHandler();
        mMyHandler = new Handler();
        mContext = context;
        mBleReqQueue = new LinkedBlockingQueue<>();
        mBleReqWorker = new BleReqWorker();
        mBleReqWorker.start();
        SharedPreferences pref = mContext.getSharedPreferences(SwmPref.PREF_NAME, Context.MODE_PRIVATE);
        mPrefAddress = pref.getString(SwmPref.SWM_DEVICE_ADDRESS, "");
    }

    public static synchronized void init(Context context) {
        if (mSwmDeviceController == null) {
            mSwmDeviceController = new SwmDeviceController(context);
        }
    }

    static synchronized SwmDeviceController getIns() {
        return mSwmDeviceController;
    }

    void connectDevice(BluetoothDevice device) {
        mDevice = device;

        if(mScanning)
            stopScan();

        if (mAutoRescan)
            mAutoRescan = false;

        savePrefDevice(device);
        Log.i(LOG_TAG, "Connect to bluetooth device");
        mBluetoothGatt = mDevice.connectGatt(mContext, true, mGattCallback);
        if (Build.VERSION.SDK_INT >= 21)
            mBluetoothGatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);

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

    void onScanDone(BluetoothDevice[] devices) {

        if (mUIHandler == null)
            return;

        Message msg = mUIHandler.obtainMessage(MSG_NEW_DEVICES);
        Bundle data = new Bundle();
        data.putParcelableArray(DATA_DEVICES, devices);
        msg.setData(data);
        mUIHandler.sendMessage(msg);

    }

    synchronized void startScan(boolean autoRescan) {
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

                    if (TextUtils.equals(mPrefAddress, device.getAddress())) {
                        connectDevice(device);
                        stopScan();
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

                        if (TextUtils.equals(mPrefAddress, device.getAddress())) {
                            connectDevice(device);
                            stopScan();
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
                onScanDone(devices.values().toArray(new BluetoothDevice[devices.values().size()]));
                if (mAutoRescan)
                    startScan(mAutoRescan);
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
            if (mTimer != null) {
                mMyHandler.removeCallbacks(mTimer);
            }
            mBluetoothAdapter.stopLeScan(mCallback);
        }
    }

    public void request(BleRequest bleRequest) {
        mBleReqQueue.offer(bleRequest);
    }

    void setListener(SwmDeviceListener listener) {
        mListener = listener;
        mListener.onConnectStateChanged(sConnected ? SwmDeviceListener.CONNECTED : SwmDeviceListener.DISCONNECTED);
        if (!sConnected)
            startScan(true);

    }

    void removeListener() {
        mListener = null;
    }

    void disconnect() {
        if (mBluetoothGatt == null)
            return;

        mBluetoothGatt.disconnect();
    }
}
