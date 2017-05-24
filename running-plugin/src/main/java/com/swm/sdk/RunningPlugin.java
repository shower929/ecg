package com.swm.sdk;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yangzhenyu on 2017/3/28.
 */

public class RunningPlugin extends SwmPlugin implements HeartEngineOutput, MotionEngineOutput {
    public static final String ACTION_STEP = "action_step";
    public static final String EXTRA_STEP = "extra_step";
    public static final String ACTION_JUMP = "action_jump";
    public static final String EXTRA_JUMP = "extra_jump";
    public static final int NONE = 0;
    public static final int JUMP = 1;
    private static final int WINDOW = 1 * 50; // 300 samples

    private List<Double> accX;
    private List<Double> accY;
    private List<Double> accZ;

    private volatile boolean on = false;

    static {
        System.loadLibrary("swm_running_algo");
    }

    private static native int GetStep(double[] accX, double[] accY, double[] accZ);
    private static native int IsJump(double[] accX, double[] accY, double[] accZ);

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
    public void onAcceleratorDataAvailable(AcceleratorData accelerator) {

        if(accX == null)
            accX = new ArrayList<>();
        if (accY == null)
            accY = new ArrayList<>();
        if(accZ == null)
            accZ = new ArrayList<>();

        accX.add(accelerator.x);
        accY.add(accelerator.y);
        accZ.add(accelerator.z);

        if(accX.size() >= WINDOW && accY.size() >= WINDOW && accZ.size() >= WINDOW) {

            double[] dataX = cut(accX);
            double[] dataY = cut(accY);
            double[] dataZ = cut(accZ);

            int step = GetStep(dataX, dataY, dataZ);
            broadcast(ACTION_STEP, EXTRA_STEP, step);

            int jump = IsJump(dataX, dataY, dataZ);
            broadcast(ACTION_JUMP, EXTRA_JUMP, jump == 1 ? JUMP : NONE);

            accX.remove(0);
            accY.remove(0);
            accZ.remove(0);

        }
    }

    @Override
    public void onGyroDataAvailable(GyroData gyro) {

    }

    @Override
    public void onMagneticDataAvailable(MagneticData magnetic) {

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
