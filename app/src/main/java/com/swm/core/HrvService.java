package com.swm.core;

import com.swm.hrv.RriFrequencyListener;
import com.swm.hrv.RriDistributionListener;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Created by yangzhenyu on 2016/12/13.
 */

class HrvService implements RtoRintervalDataListener{
    private static final String LOG_TAG = "HRV";

    private BlockingQueue<RtoRintervalData> mRriDistributionServiceQueue;
    private RriDistributionService mRriDistributionService;
    private Thread mRriDistributionServiceQueueWorker;

    private BlockingQueue<RtoRintervalData> mRriFrequencyServiceQueue;
    private RriFrequencyService mRriFrequencyService;
    private Thread mRriFreqServiceQueueWorker;

    static {
        System.loadLibrary("swm_ecg_hrv_algo");
    }

    static native int GetRriDistributionSize(double[] rriAry);
    static native void GetRriDistribution(double[] rriAry, double[] timeAry, double[] rriDistribution, double[] rriDistributionIdx, int distributionSize);
    static native void GetFrequencyData(double[] rriAry, double[] timeAry, double[] frequencyData);

    HrvService() {
    }

    private void initRriDistributionService() {
        mRriDistributionServiceQueue = new LinkedBlockingQueue<>();
        mRriDistributionService = new RriDistributionService();
        mRriDistributionServiceQueueWorker = new Thread() {
            @Override
            public void run() {
                super.run();
                for(;;) {
                    if(!SwmCore.sRunning)
                        return;

                    try {
                        RtoRintervalData data = mRriDistributionServiceQueue.take();
                        mRriDistributionService.onRtoRintervalDataAvailable(data);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        };
        mRriDistributionServiceQueueWorker.start();
    }

    private void initRriFrequencyService() {
        mRriFrequencyServiceQueue = new LinkedBlockingQueue<>();
        mRriFrequencyService = new RriFrequencyService();
        mRriFreqServiceQueueWorker = new Thread() {
            @Override
            public void run() {
                super.run();
                for(;;) {
                    if(!SwmCore.sRunning)
                        return;

                    try {
                        RtoRintervalData data = mRriFrequencyServiceQueue.take();
                        mRriFrequencyService.onRtoRintervalDataAvailable(data);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        };
        mRriFreqServiceQueueWorker.start();
    }

    synchronized void setRriDistributionListener(RriDistributionListener listener) {
        if(mRriDistributionServiceQueue == null) {
            initRriDistributionService();
            SwmCore.getIns().getHeartRateService().setRriDataListener(this);
        }
        mRriDistributionService.setListener(listener);
    }

    synchronized void removeRriDistributionListener() {
        SwmCore.getIns().getHeartRateService().removeRriDataListener();
        mRriDistributionService.removeListener();
        mRriDistributionServiceQueueWorker.interrupt();
        mRriDistributionServiceQueue = null;
    }

    synchronized void setRriFreqListener(RriFrequencyListener listener) {
        if(mRriFrequencyServiceQueue == null) {
            initRriFrequencyService();
            SwmCore.getIns().getHeartRateService().setRriDataListener(this);
        }
        mRriFrequencyService.setListener(listener);
    }

    synchronized void removeRriFreqListener() {
        SwmCore.getIns().getHeartRateService().removeRriDataListener();
        mRriFrequencyService.removeListener();
        mRriFreqServiceQueueWorker.interrupt();
        mRriFrequencyServiceQueue = null;
    }

    @Override
    public synchronized void onRtoRintervalDataAvailable(RtoRintervalData data) {
        if(mRriDistributionServiceQueue != null)
            mRriDistributionServiceQueue.offer(data);

        if (mRriFrequencyServiceQueue != null)
            mRriFrequencyServiceQueue.offer(data);


    }
}
