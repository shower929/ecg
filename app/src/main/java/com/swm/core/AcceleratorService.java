package com.swm.core;

import com.swm.accelerator.AcceleratorData;
import com.swm.accelerator.AcceleratorListener;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by yangzhenyu on 2016/10/2.
 */

class AcceleratorService {
    private AcceleratorListener mListener;
    private LinkedBlockingQueue<AcceleratorData> mListenerDataQueue;
    private ListenerWorker mListenerWorker;

    class ListenerWorker extends Thread {
        ListenerWorker() {
           super("AcceleratorListenerWorker");
        }

        @Override
        public void run() {
            super.run();
            for (;;) {
                if(!SwmCore.sRunning) {
                    break;
                }
                AcceleratorData acceleratorData = null;
                try {
                    acceleratorData = mListenerDataQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (mListener != null) {
                    mListener.onAcceleratorDataAvailable(acceleratorData);
                }
            }
        }
    }

    AcceleratorService() {
        mListenerDataQueue = new LinkedBlockingQueue<>();
        mListenerWorker = new ListenerWorker();
        mListenerWorker.start();
    }

    void onSwmDataAvailable(SwmData swmData) {
        short x = ((short) (((swmData.value[1] & 0xFF) << 8) | (swmData.value[0] & 0xFF)));
        short y = ((short) (((swmData.value[3] & 0xFF) << 8) | (swmData.value[2] & 0xFF)));
        short z = ((short) (((swmData.value[5] & 0xFF) << 8) | (swmData.value[4] & 0xFF)));

        AcceleratorData acceleratorData = new AcceleratorData(x, y, z);
        mListenerDataQueue.offer(acceleratorData);
    }

    void setListener(AcceleratorListener listener) {
        mListener = listener;
    }
    void removeListener() {
        mListener = null;
    }
}
