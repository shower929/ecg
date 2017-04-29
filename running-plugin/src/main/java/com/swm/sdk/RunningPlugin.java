package com.swm.sdk;

import com.swm.running.RunningPluginListener;

/**
 * Created by yangzhenyu on 2017/3/28.
 */

public class RunningPlugin implements HeartEngineOutput, MotionEngineOutput {
    private RunningPluginListener listener;
    private volatile int heartRate;
    private volatile double speed;
    private double vdot;

    static {
        System.loadLibrary("swm_running_algo");
    }

    private static native double calculateVdot(int heartRate, double speed);
    private static native double calculateTrainScore(int heartRate);

    private class Worker extends Thread {
        @Override
        public void run() {
            super.run();
            for (;;) {

                double newVdot = calculateVdot(heartRate, speed);
                double score = calculateTrainScore(heartRate);

                if(vdot != newVdot && listener != null) {
                    listener.onVdotChanged(newVdot);
                    vdot = newVdot;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public void onHeartRateAvailable(int heartRate) {
        this.heartRate = heartRate;
    }

    public void setListener(RunningPluginListener listener) {
        this.listener = listener;
    }

    @Override
    public void onSpeed(double speed) {
        this.speed = speed;
    }
}
