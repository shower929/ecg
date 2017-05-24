package com.swm.sdk;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Created by yangzhenyu on 2017/5/14.
 */

public class EcgData {
    public final double[] samples;

    private EcgData(double[] samples) {
        this.samples = samples;
    }

    static EcgData fromBle(BleData data) {
        int len = data.rawData.length;
        double[] samples = new double[5];
        int j = 0;

        for(int i = 10; i < len - 1; i = i + 2)
            samples[j++] = (data.rawData[i+1] << 8 & 0xFF00) | (data.rawData[i] & 0xFF);

        return new EcgData(samples);
    }
}
