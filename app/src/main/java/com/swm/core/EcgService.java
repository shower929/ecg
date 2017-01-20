package com.swm.core;

import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by yangzhenyu on 2016/9/27.
 */
class EcgService {
    private static final String LOG_TAG = "ECG";
    private static final long ECG_SAMPLE_RATE = 50;
    private static final long ECG_CLOCK = 1l/ECG_SAMPLE_RATE;
    private Vector<EcgRawDataListener> mListeners;
    private BlockingQueue<EcgRawData> mListenerDataQueue;
    private Thread mListenerDataWorker;

    private static final Object LISTENER_LOCK = new Object();

    Dump mDump;


    EcgService() {
    }

    synchronized void onSwmDataAvailable(SwmData swmData) {

        int len = swmData.value.length;
        List<Short> values = new ArrayList<>();

        for(int i = 10; i < len - 1; i = i + 2) {
            values.add((short) (((swmData.value[i+1] & 0xFF) << 8) | (swmData.value[i] & 0xFF)));
        }

        EcgRawData ecgRawData = new EcgRawData(values);

        mListenerDataQueue.offer(ecgRawData);

        if(mDump != null) {
            mDump.putData(swmData);
        }
    }

    private void initListenerTask() {
        mListeners = new Vector<>();
        mListenerDataQueue = new LinkedBlockingQueue<>();

        mListenerDataWorker = new Thread() {
            @Override
            public void run() {
                super.run();
                for(;;) {
                    if(!SwmCore.sRunning)
                        return;

                    try {
                        EcgRawData ecgRawData = mListenerDataQueue.take();
                        if(mListeners != null && mListeners.size() > 0) {
                            for (EcgRawDataListener listener : mListeners) {
                                EcgRawData copy = new EcgRawData(ecgRawData.ecgData);
                                listener.onEcgRawDataAvailable(copy);
                            }
                        }
                        Thread.sleep(ECG_CLOCK);
                    } catch (InterruptedException e) {
                        Log.d(LOG_TAG, e.getMessage(), e);
                    }

                }
            }
        };
        mListenerDataWorker.start();
    }

    synchronized void registerListener(EcgRawDataListener listener) throws Exception {
        synchronized (LISTENER_LOCK) {
            if (mListeners == null) {
                initListenerTask();
            }

            if(mListeners.contains(listener))
                throw new Exception("Duplicated register listener");

            mListeners.add(listener);
        }
    }

    void removeListener(EcgRawDataListener listener) {
        mListeners.remove(listener);
    }

    void startRecord() {
        mDump = new Dump("raw_Ecg");
        mDump.setWithoutComma(true);
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
        mListenerDataWorker.interrupt();
        if (mDump != null)
            mDump.stop();

    }
}
