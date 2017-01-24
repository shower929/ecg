package com.swm.core;

import java.util.Arrays;

/**
 * Created by yangzhenyu on 2016/9/27.
 */

class SwmData extends DumpData{
    public static final int MOTION = 1;
    public static final int ECG = 2;
    public static final int ACC = 3;
    public static final int BREATH = 4;
    static final int INFORMATION = 6;

    public final int dataType;
    public final byte[] value;

    public SwmData(int dataType, byte[] value) {
        this.dataType = dataType;
        this.value = value;
    }

    public static SwmData motionDataFrom(BleData bleData) {
        return new SwmData(SwmData.MOTION, bleData.rawData);
    }

    public static SwmData ecgDataFrom(BleData bleData) {
        return new SwmData(SwmData.ECG, bleData.rawData);
    }

    public static SwmData accDataFrom(BleData bleData) {
        return new SwmData(SwmData.ACC, Arrays.copyOfRange(bleData.rawData, 0, 6));
    }

    public static SwmData breathDataFrom(BleData bleData) {
        return new SwmData(SwmData.BREATH, Arrays.copyOfRange(bleData.rawData, 8, 10));
    }

    static SwmData informationDataFrom(BleData bleData) {
        return new SwmData(SwmData.INFORMATION, bleData.rawData);
    }
    @Override
    byte[] dump() {
        return value;
    }

}
