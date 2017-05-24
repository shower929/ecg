#pragma once
#include "SWM_Algo_ClassMethod.h"
#include "SWM_Algo_Methods.h"
#include "mymath.h"
using namespace std;



////////////// Heart beat classification////////////////////////
// feature extraction///
void SWM_Algo_FeatureExtraction(vector <vector<float>> dataBuff_ECG,vector <vector<int>> dataBuff_Rpeak,int n_Rpeak, int SampleRate, vector<vector<float>>* dataBuff_feature);
void StatisticFeatures(MyMath <float> Signal, vector <float>* statisticfeature);
float get_FractalDimension(vector <float> data);
//classification///
void SWM_Algo_HeartBeatClassification(vector<vector<float>> dataBuff_feature, vector<vector<float>> *PredictedProbability, vector<int>* PredictedLabel, DNN_Net_HeartBeatClassification DNN);
void DNN_Strutcure(DNN_Net_HeartBeatClassification *DNN);
void DNN_Test_Normalization(vector <vector<float>> *Testing_data,vector <vector<float>> mu,vector <vector<float>> sigma);
//////////////////////////////////////////////////////////////






void RPeakDetection_Medical(vector <float> data, int n_data,int sr, vector <long>* R_index,vector <bool>* PVC_index, long* n_Rpeak);


void QRSDetection_WholeSignal_250Hz(vector <vector<float>>  dataBuff_ECG,vector <int>* Index_R, vector <bool>* Index_PVC,int windowsize, int stepsize,int sr,int n_ECGdata, int* n_totalRpeak);

void QRSDetection_WholeSignal_360Hz(vector <vector<float>>  dataBuff_ECG,vector <int>* Index_R, vector <bool>* Index_PVC,int windowsize, int stepsize,int sr,int n_ECGdata, int* n_totalRpeak);


void notchFilter_60Hz_sr250Hz(vector <float> x,vector <float>* y, long ndata);
// 60Hz notch filter, design for the signal with 250Hz sample rate.
void notchFilter_60Hz_sr360Hz(vector <float> x,vector <float>* y, long ndata);
// 60Hz notch filter, design for the signal with 360Hz sample rate.
// x: input signal, 
// y: filter signal, 
// ndata: length of input signal

void FIR_LP_250Hz(vector <float> x,vector <float>* y, long ndata);
// FIR low-pass filter (<40Hz), design for the signal with 250Hz sample rate.
void FIR_LP_360Hz(vector <float> x,vector <float>* y, long ndata);
// FIR low-pass filter (<40Hz), design for the signal with 360Hz sample rate.
// x: input signal, 
// y: filter signal, 
// ndata: length of input signal


void BaselineWanderFilter_250Hz(vector <float> x,vector <float>* y, long ndata);
// Remove the baseline Wander 
void BaselineWanderFilter_360Hz(vector <float> x,vector <float>* y, long ndata);
// Remove the baseline Wander 

void SignalEnhance_2D(vector <float> x,vector <float>* y, long ndata);
// The R wave would be enlarged in this API

void MeanFilter(vector <float> x,vector <float>* y,long ndata, int filtersize);
void MeidanFilter(vector <float> x,vector <float>* y,long ndata, int filtersize);

///////////////////////////////////////////////////////////////////////////////////




int QuickSortOnce(float data[], int low, int high);  
void QuickSort(float data[], int low, int high) ;  
float EvaluateMedian(float data[], int ndata);
float Math_Mean(vector <float> data);
float Statistic_SampleStandardDeviation(vector <float> data);
float ToNumber2(const string& text);
void FileReading2(char* file,vector<vector<float>>* array_2D ,int n_row,int n_cloumn);
float variance(vector <float> data);