//
// Created by 楊鎮宇 on 2017/4/27.
//

#include <jni.h>
#include "SWM_AccFunctionList.h"
#include <android/log.h>

#define  LOG_TAG    "HR"

#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

extern "C" {
    JNIEXPORT int JNICALL
    Java_com_swm_sdk_MotionEngineImpl_GetStep(JNIEnv *env, jobject thiz, jdoubleArray accX, jdoubleArray accY, jdoubleArray accZ) {
        jdouble *dataX = env->GetDoubleArrayElements(accX, 0);
        jdouble *dataY = env->GetDoubleArrayElements(accY, 0);
        jdouble *dataZ = env->GetDoubleArrayElements(accZ, 0);
        double *output = new double[4];

        GT_ACC_Motion_SuperRun_VelocityByPDR(output, dataX, dataY, dataZ);
        short step = output[1];

        delete output;

        return step;
    }
}