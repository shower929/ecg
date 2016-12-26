//
// Created by 楊鎮宇 on 2016/10/8.
//
#include <jni.h>
#include "SWM_ECG_ALGO.h"
extern "C" {
JNIEXPORT jint JNICALL
Java_com_swm_core_HeartRateService_AppsEcgSimulationRTOS(JNIEnv *env, jobject thiz,
                                                         jintArray arr) {
    jint *data = env->GetIntArrayElements(arr, 0);
    short heartbeat = APPS_ECG_SimulationRTOS(data);
    env->ReleaseIntArrayElements(arr, data, 0);
    return heartbeat;
}
}