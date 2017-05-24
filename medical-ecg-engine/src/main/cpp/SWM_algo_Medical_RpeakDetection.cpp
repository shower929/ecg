
#include <vector>
#include"SWM_Algo_API.h"
#include"mymath.h"
#include <math.h>
using namespace std;

//void RPeakDetection_Medical(vector <float> data, int n_data,int sr, vector <long>* R_index,vector <bool>* PVC_index, long* n_Rpeak, float RRI_bar)
void RPeakDetection_Medical(vector <float> data, int n_data,int sr, vector <long>* R_index,vector <bool>* PVC_index, long* n_Rpeak)
{
	float M_PI=3.14159265358979323846;
	int meanfiltersize=int(7*float(sr)/256+0.5)+5;
	int *tmparrary_Left;   tmparrary_Left = (int*) malloc (sizeof(int)*((n_data/sr)*5));
	int *tmparrary_Right;  tmparrary_Right = (int*) malloc (sizeof(int)*((n_data/sr)*5)); 
	int c_left=0,c_right=0,c_Rpeak=0;
	int pos_start=0,pos_end=0;
	float tmp1=0,tmp2=0,index_tmp_max_qrs=0,index_tmp_max_sig=0,index_tmp_R=0;
	float std_sqr=0;
	int NMeanfilterThreshold;
	vector <float> sqr; 	sqr.resize(n_data); 
	vector <float> mdfint; 	mdfint.resize(n_data);
	vector <float> zt; 	zt.resize(n_data);
	vector <float> tmp_phase; 	tmp_phase.resize(n_data);
	vector <float> DynamicThreshold; 	DynamicThreshold.resize(n_data);
	
	// signal processing
	SignalEnhance_2D(data, &sqr, n_data);	
	MeanFilter(sqr, &mdfint,n_data, meanfiltersize);
	
	// dynamic threshold
	// reference:TKEO R Wave Detection for Real-time Processing using Nonlinear Transform and Dynamic Thresholding
	std_sqr=Statistic_SampleStandardDeviation(sqr);
	//NMeanfilterThreshold=int(1.5*(RRI_bar/1000*sr)+0.5);
	NMeanfilterThreshold=1.8*sr;
	MeanFilter(mdfint, &zt,n_data, NMeanfilterThreshold); // for dynamic threshold
	for (int i=0; i<n_data;i++)
		DynamicThreshold[i]=3*zt[i]+0.05*std_sqr;


	//// find indices into boudaries of each segment %%%
	c_left=0,c_right=0;c_Rpeak=0;
	for (int i=0; i<n_data;i++)
	{
		tmp_phase[i]=atan2(0,data[i]); // phase transformation
		if (mdfint[i]>DynamicThreshold[i])
			zt[i]=1;
		else
			zt[i]=0;
			
		if (i==0)
		{
			if ((int(zt[i])-0)==1)
				tmparrary_Left[c_left++]=i;
		}
		else if (i>=1 & i<n_data-1)
		{
			if ((int(zt[i])-int(zt[i-1]))==1)
				tmparrary_Left[c_left++]=i;
			else if ((int(zt[i])-int(zt[i-1]))==-1)
				tmparrary_Right[c_right++]=i-1;			
		}
		else if (i==n_data-1)
		{
			if ((0-int(zt[i]))==-1)
				tmparrary_Right[c_right++]=i-1;
		}	
	}

		
	if (c_left>0) // if R-peak exist
	{
		for (int i=0;i<c_left;i++)
		{
			pos_start=tmparrary_Left[i]-int(0.1*sr);
			pos_end=tmparrary_Right[i]+int(0.1*sr);

			if (pos_start>=(0+int(0.1*sr)) & pos_end<=(n_data-int(0.1*sr)))
			{
				for (int j=pos_start;j<=pos_end;j++)
				{
					if (j==pos_start)
						{tmp1=mdfint[j]; index_tmp_max_qrs=j;}
					else
					{
						if (mdfint[j]>tmp1)
						{tmp1=mdfint[j]; index_tmp_max_qrs=j;}
					}	
				}

				for (int j=pos_start;j<=index_tmp_max_qrs;j++)
				{
					if (j==pos_start)
						{tmp2=data[j];	 index_tmp_max_sig=j;}
					else
					{
						if (data[j]>tmp2)
						{tmp2=data[j]; index_tmp_max_sig=j;}
					}	
				}
				
				//初步判讀R波是不是有問題，如果微分局極大值和訊號極大值差很遠，QRS可能過寬，所以比較有可能有問題
				if (abs(index_tmp_max_qrs-index_tmp_max_sig)<=int(0.04*sr))
				(*PVC_index)[c_Rpeak]=0;
				else if (abs(index_tmp_max_qrs-index_tmp_max_sig)>int(0.04*sr))
				(*PVC_index)[c_Rpeak]=1;
				////////////////////////

				// % non-inverse, 處理過的訊號 phase如果是0，代表R波是正波
				if (tmp_phase[index_tmp_max_qrs]==0) 
				{
					for (int j=pos_start;j<=pos_end;j++)
					{
						if (tmp_phase[j]==0)
							zt[j]=data[j]*1;
						else
							zt[j]=0;
						
						// find the maximum
						if (j==pos_start)
						{tmp1=zt[j]; index_tmp_R=j;}
						else
						{
							if (zt[j]>tmp1)
							{tmp1=zt[j]; index_tmp_R=j;}
						}		
					}
				}
				// inverse，反之，可能是倒波。
				else 
				{
					for (int j=pos_start;j<=pos_end;j++)
					{
						if (tmp_phase[j]==M_PI)
							zt[j]=data[j]*1;
						else
							zt[j]=0;
						
						// find the maximum
						if (j==pos_start)
						{tmp1=zt[j]; index_tmp_R=j;}
						else
						{
							if (zt[j]<tmp1)
							{tmp1=zt[j]; index_tmp_R=j;}
						}		
					}
					(*PVC_index)[c_Rpeak]=1;
				}
				(*R_index)[c_Rpeak]=index_tmp_R;

				c_Rpeak++;
			}
		}	
	}
	(*n_Rpeak)=c_Rpeak;
	sqr.clear(); 
	mdfint.clear();
	zt.clear();
	tmp_phase.clear();
	DynamicThreshold.clear();
	delete []tmparrary_Left;
	delete []tmparrary_Right;
	tmparrary_Left=NULL;
	tmparrary_Right=NULL;

}

