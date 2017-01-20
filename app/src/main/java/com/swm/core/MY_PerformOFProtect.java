package com.swm.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by yangzhenyu on 2016/10/5.
 */

class MY_PerformOFProtect extends Thread {
    private BlockingQueue<WindowedEcgData> mQueue;

    MY_PerformOFProtect() {
        mQueue = new LinkedBlockingQueue();
    }

    @Override
    public void run() {
        super.run();
        for(;;) {
            if(!SwmCore.sRunning)
                return;

            try {
                WindowedEcgData data = mQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
