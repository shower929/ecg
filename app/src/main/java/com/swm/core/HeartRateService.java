package com.swm.core;

import com.swm.heartbeat.HeartRateListener;
import com.swm.hrv.RmssdListener;
import com.swm.hrv.SdnnListener;
import com.swm.sdk.EcgData;
import com.swm.sdk.EcgProviderClient;

import java.util.Vector;

/**
 * Created by yangzhenyu on 2016/10/5.
 */

class HeartRateService implements EcgProviderClient {
    private static final String LOG_TAG = "HeartRate";

    private Vector<HeartRateListener> mListeners;


    private RriCalculationService mRriCalculationService;

    private SdnnService mSdnnService;
    private RmssdService mRmssdService;
    private int mClient;


    @Override
    public void onEcgDataAvailable(final EcgData ecgData) {

    }

    HeartRateService() {

    }

    synchronized void addListener(HeartRateListener listener) throws Exception {
        if (mListeners == null)
            mListeners = new Vector<>();

        if (mListeners.contains(listener))
            throw new Exception("Listener is added already");
        mListeners.add(listener);
    }

    void removeListener(HeartRateListener listener) {
        if (mListeners == null)
            return;

        if(mListeners.contains(listener))
            mListeners.remove(listener);

    }

    void startRecord() {

    }

    void stopRecord() {

    }

    boolean isRecording() {
        return mDump != null;
    }

    void stop() {

    }

    private synchronized void initRriCalculationService() {
        if (mRriCalculationService == null)
            mRriCalculationService = new RriCalculationService();

    }

    private synchronized void stopRriCalculationService() {
        mRriCalculationService = null;
    }

    private synchronized void initSdnnService() {
        if(mSdnnService == null)
            mSdnnService = new SdnnService();
    }

    private synchronized void stopSdnnService() {
        mSdnnService = null;
    }

    private synchronized void initRmssdService() {
        if(mRmssdService == null)
            mRmssdService = new RmssdService();
    }

    private synchronized void stopRmssdService() {
        mRmssdService = null;
    }

    synchronized void startMonitorHrv() {
        mClient++;
        InitialForModeChange(1);
        initRriCalculationService();
        initSdnnService();
        initRmssdService();

        try {
            addListener(mRriCalculationService);
            addListener(mSdnnService);
            addListener(mRmssdService);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    synchronized void stopMonitorHrv() {
        mClient--;
        if (mClient > 0)
            return;
        removeListener(mRmssdService);
        removeListener(mSdnnService);
        removeListener(mRriCalculationService);

        stopRmssdService();
        stopSdnnService();
        stopRriCalculationService();
        InitialForModeChange(0);
    }

    void setRriDataListener(RtoRintervalDataListener listener) {
        mRriCalculationService.setListener(listener);
    }

    void removeRriDataListener() {
        mRriCalculationService.removeListener();
    }

    void setSdnnListener(SdnnListener listener) {
        if (mSdnnService == null) {
            throw new RuntimeException("Must start monitor hrv first");
        }
        mSdnnService.setListener(listener);
    }

    void removeSdnnListener() {
        mSdnnService.removeListener();
    }

    void setRmssdListener(RmssdListener listener) {
        if (mRmssdService == null) {
            throw new RuntimeException("Must start monitor hrv first");
        }
        mRmssdService.setListener(listener);
    }

    void removeRmssdListener() {
        mRmssdService.removeListener();
    }
}
