package com.swm.sdk;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by yangzhenyu on 2017/4/19.
 */

class SportHeartEngine extends HeartEngine {
    private static String LOG_TAG = "HeartEngine";

    static {
        System.loadLibrary("swm_ecg_algo");
    }

    static native int CalculateHeartRate(int[] i32ECGRawBuffer);
    static native void GetRtoRIntervalData(double[] rriAry, double[] timeAry);
    static native float GetSdnn();
    static native float GetRmssd();
    static native int InitialForModeChange(int mode);

    private Context context;
    private static final double IIR_COEFF = 0.992;

    private HeartRateWorker mHeartRateWorker;
    private boolean beating = false;

    private Vector<Integer> mFirstLevelBuffer;
    private Vector<Integer> mSecondLevelBuffer;

    private static final int CAL_TIME_BUF = 6;
    private static final int ECG_SAMPLE_RATE = 250;
    private static final int  g_i32CalcultedLength = CAL_TIME_BUF * ECG_SAMPLE_RATE;

    private BlockingQueue<HeartRateData> mCallbackDataQueue;

    private boolean running = true;
    private Dump<HeartRateData> mDump;

    private Runnable mCheckQueueSize;

    private final Object LOCK = new Object();

    private Handler mProfillingHandler;
    private Thread mCallbackWorker;

    private class HeartRateWorker extends Thread{

        @Override
        public void run() {
            super.run();
            for(;;) {
                if (!running)
                    return;

                try {

                    Thread.sleep(1000);

                    synchronized (mSecondLevelBuffer) {
                        if (mSecondLevelBuffer.size() < g_i32CalcultedLength)
                            continue;

                        mFirstLevelBuffer = new Vector<>(mSecondLevelBuffer);
                        mSecondLevelBuffer.clear();
                    }

                    Integer[] ecgData = new Integer[g_i32CalcultedLength];
                    int[] data = new int[g_i32CalcultedLength];

                    List<Integer> tmp = mFirstLevelBuffer.subList(0, g_i32CalcultedLength);
                    tmp.toArray(ecgData);
                    int len = ecgData.length;

                    for (int i = 0; i < len; i++) {
                        data[i] = ecgData[i].intValue();
                    }

                    int heartRate = CalculateHeartRate(data);
                    HeartRateData heartRateData = new HeartRateData(heartRate);

                    if(outputs != null)
                        mCallbackDataQueue.offer(heartRateData);

                    if (mDump != null) {
                        mDump.putData(heartRateData);
                    }
                    mFirstLevelBuffer.subList(0, ECG_SAMPLE_RATE).clear();

                    synchronized (mSecondLevelBuffer) {
                        mSecondLevelBuffer.addAll(0, mFirstLevelBuffer);
                    }

                } catch (InterruptedException e) {
                    Log.d(LOG_TAG, e.getMessage(), e);
                }
            }
        }
    }

    SportHeartEngine(Context context) {
        this.context = context;
        mSecondLevelBuffer = new Vector<>();

        mHeartRateWorker = new HeartRateWorker();
        mHeartRateWorker.start();

        mCallbackDataQueue = new LinkedBlockingQueue<>();

        mProfillingHandler = new Handler();

        mCheckQueueSize = new Runnable() {
            @Override
            public void run() {
                if(!running)
                    return;
                Log.d(LOG_TAG, "Heart beat processing buffer: " + mSecondLevelBuffer.size());
                mProfillingHandler.postDelayed(this, 1000);
            }
        };

        mCallbackWorker = new Thread(new Runnable() {
            @Override
            public void run() {
                for (;;) {
                    if (!running)
                        return;

                    try {
                        HeartRateData heartRateData = mCallbackDataQueue.take();
                        if (outputs != null) {
                            for(HeartEngineOutput output : outputs) {
                                output.onHeartRateAvailable(heartRateData.heartRate);
                            }

                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        mCallbackWorker.start();
    }

    @Override
    public void start() {
        if (mDump != null)
            return;

        mDump = new Dump<HeartRateData>("Hrr");
        mDump.start();

    }

    @Override
    public void stop() {
        if (mDump != null)
            mDump.stop();
        mCallbackWorker.interrupt();
        mHeartRateWorker.interrupt();
    }

    @Override
    public void onFuel(BleData data) {
        int len = data.rawData.length;

        for(int i = 10; i < len - 1; i = i + 2) {
            Integer value = (data.rawData[i+1] & 0xFF) << 8 | (data.rawData[i] & 0xFF);
            mSecondLevelBuffer.add(value);
        }

        if (!beating) {
            beating = true;
            mProfillingHandler.postDelayed(mCheckQueueSize, 1000);
        }
    }
}
