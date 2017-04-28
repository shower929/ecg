// #include "SWM_SV_DnnFunction.h"
//#include "stdafx.h"
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include "SWM_SV_DnnModel.h"

double* PSD = NULL;
double* Freqs = NULL;
double* g_d64Buffer = NULL;
double* g_d64DnnBuffer_1 = NULL;
double* g_d64DnnBuffer_2 = NULL;
double* g_d64DnnBuffer_3 = NULL;
double* g_d64TranBuffer_1 = NULL;
double* g_d64TranBuffer_2 = NULL;
double* g_d64TranBuffer_3 = NULL;
double* g_d64DnnBufferPro_1 = NULL;
double* g_d64DnnBufferPro_2 = NULL;
double* g_d64DnnBufferPro_3 = NULL;
double* g_d64DnnFeatureGroup = NULL;
double* g_d64DnnFeatureGroupWork = NULL;
double** g_d64tV = NULL;
double** g_d64tZ = NULL;
double*** g_dnnWeight = NULL;

//---------------------------------------------------------------------------------------------------
// Compute feature function list
void FindMax(double* Max, double* Input, long dataSize)
{
	long i, p = 0;
	double temp = Input[0];

	for (i = 1; i < dataSize; i++)
	{
		if (Input[i] > temp)
		{
			temp = Input[i];
			p = i;
		}
	}
	Max[0] = temp;
	Max[1] = p;
}

void FindMin(double* Min, double* Input, long dataSize)
{
	long i, p = 0;
	double temp = Input[0];

	for (i = 1; i < dataSize; i++)
	{
		if (Input[i] < temp)
		{
			temp = Input[i];
			p = i;
		}
	}
	Min[0] = temp;
	Min[1] = p;
}

void FFT(double* outputReal, double* outputImaginary, double* input, long dataSize)
{
	double real = 0;
	double imaginary = 0;
	double pi = 3.14159265359;

	for (int k = 0; k < dataSize; k++)
	{
		for (int n = 0; n < dataSize; n++)
		{
			real += input[n] * cos(-1 * 2 * pi * (k) * (n) / dataSize);
			imaginary += input[n] * sin(-1 * 2 * pi * (k) * (n) / dataSize);
		}
		outputReal[k] = real;
		outputImaginary[k] = imaginary;
		real = 0;
		imaginary = 0;
	}
}

void FreqsHistogram(double* PSD, double* Freqs, double* Input, long dataSize, long sampleRate)
{
	long i, j, k;
	double avg = 0;
	double* PB = NULL;
	double* Real = NULL;
	double* Imaginary = NULL;
	double* DataBuffer = NULL;

	PB = new double [dataSize];
	Real = new double [dataSize];
	Imaginary = new double [dataSize];
	DataBuffer = new double [dataSize];
	
	for(i = 0; i < dataSize; i++)
	{
		avg += Input[i];
	}
	avg = avg / dataSize;

	for(i = 0; i < dataSize; i++)
	{
		DataBuffer[i] = Input[i] - avg;
	}

	FFT(Real, Imaginary, DataBuffer, dataSize);

	for(i = 0; i < dataSize / 2 + 1; i++)
	{
		PSD[i] = Real[i]*Real[i] + Imaginary[i]*Imaginary[i];
		PSD[i] = 2 * PSD[i] / (dataSize*dataSize);
		Freqs[i] = (double )sampleRate / 2 * i / (dataSize / 2);
	}

	delete [] PB;
	delete [] Real;
	delete [] Imaginary;
	delete [] DataBuffer;

	PB = NULL;
	Real = NULL;
	Imaginary = NULL;
	DataBuffer = NULL;
}

void StepSpeedFeatureByDnnOfFFT(double* output, double* Input, long dataSize, long sampleRate)
{
	double maxVaule;
	long maxPosition;
	double Max[2] = {0};

	PSD = new double [dataSize/2+1];
	Freqs = new double [dataSize/2+1];

	FreqsHistogram(PSD, Freqs, Input, dataSize, sampleRate);
	FindMax(Max, PSD, dataSize/2 + 1);

	output[0] = Freqs[(long)Max[1]];
	output[1] = Max[0];

	delete [] PSD;
	delete [] Freqs;

	PSD = NULL;
	Freqs = NULL;
}

