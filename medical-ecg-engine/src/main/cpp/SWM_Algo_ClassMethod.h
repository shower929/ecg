#pragma once
#include <vector>
#include <math.h>  
#include <cmath>  
#include <iterator>     // std::back_inserter
#include <algorithm>    // std::copy
using namespace std;

template <typename T> class Algo
{
public:
	void SWM_algo_timewarp(int evLatency[2],int newLatency[2],vector<vector<T>>* M);
	void SWM_algo_FeatureExtraction_Morphological(vector<T> h,int size, vector<T>* feature);
	void dft1(vector<T> inputData, vector<T>* Xre, vector<T>* Xim, vector<T>* PowerSpectrum, long nfft);
};


template <typename T>
void Algo<T>::SWM_algo_timewarp(int evLatency[2],int newLatency[2],vector<vector<T>>* M)
{
	vector<T> t; 
	vector<T> tp; 
	vector<T> ts;
	int tmp_M=0,tmp_N=0,k=0,max_evLatency=0;;
	max_evLatency=evLatency[1];
	t.resize(max_evLatency);
	tp.resize(max_evLatency);


	for (int i=0;i<max_evLatency;i++)
		t[i]=i+1;

	for (int i=0;i<max_evLatency-1;i++)
		tp[i]=(t[i]-evLatency[0]) * (newLatency[1] - newLatency[0])/(evLatency[1] - evLatency[0])+ +newLatency[0];
	
	tp[max_evLatency-1] = newLatency[1];
	
	//copy(tp.begin(), tp.end(), back_inserter(ts));
	ts=tp;

	tmp_M=newLatency[1]-newLatency[0]+1;
	tmp_N=evLatency[1];
	
	(*M).resize(tmp_M);// 
	for(int i=0; i!=tmp_M; ++i) (*M)[i].resize(tmp_N);

	for(int i=0; i!=tmp_M; ++i)
		for(int j=0; j!=tmp_N; ++j)
			(*M)[i][j] = 0;

	k=0;
	for (int i=0;i<tmp_M;i++)
	{
		while((i+1)>ts[k])
		{
			k=k+1;
		}
		if (k==0) (*M)[0][0]=1;
		else
		{	
			(*M)[i][k-1]=1-((i+1)-ts[k-1])/(ts[k]-ts[k-1]);
			(*M)[i][k]=1-(ts[k]-(i+1))/(ts[k]-ts[k-1]);
		}
	}	
	t.clear(); // free the memory
	tp.clear();
	ts.clear();
};

