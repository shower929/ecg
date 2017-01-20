package com.swm.core;

import android.os.Handler;
import android.util.Log;

import com.swm.heartbeat.HeartBeatListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.swm.core.SWM_ECG_ALGO_HEADER.*;

/**
 * Created by yangzhenyu on 2016/10/5.
 */

class HeartRateService {
    private static final String LOG_TAG = "HeartBeat";

    private HeartRateWorker mHeartRateWorker;
    private BlockingQueue<WorkData> mWorkDataQueue;
    Dump<HeartBeatData> mDump;

    private List<Long> mEcgBuffer;

    private Vector<HeartBeatListener> mListeners;
    private BlockingQueue<HeartBeatData> mCallbackDataQueue;

    private boolean mBeating = false;
    private Handler mProfillingHandler;

    private Thread mCallbackWorker;

    private Runnable mBeat = new Runnable() {
        private Long[] mData;

        @Override
        public void run() {
            for (;;) {
                if(!SwmCore.sRunning)
                    return;

                synchronized (LOCK) {
                    if (mEcgBuffer.size() >= SWM_ECG_ALGO.g_i32CalcultedLength) {
    static {
        System.loadLibrary("swm_ecg_algo");
    }

                        mData = new Long[SWM_ECG_ALGO.g_i32CalcultedLength];
                        mEcgBuffer.subList(0, SWM_ECG_ALGO.g_i32CalcultedLength).toArray(mData);
    static native int CalculateHeartRate(int[] i32ECGRawBuffer);
    static native void GetRtoRIntervalData(double[] rriAry, double[] timeAry);
    static native float GetSdnn();
    static native float GetRmssd();
    static native int InitialForModeChange(int mode);

                        WorkData workData = new WorkData(mData);
                        mWorkDataQueue.offer(workData);
                        mEcgBuffer.subList(0, ECG_SAMPLE_RATE).clear();
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    };

    private Runnable mCheckQueueSize;

    private Thread mHeartBeat = new Thread(mBeat);

    private final Object LOCK = new Object();


    private class HeartRateWorker extends Thread{


        @Override
        public void run() {
            super.run();
            for(;;) {
                if (!SwmCore.sRunning)
                    return;

                try {
                    WorkData workData = mWorkDataQueue.take();
                    int[] g_i32ECGInBuffer = new int[workData.g_i32ECGInBuffer.length];
                    int i = 0;
                    for (Long value: workData.g_i32ECGInBuffer) {
                        g_i32ECGInBuffer[i] = value.intValue();
                        i++;
                    }
                    EcgMetaData ecgMetaData = new EcgMetaData();
                    SwmCore.CalculateEcgMetaData(ecgMetaData, g_i32ECGInBuffer);
                    SwmCore.getIns().setEcgMetaData(ecgMetaData);

                    if (SwmCore.getIns().getEcgMetaData().heartRate == 0)
                        continue;

                    HeartBeatData heartBeatData = new HeartBeatData(SwmCore.getIns().getEcgMetaData().heartRate);
                    if(mListeners != null)
                        mCallbackDataQueue.offer(heartBeatData);

                    if (mDump != null) {
                        mDump.putData(heartBeatData);
                    }
                } catch (InterruptedException e) {
                    Log.d(LOG_TAG, e.getMessage(), e);
                }
            }
        }
    }

    HeartRateService() {
        mEcgBuffer = new ArrayList<>();

        mWorkDataQueue = new LinkedBlockingQueue<>();
        mHeartRateWorker = new HeartRateWorker();
        mHeartRateWorker.start();

        mCallbackDataQueue = new LinkedBlockingQueue<>();
        mProfillingHandler = new Handler();
        mCheckQueueSize = new Runnable() {
            @Override
            public void run() {
                if(!SwmCore.sRunning)
                    return;
                Log.d(LOG_TAG, "Heart beat processing buffer: " + mEcgBuffer.size());
                mProfillingHandler.postDelayed(this, 1000);
            }
        };
        mCallbackWorker = new Thread(new Runnable() {
            @Override
            public void run() {
                for (;;) {
                    if (!SwmCore.sRunning)
                        return;

                    try {
                        HeartBeatData heartBeatData = mCallbackDataQueue.take();
                        if (mListeners != null) {
                            for(HeartBeatListener listener : mListeners) {
                                listener.onHeartBeatDataAvailable(heartBeatData);
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

    public void onSwmDataAvailable(SwmData swmData) {
        synchronized (LOCK) {
            int len = swmData.value.length;
            for(int i = 0; i < len - 1; i = i + 2) {
                long ecg = ((swmData.value[i + 1] & 0xFF) << 8) | (swmData.value[i] & 0xFF);
                mEcgBuffer.add(ecg);
            }
        }

        synchronized (mHeartBeat) {
            if (!mBeating) {
                mHeartBeat.start();
                mBeating = true;
                mProfillingHandler.postDelayed(mCheckQueueSize, 1000);
            }
        }


    }

    synchronized void addListener(HeartBeatListener listener) throws Exception {
        if (mListeners == null)
            mListeners = new Vector<>();

        if (mListeners.contains(listener))
            throw new Exception("Listener is added already");
        mListeners.add(listener);
    }

    void removeListener(HeartBeatListener listener) {
        if (mListeners != null)
            mListeners.remove(listener);
    }

    void startRecord() {
        if (mDump != null)
            return;

        mDump = new Dump<HeartBeatData>("Hrr");
        mDump.start();
    }

    void stopRecord() {
        mDump.stop();
        mDump = null;
    }

    boolean isRecording() {
        return mDump != null;
    }

    void stop() {
        if (mDump != null)
            mDump.stop();
        mCallbackWorker.interrupt();
        mHeartBeat.interrupt();
        mHeartRateWorker.interrupt();
    }
}
