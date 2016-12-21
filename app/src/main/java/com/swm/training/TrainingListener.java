package com.swm.training;

/**
 * Created by yangzhenyu on 2016/10/15.
 */

public interface TrainingListener {
    public static final int INTENSITY_NONE = 0;
    public static final int INTENSITY_E = 1;
    public static final int INTENSITY_M = 2;
    public static final int INTENSITY_T = 3;
    public static final int INTENSITY_10K = 4;
    public static final int INTENSITY_I = 5;
    public static final int INTENSITY_R = 6;
    public static final int DANGER = 7;

    void onTrainingIntensityChanged(int newIntensity);
}
