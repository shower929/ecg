package com.swm.sdk;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

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

    private HeartRateWorker mHeartRateWorker;
    private boolean beating = false;

    private Vector<Integer> mFirstLevelBuffer;
    private Vector<Integer> mSecondLevelBuffer;

    private static final int CAL_TIME_BUF = 6;
    private static final int ECG_SAMPLE_RATE = 250;
    private static final int  g_i32CalcultedLength = CAL_TIME_BUF * ECG_SAMPLE_RATE;

    private BlockingQueue<HeartData> mCallbackDataQueue;

    private volatile boolean running;
    private Dump<HeartData> mDump;
    private Dump<RawEcg> rawDump;

    private Runnable mCheckQueueSize;

    private Handler mProfillingHandler;
    private Thread mCallbackWorker;

    private Context context;

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
                    float sdnn = GetSdnn();
                    float rmssd = GetRmssd();

                    HeartData heartData = new HeartData(heartRate, sdnn, rmssd);
                    sendHeartRateBroadcast(heartRate);

                    if(outputs != null)
                        mCallbackDataQueue.offer(heartData);

                    if (mDump != null) {
                        mDump.putData(heartData);
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
        super(context);

        mSecondLevelBuffer = new Vector<>();

        mHeartRateWorker = new HeartRateWorker();

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
                        HeartData heartData = mCallbackDataQueue.take();
                        if (outputs != null) {
                            for(HeartEngineOutput output : outputs) {
                                output.onHeartDataAvailable(heartData);
                            }

                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }

    @Override
    public void start() {
        running = true;
        InitialForModeChange(1);
        mHeartRateWorker.start();
        mCallbackWorker.start();
        mDump = new Dump<>("SportHeartEcg");
        mDump.start();
        rawDump = new Dump<>("SportRawEcg");
        rawDump.setWithoutComma(true);
        rawDump.start();
    }

    @Override
    public void stop() {
        running = false;
        InitialForModeChange(0);
        if (mDump != null)
            mDump.stop();
        mCallbackWorker.interrupt();
        mHeartRateWorker.interrupt();
        mSecondLevelBuffer.clear();
        mDump.stop();
        mDump = null;
        rawDump.stop();
        rawDump = null;
    }

    @Override
    synchronized void onFuel(BleData data) {
        int len = data.rawData.length;

        for(int i = 10; i < len - 1; i = i + 2) {
            Integer value = ((data.rawData[i+1] << 8) & 0xFF00) | (data.rawData[i] & 0xFF);
            mSecondLevelBuffer.add(value);
        }

        if (!beating) {
            beating = true;
            mProfillingHandler.postDelayed(mCheckQueueSize, 1000);
        }

        if (rawOutput != null) {
            for(int i = 10; i < len - 1; i = i + 2) {
                Integer value = ((data.rawData[i + 1] << 8) & 0xFF00) | (data.rawData[i] & 0xFF);
                rawOutput.onRawDataAvailable(value);
            }
        }

        if (rawDump != null)
            rawDump.putData(new RawEcg(data.rawData));

    }
}
