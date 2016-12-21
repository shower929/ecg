package com.swm.core;

/**
 * Created by yangzhenyu on 2016/10/5.
 */

public class HeartBeatData extends DumpData {
    public final int heartRate;

    HeartBeatData(int heartRate) {
        this.heartRate = heartRate;
    }

    @Override
    byte[] dump() {
        return String.valueOf(heartRate).getBytes();
    }

}
