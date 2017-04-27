package com.swm.sdk;

import android.content.Context;

import java.util.Deque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by yangzhenyu on 2017/4/26.
 */

class MotionEngineImpl implements MotionEngine {
    private static final int CLOCK = 50;    //Hz
    private static final int WINDOW = 6 * 50; // 300 samples
    private MotionEngineOutput output;
    private Worker worker;
    private BlockingQueue<MotionData> queue;
    private Deque<Double> accX;
    private Deque<Double> accY;
    private Deque<Double> accZ;

    private volatile boolean running = false;

    private class Worker extends Thread {
        @Override
        public void run() {
            super.run();
            for(;;) {
                if(!running)
                    return;

                try {
                    MotionData data = queue.take();
                    accX.add(Double.data.accelerator.x);
                    accY.add(data.accelerator.y);
                    accZ.add(data.accelerator.z);
                    if(accX.size() >= WINDOW && accY.size() >= WINDOW && accZ.size() >= WINDOW)
                    int step = GetStep(accX, accY, accZ);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static {
        System.loadLibrary("swm_motion_algo");
    }

    static native int GetStep(double[] accX, double[] accY, double[] accZ);

    MotionEngineImpl () {
        queue = new LinkedBlockingQueue<>();
        worker = new Worker();
    }

    @Override
    public void start() {
        running = true;
        worker.start();
    }

    @Override
    public void stop() {
        running = false;
        worker.interrupt();
        queue.clear();
    }

    @Override
    public void onFuel(BleData data) {
        MotionData motionData = new MotionData(
                data.rawData[1] & 0xFF << 8 | data.rawData[0] & 0xFF
                , data.rawData[3] & 0xFF << 8 | data.rawData[2] & 0xFF
                , data.rawData[5] & 0xFF << 8 | data.rawData[4] & 0xFF
                , data.rawData[7] & 0xFF << 8 | data.rawData[6] & 0xFF
                , data.rawData[9] & 0xFF << 8 | data.rawData[8] & 0xFF
                , data.rawData[11] & 0xFF << 8 | data.rawData[10] & 0xFF
                , data.rawData[13] & 0xFF << 8 | data.rawData[12] & 0xFF
                , data.rawData[15] & 0xFF << 8 | data.rawData[14] & 0xFF
                , data.rawData[17] & 0xFF << 8 | data.rawData[16] & 0xFF
                , data.rawData[18]
        );
        queue.add(motionData);
    }

    @Override
    public void setOutput(MotionEngineOutput output) {
        this.output = output;
    }
}