template <typename T> 
void Algo<T>::SWM_algo_FeatureExtraction_Morphological(vector <T> h, int size, vector<T>* feature)
{
	double PI2=6.283185307179586476925286766559;
	double PI =3.141592653589793238462643383189;
	vector<T>  Xre; Xre.resize(size); // store the real part of DFT
	vector<T>  Xim; Xim.resize(size); // store the imaginary part of DFT
	vector<vector<T>> PB_Real; // Store the result from S-transformation
	vector<vector<T>> PB_Imag; // Store the result from S-transformation
	vector<vector<T>> PA; // Store the result from S-transformation
	vector<vector<T>> PB_Real_T; 
	vector<vector<T>> PB_Imag_T; 
	vector<vector<T>> S_Real; // Store the result from S-transformation	
	vector<vector<T>> S_Imag; // Store the result from S-transformation	
	vector<vector<T>> TF_contour; // Store the result from S-transformation	
	vector<T> TF_contour_LF; //store the row_maxTF_contour
	vector<T> TmA_plot; //store the column_maxTF_contour
	vector<T> PowerSpectrum;

	T tmp=-2*pow(PI,2);
	T tmp1=0,tmp2=0,tmp3=0;
	//MyMath<T> tmparry(h);
	MyMath<T> tmparry1(h);
	MyMath<T> tmparry2(h);


	PB_Real_T.resize(1);
	PB_Imag_T.resize(1);
	PB_Real.resize(size);// defalut a size 
	PB_Imag.resize(size);// defalut a size 
	PA.resize(size);// defalut a size 
	S_Real.resize(size);// defalut a size
	S_Imag.resize(size);// defalut a size 
	TF_contour.resize(size);// defalut a size
	TF_contour_LF.resize(size);
	TmA_plot.resize(size);
	PowerSpectrum.resize(size);
	for(int i=0; i!=size; ++i) {
		PB_Real[i].resize(size); // defalut a size 	
		PB_Imag[i].resize(size); // defalut a size 	
		PA[i].resize(size); // defalut a size 
		S_Real[i].resize(size); // defalut a size
		S_Imag[i].resize(size); // defalut a size 
		TF_contour[i].resize(size); // defalut a size 
	}
	
	// zero-mean
	//tmparry.Math_Vector_ZeroMean();
	//h=tmparry.data;
	//copy(tmparry.data.begin(), tmparry.data.end(), back_inserter(h));
	
	// DTF
	dft1(h, &Xre, &Xim, &PowerSpectrum, size);

	// S-transformation	
	for (int j=0;j<size;j++){
		tmp1=PI2*(j+1)/size;
		for (int m=0;m<size;m++){	
			PB_Real[j][m]=(Xre[m]*cos(tmp1*(m+1)))-(Xim[m]*sin(tmp1*(m+1)));
			PB_Imag[j][m]=(Xre[m]*sin(tmp1*(m+1)))+(Xim[m]*cos(tmp1*(m+1)));
			tmp2=pow(T(m+1),2);
			tmp3=pow(T(j+1),2);
			PA[j][m]=exp(tmp*tmp2/tmp3);
		}
	}

	Matrix_Transpose(PB_Real, &PB_Real_T); 
	Matrix_Transpose(PB_Imag, &PB_Imag_T) ;
	Matrix_multiplication(PA,PB_Real_T, &S_Real);  
	Matrix_multiplication(PA,PB_Imag_T, &S_Imag);  
	for (int j=0;j<size;j++){
		for (int m=0;m<size;m++){
			S_Real[j][m]=S_Real[j][m]/size;
			S_Imag[j][m]=S_Imag[j][m]/size;
			tmp1=pow(S_Real[j][m],2);
			tmp2=pow(S_Imag[j][m],2);
			tmp3=tmp1+tmp2;
			TF_contour[j][m]=sqrt(tmp3);
		}
	}
	/////////////////////////////////////////
	for (int i=0;i<size;i++){
		for (int j=0;j<size;j++){
			tmparry1.data[j]=TF_contour[i][j];
			tmparry2.data[j]=TF_contour[j][i];
		}
		TmA_plot[i]=tmparry1.findMaxValue();
		TF_contour_LF[i]=tmparry2.findMaxValue();
	}
	tmp1=0; tmp=0;
	for (int i=0;i<size;i++){
		tmparry1.data[i]=TmA_plot[i];
		tmparry2.data[i]=TF_contour_LF[i];
		tmp=TF_contour_LF[i];
		tmp1=tmp1+pow(tmp,2);
	}
	
	(*feature)[0]=tmparry2.Statistic_SampleStandardDeviation();
	(*feature)[1]=tmparry2.Math_Mean();
	(*feature)[2]=tmp1;
	(*feature)[3]=tmparry1.Math_Mean();
	(*feature)[4]=tmparry1.Statistic_SampleStandardDeviation();
	for (int i=1; i<26; i++)
	{
		(*feature)[4+i]=PowerSpectrum[i];
	}
	// clear memory
	
	h.clear();
	S_Real.clear();
	S_Imag.clear();
	PB_Real.clear();
	PB_Imag.clear();
	PB_Real_T.clear();
	PB_Imag_T.clear();
	Xre.clear();
	Xim.clear();
	TF_contour_LF.clear();
	TmA_plot.clear();
	PowerSpectrum.clear();
	tmparry1.data.clear();
	tmparry2.data.clear();
	//
};


template <typename T> 
void Algo<T>::dft1(vector<T> inputData, vector<T>* Xre, vector<T>* Xim, vector<T>* PowerSpectrum, long nfft)
{
    double PI2=6.283185307179586476925286766559;
    // Calculate DFT of x using brute force
    for (int k=0 ; k<nfft ; ++k)
    {
        // Real part of X[k]
        (*Xre)[k] = 0;
        for (int n=0 ; n<nfft ; ++n) (*Xre)[k] += inputData[n] * cos(n * k * PI2 / nfft);
         
        // Imaginary part of X[k]
        (*Xim)[k] = 0;
        for (int n=0 ; n<nfft ; ++n) (*Xim)[k] -= inputData[n] * sin(n * k * PI2 / nfft);


		(*PowerSpectrum)[k]=(*Xim)[k]*(*Xim)[k] + (*Xre)[k]*(*Xre)[k];
		(*PowerSpectrum)[k] = 2*(*PowerSpectrum)[k]/(nfft*nfft);
    }
	(*Xre)[0] = 0;(*Xim)[0] = 0;
};
