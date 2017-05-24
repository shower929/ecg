package com.swm.sdk;

/**
 * Created by yangzhenyu on 2017/4/5.
 */
interface MotionEngineOutput {
    void onAcceleratorDataAvailable(AcceleratorData acceleratorData);
    void onGyroDataAvailable(GyroData gyroData);
    void onMagneticDataAvailable(MagneticData magneticData);
}
