#include <stdlib.h>
#include <math.h> 
#include <vector>
#include <complex>
#include "mymath.h"
#include "SWM_algo_HRV.h"
#include "matrix.h"

using namespace std;


void SWM_HRV_Time_Histogram(double RRI[],double DataBuf_RRITime[], long pDataSize, double output[6], double* BinCount, double* BinTimeIndex)
{
	double maxRRI=findMaxValue(RRI,0, pDataSize);
	double minRRI=findMinValue(RRI,0, pDataSize);
	// Histogram, from the min RRI to max RRI, the window size is 7.8125ms/////
	///7.8125 ms (1/128 s)
	double windowlength=7.8125;// ms
	double StartTime=minRRI;
	int count=0;
	double BinStart=0; 
	double BinEnd=0;
	double ShannonEntropy=0;
	int NumofBin=int((maxRRI-minRRI)/windowlength)+2;

	vector <double> ProbabilityofBin; // Declared a probability vector which derived from the Bin vector.
	ProbabilityofBin.assign(NumofBin,0);// 

	while (1)
	{
		if (count>NumofBin-1)
			 break;

		 BinStart=StartTime+(count)*windowlength; //Start timeindex for each bin.
		 BinEnd=StartTime+(count+1)*windowlength; //End timeindex for each bin.

		 for (int i=0; i<pDataSize;i++)
			 if (RRI[i]>=BinStart & RRI[i]<BinEnd)
				 BinCount[count]++;
	
		 ProbabilityofBin[count]=BinCount[count]/pDataSize;

		 if (ProbabilityofBin[count]!=0)
			ShannonEntropy+=ProbabilityofBin[count] * (log(ProbabilityofBin[count])/log(2.0));//log10 to log2

		 BinTimeIndex[count]=(BinStart+BinEnd)/2;
		 count++;
	}
	 ShannonEntropy=-ShannonEntropy; // Shannon Entropy

	// HRV triangular index 
	double HRV_TRI=pDataSize/findMaxValue(BinCount,0, count);

	///// TINN///////
	double M=BinTimeIndex[count-1],N=BinTimeIndex[0];
	int pos_max=findmaxIndex(BinCount,0, count);
	
	for (int i=pos_max;i<count;i++)
	if (int(BinCount[i])==0)
		{
			M=BinTimeIndex[i];
			break;
		}

	for (int i=pos_max;i<0;i--)
		if (int(BinCount[i])==0)
		{
			N=BinTimeIndex[i];
			break;
		}
	
	double TINN=M-N;

	/////////////////////////
	double SD=Statistic_SampleStandardDeviation(BinCount, count);// StandardDeviation for the Histogram
	double Kurtosis=Statistic_Kurtosis(BinCount, count); //Kurtosis for the Histogram
	double Skewness=Statistic_Skewness(BinCount, count); //Skewness for the Histogram

	output[0]=HRV_TRI;
	output[1]=TINN;
	output[2]=SD;
	output[3]=Kurtosis;
	output[4]=Skewness;
	output[5]=ShannonEntropy;
	
	// free the declared vector 
	ProbabilityofBin.clear();

}

