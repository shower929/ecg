package com.swm.sdk;

/**
 * Created by yangzhenyu on 2017/4/5.
 */
interface MotionEngineOutput {
    void onAcceleratorDataAvailale(MotionData.Accelerator accelerator);
    void onGyroDataAvailable(MotionData.Gyro gyro);
    void onMagneticDataAvailable(MotionData.Magnetic magnetic);
}
