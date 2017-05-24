package com.swm.sdk;

/**
 * Created by yangzhenyu on 2017/4/17.
 */

class MedicalHeartEngine extends HeartEngine {
    static {
        System.loadLibrary("swm_medical_ecg_algo");
    }

    static native void HeartClassification();

    @Override
    public void start() {
        
    }

    @Override
    public void stop() {

    }

    @Override
    public void onFuel(BleData data) {

    }
}