void SWM_HRV_poincare(double RRI[], long pDataSize,double SD[2], double EigenVector[2][2])
{
	long newpDataSize=pDataSize-1;

	vector <double> RRI_n; // 
	RRI_n.assign(newpDataSize,0);// 
	vector <double> RRI_n1; // 
	RRI_n1.assign(newpDataSize,0);// 
	
	matrix <double> X(newpDataSize,2); // used for store the RRI_n and RRI_n1
	matrix <double> CovM(2,2); // covariance matrix from X
	matrix <double> Eigvalue(2,2); // Eigenvalue matrix from X
	matrix <double> Eigvector(2,2); // Eigenvector matrix from X

	// zeros mean for each vector
	for (int i=0;i<newpDataSize;i++)
	{
		RRI_n[i]=RRI[i];
		RRI_n1[i]=RRI[i+1];
	}
	Math_Vector_ZeroMean(&RRI_n.front(), newpDataSize);
	Math_Vector_ZeroMean(&RRI_n1.front(), newpDataSize);
	////////
	////// set the vector value to matrix
	for (int i=0;i<newpDataSize;i++)
	{
		X.setvalue(i,0,RRI_n[i]);
		X.setvalue(i,1,RRI_n1[i]);
	}
	RRI_n.clear(); // free the memory
	RRI_n1.clear(); // free the memory 
	///////////////////
	// covariance matrix from matrix X (size: n x dim);
	CovM.CovarianceMatrix(X); 
	
	// diagonal matrix of eigenvalue 
	Eigvalue.eigenvalue_dim2(CovM);
	// eigenvectors
	Eigvector.eigenvector_dim2(CovM, Eigvalue);


	SD[0]=sqrt(Eigvalue.data[0]); // first eigenvalue
	SD[1]=sqrt(Eigvalue.data[3]); // Second eigenvalue
	EigenVector[0][0]=Eigvector.data[0];
	EigenVector[0][1]=Eigvector.data[1];
	EigenVector[1][0]=Eigvector.data[2];
	EigenVector[1][1]=Eigvector.data[3];
}

void SWM_HRV_Frequency(double RRI[],double DataBuf_RRITime[], long pDataSize,double samplerate, double Output[5])
{
	vector <double> newDataBuf_RRI; // Declared a vector to store the upsample RRI.
	vector <double> new_DataBuf_RRITime; // Declared a vector to store the upsample RRI time.
	vector <double> PowerSpectrum; // Declared a vector to store the result from the DTF.
	vector <double> Freq; /// Declared a vector to store the Frequency band.
	long pNewDataSize=0; // using to count the number of the upsampling RRIs.
	double aVLF=0; // initialize the output energy of very low freqency.
	double aLF=0; // initialize the output energy of low freqency.
	double aHF=0; // initialize the output energy of high freqency.
	double aTP=0; // initialize the output energy of total power. 

	//reassign the size of the vector (sample rate*originalsize), and value of the vector to 0.
	newDataBuf_RRI.assign(pDataSize*samplerate,0);// 
	new_DataBuf_RRITime.assign(pDataSize*samplerate,0);//
	PowerSpectrum.assign(pDataSize*samplerate,0);//
	Freq.assign(pDataSize*samplerate,0);//


	// upsampling the RRI and RRItime by the Linear Interpolation.
	SWM_algo_LinearInterpolation(DataBuf_RRITime,
		&new_DataBuf_RRITime.front(),
		RRI,
		&newDataBuf_RRI.front(),
		&pDataSize, 
		&pNewDataSize, 
		samplerate);
	
	//RRI zero mean
	 Math_Vector_ZeroMean(&newDataBuf_RRI.front(), pNewDataSize);
	
	// DTF compute the power spectrum
	dft1(&newDataBuf_RRI.front(), &PowerSpectrum.front(), pNewDataSize);
	// The corresponding frequency band.
	dft1_Freq(&Freq.front(), pNewDataSize, samplerate);

	// Extract the specific band power (VLF,LF,HF)
	double tmp=0;
	for(int k=0; k<pNewDataSize/2+1; k++){
		tmp=PowerSpectrum[k];
		if (Freq[k]>VLF_L && Freq[k]<=VLF_U)
			aVLF=aVLF+tmp;

		if (Freq[k]>LF_L && Freq[k]<=LF_U)
			aLF=aLF+tmp;

		if (Freq[k]>HF_L && Freq[k]<=HF_U)
			aHF=aHF+tmp;
	
	}
	aTP=aVLF+aLF+aHF;	
	Output[0]=aVLF;
	Output[1]=aLF;
	Output[2]=aHF;
	Output[3]=aTP;
	Output[4]=aLF/aHF;

	newDataBuf_RRI.clear();
	new_DataBuf_RRITime.clear();
	PowerSpectrum.clear();
	Freq.clear();

	//*/
}

