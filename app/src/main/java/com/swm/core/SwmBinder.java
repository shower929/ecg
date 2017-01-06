package com.swm.core;

import android.location.Location;
import android.os.Binder;

import com.google.firebase.auth.FirebaseAuth;
import com.swm.accelerator.AcceleratorListener;
import com.swm.breath.BreathListener;
import com.swm.device.SwmDeviceListener;
import com.swm.emergency.UserQueryCallback;
import com.swm.heartbeat.HeartBeatListener;
import com.swm.hrv.HrvListener;
import com.swm.hrv.RriListener;
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

    void registerEcgListener(EcgListener listener) throws Exception {
        SwmCore.getIns().getEcgService().registerListener(listener);
    }

    void removeEcgListener(EcgListener listener) {
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

    public void registerHeartRateListener(HeartBeatListener listener) throws Exception{
        SwmCore.getIns().getHeartBeatService().addListener(listener);
    }

    public void removeHeartRateListener(HeartBeatListener listener) {
        SwmCore.getIns().getHeartBeatService().removeListener(listener);
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

    public void registerHrvListener(HrvListener listener) throws Exception {
        SwmCore.getIns().getHrvService().addListener(listener);
    }

    public void removeHrvListener(HrvListener listener) {
        SwmCore.getIns().removeHrvListener(listener);
    }

    public void setRriListener(RriListener listener) {
        SwmCore.getIns().getHrvService().setRriListener(listener);
    }

    public void removeRriListener() {
        SwmCore.getIns().getHrvService().removeRriListener();
    }
}
