package com.swm.sdk;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by yangzhenyu on 2017/5/13.
 */
 class SwmEcgMonitor {
    private BlockingDeque<EcgData> queue;
    private volatile boolean running;
    private SwmEngineListener listener;
    private OutputWorker outputWorker;

    SwmEcgMonitor() {
        queue = new LinkedBlockingDeque<>();
        outputWorker = new OutputWorker();
        running = true;
        outputWorker.start();
    }

    private class OutputWorker extends Thread {
        @Override
        public void run() {
            for(;;) {
                if(!running)
                    return;

                try {
                    EcgData data = queue.take();
                    if (listener != null)
                        listener.onEcgDataAvailable(data);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    void offer(EcgData data) {
        queue.offer(data);
    }

    void setListener(SwmEngineListener listener) {
        this.listener = listener;
    }

    void off () {
        running = false;
        outputWorker.interrupt();
    }
}
