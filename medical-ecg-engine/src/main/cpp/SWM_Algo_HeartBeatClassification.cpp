
#include "mymath.h" 
#include"MatrixCalcualtion.h"
#include "SWM_Algo_Methods.h"
#include"SWM_Algo_API.h"
using namespace std;


void SWM_Algo_FeatureExtraction(vector <vector<float>> dataBuff_ECG,vector <vector<int>> dataBuff_Rpeak,int n_Rpeak, int SampleRate, vector<vector<float>>* dataBuff_feature)
{
	// Declare
	float tmp=0;
	int dataBuff_Rpeak_previous=0, dataBuff_Rpeak_next=0, dataBuff_Rpeak_current=0;
	int Index_StrPoint=0,Index_EndPoint=0;
	int n_tmp=0, count_tmp=0,count_feature=0;
	int evLatency[2]={1, 1}; // for timewarp used 
	int newLatency[2]={1, 1}; // for timewarp used 
	vector <vector<float>> dataBuff_subECG; (dataBuff_subECG).resize(1); // for the segmented ECG wave
	vector<vector<float>> M;// for timewarp martix
	vector<vector<float>> dataBuff_subECG_warp; 
	vector<float>  dataBuff_ECG_warp; dataBuff_ECG_warp.resize(50);//Store the extracted complex ECG, fixed at 50 points
	vector <float> featuredata_Morphological; featuredata_Morphological.resize(30); // 5­ÓMorphological©M25­ÓDTF
	vector <float> statisticfeature;  statisticfeature.resize(4); // kurtosis, skewness, variance of histgoram, entropy
	Algo <float> AlgoApproach;
	MyMath<float> tmparry(dataBuff_ECG_warp);
	
	vector <float> tmpsig;           tmpsig.resize(dataBuff_ECG.size());  // used for filer data
	vector <float> tmpsig_filter;    tmpsig_filter.resize(dataBuff_ECG.size()); // used for filer data 
		 
	// Store Extracted features, resize the memory
	(*dataBuff_feature).resize(n_Rpeak-2);// 
	for(int i=0; i!=(n_Rpeak-2); ++i) (*dataBuff_feature)[i].resize(87);// total 87 features


	
	
	if (SampleRate==360)
	{
		for (int i=0; i<dataBuff_ECG.size(); i++)
			tmpsig[i]=dataBuff_ECG[i][0];
		
		// 1.notch filter (60Hz)
		notchFilter_60Hz_sr360Hz(tmpsig,&tmpsig_filter, dataBuff_ECG.size());
		// 2. Baseline wander cancelation
		BaselineWanderFilter_360Hz(tmpsig_filter, &tmpsig, dataBuff_ECG.size());
		for (int i=0; i<dataBuff_ECG.size(); i++)
			dataBuff_ECG[i][0]=tmpsig[i];
	}
	else if (SampleRate==250)
	{
		for (int i=0; i<dataBuff_ECG.size(); i++)
			tmpsig[i]=dataBuff_ECG[i][0];
		
		// 1.notch filter (60Hz)
		notchFilter_60Hz_sr250Hz(tmpsig,&tmpsig_filter, dataBuff_ECG.size());
		// 2. Baseline wander cancelation
		BaselineWanderFilter_250Hz(tmpsig_filter, &tmpsig, dataBuff_ECG.size());
		for (int i=0; i<dataBuff_ECG.size(); i++)
			dataBuff_ECG[i][0]=tmpsig[i];
	}

	// time-warp matrix
	tmp=SampleRate/4;
	n_tmp=2*floor(tmp)+1;
	evLatency[1]=n_tmp;
	newLatency[1]=50;	
	AlgoApproach.SWM_algo_timewarp(evLatency,newLatency,&M);
	

	/////////////// Feature Extraction /////////////////////////////
	for (int i=1; i<n_Rpeak-1; i++) // First and last R-peaks excluded.
	{
		// minus one, because data array starts from position 0 in c++.
		dataBuff_Rpeak_previous=dataBuff_Rpeak[i-1][0] - 1;// previous R-peak
		dataBuff_Rpeak_current=dataBuff_Rpeak[i][0] - 1; // current R-peak (we try to classify this R-peak)
		dataBuff_Rpeak_next=dataBuff_Rpeak[i+1][0] - 1; // next R-peak
		
		/// used approach (ECG was segmented by R-peak-SampleRate/4 : R-peak+SampleRate/4)
		tmp=SampleRate/4;
		Index_StrPoint=dataBuff_Rpeak_current-floor(tmp);
		Index_EndPoint=dataBuff_Rpeak_current+floor(tmp);
		n_tmp=Index_EndPoint-Index_StrPoint+1;
		dataBuff_subECG.resize(n_tmp);
		//dataBuff_subECG.reserve(n_tmp);
		for(int j=0; j!=n_tmp; ++j) 
			dataBuff_subECG[j].resize(1);

		count_tmp=0;
		for (int j=Index_StrPoint; j<=Index_EndPoint;j++){
			dataBuff_subECG[count_tmp][0]=dataBuff_ECG[j][0];
			count_tmp++;
		}
		
		
		Matrix_multiplication(M,dataBuff_subECG,&dataBuff_subECG_warp);
		for (int j=0; j<50;j++)
			dataBuff_ECG_warp[j]=dataBuff_subECG_warp[j][0];

		dataBuff_subECG_warp.clear();


		/* old approach (ECG was segmented by RR-interval) 
		///////////////////////////////////////////////
		//// segment a complex ECG wave with timewarp approach ///
		// the first half
		tmp=(dataBuff_Rpeak_current-dataBuff_Rpeak_previous)/3;
		Index_StrPoint=dataBuff_Rpeak_current-floor(tmp);
		Index_EndPoint=dataBuff_Rpeak_current;
		n_tmp=Index_EndPoint-Index_StrPoint+1;
		dataBuff_subECG.resize(n_tmp);
		dataBuff_subECG.reserve(n_tmp);
		for(int j=0; j!=n_tmp; ++j) 
			dataBuff_subECG[j].resize(1);

		count_tmp=0;
		for (int j=Index_StrPoint; j<=Index_EndPoint;j++){
			dataBuff_subECG[count_tmp][0]=dataBuff_ECG[j][0];
			count_tmp++;
		}
		evLatency[1]=n_tmp;
		newLatency[1]=25;	
		AlgoApproach.SWM_algo_timewarp(evLatency,newLatency,&M);
		Matrix_multiplication(M,dataBuff_subECG,&dataBuff_subECG_warp);
		for (int j=0; j<25;j++)
		dataBuff_ECG_warp[j]=dataBuff_subECG_warp[j][0];

		dataBuff_subECG_warp.clear();

		// the second half
		tmp=(dataBuff_Rpeak_next-dataBuff_Rpeak_current)/2;
		Index_StrPoint=dataBuff_Rpeak_current;
		Index_EndPoint=dataBuff_Rpeak_current+tmp;
		n_tmp=Index_EndPoint-Index_StrPoint+1;
		dataBuff_subECG.resize(n_tmp);
		for(int j=0; j!=n_tmp; ++j) dataBuff_subECG[j].resize(1);

		count_tmp=0;
		for (int j=Index_StrPoint; j<=Index_EndPoint;j++){
			dataBuff_subECG[count_tmp][0]=dataBuff_ECG[j][0];
			count_tmp++;
		}
		evLatency[1]=n_tmp;
		newLatency[1]=26;
		AlgoApproach.SWM_algo_timewarp(evLatency,newLatency,&M);
		Matrix_multiplication(M,dataBuff_subECG,&dataBuff_subECG_warp);
		for (int j=1; j<26;j++) // First point must exclude
			dataBuff_ECG_warp[25+j-1]=dataBuff_subECG_warp[j][0];
		
		dataBuff_subECG_warp.clear();
		*/

		///////////////////////////////////////////////////
		/// zscore
		(tmparry.data).assign(dataBuff_ECG_warp.begin(), dataBuff_ECG_warp.end());
		tmparry.Statistic_Zsore();
		dataBuff_ECG_warp.clear();
		dataBuff_ECG_warp=tmparry.data;
		

		///////////////////////////////////////////////////////////
		// feature 0:49 (timewarp ECG)
		for (int j=0; j<50;j++){
			(*dataBuff_feature)[count_feature][j]=dataBuff_ECG_warp[j];
		}
		
		AlgoApproach.SWM_algo_FeatureExtraction_Morphological(dataBuff_ECG_warp, 50, &featuredata_Morphological);
		// feature 50:74 PSD
		for (int j=0; j<25;j++) {(*dataBuff_feature)[count_feature][j+50]=featuredata_Morphological[j+5];};
		// feature 75:79  Morphological
		for (int j=0; j<5;j++) {(*dataBuff_feature)[count_feature][j+75]=featuredata_Morphological[j];};
		///////////////////////////////////////
		// RRI for previous R-peak, unit: second 
        (*dataBuff_feature)[count_feature][80]=(dataBuff_Rpeak_current-dataBuff_Rpeak_previous)/(float(SampleRate));
		// RRI for next R-peak, unit: second 
		(*dataBuff_feature)[count_feature][81]=(dataBuff_Rpeak_next-dataBuff_Rpeak_current)/(float(SampleRate));
		
		// kurtosis, skewness, variance of histgoram, entropy
		StatisticFeatures(tmparry, &statisticfeature);
		(*dataBuff_feature)[count_feature][82]=statisticfeature[0];
		(*dataBuff_feature)[count_feature][83]=statisticfeature[1];
		(*dataBuff_feature)[count_feature][84]=statisticfeature[2];
		(*dataBuff_feature)[count_feature][85]=statisticfeature[3];
		// Fractal Dimension
		(*dataBuff_feature)[count_feature][86]=get_FractalDimension(dataBuff_ECG_warp);
		count_feature++;
	}
	// free memory
	tmparry.data.clear();
	dataBuff_subECG.clear();
	M.clear();
	dataBuff_subECG_warp.clear();
	dataBuff_ECG_warp.clear();
	featuredata_Morphological.clear();
	statisticfeature.clear();
	tmpsig_filter.clear();
	tmpsig.clear();
}


