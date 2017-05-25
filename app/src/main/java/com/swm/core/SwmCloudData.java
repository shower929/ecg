package com.swm.core;

import com.swm.sdk.EcgData;

/**
 * Created by yangzhenyu on 2016/10/30.
 */

public class SwmCloudData {
    public final int heartRate;
    public final EcgData ecg;
    public final int breathRate;

    SwmCloudData(int heartRate, EcgData ecg, int breathRate) {
        this.heartRate = heartRate;
        this.ecg = ecg;
        this.breathRate = breathRate;
    }
}
