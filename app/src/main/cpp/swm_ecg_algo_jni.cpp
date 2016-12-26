//
// Created by 楊鎮宇 on 2016/10/8.
//
#include <jni.h>
#include "GlobalWare/SWM_COM_Header.h"
#include "AlgorithmWare/SWM_ECG_ALGO.h"
extern "C" {
JNIEXPORT void JNICALL
Java_com_swm_core_SwmCore_CalculateEcgMetaData(JNIEnv *env, jobject thiz,
                                                        jobject obj,
                                                         jintArray arr) {
    jint *data = env->GetIntArrayElements(arr, 0);
    APPS_RealTime_OS((long *) data);

    short heartRate = APPS_ECG_GetValueHR();
    short sdnn = APPS_HRV_GetValueSDNN();
    short rmssd = APPS_HRV_GetValueRMSSD();

    jclass clazz = env->GetObjectClass(obj);
    jfieldID heartRateParam = env->GetFieldID(clazz, "heartRate", "I");
    jfieldID  sdnnParam = env->GetFieldID(clazz, "sdnn", "I");
    jfieldID  rmssdParam = env->GetFieldID(clazz, "rmssd", "I");
    env->SetIntField(obj, heartRateParam, heartRate);
    env->SetIntField(obj, sdnnParam, sdnn);
    env->SetIntField(obj, rmssdParam, rmssd);

    env->ReleaseIntArrayElements(arr, data, 0);
}

JNIEXPORT jint JNICALL
Java_com_swm_core_SwmCore_APPSHrvGetValueSDNN(JNIEnv *env, jobject thiz) {
    short sdnn = APPS_HRV_GetValueSDNN();
    return sdnn;
}

JNIEXPORT jint JNICALL
Java_com_swm_core_SwmCore_APPSHrvGetValueRMSSD(JNIEnv *env, jobject thiz) {
    short rmssd = APPS_HRV_GetValueRMSSD();
    return rmssd;
}

JNIEXPORT void JNICALL
Java_com_swm_core_SwmCore_APPSEcgInitialForModeChange(JNIEnv *env, jobject thiz) {
    APPS_ECG_InitialForModeChange(1);
}
}