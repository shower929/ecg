package com.swm.sdk;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by yangzhenyu on 2017/3/9.
 */

class OneFiveZeroOne implements SwmDevice, BleDevice {
    private static final String LOG_TAG = "OneFiveZeroOne";
    private BluetoothDevice mDevice;
    private BluetoothGatt mBluetoothGatt;
    private LinkedBlockingQueue<BleCommand> queue;
    private boolean mRunning = true;
    private BleReqWorker mBleReqWorker;
    private Context mContext;
    private boolean sConnected = false;
    private SwmListener mListener;
    private EcgBleProfile mEcgBleProfile;
    private MotionBleProfile mMotionBleProfile;
    private BatteryBleProfile mBatteryBleProfile;
    private InformationBleProfile mInformationBleProfile;

    private DeviceCallback callback;
    private SwmService mSwmService;

    private volatile boolean ecgEnable;
    private volatile boolean motionEnable;

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            super.onConnectionStateChange(gatt, status, newState);
            Log.i(LOG_TAG, "Connect status: " + status + ", new state: " + newState);

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                if(!gatt.discoverServices())
                    Log.e(LOG_TAG, "Bluetooth device is busy");
                sConnected = true;
                if (Build.VERSION.SDK_INT >= 21)
                    mBluetoothGatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);

                if(mListener != null) {
                    mListener.onConnectStateChanged(SwmListener.CONNECTED);
                }
            }
            if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                sConnected = false;
                if (mListener != null) {
                    mListener.onConnectStateChanged(SwmListener.DISCONNECTED);
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
                    mEcgBleProfile = new EcgBleProfile(service, OneFiveZeroOne.this);
                    try {
                        enableEcg(true);
                        mEcgBleProfile.enableEcgNotification(gatt, OneFiveZeroOne.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if(service.getUuid().equals(MotionBleProfile.SERVICE)) {
                    mMotionBleProfile = new MotionBleProfile(service, OneFiveZeroOne.this);
                    try {
                        enableMotion(true);
                        mMotionBleProfile.enableMotionNotification(gatt, OneFiveZeroOne.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if(service.getUuid().equals(BatteryBleProfile.SERVICE)) {
                    mBatteryBleProfile = new BatteryBleProfile(service, OneFiveZeroOne.this);
                    try {
                        mBatteryBleProfile.enableBatteryPercentNoti(gatt, OneFiveZeroOne.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                if (service.getUuid().equals(InformationBleProfile.SERVICE)) {
                    mInformationBleProfile = new InformationBleProfile(service, OneFiveZeroOne.this);
                    mInformationBleProfile.readFirmwareRevision(gatt);
                    mInformationBleProfile.readManufactureName(gatt);
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if(status != BluetoothGatt.GATT_SUCCESS)
                return;
            BleCommand.Builder builder = new BleCommand.Builder();
            builder.setCharacteristic(characteristic)
                    .setCommand(BleCommand.READ_DONE);
            queue.offer(builder.build());
            SwmConfig config = SwmConfig.from(characteristic);
            callback.readConfig(config);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            BleCommand.Builder builder = new BleCommand.Builder();
            builder.setCharacteristic(characteristic)
                .setCommand(BleCommand.WRITE_DONE);
            queue.offer(builder.build());
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            mSwmService.process(BleData.from(characteristic));
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            BleCommand.Builder builder = new BleCommand.Builder();
            builder.setDescriptor(descriptor)
                    .setCommand(BleCommand.READ_DONE);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            BleCommand.Builder builder = new BleCommand.Builder();
            builder.setCommand(BleCommand.ENABLE_NOTI_DONE);
            queue.offer(builder.build());
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

    private class BleReqWorker extends Thread {

        private BleCommand processing;
        private List<BleCommand> pending;

        BleReqWorker() {
            pending = new ArrayList<>();
        }

        @Override
        public void run() {
            super.run();
            for(;;) {
                if(mRunning) {
                    try {

                        BleCommand command = queue.take();

                        switch(command.command) {
                            case BleCommand.ENABLE_NOTI:
                                if(processing != null)
                                    pending.add(command);
                                else
                                    if(mBluetoothGatt.writeDescriptor(command.descriptor))
                                        processing = command;
                                break;
                            case BleCommand.WRITE:
                                if(processing != null)
                                    pending.add(command);
                                else
                                    if(mBluetoothGatt.writeCharacteristic(command.characteristic))
                                        processing = command;
                                    break;
                            case BleCommand.READ:
                                if(processing != null)
                                    pending.add(command);
                                else
                                    if(mBluetoothGatt.readCharacteristic(command.characteristic))
                                        processing = command;
                                break;
                            case BleCommand.WRITE_DONE:
                                if(command.characteristic.getUuid().equals(EcgBleProfile.CONF)) {
                                    if(mEcgBleProfile.enable(command.characteristic.getValue()))
                                        ecgEnable = true;
                                    else
                                        ecgEnable = false;
                                } else if(command.characteristic.getUuid().equals(MotionBleProfile.CONF))
                                    if(mMotionBleProfile.enable(command.characteristic.getValue()))
                                        motionEnable = true;
                                    else
                                        motionEnable = false;
                                notifyUpdate();
                            case BleCommand.READ_DONE:
                            case BleCommand.ENABLE_NOTI_DONE:

                                processing = null;

                                for(BleCommand c : pending) {
                                    queue.offer(c);
                                }
                                pending.clear();
                                break;
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    OneFiveZeroOne(Context context, BluetoothDevice device, SwmService service) {
        mContext = context;
        mDevice = device;
        mSwmService = service;
        queue = new LinkedBlockingQueue<>();
        mBleReqWorker = new BleReqWorker();
        mBleReqWorker.start();
    }

    @Override
    public void sendCommand(BleCommand command) {
        queue.offer(command);
    }

    @Override
    public void connect(DeviceCallback callback) {
        this.callback = callback;
        mBluetoothGatt = mDevice.connectGatt(mContext, true, mGattCallback);
    }

    @Override
    public void disconnect() {
        if (mBluetoothGatt == null)
            return;

        mBluetoothGatt.disconnect();
    }

    @Override
    public synchronized void setListener(SwmListener listener) {
        mListener = listener;
        if(mListener == null)
            return;
        notifyUpdate();
    }

    private void notifyUpdate() {
        mListener.onConnectStateChanged(sConnected ? SwmListener.CONNECTED : SwmListener.DISCONNECTED);
        mListener.onServiceStateChange(SwmListener.Service.ECG, ecgEnable);
        mListener.onServiceStateChange(SwmListener.Service.MOTION, motionEnable);
    }

    @Override
    public synchronized void removeListener() {
        mListener = null;
    }

    @Override
    public boolean isConnected(){
        return sConnected;
    }

    @Override
    public void enableEcg(boolean enable) {
        if(enable)
            mEcgBleProfile.enableService();
        else
            mEcgBleProfile.disableService();
    }

    @Override
    public void enableMotion(boolean enable) {
        if(enable)
            mMotionBleProfile.enableService();
        else
            mMotionBleProfile.disableService();
    }

    @Override
    public boolean isEcgEnable() {
        return ecgEnable;
    }

    @Override
    public boolean isMotionEnable() {
        return motionEnable;
    }
}
