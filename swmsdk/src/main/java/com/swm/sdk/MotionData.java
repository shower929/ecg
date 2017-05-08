package com.swm.sdk;

/**
 * Created by yangzhenyu on 2016/9/27.
 */

abstract class MotionData {
    public final double x;
    public final double y;
    public final double z;

    MotionData(int x, int y, int z) {
        this.x = twosComplement(x);
        this.y = twosComplement(y);
        this.z = twosComplement(z);
    }

    private int twosComplement(int in) {
        if ((in & 0x8000) == 0x8000)
            return -1 * ((in ^ 0xFFFF) + 0x0001);
        else
            return in;
    }

    void putData() {

    }
}
