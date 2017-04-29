package com.swm.sdk;

/**
 * Created by yangzhenyu on 2016/9/27.
 */

class MotionData {
     class Gyro{
        public final int x;
        public final int y;
        public final int z;

        Gyro(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

     class Accelerator {
        public final int x;
        public final int y;
        public final int z;

        Accelerator(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

     class Magnetic {
        public final int x;
        public final int y;
        public final int z;

        Magnetic(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    final Gyro gyro;
    final Accelerator accelerator;
    final Magnetic magnetic;

    MotionData(int gx, int gy, int gz, int ax, int ay, int az, int mx, int my, int mz, int idx) {
        gx = twosComplement(gx);
        gy = twosComplement(gy);
        gz = twosComplement(gz);
        ax = twosComplement(ax);
        ay = twosComplement(ay);
        az = twosComplement(az);
        mx = twosComplement(mx);
        my = twosComplement(my);
        mz = twosComplement(mz);

        gyro = new Gyro(gx, gy, gz);
        accelerator = new Accelerator(ax, ay, az);
        magnetic = new Magnetic(mx, my, mz);
    }

    private int twosComplement(int in) {
        if ((in & 0x8000) == 0x8000)
            return -1 * ((in ^ 0xFFFF) + 0x0001);
        else
            return in;
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
