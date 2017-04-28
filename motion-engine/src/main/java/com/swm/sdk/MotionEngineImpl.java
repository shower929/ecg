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

class MotionEngineImpl implements MotionEngine {
    private static final int CLOCK = 50;    //Hz
    private static final int WINDOW = 6 * 50; // 300 samples
    private MotionEngineOutput output;
    private Worker worker;
    private BlockingQueue<MotionData> queue;
    private List<Double> accX;
    private List<Double> accY;
    private List<Double> accZ;

    private volatile boolean running = false;

    static {
        System.loadLibrary("swm_motion_algo");
    }

    static native int GetStep(double[] accX, double[] accY, double[] accZ);

    private class Worker extends Thread {
        @Override
        public void run() {
            super.run();
            for(;;) {
                if(!running)
                    return;

                try {
                    // Blocking while data available
                    MotionData data = queue.take();

                    if(accX == null)
                        accX = new ArrayList<>();
                    if (accY == null)
                        accY = new ArrayList<>();
                    if(accZ == null)
                        accZ = new ArrayList<>();

                    accX.add((double) data.accelerator.x);
                    accY.add((double) data.accelerator.y);
                    accZ.add((double) data.accelerator.z);

                    if(accX.size() >= WINDOW && accY.size() >= WINDOW && accZ.size() >= WINDOW) {

                        double[] dataX = cut(accX);
                        double[] dataY = cut(accY);
                        double[] dataZ = cut(accZ);

                        int step = GetStep(dataX, dataY, dataZ);
                        output.onStepDataAvailable(step);

                        accX.remove(0);
                        accY.remove(0);
                        accZ.remove(0);

                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private double[] cut(List<Double> original) {
            List<Double> sample = original.subList(0, WINDOW);

            double[] data = new double[sample.size()];

            int i = 0;
            for (Double value: sample)
                data[i++] = value.doubleValue();

            return data;
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
