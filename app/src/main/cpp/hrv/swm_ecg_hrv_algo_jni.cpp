//
// Created by 楊鎮宇 on 2017/1/19.
//
#include <jni.h>
#include <vector>
#include "SWM_algo_HRV.h"
#include "mymath.h"


extern "C" {

    JNIEXPORT jint JNICALL
    Java_com_swm_core_HrvService_GetRriDistributionSize(JNIEnv *env, jobject thiz, jdoubleArray jrriAry) {

        jdouble* rriCount = env->GetDoubleArrayElements(jrriAry, 0);
        jsize size = env->GetArrayLength(jrriAry);
        //// HRV Time ////
        double maxRRI=findMaxValue(rriCount,0, size);// used for estimated how many bins for Histogram
        double minRRI=findMinValue(rriCount,0, size);// used for estimated how many bins for Histogram
        double dataSize=int((maxRRI-minRRI)/7.8125)+2;				// used for estimated how many bins for Histogram
        return dataSize;
    }

    JNIEXPORT void JNICALL
    Java_com_swm_core_HrvService_GetRriDistribution(JNIEnv *env, jobject thiz, jdoubleArray jrriAry, jdoubleArray jtimeAry, jdoubleArray jrriDistribution, jdoubleArray jrriDistributionIdx, jint distributionSize) {
        jdouble* rriAry = env->GetDoubleArrayElements(jrriAry, 0);
        jdouble* timeAry = env->GetDoubleArrayElements(jtimeAry, 0);

        std::vector <double> BinCount; // Bins
        std::vector <double> BinTimeIndex; // Bins time index
        BinCount.assign(distributionSize,0);//
        BinTimeIndex.assign(distributionSize,0);//
        double Output_time[6]={0};

        SWM_HRV_Time_Histogram(rriAry, timeAry, distributionSize, Output_time, &BinCount.front(), &BinTimeIndex.front());

        // Copy to output
        jdouble rriBinSize[distributionSize];
        jdouble rriBinIdx[distributionSize];

        int i = 0;

        for (i = 0; i < BinCount.size(); i++) {
            rriBinSize[i] = BinCount.at(i);
            rriBinIdx[i] = BinTimeIndex.at(i);
        }

        BinCount.clear();
        BinTimeIndex.clear();

        env->SetDoubleArrayRegion(jrriDistribution, 0, distributionSize, rriBinSize);
        env->SetDoubleArrayRegion(jrriDistributionIdx, 0, distributionSize, rriBinIdx);
    }

    JNIEXPORT void JNICALL
    Java_com_swm_core_HrvService_GetFrequencyData(JNIEnv *env, jobject thiz, jdoubleArray jrriAry, jdoubleArray jtimeAry, jdoubleArray frequencyData) {
        jdouble* rriAry = env->GetDoubleArrayElements(jrriAry, 0);
        jdouble* timeAry = env->GetDoubleArrayElements(jtimeAry, 0);
        jsize size = env->GetArrayLength(jrriAry);

        ////// HRV Frequency//////////////////////
        double Output[5]={0};
        double samplerate=10;
        SWM_HRV_Frequency(rriAry, timeAry, size, samplerate, Output);

        env->SetDoubleArrayRegion(frequencyData, 0, 5, Output);
    }
}