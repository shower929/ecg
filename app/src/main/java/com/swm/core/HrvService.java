package com.swm.core;

import android.util.Log;

import com.swm.hrv.FrequencyListener;
import com.swm.hrv.HrvListener;
import com.swm.hrv.RriListener;

import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Created by yangzhenyu on 2016/12/13.
 */

class HrvService {
    private static final String LOG_TAG = "HeartBeat";

    private Vector<HrvListener> mListeners;
    private BlockingQueue<HrvData> mCallbackDataQueue;

    private RriListener mRriListener;

    private FrequencyListener mFrequencyListener;


    private Thread mCallbackWorker;
    static {
        System.loadLibrary("swm_ecg_hrv_algo");
    }

    private Thread mHrvWorker;
    static native int GetRriDistributionSize(double[] rriAry);
    static native void GetRriDistribution(double[] rriAry, double[] timeAry, double[] rriDistribution, double[] rriDistributionIdx, int distributionSize);
    static native void GetFrequencyData(double[] rriAry, double[] timeAry, double[] frequencyData);

    HrvService() {

        mHrvWorker = new Thread() {

            @Override
            public void run() {
                super.run();
                for(;;) {
                    if (!SwmCore.sRunning)
                        return;

                    try {
                        monitorHrv();

                        getRriDistribution();

                        getFrequencyData();

                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Log.d(LOG_TAG, e.getMessage(), e);
                    }
                }
            }
        };

        mHrvWorker.start();

        mCallbackDataQueue = new LinkedBlockingQueue<>();

        mCallbackWorker = new Thread(new Runnable() {
            @Override
            public void run() {
                for (;;) {
                    if (!SwmCore.sRunning)
                        return;

                    try {
                        HrvData hrvData = mCallbackDataQueue.take();
                        if (mListeners != null) {
                            for(HrvListener listener : mListeners) {
                                listener.onHrvDataAvailable(hrvData);
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

    private void getFrequencyData() {
        if(mFrequencyListener == null)
            return;

        double[] frequencyData = new double[5];
        SwmCore.GetFrequencyData(frequencyData);

        if(mFrequencyListener == null)
            return;

        mFrequencyListener.onFrequencyDataAvailable(frequencyData);
    }

    private void getRriDistribution() {
        if(mRriListener == null)
            return;

        int numOfBins = SwmCore.GetBinSize();
        double[] rriCount = new double[numOfBins];
        double[] rriTime = new double[numOfBins];
        SwmCore.GetRriBins(rriCount, rriTime);

        mRriListener.onRriBinsDataAvailable(rriCount, rriTime);

    }

    private void monitorHrv() {
        if(mListeners == null)
            return;

        EcgMetaData ecgMetaData = SwmCore.getIns().getEcgMetaData();

        if(ecgMetaData != null){
            HrvData hrvData = new HrvData(ecgMetaData.sdnn, ecgMetaData.rmssd);
            mCallbackDataQueue.offer(hrvData);
        }
    }

    synchronized void addListener(HrvListener listener) throws Exception {
        if (mListeners == null)
            mListeners = new Vector<>();

        if (mListeners.contains(listener))
            throw new Exception("Listener is added already");
        mListeners.add(listener);
    }

    synchronized void removeListener(HrvListener listener) {
        if (mListeners != null)
            mListeners.remove(listener);
    }

    void stop() {

        mCallbackWorker.interrupt();
        if(mHrvWorker != null)
            mHrvWorker.interrupt();
    }

    void setRriListener(RriListener listener) {
        mRriListener = listener;
    }

    void removeRriListener() {
        mRriListener = null;
    }

    void setFrequencyListener(FrequencyListener listener) {
        mFrequencyListener = listener;
    }

    void removeFrequencyListener() {
        mFrequencyListener = null;
    }
}
