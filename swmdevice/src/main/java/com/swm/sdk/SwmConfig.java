package com.swm.sdk;

import android.bluetooth.BluetoothGattCharacteristic;
import android.text.TextUtils;

/**
 * Created by yangzhenyu on 2017/3/10.
 */

public class SwmConfig {
    private static class Builder {
        private String systemId;
        private String model;
        private String serial;
        private String hardwareRevision;
        private String firmwareRevision;
        private String softwareRevision;
        private String manufacture;

        private SwmConfig build(){
            return new SwmConfig(systemId, model, serial, hardwareRevision, firmwareRevision, softwareRevision, manufacture);
        }

        private Builder setSystemId(String systemId) {
            this.systemId = systemId;
            return this;
        }

        private Builder setModel(String model) {
            this.model = model;
            return this;
        }

        private Builder setSerial(String serial) {
            this.serial = serial;
            return this;
        }

        private Builder setHardwareRevision(String revision) {
            this.hardwareRevision = revision;
            return this;
        }

        private Builder setFirmwareRevision(String revision) {
            this.firmwareRevision = revision;
            return this;
        }

        private Builder setSoftwareRevision(String revision) {
            this.softwareRevision = revision;
            return this;
        }

        private Builder setManufacture(String manufacture) {
            this.manufacture = manufacture;
            return this;
        }
    }

    public final String systemId;
    public final String model;
    public final String serial;
    public final String hardwareRevision;
    public final String firmwareRevision;
    public final String softwareRevision;
    public final String manufacture;

    private SwmConfig(String systemId, String model, String serial, String hardwareRevision, String firmwareRevision, String softwareRevision, String manufacture) {
        this.systemId = systemId;
        this.model = model;
        this.serial = serial;
        this.hardwareRevision = hardwareRevision;
        this.firmwareRevision = firmwareRevision;
        this.softwareRevision = softwareRevision;
        this.manufacture = manufacture;
    }

    static SwmConfig from(BluetoothGattCharacteristic characteristic) {
        SwmConfig.Builder builder = new SwmConfig.Builder();
        if (characteristic.getUuid().equals(InformationBleProfile.SYSTEM_ID)) {
            String systemId = new String(characteristic.getValue());
            builder.setSystemId(systemId);
        }

        if (characteristic.getUuid().equals(InformationBleProfile.MODEL_NUMBER)) {
            String model = new String(characteristic.getValue());
            builder.setModel(model);
        }

        if (characteristic.getUuid().equals(InformationBleProfile.SERIAL_NUMBER)) {
            String serial = new String(characteristic.getValue());
            builder.setSerial(serial);
        }

        if (characteristic.getUuid().equals(InformationBleProfile.HARDWARE_REVISION)) {
            String hardwareRevision = new String(characteristic.getValue());
            builder.setHardwareRevision(hardwareRevision);
        }

        if (characteristic.getUuid().equals(InformationBleProfile.FIRMWARE_REVISION)) {
            String firmware = new String(characteristic.getValue());
            builder.setFirmwareRevision(firmware);
        }

        if (characteristic.getUuid().equals(InformationBleProfile.SOFTWARE_REVISION)) {
            String softwareRevision = new String(characteristic.getValue());
            builder.setSoftwareRevision(softwareRevision);
        }

        if (characteristic.getUuid().equals(InformationBleProfile.MANUFACTURE_NAME)) {
            String manufacture = new String(characteristic.getValue());
            builder.setManufacture(manufacture);
        }
        return builder.build();
    }

    public boolean hasSystemId() {
        return !TextUtils.isEmpty(systemId);
    }

    public boolean hasModel() {
        return !TextUtils.isEmpty(model);
    }

    public boolean hasSerial() {
        return !TextUtils.isEmpty(serial);
    }

    public boolean hasHardwareRevision() {
        return !TextUtils.isEmpty(hardwareRevision);
    }

    public boolean hasFirmwareRevision() {
        return !TextUtils.isEmpty(firmwareRevision);
    }

    public boolean hasSoftwareRevision() {
        return !TextUtils.isEmpty(softwareRevision);
    }

    public boolean hasManufactureName() {
        return !TextUtils.isEmpty(manufacture);
    }
}
