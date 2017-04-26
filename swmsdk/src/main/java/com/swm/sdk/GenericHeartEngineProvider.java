package com.swm.sdk;

import android.content.Context;
import android.util.Log;

/**
 * Created by yangzhenyu on 2017/3/29.
 */

abstract class GenericHeartEngineProvider extends SwmModule{
    protected static HeartEngine HEART_ENGINE;

    public static synchronized HeartEngine newEngine(SwmDevice device) {
        if(!(device instanceof HeartMotionDevice))
            throw new RuntimeException("The device is not support heart service");

        HeartMotionDevice heartMotionDevice = (HeartMotionDevice)device;
        heartMotionDevice.installHeartEngine(HEART_ENGINE);

        return HEART_ENGINE;
    }

    public synchronized static void disassemble(SwmDevice device) {
        if (device instanceof HeartMotionDevice)
            ((HeartMotionDevice)device).uninstallHeartEngine();
        else
            throw new RuntimeException("The device is not support heart service");
    }

}
