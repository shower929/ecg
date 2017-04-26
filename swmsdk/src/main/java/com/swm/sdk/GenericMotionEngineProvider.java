package com.swm.sdk;

import android.content.Context;

/**
 * Created by yangzhenyu on 2017/3/29.
 */

abstract class GenericMotionEngineProvider {
    static MotionEngine MOTION_ENGINE;

    public synchronized static MotionEngine newEngine(SwmDevice device) {
        if(!(device instanceof HeartMotionDevice))
            throw new RuntimeException("The device is not support motion service");

        HeartMotionDevice heartMotionDevice = (HeartMotionDevice)device;
        heartMotionDevice.installMotionEngine(MOTION_ENGINE);

        return MOTION_ENGINE;
    }

    public synchronized static MotionEngine getEngine() {
        return MOTION_ENGINE;
    }

}
