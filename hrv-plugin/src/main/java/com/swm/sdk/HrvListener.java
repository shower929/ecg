package com.swm.sdk;

/**
 * Created by yangzhenyu on 2017/4/26.
 */

public interface HrvListener {
    void onStressChanged(HrvPlugin.Stress newStress);
    void onPhyAgeChanged(int newPhyAge);
}