void QRSDetection_WholeSignal_250Hz(vector <vector<float>>  dataBuff_ECG,vector <int>* Index_R, vector <bool>* Index_PVC,int windowsize, int stepsize,int sr,int n_ECGdata, int* n_totalRpeak)
{
	long t=0; // count how many loop for ECG subsegment
	long t_start=0,t_end=0; // time index for each ECG subsegment, unit: point
	int n_tmpdata=windowsize*sr; 
	long n_Rpeak=0, n_subRpeak=0;
	float tmp=0;
	// float RRI_bar=1000; //used for Median filter for RPeakDetection_Medical, however, it's easy with error (in case R can't be detected). Hence, marked. 
	vector <long> R_index;           R_index.resize(windowsize*5); 
	vector <bool> PVC_index;         PVC_index.resize(windowsize*5); 
	vector <long> tmpR_index;        tmpR_index.resize(windowsize*5); 
	vector <bool> tmpPVC_index;      tmpPVC_index.resize(windowsize*5); 
	vector <int> tmp_R;				 // dynamic change, so not define here
	vector <float> tmpsig;           tmpsig.resize(n_tmpdata); // allocate a tmp memory for a short ECG segment
	vector <float> tmpsig_filter;    tmpsig_filter.resize(n_tmpdata); // allocate a tmp memory for a short ECG segment
	vector <int> n_RRI_eachSegment;  n_RRI_eachSegment.resize(int(n_ECGdata/windowsize)+1);

	while (1)
	{
		t_start=stepsize*sr*(t);
		t_end=t_start+windowsize*sr-1;
	
		if (t_end > n_ECGdata)
			break;
		
		for (int i=0; i<n_tmpdata; i++)
		{
			tmpsig[i]=dataBuff_ECG[t_start+i][0];
		}
		// 1.notch filter (60Hz)
		notchFilter_60Hz_sr250Hz(tmpsig,&tmpsig_filter, n_tmpdata);
		// 2. Low pass (<40Hz)
		FIR_LP_250Hz(tmpsig_filter,&tmpsig, n_tmpdata);
		//3. Baseline wander cancelation
		BaselineWanderFilter_250Hz(tmpsig, &tmpsig_filter, n_tmpdata);
		n_Rpeak=0;

		RPeakDetection_Medical(tmpsig_filter, n_tmpdata, sr, &R_index, &PVC_index, &n_Rpeak);
	
		// 解決 R 找出來 太近的問題 //RR距離120ms太近可能錯誤去掉
		for (int i=0;i<n_Rpeak-1;i++)
		{
			if (R_index[i]!=0)
			{
				if((R_index[i+1]-R_index[i])<=(0.12*sr))
				{
					if (tmpsig_filter[R_index[i+1]]>tmpsig_filter[R_index[i]])
					{R_index[i]=0; PVC_index[i]=0;} //{R_index.erase(R_index.begin()+i);   PVC_index.erase(PVC_index.begin()+i); n_subRpeak--;}
					else
					{R_index[i+1]=0; PVC_index[i+1]=0;}
				}
			}
		}
		
		n_subRpeak=0;
		for (int i=0;i<n_Rpeak;i++)
		{
			if (R_index[i]!=0)
			{tmpR_index[n_subRpeak]=R_index[i]+t_start;tmpPVC_index[n_subRpeak]=PVC_index[i];n_subRpeak++;}
			//{R_index.erase(R_index.begin()+i);   PVC_index.erase(PVC_index.begin()+i); n_subRpeak--;}
		}

		
		/* average RRI
		tmp=0;
		for (int i=0;i<n_subRpeak-1;i++)
		{tmp+=tmpR_index[i+1]-tmpR_index[i];}
		RRI_bar=tmp/(n_subRpeak-1)/sr*1000;
		*/

		n_RRI_eachSegment[t]=n_subRpeak;

		if (t>=1)
		{
			tmp_R.resize(n_RRI_eachSegment[t-1]);
			for (int i=0;i<n_RRI_eachSegment[t-1];i++)
			{
				tmp_R[i]=(*Index_R)[*n_totalRpeak-i];
			}
			for (int i=0;i<n_subRpeak;i++)
			{
				tmp=0;
				for (int j=0;j<tmp_R.size();j++)
					tmp+=(abs(tmpR_index[i]-tmp_R[j])>20);
				
				if (tmp==n_RRI_eachSegment[t-1])
				{
					(*Index_R)[*n_totalRpeak]=tmpR_index[i];
					(*Index_PVC)[*n_totalRpeak]=tmpPVC_index[i];
					(*n_totalRpeak)++;
				}
			}
		}
		else
		{
			for (int i=0;i<n_subRpeak;i++)
			{
				if (tmpR_index[i]!=0)
				{
					(*Index_R)[*n_totalRpeak]=tmpR_index[i]+t_start;
					(*Index_PVC)[*n_totalRpeak]=tmpPVC_index[i];
					(*n_totalRpeak)++;
				}
			}
		}
		t=t+1;
	}

	R_index.clear(); 
	PVC_index.clear(); 
	tmpR_index.clear(); 
	tmpPVC_index.clear(); 
	tmp_R.clear();				
	tmpsig.clear(); 
	tmpsig_filter.clear(); 
	n_RRI_eachSegment.clear();
}


