package com.swm.sdk;

/**
 * Created by yangzhenyu on 2017/4/5.
 */

abstract class HeartMotionDevice implements SwmDevice, BleDevice  {
    SwmEngine heartEngine;
    SwmEngine motionEngine;

    void installHeartEngine(SwmEngine heartEngine) {
        this.heartEngine = heartEngine;
    }

    void uninstallHeartEngine() {
        this.heartEngine = null;
    }

    void installMotionEngine(SwmEngine motionEngine) {
        this.motionEngine = motionEngine;
    }

    void uninstallMotionEngine() {
        this.motionEngine = null;
    }
}
