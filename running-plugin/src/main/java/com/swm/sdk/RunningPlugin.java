package com.swm.sdk;

import android.content.Context;
import android.content.Intent;

import com.swm.running.RunningPluginListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by yangzhenyu on 2017/3/28.
 */

public class RunningPlugin extends SwmPlugin implements HeartEngineOutput, MotionEngineOutput {
    public static final String ACTION_STEP = "action_step";
    public static final String EXTRA_STEP = "extra_step";

    private static final int WINDOW = 6 * 50; // 300 samples

    private List<Double> accX;
    private List<Double> accY;
    private List<Double> accZ;

    private volatile boolean on = false;

    static {
        System.loadLibrary("swm_running_algo");
    }

    static native int GetStep(double[] accX, double[] accY, double[] accZ);

    public RunningPlugin(Context context) {
        super(context);
    }

    public void on() {
        on = true;
    }

    public void off() {
        on = false;
        accX.clear();
        accY.clear();
        accZ.clear();
    }

    @Override
    public void onHeartDataAvailable(HeartData heartData) {

    }

    @Override
    public void onAcceleratorDataAvailale(MotionData.Accelerator accelerator) {


        if(accX == null)
            accX = new ArrayList<>();
        if (accY == null)
            accY = new ArrayList<>();
        if(accZ == null)
            accZ = new ArrayList<>();

        accX.add((double) accelerator.x);
        accY.add((double) accelerator.y);
        accZ.add((double) accelerator.z);

        if(accX.size() >= WINDOW && accY.size() >= WINDOW && accZ.size() >= WINDOW) {

            double[] dataX = cut(accX);
            double[] dataY = cut(accY);
            double[] dataZ = cut(accZ);

            int step = GetStep(dataX, dataY, dataZ);
            broadcast(ACTION_STEP, EXTRA_STEP, step);

            accX.remove(0);
            accY.remove(0);
            accZ.remove(0);

        }
    }

    @Override
    public void onGyroDataAvailable(MotionData.Gyro gyro) {

    }

    @Override
    public void onMagneticDataAvailable(MotionData.Magnetic magnetic) {

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
