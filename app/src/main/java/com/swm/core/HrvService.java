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

    private Thread mHrvWorker;

    HrvService() {

        mHrvWorker = new Thread() {

            @Override
            public void run() {
                super.run();
                for(;;) {
                    if (!SwmCore.sRunning)
                        return;

                    try {
                        EcgMetaData ecgMetaData = SwmCore.getIns().getEcgMetaData();

                        if(ecgMetaData != null){
                            HrvData hrvData = new HrvData(ecgMetaData.sdnn, ecgMetaData.rmssd);

                            if(mListeners != null)
                                mCallbackDataQueue.offer(hrvData);
                        }

                        int numOfBins = SwmCore.GetBinSize();
                        double[] rriCount = new double[numOfBins];
                        double[] rriTime = new double[numOfBins];
                        SwmCore.GetRriBins(rriCount, rriTime);

                        if(mRriListener != null)
                            mRriListener.onRriBinsDataAvailable(rriCount, rriTime);

                        double[] frequencyData = new double[5];
                        SwmCore.GetFrequencyData(frequencyData);

                        if(mFrequencyListener != null)
                            mFrequencyListener.onFrequencyDataAvailable(frequencyData);

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
