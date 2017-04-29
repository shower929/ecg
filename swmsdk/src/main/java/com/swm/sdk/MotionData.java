package com.swm.core;

/**
 * Created by yangzhenyu on 2016/9/27.
 */

public class MotionData {
    public class Gyro{
        public final int x;
        public final int y;
        public final int z;

        Gyro(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public class Accelerator {
        public final int x;
        public final int y;
        public final int z;

        Accelerator(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public class Magnetic {
        public final int x;
        public final int y;
        public final int z;

        Magnetic(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public final Gyro gyro;
    public final Accelerator accelerator;
    public final Magnetic magnetic;

    MotionData(int gx, int gy, int gz, int ax, int ay, int az, int mx, int my, int mz, int idx) {
        gyro = new Gyro(gx, gy, gz);
        accelerator = new Accelerator(ax, ay, az);
        magnetic = new Magnetic(mx, my, mz);
    }

    @Override
    public boolean equals(Object obj) {
        MotionData motionData = (MotionData)obj;
        return gyro.x == motionData.gyro.x
                && gyro.y == motionData.gyro.y
                && gyro.z == motionData.gyro.z
                && accelerator.x == motionData.accelerator.x
                && accelerator.y == motionData.accelerator.y
                && accelerator.z == motionData.accelerator.z
                && magnetic.x == motionData.magnetic.x
                && magnetic.y == motionData.magnetic.y
                && magnetic.z == motionData.magnetic.z;
    }
}