void QRSDetection_WholeSignal_360Hz(vector <vector<float>>  dataBuff_ECG,vector <int>* Index_R, vector <bool>* Index_PVC,int windowsize, int stepsize,int sr,int n_ECGdata, int* n_totalRpeak)
{
	long t=0; // count how many loop for ECG subsegment
	long t_start=0,t_end=0; // time index for each ECG subsegment, unit: point
	int n_tmpdata=windowsize*sr; 
	long n_Rpeak=0, n_subRpeak=0;
	float tmp=0;
	// float RRI_bar=1000; //used for Median filter for RPeakDetection_Medical, however, it's easy with error (in case R can't be detected). Hence, marked. 
	vector <long> R_index;           R_index.resize(windowsize*5); 
	vector <bool> PVC_index;         PVC_index.resize(windowsize*5); 
	vector <long> tmpR_index;        tmpR_index.resize(windowsize*5); 
	vector <bool> tmpPVC_index;      tmpPVC_index.resize(windowsize*5); 
	vector <int> tmp_R;				 // dynamic change, so not define here
	vector <float> tmpsig;           tmpsig.resize(n_tmpdata); // allocate a tmp memory for a short ECG segment
	vector <float> tmpsig_filter;    tmpsig_filter.resize(n_tmpdata); // allocate a tmp memory for a short ECG segment
	vector <int> n_RRI_eachSegment;  n_RRI_eachSegment.resize(int(n_ECGdata/windowsize)+1);

	while (1)
	{
		t_start=stepsize*sr*(t);
		t_end=t_start+windowsize*sr-1;
	
		if (t_end > n_ECGdata)
			break;
		
		for (int i=0; i<n_tmpdata; i++)
		{
			tmpsig[i]=dataBuff_ECG[t_start+i][0];
		}
		// 1.notch filter (60Hz)
		notchFilter_60Hz_sr360Hz(tmpsig,&tmpsig_filter, n_tmpdata);
		// 2. Low pass (<40Hz)
		FIR_LP_360Hz(tmpsig_filter,&tmpsig, n_tmpdata);
		//3. Baseline wander cancelation
		BaselineWanderFilter_360Hz(tmpsig, &tmpsig_filter, n_tmpdata);
		n_Rpeak=0;

		RPeakDetection_Medical(tmpsig_filter, n_tmpdata, sr, &R_index, &PVC_index, &n_Rpeak);
	
		// 解決 R 找出來 太近的問題 //RR距離120ms太近可能錯誤去掉
		for (int i=0;i<n_Rpeak-1;i++)
		{
			if (R_index[i]!=0)
			{
				if((R_index[i+1]-R_index[i])<=(0.12*sr))
				{
					if (tmpsig_filter[R_index[i+1]]>tmpsig_filter[R_index[i]])
					{R_index[i]=0; PVC_index[i]=0;} //{R_index.erase(R_index.begin()+i);   PVC_index.erase(PVC_index.begin()+i); n_subRpeak--;}
					else
					{R_index[i+1]=0; PVC_index[i+1]=0;}
				}
			}
		}
		
		n_subRpeak=0;
		for (int i=0;i<n_Rpeak;i++)
		{
			if (R_index[i]!=0)
			{tmpR_index[n_subRpeak]=R_index[i]+t_start;tmpPVC_index[n_subRpeak]=PVC_index[i];n_subRpeak++;}
			//{R_index.erase(R_index.begin()+i);   PVC_index.erase(PVC_index.begin()+i); n_subRpeak--;}
		}

		
		/* average RRI
		tmp=0;
		for (int i=0;i<n_subRpeak-1;i++)
		{tmp+=tmpR_index[i+1]-tmpR_index[i];}
		RRI_bar=tmp/(n_subRpeak-1)/sr*1000;
		*/

		n_RRI_eachSegment[t]=n_subRpeak;

		if (t>=1)
		{
			tmp_R.resize(n_RRI_eachSegment[t-1]);
			for (int i=0;i<n_RRI_eachSegment[t-1];i++)
			{
				tmp_R[i]=(*Index_R)[*n_totalRpeak-i];
			}
			for (int i=0;i<n_subRpeak;i++)
			{
				tmp=0;
				for (int j=0;j<tmp_R.size();j++)
					tmp+=(abs(tmpR_index[i]-tmp_R[j])>20);
				
				if (tmp==n_RRI_eachSegment[t-1])
				{
					(*Index_R)[*n_totalRpeak]=tmpR_index[i];
					(*Index_PVC)[*n_totalRpeak]=tmpPVC_index[i];
					(*n_totalRpeak)++;
				}
			}
		}
		else
		{
			for (int i=0;i<n_subRpeak;i++)
			{
				if (tmpR_index[i]!=0)
				{
					(*Index_R)[*n_totalRpeak]=tmpR_index[i]+t_start;
					(*Index_PVC)[*n_totalRpeak]=tmpPVC_index[i];
					(*n_totalRpeak)++;
				}
			}
		}
		t=t+1;
	}

	R_index.clear(); 
	PVC_index.clear(); 
	tmpR_index.clear(); 
	tmpPVC_index.clear(); 
	tmp_R.clear();				
	tmpsig.clear(); 
	tmpsig_filter.clear(); 
	n_RRI_eachSegment.clear();
}


float Math_Mean(vector <float> data)
{
	float sum = 0;
	for(int i = 0; i < data.size(); i++)
		sum = sum+ data[i];
	return sum/data.size();
};

float Statistic_SampleStandardDeviation(vector <float> data)
{
	float mean= Math_Mean(data);
	float temp = 0;
	for(int i = 0; i < data.size(); i++)
		temp += (data[i] - mean) * (data[i] - mean);

	return sqrt(temp / (data.size()-1)) ;
};

