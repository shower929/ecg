package com.swm.core;

import com.swm.body.PersonalModule;
import com.swm.heartbeat.HeartBeatListener;
import com.swm.training.TrainingListener;

import static com.swm.training.TrainingListener.DANGER;
import static com.swm.training.TrainingListener.INTENSITY_10K;
import static com.swm.training.TrainingListener.INTENSITY_E;
import static com.swm.training.TrainingListener.INTENSITY_I;
import static com.swm.training.TrainingListener.INTENSITY_M;
import static com.swm.training.TrainingListener.INTENSITY_NONE;
import static com.swm.training.TrainingListener.INTENSITY_T;

/**
 * Created by yangzhenyu on 2016/11/11.
 */

class SportService implements HeartBeatListener{
    private TrainingListener mListener;
    private int mMaxHeartRate;
    private int mCurrentIntensity;

    SportService () {
        mMaxHeartRate = PersonalModule.getIns().getMaxHeartRate();
    }

    void setTrainingListener(TrainingListener listener) {
        mListener = listener;
        try {
            SwmCore.getIns().getHeartBeatService().addListener(this);
        } catch (Exception exception) {

        }
        mListener.onTrainingIntensityChanged(mCurrentIntensity);
    }

    void removeTrainingListener() {
        mListener = null;
        SwmCore.getIns().getHeartBeatService().removeListener(this);
    }

    private int calculateIntensity(float maxHeartRate) {

        if (maxHeartRate < 0.65f)
            return INTENSITY_NONE;

        if (maxHeartRate >= 0.65f && maxHeartRate <= .79f)
            return INTENSITY_E;

        if (maxHeartRate >= 0.8f && maxHeartRate <= 0.89f)
            return INTENSITY_M;

        if (maxHeartRate >= 0.88f && maxHeartRate <= 0.92f)
            return INTENSITY_T;

        if (maxHeartRate >= 0.925 && maxHeartRate <= 0.97f)
            return INTENSITY_10K;

        if (maxHeartRate >= 0.975f && maxHeartRate <= 1f)
            return INTENSITY_I;

        if (maxHeartRate > 1f)
            return DANGER;

        return -1;
    }

    @Override
    public void onHeartBeatDataAvailable(HeartBeatData heartBeatData) {
        float maxHeartRate = (float) heartBeatData.heartRate / mMaxHeartRate;
        int intensity = calculateIntensity(maxHeartRate);

        if (intensity == mCurrentIntensity || intensity == -1)
            return;

        mCurrentIntensity = intensity;

        mListener.onTrainingIntensityChanged(intensity);
        SwmCore.getIns().getSuperRunCloudService().logIntensity(mCurrentIntensity);
    }
}