void Compute7FeatureDNN(double* d64Output, double* d64Input, long i32inputSize)
{
	long i, j;
	double temp;
	double d64mean = 0;
	double d64median = 0;
	double d64var = 0;
	double d64skewness;
	double d64kurtosis;
	double d64max;
	double d64min;
	double k3 = 0, k2 = 0, k4 = 0;

	g_d64Buffer = new double [i32inputSize];
	
	for (i = 0; i < i32inputSize; i++)
	{
		g_d64Buffer[i] = d64Input[i];
		d64mean += g_d64Buffer[i];
	}
	d64mean = d64mean / i32inputSize;  // mean

	for (i = 0; i < i32inputSize; i++)
	{
		for (j = 0; j < i32inputSize; j++)
		{
			if (g_d64Buffer[i] < g_d64Buffer[j])
			{
				temp = g_d64Buffer[j];
				g_d64Buffer[j] = g_d64Buffer[i];
				g_d64Buffer[i] = temp;
			}
		}
	}
	if (i32inputSize%2 == 0)
		d64median = (g_d64Buffer[i32inputSize/2] + g_d64Buffer[i32inputSize/2-1]) / 2; // median
	else
		d64median = g_d64Buffer[i32inputSize/2]; // median
	d64max = g_d64Buffer[i32inputSize-1]; // max
	d64min = g_d64Buffer[0]; // min

	for (i = 0; i < i32inputSize; i++)
	{
		d64var += (g_d64Buffer[i] - d64mean) * (g_d64Buffer[i] - d64mean);
		k3 += (g_d64Buffer[i] - d64mean) * (g_d64Buffer[i] - d64mean) * (g_d64Buffer[i] - d64mean);
		k4 += (g_d64Buffer[i] - d64mean) * (g_d64Buffer[i] - d64mean) * (g_d64Buffer[i] - d64mean) * (g_d64Buffer[i] - d64mean);
	}
	k4 = k4 / i32inputSize;
	k3 = k3 / i32inputSize;
	k2 = pow(d64var / i32inputSize, 1.5);
	d64skewness = k3 / k2; // skewness
	k2 = pow(d64var / i32inputSize, 2);
	d64kurtosis = k4 / k2; // kurtosis
	d64var = d64var / (i32inputSize - 1); // variance

	//return data
	d64Output[0] = d64mean;
	d64Output[1] = d64median;
	d64Output[2] = d64var;
	d64Output[3] = d64skewness;
	d64Output[4] = d64kurtosis;
	d64Output[5] = d64max;
	d64Output[6] = d64min;

	delete [] g_d64Buffer;
	g_d64Buffer = NULL;
}

void ComputeCovFeatureNN(double* d64output, double* d64Input1, double* d64Input2, long i32inputSize)
{
	long i, j;
	double d64sum = 0;
	double d64mean1 = 0;
	double d64mean2 = 0;

	for (i = 0; i < i32inputSize; i++)
	{
		d64mean1 += d64Input1[i];
		d64mean2 += d64Input2[i];
	}
	d64mean1 = d64mean1 / i32inputSize;
	d64mean2 = d64mean2 / i32inputSize;

	for (i = 0; i < i32inputSize; i++)
	{
		d64sum += (d64Input1[i] - d64mean1) * (d64Input2[i] - d64mean2);
	}

	// return data
	*d64output = d64sum / (i32inputSize - 1);
}