void SWM_Algo_HeartBeatClassification(vector<vector<float>> dataBuff_feature, vector<vector<float>> *PredictedProbability, vector<int>* PredictedLabel, DNN_Net_HeartBeatClassification DNN)
{
	long n_pattern=dataBuff_feature.size();
	int a=1;
	int maxIndex=-1;
	vector<vector <float>> tmp_data,tmp_matrix1,tmp_matrix2,tmp_matrix3;
	float tmp=0,tmp_sum=0;

	tmp_data.resize(87);
	for(int i=0; i<87; i++) tmp_data[i].resize(1);
	tmp_matrix1.resize(150);
	tmp_matrix2.resize(150);
	tmp_matrix3.resize(4);
	for(int i=0; i<150; i++) 
	{
		if (i<4) 
		{tmp_matrix3[i].resize(1);}

		tmp_matrix1[i].resize(1);
		tmp_matrix2[i].resize(1);
	}

	for (int i=0; i<n_pattern;i++)
	{
		for (int in=0; in<87;in++)
			tmp_data[in][0]=dataBuff_feature[i][in];

		// Z-score
		DNN_Test_Normalization(&tmp_data,DNN.mu,DNN.sigma);
	
		// input to hidden 1
		Matrix_multiplication(DNN.DNN_W1,tmp_data, &tmp_matrix1);
		
		for (int j=0;j<150;j++)
		{
			tmp=tmp_matrix1[j][0]+DNN.DNN_b1[j][0];
			tmp_matrix1[j][0]=(1/(1+exp(-tmp)));
		}
		
		// hidden 1 to hidden 2
		Matrix_multiplication(DNN.DNN_W2,tmp_matrix1, &tmp_matrix2);
		for (int j=0;j<150;j++)
		{
			tmp=tmp_matrix2[j][0]+DNN.DNN_b2[j][0];
			tmp_matrix2[j][0]=(1/(1+exp(-tmp)));
		}
		// hidden 2 to output
		Matrix_multiplication(DNN.DNN_W3,tmp_matrix2, &tmp_matrix3);
		tmp_sum=0;
		for (int j=0;j<4;j++)
		{
			tmp=tmp_matrix3[j][0]+DNN.DNN_b3[j][0];
			tmp_matrix3[j][0]=(1/(1+exp(-tmp))); // 
			tmp_matrix3[j][0]=exp(tmp_matrix3[j][0]);
			tmp_sum+=tmp_matrix3[j][0]; // 
		}
		// 
		for (int j=0;j<4;j++)
			(*PredictedProbability)[i][j]=tmp_matrix3[j][0]/tmp_sum;
		
		// Labeling to 1,2,3,4
		maxIndex=-1;
		for (int j=0; j<4;j++)
			if (maxIndex<0 || (*PredictedProbability)[i][j]>(*PredictedProbability)[i][maxIndex])
				maxIndex=j;
		
		(*PredictedLabel)[i]=maxIndex+1;
			
	}
}



