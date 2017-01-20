package com.swm.core;

import android.location.Location;
import android.os.Binder;

import com.swm.accelerator.AcceleratorListener;
import com.swm.breath.BreathListener;
import com.swm.device.SwmDeviceListener;
import com.swm.heartbeat.HeartRateListener;
import com.swm.hrv.RmssdListener;
import com.swm.hrv.RriFrequencyListener;
import com.swm.hrv.RriDistributionListener;
import com.swm.hrv.SdnnListener;
import com.swm.motion.MotionListener;
import com.swm.training.TrainingListener;

/**
 * Created by yangzhenyu on 2016/9/27.
 */

public class SwmBinder extends Binder {
    public static final String SCENARIO_TRAINING = "scenario_training";

    SwmBinder() {
    }

    public void setMotionListener(MotionListener listener) {
        SwmCore.getIns().getMotionService().setListener(listener);
    }

    public void removeMotionListener() {
        SwmCore.getIns().getMotionService().removeListener();
    }

    void registerEcgRawDataListener(EcgRawDataListener listener) throws Exception {
        SwmCore.getIns().getEcgService().registerListener(listener);
    }

    void removeEcgRawDataListener(EcgRawDataListener listener) {
        SwmCore.getIns().getEcgService().removeListener(listener);
    }
    public void setAcceleratorListener(AcceleratorListener listener) {
        SwmCore.getIns().getAcceleratorService().setListener(listener);
    }

    public void removeAcceleratorListener() {
        SwmCore.getIns().getAcceleratorService().removeListener();
    }

    public void setBreathListener(BreathListener listener) {
        SwmCore.getIns().getBreathService().setListener(listener);
    }

    public void removeBreathListener() {
        SwmCore.getIns().getBreathService().removeListener();
    }

    public void registerHeartRateListener(HeartRateListener listener) throws Exception{
        SwmCore.getIns().getHeartRateService().addListener(listener);
    }

    public void removeHeartRateListener(HeartRateListener listener) {
        SwmCore.getIns().getHeartRateService().removeListener(listener);
    }

    public void setDeviceListener(SwmDeviceListener listener) {
        SwmDeviceController.getIns().setListener(listener);
    }

    public void removeDeviceListener() {
        SwmDeviceController.getIns().removeListener();
    }


    public void logLocation(Location location) {
        if (SwmCore.getIns().getSuperRunCloudService().isMonitoring())
            SwmCore.getIns().getSuperRunCloudService().logLocation(location);
    }

    public void setTrainingListener(TrainingListener listener) {
        SwmCore.getIns().getSportService().setTrainingListener(listener);
    }

    public void removeTrainingListener() {
        SwmCore.getIns().getSportService().removeTrainingListener();
    }

    public void startSport() {
        SwmCore.getIns().getSuperRunCloudService().logEvent(new UserEvent("start_sport"));
    }

    public void stopSport() {
        SwmCore.getIns().getSuperRunCloudService().logEvent(new UserEvent("stop_sport"));
    }

    public void startMonitorHrv() {
        SwmCore.getIns().getHeartRateService().startMonitorHrv();
    }

    public void stopMonitorHrv() {
        SwmCore.getIns().getHeartRateService().stopMonitorHrv();
    }

    public void setRriDistributionListener(RriDistributionListener listener) {
        SwmCore.getIns().getHrvService().setRriDistributionListener(listener);
    }

    public void removeRriDistributionListener() {
        SwmCore.getIns().getHrvService().removeRriDistributionListener();
    }

    public void setRriFreqListener(RriFrequencyListener listener) {
        SwmCore.getIns().getHrvService().setRriFreqListener(listener);
    }

    public void removeRriFreqListener() {
        SwmCore.getIns().getHrvService().removeRriFreqListener();
    }

    public void setSdnnListener(SdnnListener listener) {
        SwmCore.getIns().getHeartRateService().setSdnnListener(listener);
    }

    public void removeSdnnListener() {
        SwmCore.getIns().getHeartRateService().removeSdnnListener();
    }

    public void setRmssdListener(RmssdListener listener) {
        SwmCore.getIns().getHeartRateService().setRmssdListener(listener);
    }

    public void removeRmssdListener() {
        SwmCore.getIns().getHeartRateService().removeRmssdListener();
    }
}