void dft1(double inputData[], double *PowerSpectrum, long nfft)
{

    double PI2=6.283185307179586476925286766559;
	
    // Calculate DFT of x using brute force
    for (int k=0 ; k<nfft ; ++k)
    {
        /*// Real part of X[k]
        Xre[k] = 0;
        for (int n=0 ; n<nfft ; ++n) Xre[k] += inputData[n] * cos(n * k * PI2 / nfft);
         
        // Imaginary part of X[k]
        Xim[k] = 0;
        for (int n=0 ; n<nfft ; ++n) Xim[k] -= inputData[n] * sin(n * k * PI2 / nfft);
		*/

		double Xre=0,Xim=0;

		for (int n=0 ; n<nfft ; ++n) {
		Xre += inputData[n] * cos(n * k * PI2 / nfft);
		Xim -= inputData[n] * sin(n * k * PI2 / nfft);
		}
		PowerSpectrum[k]=Xre*Xre + Xim*Xim;
         
         
        // Power at kth frequency bin
        //PowerSpectrum[k] = Xre[k]*Xre[k] + Xim[k]*Xim[k];
		PowerSpectrum[k] = 2*PowerSpectrum[k]/(nfft*nfft);
    }
}
void dft1_Freq(double Freq[], long nfft, long samplerate)
{
	int c=0;
	double t=0;

	double n=1/((double(nfft)/2)+1);
	
	while (1)
	{
		if (c>int(nfft/2)+1) break;
		Freq[c]=t;
		t=(t+n);
		c++;
	}
	for (int i=0;i<c;i++)
	Freq[i]=Freq[i]*(samplerate/2);
}



void SWM_algo_LinearInterpolation(double* pTimeBuf,	double* pNewTimeBuf, double* pData, double* pNewData,
			long* pDataSize, long* pNewDataSize, double samplerate)
{
	double t_end=*(pTimeBuf+*pDataSize-1);
	double new_t=*pTimeBuf;
	double steplength=(1/samplerate);
	*pNewDataSize=0;
	while(new_t<t_end)
	{
		*(pNewTimeBuf+*pNewDataSize)= new_t;
		new_t=new_t+steplength; //  sample rate
		(*pNewDataSize)++;
	}
	///

	int stratpoint=0;
	for (int i=0; i<*pDataSize; i++)
	{
		double tmp_new=0,tmp_old=0;
		int tmpindex=0;
		for (int j=stratpoint; j<*pNewDataSize;j++)
		{
			tmp_new=pow(pTimeBuf[i]-pNewTimeBuf[j],2);
			if (j==stratpoint)
			{
				tmp_old=tmp_new;
			}
			if (j>=stratpoint+1)
			{		
				if (tmp_old>=tmp_new)
				{
					tmpindex=j;
					tmp_old=tmp_new;
				}
				else
				{
					tmpindex=tmpindex;
				}
			}
			else
			{
				tmp_old=tmp_new;
			}
			
		}
		stratpoint=tmpindex;
		pTimeBuf[i]=double(tmpindex);
	}


	int count_total=0;
	for (int i=0;i<=*pDataSize-1;i++)
	{

		int range=int(pTimeBuf[i+1])-int(pTimeBuf[i]);

		double step=(*(pData+i+1)-*(pData+i))/range;

		if (step!=0)
		{
			for (int ii=0;ii<range;ii++)
			{
				*(pNewData+count_total)=*(pData+i)+ii*step;
				count_total++;
			}
		}
		else
		{
			for (int ii=0;ii<range;ii++)
			{
				*(pNewData+count_total)=*(pData+i);				
				count_total++;
			}
		}		
	}
	*(pNewData+count_total)=*(pData+*pDataSize-1); // Final element must include

}
