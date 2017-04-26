package com.swm.sdk;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.List;
import java.util.UUID;

/**
 * Created by yangzhenyu on 2017/3/17.
 */

abstract class GenericBleProfile {
    private BleDevice device;
    private BluetoothGatt gatt;
    private BluetoothGattCharacteristic config;
    private BluetoothGattCharacteristic data;
    abstract byte[] getEnableData();
    abstract byte[] getDisableData();
    abstract UUID getDataUuid();
    abstract UUID getConfigUuid();

    public GenericBleProfile(BleDevice device, BluetoothGattService service, BluetoothGatt gatt) {
        this.device = device;
        this.gatt = gatt;
        setService(service);
    }

    private void setService(BluetoothGattService service) {
        UUID dataUuid = getDataUuid();
        UUID configUuid = getConfigUuid();

        List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
        for (BluetoothGattCharacteristic characteristic : characteristics) {

            if (characteristic.getUuid().equals(dataUuid)) {
                data = characteristic;
            }

            if (characteristic.getUuid().equals(configUuid)) {
                config = characteristic;
            }
        }
    }

    boolean isEnableValue(byte[] value) {
        byte[] enableData = getEnableData();
        if (enableData.length != value.length) {
            StringBuilder data = new StringBuilder();

            for(byte v: value)
                data.append(v);

            throw new RuntimeException("Invalid value: " + data.toString());
        }


        for(int i = 0; i < value.length; i++) {
            if (value[i] != enableData[i])
                return false;
        }

        return true;
    }

    synchronized void enableService() {
        config.setValue(getEnableData());
        device.sendCommand(BleCommandHelper.getWriteCommand(config));
    }

    synchronized void disableService() {
        config.setValue(getDisableData());
        BleCommand command = BleCommandHelper.getWriteCommand(config);
        device.sendCommand(command);
    }

    void enableNotification() throws Exception {
        if(!gatt.setCharacteristicNotification(data, true))
            throw new Exception("Set bluetooth notification fail");
        BleCommand command = BleCommandHelper.getEnableNotificationCommand(data);
        device.sendCommand(command);
    }

    void disableNotification() throws Exception {
        if(!gatt.setCharacteristicNotification(data, false))
            throw new Exception("Set bluetooth notification fail");
        BleCommand command = BleCommandHelper.getEnableNotificationCommand(data);
        device.sendCommand(command);
    }
}
