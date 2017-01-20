package com.swm.core;

/**
 * Created by yangzhenyu on 2017/1/19.
 */

class SlidingWindow extends Thread {
    @Override
    public void run() {
        super.run();
        for(;;) {
            if(!SwmCore.sRunning)
                return;
        }
    }
}