void StepSpeedFeatureGroup(double* Output, double* Lateral, double* Vertical, double* Frontal, long dataSize, long sampleRate)
{
	long add = 0;
	double feature7Lateral [7] = {0};
	double feature7Vertical [7] = {0};
	double feature7Frontal [7] = {0};

	double featureFFTLateral [2] = {0};
	double featureFFTVertical [2] = {0};
	double featureFFTFrontal [2] = {0};

	double featureCovFV [1] = {0};
	double featureCovFL [1] = {0};
	double featureCovVL [1] = {0}; 

	Compute7FeatureDNN(feature7Lateral, Lateral, dataSize);
	Compute7FeatureDNN(feature7Vertical, Vertical, dataSize);
	Compute7FeatureDNN(feature7Frontal, Frontal, dataSize);

	ComputeCovFeatureNN(featureCovFV, Frontal, Vertical, dataSize);
	ComputeCovFeatureNN(featureCovFL, Frontal, Lateral, dataSize);
	ComputeCovFeatureNN(featureCovVL, Vertical, Lateral, dataSize);

	StepSpeedFeatureByDnnOfFFT(featureFFTLateral, Lateral, dataSize, sampleRate);
	StepSpeedFeatureByDnnOfFFT(featureFFTVertical, Vertical, dataSize, sampleRate);
	StepSpeedFeatureByDnnOfFFT(featureFFTFrontal, Frontal, dataSize, sampleRate);

	memcpy(Output + add, feature7Lateral, sizeof(feature7Lateral)); add += sizeof(feature7Lateral)/sizeof(double);
	memcpy(Output + add, feature7Vertical, sizeof(feature7Vertical)); add += sizeof(feature7Vertical)/sizeof(double);
	memcpy(Output + add, feature7Frontal, sizeof(feature7Frontal)); add += sizeof(feature7Frontal)/sizeof(double);
	memcpy(Output + add, featureFFTLateral, sizeof(featureFFTLateral)); add += sizeof(featureFFTLateral)/sizeof(double);
	memcpy(Output + add, featureFFTVertical, sizeof(featureFFTVertical)); add += sizeof(featureFFTVertical)/sizeof(double);
	memcpy(Output + add, featureFFTFrontal, sizeof(featureFFTFrontal)); add += sizeof(featureFFTFrontal)/sizeof(double);
	memcpy(Output + add, featureCovFV, sizeof(featureCovFV)); add += sizeof(featureCovFV)/sizeof(double);
	memcpy(Output + add, featureCovFL, sizeof(featureCovFL)); add += sizeof(featureCovFL)/sizeof(double);
	memcpy(Output + add, featureCovVL, sizeof(featureCovVL)); add += sizeof(featureCovVL)/sizeof(double);
}

//---------------------------------------------------------------------------------------------------
// Buffer allocate and delete
void AllocateDnnModelWeight()
{
	g_dnnWeight = new double** [DNN_structure];

	for (int i = 0; i < DNN_structure; i++)
	{
		g_dnnWeight[i] = new double* [DNN_columnList[i]];

		for (int j = 0; j < DNN_columnList[i]; j++)
		{
			g_dnnWeight[i][j] = new double [DNN_rowList[i]];

			for (int k = 0; k < DNN_rowList[i]; k++)
			{
				g_dnnWeight[i][j][k] = DNN_Weight[i][j][k];
			}
		}
	}
}

void DeleteDnnModelWeight(void)
{
	for (int i = 0; i < DNN_structure; i++)
	{
		for (int j = 0; j < DNN_columnList[i]; j++)
		{
			delete [] g_dnnWeight[i][j];
		}
		delete [] g_dnnWeight[i];
	}
	delete [] g_dnnWeight;

	g_dnnWeight = NULL;
}

void ACC_DNN_NewBuffer(void)
{
  if (g_d64DnnBuffer_1 == NULL)
    g_d64DnnBuffer_1 = new double[com_SlideWindow];

  if (g_d64DnnBuffer_2 == NULL)
    g_d64DnnBuffer_2 = new double[com_SlideWindow];

  if (g_d64DnnBuffer_3 == NULL)
    g_d64DnnBuffer_3 = new double[com_SlideWindow];

  if (g_d64TranBuffer_1 == NULL)
    g_d64TranBuffer_1 = new double[com_SlideWindow];

  if (g_d64TranBuffer_2 == NULL)
    g_d64TranBuffer_2 = new double[com_SlideWindow];

  if (g_d64TranBuffer_3 == NULL)
    g_d64TranBuffer_3 = new double[com_SlideWindow];
}

