package com.swm.core;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.List;
import java.util.UUID;

/**
 * Created by yangzhenyu on 2017/1/24.
 */

class InformationBleProfile {
    static final UUID SERVICE = UUID.fromString("00000180A-0000-1000-8000-00805F9B34FB");

    static final UUID SYSTEM_ID = UUID.fromString("000002A23-0000-1000-8000-00805F9B34FB");
    private BluetoothGattCharacteristic mSystemIdChar;

    static final UUID MODEL_NUMBER = UUID.fromString("00002A24-0000-1000-8000-00805F9B34FB");
    private BluetoothGattCharacteristic mModelNumberChar;

    static final UUID SERIAL_NUMBER = UUID.fromString("00002A25-0000-1000-8000-00805F9B34FB");
    private BluetoothGattCharacteristic mSearialNumberChar;

    static final UUID HARDWARE_REVISION = UUID.fromString("00002A27-0000-1000-8000-00805F9B34FB");
    private BluetoothGattCharacteristic mHardwareRevisionChar;

    static final UUID FIRMWARE_REVISION = UUID.fromString("00002A26-0000-1000-8000-00805F9B34FB");
    private BluetoothGattCharacteristic mFirmwareRevisionChar;

    static final UUID SOFTWARE_REVISION = UUID.fromString("00002A28-0000-1000-8000-00805F9B34FB");
    private BluetoothGattCharacteristic mSoftwareRevisionChar;

    static final UUID MANUFACTURE_NAME = UUID.fromString("00002A29-0000-1000-8000-00805F9B34FB");
    private BluetoothGattCharacteristic mManufactureNameChar;

    static final UUID BLE_11073_CERT_DATA = UUID.fromString("20000A2A-0000-1000-8000-00805F9B34FB");
    private BluetoothGattCharacteristic mBle11073CertDataChar;

    static final UUID PNP_ID = UUID.fromString("00002A50-0000-1000-8000-00805F9B34FB");
    private BluetoothGattCharacteristic mPnpIdChar;

    InformationBleProfile(BluetoothGattService service) {
        List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
        for (BluetoothGattCharacteristic characteristic : characteristics) {

            if (characteristic.getUuid().equals(SYSTEM_ID)) {
                mSystemIdChar = characteristic;
            }

            if (characteristic.getUuid().equals(MODEL_NUMBER)) {
                mModelNumberChar = characteristic;
            }

            if (characteristic.getUuid().equals(SERIAL_NUMBER)) {
                mSearialNumberChar = characteristic;
            }

            if (characteristic.getUuid().equals(HARDWARE_REVISION)) {
                mHardwareRevisionChar = characteristic;
            }

            if (characteristic.getUuid().equals(FIRMWARE_REVISION)) {
                mFirmwareRevisionChar = characteristic;
            }

            if (characteristic.getUuid().equals(SOFTWARE_REVISION)) {
                mSoftwareRevisionChar = characteristic;
            }

            if (characteristic.getUuid().equals(MANUFACTURE_NAME)) {
                mManufactureNameChar = characteristic;
            }

            if (characteristic.getUuid().equals(BLE_11073_CERT_DATA)) {
                mBle11073CertDataChar = characteristic;
            }

            if (characteristic.getUuid().equals(PNP_ID)) {
                mPnpIdChar = characteristic;
            }
        }
    }

    void readSystemId(BluetoothGatt gatt) {
        gatt.readCharacteristic(mSystemIdChar);
    }

    void readModelNumber(BluetoothGatt gatt) {
        gatt.readCharacteristic(mModelNumberChar);
    }

    void readSerialNumber(BluetoothGatt gatt) {
        gatt.readCharacteristic(mSearialNumberChar);
    }

    void readHardwareRevision(BluetoothGatt gatt) {
        gatt.readCharacteristic(mHardwareRevisionChar);
    }

    void readFirmwareRevision(BluetoothGatt gatt) {
        BleProfileHelper.readCharacteristic(mFirmwareRevisionChar);
    }

    void readSoftwareRevision(BluetoothGatt gatt) {
        gatt.readCharacteristic(mSoftwareRevisionChar);
    }

    void readManufactureName(BluetoothGatt gatt) {
        BleProfileHelper.readCharacteristic(mManufactureNameChar);
    }

    void read11073CertData(BluetoothGatt gatt) {
        gatt.readCharacteristic(mBle11073CertDataChar);
    }

    void readPnpId(BluetoothGatt gatt) {
        gatt.readCharacteristic(mPnpIdChar);
    }
}
