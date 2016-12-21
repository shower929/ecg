package com.swm.core;

/**
 * Created by yangzhenyu on 2016/10/2.
 */

public class BreathData extends DumpData{
    public final int breathRate;

    BreathData (int breathRate) {
        this.breathRate = breathRate;
    }

    @Override
    public byte[] dump() {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) breathRate;
        return bytes;
    }

}