void StatisticFeatures(MyMath <float> Signal, vector <float> *statisticfeature)
{
	float minvalue=0,maxvalue=0, t1=0, t2=0,stepsize=0.1,tmp=0,tmpsum=0;
	int count=0, index=0, c_count=0, tmp_count=0;
	vector <float> n;
	
	sort(Signal.data.begin(), Signal.data.end());
	minvalue=Signal.data[0];
	maxvalue=Signal.data[Signal.data.size()-1];
	count=floor((maxvalue-(minvalue-0.5))/stepsize + 1);
	n.resize(count);

	// Histgoram of ECG wave
	t1=minvalue;
	t2=t1+0.1;
	while(1)
	{
		t1=minvalue+stepsize*c_count-0.5;
		t2=t1+stepsize;
		tmp_count=0;
		while (Signal.data[index]<t2)
		{
			if (Signal.data[index]>=t1)
			{
				tmp_count++; index++; 
				if(index>=Signal.data.size()) break;
			}
		}
		n[c_count]=tmp_count;
		
		if (t2>maxvalue)
			break;

		c_count++;
	}
	
	(*statisticfeature)[0]=Signal.Statistic_Kurtosis();
	(*statisticfeature)[1]=Signal.Statistic_Skewness();
	(*statisticfeature)[2]=variance(n);
	
	// entropy
	tmpsum=0;tmp=0;
	for (int i=0; i<count;i++)
	{
		if (n[i]!=0)
		{
			tmp=n[i]/Signal.data.size();
			tmpsum+=-tmp*(log(tmp)/log(float(2)));
		}
	}
	(*statisticfeature)[3]=tmpsum;
	n.clear();
}
float variance(vector <float> data)
{
// variance 
	float mean = 0,temp = 0;
	for(int i = 0; i < data.size(); i++)
		mean = mean+ data[i];
	
	mean=mean/data.size();
	for(int i = 0; i < data.size(); i++)
		temp += pow((data[i] - mean), 2) ;

	return temp=temp/(data.size()-1);
}
float get_FractalDimension(vector <float> data)
{
	vector <float> tmparrary;
	vector <float> y;
	float minvalue=0, maxvalue=0,span=0, L=0, D=0, tmp=0, x=0;
	y.resize(data.size()-1);

	tmparrary=data;
	sort(tmparrary.begin(),tmparrary.end());
	minvalue=tmparrary[0];
	maxvalue=tmparrary[tmparrary.size()-1];
	span=maxvalue-minvalue;
	if (span<0.000001)
		D = 1;
	else
	{
		x=float(1)/float((data.size()-1));
		for (int i=0; i<data.size();i++)
			tmparrary[i]=(data[i]-minvalue)/span;

		for (int i=0; i<data.size()-1;i++)
		{	
			y[i]=tmparrary[i+1]-tmparrary[i];
			L+=sqrt(x*x+y[i]*y[i]);
		}
		tmp=2*(data.size()-1);
		D=1+log(L)/ log(float(tmp));
	}
	tmparrary.clear();
	y.clear();
	return D-1;
}
void DNN_Test_Normalization(vector <vector<float>> *Testing_data,vector <vector<float>> mu,vector <vector<float>> sigma)
{
	for (int i=0;i<(*Testing_data).size();i++)
	{
		(*Testing_data)[i][0]=((*Testing_data)[i][0]- mu[i][0]) / sigma[i][0];
	}
}
