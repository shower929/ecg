//
// Created by 楊鎮宇 on 2016/10/8.
//
#include <jni.h>
#include "SWM_ECG_ALGO.h"
#include <android/log.h>

#define  LOG_TAG    "HR"

#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

extern "C" {
    JNIEXPORT int JNICALL
    Java_com_swm_sdk_SportHeartEngine_CalculateHeartRate(JNIEnv *env, jobject thiz, jintArray arr) {

        jint *data = env->GetIntArrayElements(arr, 0);
        short heartRate = APPS_ECG_SimulationRTOS(data);
        LOGI("Heart rate: %d", heartRate);
        env->ReleaseIntArrayElements(arr, data, 0);
        return heartRate;
    }

    JNIEXPORT void JNICALL
    Java_com_swm_sdk_SportHeartEngine_GetRtoRIntervalData(JNIEnv *env, jobject thiz, jdoubleArray jrriCount, jdoubleArray jrriTime) {
        double *rriCount = new double[HRV_RRI_MAX_BUF];
        double *rriTime = new double[HRV_RRI_MAX_BUF];

        APPS_ECG_RRI_DATA(rriCount, rriTime);

        env->SetDoubleArrayRegion(jrriCount, 0, HRV_RRI_MAX_BUF, rriCount);
        env->SetDoubleArrayRegion(jrriTime, 0, HRV_RRI_MAX_BUF, rriTime);

        delete []rriCount;
        delete []rriTime;
    }

    JNIEXPORT jfloat JNICALL
    Java_com_swm_sdk_SportHeartEngine_GetRmssd(JNIEnv *env, jobject thiz) {
        float rmssd = APPS_ECG_GetRMSSD();
        return rmssd;
    }

    JNIEXPORT jfloat JNICALL
    Java_com_swm_sdk_SportHeartEngine_GetSdnn(JNIEnv *env, jobject thiz) {
        float rmssd = APPS_ECG_GetSDNN();
        return rmssd;
    }

    JNIEXPORT void JNICALL
    Java_com_swm_sdk_SportHeartEngine_InitialForModeChange(JNIEnv *env, jobject thiz, jint mode) {
        APPS_ECG_InitialForModeChange(mode);
    }
}