#pragma once
#include<vector>

// HRV frequency parameter
const double VLF_L (0.003);
const double VLF_U (0.04);
const double LF_L (0.04);
const double LF_U (0.15);
const double HF_L (0.15);
const double HF_U (0.4);


// DTF
void dft1(double inputData[], double PowerSpectrum[], long nfft);
void dft1_Freq(double Freq[], long nfft, long samplerate);


// HRV Frequency
void SWM_HRV_Frequency(double RRI[],double DataBuf_RRITime[]/*bin start time*/, long pDataSize/*Num of rri*/, double samplerate /*10 up sampling*/, double Output_Frequency[5]);
/*
Output_Frequency[0]=aVLF;
Output_Frequency[1]=aLF;
Output_Frequency[2]=aHF;
Output_Frequency[3]=aTP;
Output_Frequency[4]=aLF/aHF;
*/

// HRV Time
void SWM_HRV_Time_Histogram(double RRI[],double DataBuf_RRITime[], long pDataSize,double output[6], double* BinCount, double* BinTimeIndex);
/*
	Output_time[0]=HRV_TRI;
	Output_time[1]=TINN;
	Output_time[2]=SD;
	Output_time[3]=Kurtosis;
	Output_time[4]=Skewness;
	Output_time[5]=ShannonEntropy;
	BinCount: number of RRI in each bin
	BinTimeIndex: RRI time index for each bin
*/


//
void SWM_HRV_poincare(double RRI[], long pDataSize,double SD[2], double EigenVector[2][2]);
/*
	SD[0]: first standard deviation of largest variance (SD1)
	SD[1]: second standard deviation of second variance (SD2)
	EigenVector[0]: eigenvector 1 for SD1
	EigenVector[1]: eigenvector 1 for SD2
*/

// Linear Interpolation
void SWM_algo_LinearInterpolation(double* pTimeBuf,double*  pNewTimeBuf,double* pData,double*  pNewData,long* pDataSize,long* pNewDataSize, double samplerate);