void ACC_DNN_DeleteBuffer(void)
{
  if (g_d64DnnBuffer_1)
  {
    delete  []g_d64DnnBuffer_1;
    g_d64DnnBuffer_1 = NULL;
  }

  if (g_d64DnnBuffer_2)
  {
    delete [] g_d64DnnBuffer_2;
    g_d64DnnBuffer_2 = NULL;
  }

  if (g_d64DnnBuffer_3)
  {
    delete  []g_d64DnnBuffer_3;
    g_d64DnnBuffer_3 = NULL;
  }

  if (g_d64TranBuffer_1)
  {
    delete  []g_d64TranBuffer_1;
    g_d64TranBuffer_1 = NULL;
  }

  if (g_d64TranBuffer_2)
  {
    delete [] g_d64TranBuffer_2;
    g_d64TranBuffer_2 = NULL;
  }

  if (g_d64TranBuffer_3)
  {
    delete  []g_d64TranBuffer_3;
    g_d64TranBuffer_3 = NULL;
  }
}

void ACC_ProDNN_NewBuffer(long i32Size)
{
  if (g_d64DnnBufferPro_1 == NULL)
    g_d64DnnBufferPro_1 = new double[i32Size];

  if (g_d64DnnBufferPro_2 == NULL)
    g_d64DnnBufferPro_2 = new double[i32Size];

  if (g_d64DnnBufferPro_3 == NULL)
    g_d64DnnBufferPro_3 = new double[i32Size];
}

void ACC_ProDNN_DeleteBuffer(void)
{
  if (g_d64DnnBufferPro_1)
  {
    delete  []g_d64DnnBufferPro_1;
    g_d64DnnBufferPro_1 = NULL;
  }

  if (g_d64DnnBufferPro_2)
  {
    delete [] g_d64DnnBufferPro_2;
    g_d64DnnBufferPro_2 = NULL;
  }

  if (g_d64DnnBufferPro_3)
  {
    delete  []g_d64DnnBufferPro_3;
    g_d64DnnBufferPro_3 = NULL;
  }
}

void AllocateMatrixVZtest(void)
{
	g_d64tV = new double* [DNN_structure];
	g_d64tZ = new double* [DNN_structure];
	
	for (int i = 0; i < DNN_structure; i++)
	{
		g_d64tV[i] = new double [DNN_columnList[i]];
		g_d64tZ[i] = new double [DNN_columnList[i]];
	}
}

void DeleteMatrixVZtest(void)
{
	for (int i = 0; i < DNN_structure; i++)
	{
		delete [] g_d64tV[i];
		delete [] g_d64tZ[i];
	}
	delete [] g_d64tV;
	delete [] g_d64tZ;

	g_d64tV = NULL;
	g_d64tZ = NULL;
}

void AllocateFeatureGroup(void)
{
	g_d64DnnFeatureGroup = new double [DNN_FeatureNumber];
	g_d64DnnFeatureGroupWork = new double [DNN_FeatureNumber + 1];
}

void DeleteFeatureGroup(void)
{
	delete [] g_d64DnnFeatureGroup;
	delete [] g_d64DnnFeatureGroupWork;

	g_d64DnnFeatureGroup = NULL;
	g_d64DnnFeatureGroupWork = NULL;
}

void AllocateDnnAllBufferSetting()
{
	AllocateMatrixVZtest();
	AllocateDnnModelWeight();
	ACC_DNN_NewBuffer();
	AllocateFeatureGroup();
}

void DeleteDnnAllBufferSetting()
{
	DeleteFeatureGroup();
	ACC_DNN_DeleteBuffer();
	DeleteDnnModelWeight();
	DeleteMatrixVZtest();
}

//---------------------------------------------------------------------------------------------------
// DNN Function List
void DNN_AactivationFunction(double* output, double* intput, long column, char type)
{
	switch (type)
	{
	case 'S':
		for (int i = 0; i < column; i++)
		{
				output[i] = (1 / (1 + exp(-intput[i])));
		}
			break;
	case 'T':
		for (int i = 0; i < column; i++)
		{
				output[i] = ((exp(intput[i]) - exp(-intput[i])) / (exp(intput[i]) + exp(-intput[i])));
		}
			break;
	case 'R':
		for (int i = 0; i < column; i++)
		{
				output[i] = ((double)(intput>=0) * intput[i]);
		}
			break;
	case 'L':
		for (int i = 0; i < column; i++)
		{
				output[i] = intput[i];
		}
			break;
	}
}

