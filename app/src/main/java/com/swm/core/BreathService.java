package com.swm.core;

import android.os.Environment;
import android.util.Log;

import com.swm.breath.BreathListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by yangzhenyu on 2016/10/2.
 */
class BreathService {
    private static final String LOG_TAG = "BreathService";
    private BreathListener mBreathListener;
    private LinkedBlockingQueue<BreathData> mListenerDataQueue;
    private Thread mListenerWorker;


    BreathService() {
        mListenerDataQueue = new LinkedBlockingQueue<>();
        mListenerWorker = new Thread(new Runnable() {
            @Override
            public void run() {
                for(;;) {
                    if(!SwmCore.sRunning)
                        return;

                    try {
                        BreathData breathData = mListenerDataQueue.take();
                        if (mBreathListener != null) {
                            mBreathListener.onBreathDataAvailable(breathData);
                        }
                    } catch (InterruptedException e) {
                        Log.d(LOG_TAG, e.getMessage(), e);
                    }
                }
            }
        });
        mListenerWorker.start();

    }
    void onSwmDataAvailable(SwmData swmData) {
        BreathData breathData = new BreathData(swmData.value[1] & 0xFF << 8 | swmData.value[0] & 0xFF);
        if(mBreathListener != null)
            mListenerDataQueue.offer(breathData);
    }

    void setListener(BreathListener breathListener) {
        mBreathListener = breathListener;
    }

    void removeListener() {
        mBreathListener = null;
    }

    void stop() {
        mListenerWorker.interrupt();
    }
}
