package com.swm.sdk;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by yangzhenyu on 2017/3/28.
 */

class MockHeartEngine extends HeartEngine {

    @Override
    public void start() {
        running = true;
        worker.start();
    }

    @Override
    public void stop() {
        running = false;
        worker.interrupt();
    }

    private class Worker extends Thread {
        @Override
        public void run() {
            super.run();
            for (;;) {
                if (!running)
                    break;

                output.onHeartRateAvailable(new Random(System.currentTimeMillis()).nextInt() % 72);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static final String TAG = "MockHeartEngine";
    private Worker worker;
    private volatile boolean running;

    MockHeartEngine() {
        worker = new Worker();
    }

    @Override
    public void onFuel(BleData data) {
        Log.d(TAG, "Incoming data");
    }
}
