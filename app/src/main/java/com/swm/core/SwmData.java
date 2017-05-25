package com.swm.core;

import com.swm.sdk.DumpData;

import java.util.Arrays;

/**
 * Created by yangzhenyu on 2016/9/27.
 */

class SwmData extends DumpData {
    static final int MOTION = 1;
    static final int ECG = 2;
    static final int ACC = 3;
    static final int BREATH = 4;
    static final int BATTERY = 5;
    static final int INFORMATION = 6;

    final int dataType;
    final byte[] value;

    SwmData(int dataType, byte[] value) {
        this.dataType = dataType;
        this.value = value;
    }

    static SwmData motionDataFrom(BleData bleData) {
        return new SwmData(SwmData.MOTION, bleData.rawData);
    }

    static SwmData ecgDataFrom(BleData bleData) {
        return new SwmData(SwmData.ECG, bleData.rawData);
    }

    static SwmData accDataFrom(BleData bleData) {
        return new SwmData(SwmData.ACC, Arrays.copyOfRange(bleData.rawData, 0, 6));
    }

    static SwmData breathDataFrom(BleData bleData) {
        return new SwmData(SwmData.BREATH, Arrays.copyOfRange(bleData.rawData, 8, 10));
    }

    static SwmData batteryDataFrom(BleData bleData) {
        return new SwmData(SwmData.BATTERY, bleData.rawData);
    }

    static SwmData informationDataFrom(BleData bleData) {
        return new SwmData(SwmData.INFORMATION, bleData.rawData);
    }

    @Override
    byte[] dump() {
        return value;
    }

}
