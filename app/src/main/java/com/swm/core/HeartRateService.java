package com.swm.core;

import android.os.Handler;
import android.util.Log;

import com.swm.heartbeat.HeartRateListener;
import com.swm.hrv.RmssdListener;
import com.swm.hrv.SdnnListener;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.swm.core.SWM_ECG_ALGO_HEADER.*;

/**
 * Created by yangzhenyu on 2016/10/5.
 */

class HeartRateService implements EcgProviderClient{
    private static final String LOG_TAG = "HeartRate";

    private HeartRateWorker mHeartRateWorker;
    Dump<HeartRateData> mDump;

    private Vector<Integer> mFirstLevelBuffer;
    private Vector<Integer> mSecondLevelBuffer;

    private Vector<HeartRateListener> mListeners;
    private BlockingQueue<HeartRateData> mCallbackDataQueue;

    private boolean mBeating = false;
    private Handler mProfillingHandler;

    private Thread mCallbackWorker;

    private RriCalculationService mRriCalculationService;

    private SdnnService mSdnnService;
    private RmssdService mRmssdService;
    private int mClient;

    static {
        System.loadLibrary("swm_ecg_algo");
    }

    static native int CalculateHeartRate(int[] i32ECGRawBuffer);
    static native void GetRtoRIntervalData(double[] rriAry, double[] timeAry);
    static native float GetSdnn();
    static native float GetRmssd();
    static native int InitialForModeChange(int mode);


    private Runnable mCheckQueueSize;

    private final Object LOCK = new Object();

    @Override
    public void onEcgDataAvailable(final EcgData ecgData) {
        synchronized (LOCK) {
            for(Integer value : ecgData.values)
                mSecondLevelBuffer.add(value);
        }

        if (!mBeating) {
            mBeating = true;
            mProfillingHandler.postDelayed(mCheckQueueSize, 1000);
        }
    }


    private class HeartRateWorker extends Thread{

        @Override
        public void run() {
            super.run();
            for(;;) {
                if (!SwmCore.sRunning)
                    return;

                try {

                    Thread.sleep(1000);

                    synchronized (mSecondLevelBuffer) {
                        if (mSecondLevelBuffer.size() < SWM_ECG_ALGO.g_i32CalcultedLength)
                            continue;

                        mFirstLevelBuffer = new Vector<>(mSecondLevelBuffer);
                        mSecondLevelBuffer.clear();
                    }

                    Integer[] ecgData = new Integer[SWM_ECG_ALGO.g_i32CalcultedLength];
                    int[] data = new int[SWM_ECG_ALGO.g_i32CalcultedLength];

                    List<Integer> tmp = mFirstLevelBuffer.subList(0, SWM_ECG_ALGO.g_i32CalcultedLength);
                    tmp.toArray(ecgData);
                    int len = ecgData.length;

                    for (int i = 0; i < len; i++) {
                        data[i] = ecgData[i].intValue();
                    }

                    int heartRate = CalculateHeartRate(data);

                    HeartRateData heartRateData = new HeartRateData(heartRate);

                    if(mListeners != null)
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

    HeartRateService() {

        mSecondLevelBuffer = new Vector<>();

        mHeartRateWorker = new HeartRateWorker();
        mHeartRateWorker.start();

        mCallbackDataQueue = new LinkedBlockingQueue<>();
        mProfillingHandler = new Handler();
        mCheckQueueSize = new Runnable() {
            @Override
            public void run() {
                if(!SwmCore.sRunning)
                    return;
                Log.d(LOG_TAG, "Heart beat processing buffer: " + mSecondLevelBuffer.size());
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
                        HeartRateData heartRateData = mCallbackDataQueue.take();
                        if (mListeners != null) {
                            for(HeartRateListener listener : mListeners) {
                                listener.onHeartRateDataAvailable(heartRateData);
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

    synchronized void addListener(HeartRateListener listener) throws Exception {
        if (mListeners == null)
            mListeners = new Vector<>();

        if (mListeners.contains(listener))
            throw new Exception("Listener is added already");
        mListeners.add(listener);
    }

    void removeListener(HeartRateListener listener) {
        if (mListeners == null)
            return;

        if(mListeners.contains(listener))
            mListeners.remove(listener);
    }

    void startRecord() {
        if (mDump != null)
            return;

        mDump = new Dump<HeartRateData>("Hrr");
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
        mHeartRateWorker.interrupt();
        mSecondLevelBuffer.clear();
    }

    private synchronized void initRriCalculationService() {
        if (mRriCalculationService == null)
            mRriCalculationService = new RriCalculationService();

    }

    private synchronized void stopRriCalculationService() {
        mRriCalculationService = null;
    }

    private synchronized void initSdnnService() {
        if(mSdnnService == null)
            mSdnnService = new SdnnService();
    }

    private synchronized void stopSdnnService() {
        mSdnnService = null;
    }

    private synchronized void initRmssdService() {
        if(mRmssdService == null)
            mRmssdService = new RmssdService();
    }

    private synchronized void stopRmssdService() {
        mRmssdService = null;
    }

    synchronized void startMonitorHrv() {
        mClient++;
        InitialForModeChange(1);
        initRriCalculationService();
        initSdnnService();
        initRmssdService();

        try {
            addListener(mRriCalculationService);
            addListener(mSdnnService);
            addListener(mRmssdService);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    synchronized void stopMonitorHrv() {
        mClient--;
        if (mClient > 0)
            return;
        removeListener(mRmssdService);
        removeListener(mSdnnService);
        removeListener(mRriCalculationService);

        stopRmssdService();
        stopSdnnService();
        stopRriCalculationService();
        InitialForModeChange(0);
    }

    void setRriDataListener(RtoRintervalDataListener listener) {
        mRriCalculationService.setListener(listener);
    }

    void removeRriDataListener() {
        mRriCalculationService.removeListener();
    }

    void setSdnnListener(SdnnListener listener) {
        if (mSdnnService == null) {
            throw new RuntimeException("Must start monitor hrv first");
        }
        mSdnnService.setListener(listener);
    }

    void removeSdnnListener() {
        mSdnnService.removeListener();
    }

    void setRmssdListener(RmssdListener listener) {
        if (mRmssdService == null) {
            throw new RuntimeException("Must start monitor hrv first");
        }
        mRmssdService.setListener(listener);
    }

    void removeRmssdListener() {
        mRmssdService.removeListener();
    }
}
