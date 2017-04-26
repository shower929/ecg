package com.swm.sdk;

import com.swm.sdk.DumpData;

/**
 * Created by yangzhenyu on 2016/10/5.
 */

public class HeartRateData extends DumpData {
    public final int heartRate;

    HeartRateData(int heartRate) {
        this.heartRate = heartRate;
    }

    @Override
    byte[] dump() {
        return String.valueOf(heartRate).getBytes();
    }

}
