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
    private EcgBleProfile ecgBleProfile;
    private MotionBleProfile motionBleProfile;
    private BatteryBleProfile batteryBleProfile;
    private InformationBleProfile informationBleProfile;
    private PressureBleProfile pressureBleProfile;

    private DeviceCallback callback;
    private SwmEngine mSwmEngine;

    private volatile boolean ecgEnable;
    private volatile boolean motionEnable;
    private volatile boolean pressureEnable;

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
                    ecgBleProfile = new EcgBleProfile(OneFiveZeroOne.this, service, gatt);
                    try {
                        enableEcgService(true);
                        ecgBleProfile.enableNotification();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if(service.getUuid().equals(MotionBleProfile.SERVICE)) {
                    motionBleProfile = new MotionBleProfile(OneFiveZeroOne.this, service, gatt);

                    try {
                        enableMotionService(true);
                        motionBleProfile.enableNotification();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if(service.getUuid().equals(BatteryBleProfile.SERVICE)) {
                    batteryBleProfile = new BatteryBleProfile(OneFiveZeroOne.this, service, gatt);
                    try {
                        batteryBleProfile.enableNotification();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (service.getUuid().equals(InformationBleProfile.SERVICE)) {
                    informationBleProfile = new InformationBleProfile(service, OneFiveZeroOne.this);
                    informationBleProfile.readFirmwareRevision(gatt);
                    informationBleProfile.readManufactureName(gatt);
                }

                if(service.getUuid().equals(PressureBleProfile.SERVICE)) {
                    pressureBleProfile = new PressureBleProfile(OneFiveZeroOne.this, service, gatt);

                    try {
                        enablePressureService(true);
                        pressureBleProfile.enableNotification();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
            mSwmEngine.process(BleData.from(characteristic));
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
                            case BleCommand.NOTIFICATION:
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
                                    if(ecgBleProfile.isEnableValue(command.characteristic.getValue()))
                                        ecgEnable = true;
                                    else
                                        ecgEnable = false;
                                } else if(command.characteristic.getUuid().equals(MotionBleProfile.CONF)) {
                                    if (motionBleProfile.isEnableValue(command.characteristic.getValue()))
                                        motionEnable = true;
                                    else
                                        motionEnable = false;
                                } else if(command.characteristic.getUuid().equals(PressureBleProfile.CONF)) {
                                    if(pressureBleProfile.isEnableValue(command.characteristic.getValue()))
                                        pressureEnable = true;
                                    else
                                        pressureEnable = false;
                                }
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

    OneFiveZeroOne(Context context, BluetoothDevice device, SwmEngine service) {
        mContext = context;
        mDevice = device;
        mSwmEngine = service;
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
        mListener.onServiceStateChange(SwmListener.Service.PRESSURE, pressureEnable);
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
    public void enableEcgService(boolean enable) throws Exception {
        if(enable) {
            ecgBleProfile.enableNotification();
            ecgBleProfile.enableService();
        } else {
            ecgBleProfile.disableNotification();
            ecgBleProfile.disableService();
        }
    }

    @Override
    public void enableMotionService(boolean enable) throws Exception {
        if(enable) {
            motionBleProfile.enableNotification();
            motionBleProfile.enableService();
        } else {
            motionBleProfile.disableNotification();
            motionBleProfile.disableService();
        }
    }

    @Override
    public void enablePressureService(boolean enable) throws Exception {
        if(enable) {
            pressureBleProfile.enableNotification();
            pressureBleProfile.enableService();
        } else {
            pressureBleProfile.disableNotification();
            pressureBleProfile.disableService();
        }
    }

    @Override
    public boolean isEcgServiceEnable() {
        return ecgEnable;
    }

    @Override
    public boolean isMotionServiceEnable() {
        return motionEnable;
    }

    @Override
    public boolean isPressureServiceEnable() {
        return pressureEnable;
    }

    @Override
    public void registerEngine(SwmEngine swmEngine) {

    }

}