void MatrixMultiplication(double* output, double** aMatrix, double* bMatrix, long First, long Mid, long End)
{
	for (int i = 0; i < First; i++) 
	{
		output[i] = 0;
		for (int inner = 0; inner < Mid; inner++)
		{
			output[i] += aMatrix[i][inner] * bMatrix[inner];
		}
    }
}

void NormalTransform(double* Output, double* Input, double* Mu, double* Sigma, long i32featureNumber)
{// z score
	long i;

	for (i = 0; i < i32featureNumber; i++)
	{
		Output[i] = (Input[i] - Mu[i]) / Sigma[i];
	}

	Output[i32featureNumber] = 1;
}

double ForwordPropagation_Sheng(double**d64tZ, double**d64tV, double* this_pat, double*** WeightModel)
{
	long i;
	long L = DNN_structure;
	long NumhiddenLayer = L - 1;
	double pred = 0;

	for(i = 0; i < L-1; i++)
	{
		if (i == 0)
		{
			MatrixMultiplication(d64tZ[i], WeightModel[i], this_pat, DNN_columnList[i], DNN_DesignLayersize[i], 1); // input to hidden
		}
		else
		{
			MatrixMultiplication(d64tZ[i], WeightModel[i], d64tV[i-1], DNN_columnList[i], DNN_DesignLayersize[i], 1); // hidden to hidden
		}
		DNN_AactivationFunction(g_d64tV[i], g_d64tZ[i], DNN_DesignLayersize[i+1], 'S');
	}
	MatrixMultiplication(d64tZ[(L-1)], WeightModel[(L-1)], d64tV[(L-1)-1], DNN_columnList[L-1], DNN_DesignLayersize[(L-1)], 1); // hidden to output
	pred = d64tZ[(L-1)][0];
	return pred;
}

//---------------------------------------------------------------------------------------------------
// MAIN
double GT_ACC_DNN_StepVelocity(double* d64AccDataX, double* d64AccDataY, double* d64AccDataZ, long stepSize)
{
	double stepLength = 0;
	ACC_ProDNN_NewBuffer(stepSize);

	GT_MedFilter(g_d64DnnBuffer_1, d64AccDataX, com_SlideWindow, com_SampleRate);
	GT_MedFilter(g_d64DnnBuffer_2, d64AccDataY, com_SlideWindow, com_SampleRate);
	GT_MedFilter(g_d64DnnBuffer_3, d64AccDataZ, com_SlideWindow, com_SampleRate);

	for (int nn = 0 ; nn < stepSize; nn++)
	{
		g_d64DnnBufferPro_1[nn] =  g_d64DnnBuffer_1[com_SlideWindow/2 - stepSize + nn+1];
		g_d64DnnBufferPro_2[nn] =  g_d64DnnBuffer_2[com_SlideWindow/2 - stepSize + nn+1];
		g_d64DnnBufferPro_3[nn] =  -1 * g_d64DnnBuffer_3[com_SlideWindow/2 - stepSize + nn+1];
	}

	StepSpeedFeatureGroup(g_d64DnnFeatureGroup, g_d64DnnBufferPro_1, g_d64DnnBufferPro_2, g_d64DnnBufferPro_3, stepSize, com_SampleRate);
	NormalTransform(g_d64DnnFeatureGroupWork, g_d64DnnFeatureGroup, DNN_mu_train, DNN_sigma_train, 30);

	stepLength = ForwordPropagation_Sheng(g_d64tZ, g_d64tV, g_d64DnnFeatureGroupWork, g_dnnWeight);
	stepLength = stepLength * DNN_sigma_output + DNN_mu_output;

	ACC_ProDNN_DeleteBuffer();
	return stepLength;
}