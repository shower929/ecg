package com.swm.sdk;

import android.content.Context;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by yangzhenyu on 2017/4/26.
 */

class MotionEngineImpl extends MotionEngine {
    private Worker worker;
    private BlockingQueue<MotionData> queue;
    private MotionEngineOutput output;
    private volatile boolean running = false;

    private class Worker extends Thread {
        @Override
        public void run() {
            super.run();
            for(;;) {
                if(!running)
                    return;

                if (output == null)
                    return;

                try {
                    // Blocking while data available
                    MotionData data = queue.take();
                    output.onAcceleratorDataAvailale(data.accelerator);
                    output.onGyroDataAvailable(data.gyro);
                    output.onMagneticDataAvailable(data.magnetic);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

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
        if(!running)
            return;

        if (output == null)
            return;

        MotionData motionData = new MotionData(
                ((data.rawData[1] << 8) & 0xFF00) | (data.rawData[0] & 0xFF)
                , ((data.rawData[3] << 8) & 0xFF00) | (data.rawData[2] & 0xFF)
                , ((data.rawData[5] << 8) & 0xFF00) | (data.rawData[4] & 0xFF)
                , ((data.rawData[7] << 8) & 0xFF00) | (data.rawData[6] & 0xFF)
                , ((data.rawData[9] << 8) & 0xFF00) | (data.rawData[8] & 0xFF)
                , ((data.rawData[11] << 8 )& 0xFF00) | (data.rawData[10] & 0xFF)
                , ((data.rawData[13] << 8) & 0xFF00) | (data.rawData[12] & 0xFF)
                , ((data.rawData[15] << 8) & 0xFF00) | (data.rawData[14] & 0xFF)
                , ((data.rawData[17] << 8) & 0xFF00) | (data.rawData[16] & 0xFF)
                , data.rawData[18]
        );
        queue.add(motionData);
    }

    @Override
    public void setOutput(MotionEngineOutput output) {
        this.output = output;
    }
}
