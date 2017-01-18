//
// Created by 楊鎮宇 on 2016/10/8.
//
#include <jni.h>
#include <vector>
#include "GlobalWare/SWM_COM_Header.h"
#include "AlgorithmWare/SWM_ECG_ALGO.h"
#include "SWM_algo_HRV.h"
#include "mymath.h"
#include <android/log.h>

#define  LOG_TAG    "HRV"

#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

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

double *rriCount = new double[HRV_RRI_LIMIT_BUF];
double *rriTime = new double[HRV_RRI_LIMIT_BUF];
double dataSize;

JNIEXPORT jint JNICALL
Java_com_swm_core_SwmCore_GetBinSize(JNIEnv *env, jobject thiz) {

    APPS_READ_RRI_DATA(rriCount, rriTime);

    //// HRV Time////
    double maxRRI=findMaxValue(rriCount,0, HRV_RRI_LIMIT_BUF);// used for estimated how many bins for Histogram
    double minRRI=findMinValue(rriCount,0, HRV_RRI_LIMIT_BUF);// used for estimated how many bins for Histogram
    dataSize=int((maxRRI-minRRI)/7.8125)+2;				// used for estimated how many bins for Histogram
    return dataSize;
}

JNIEXPORT void JNICALL
Java_com_swm_core_SwmCore_GetRriBins(JNIEnv *env, jobject thiz, jdoubleArray jrriCount, jdoubleArray jrriTime) {
    jsize NumofBin = env->GetArrayLength(jrriCount);
    std::vector <double> BinCount; // Bins
    std::vector <double> BinTimeIndex; // Bins time index
    BinCount.assign(NumofBin,0);//
    BinTimeIndex.assign(NumofBin,0);//
    double Output_time[6]={0};

    SWM_HRV_Time_Histogram(rriCount, rriTime, NumofBin, Output_time, &BinCount.front(), &BinTimeIndex.front());

    // Copy to output
    jdouble rriCountCopy[NumofBin];
    jdouble rriTimeCopy[NumofBin];

    int i = 0;

    for (i = 0; i < BinCount.size(); i++) {
        rriCountCopy[i] = BinCount.at(i);
        rriTimeCopy[i] = BinTimeIndex.at(i);
        LOGD("Rri[%d]count:%d, time:%d", i, rriCountCopy[i], rriTimeCopy[i]);
    }

    // Clean vector
    std::vector<double>().swap(BinCount);
    std::vector<double>().swap(BinTimeIndex);

    env->SetDoubleArrayRegion(jrriCount, 0, NumofBin, rriCountCopy);
    env->SetDoubleArrayRegion(jrriTime, 0, NumofBin, rriTimeCopy);
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

JNIEXPORT void JNICALL
Java_com_swm_core_SwmCore_GetFrequencyData(JNIEnv *env, jobject thiz, jdoubleArray frequencyData) {
    double *rriCount = new double[HRV_RRI_LIMIT_BUF];
    double *rriTime = new double[HRV_RRI_LIMIT_BUF];
    double dataSize;

    APPS_READ_RRI_DATA(rriCount, rriTime);

    //// HRV Time////
    double maxRRI=findMaxValue(rriCount,0, HRV_RRI_LIMIT_BUF);// used for estimated how many bins for Histogram
    double minRRI=findMinValue(rriCount,0, HRV_RRI_LIMIT_BUF);// used for estimated how many bins for Histogram
    dataSize=int((maxRRI-minRRI)/7.8125)+2;

    ////// HRV Frequency//////////////////////
    double Output[5]={0};
    double samplerate=10;
    SWM_HRV_Frequency(rriCount, rriTime, dataSize, samplerate, Output);

    env->SetDoubleArrayRegion(frequencyData, 0, 5, Output);
}
}