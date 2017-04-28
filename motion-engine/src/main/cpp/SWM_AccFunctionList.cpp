//#include <stdafx.h>
#include "SWM_AccFunctionList.h"
#include "SWM_MotionAlgoParameterSetting.h"
#include "SWM_GTFilter.h"
#include "SWM_SV_DnnFunction.h"

double* g_d64FilterProBuffer = NULL;
double* g_d64FilterWorkBuffer = NULL;
double* g_d64SlideWindowBuffer_AccX = NULL;
double* g_d64SlideWindowBuffer_AccY = NULL;
double* g_d64SlideWindowBuffer_AccZ = NULL;
double* g_d64SlideWindowBuffer_AccC = NULL;
//---------------------------------------------------------------------------------------------------
// Buffer allocate and delete
void ACC_ALGO_NewBuffer(long i32Size)
{
  if (g_d64FilterProBuffer == NULL)
    g_d64FilterProBuffer = new double[i32Size];

  if (g_d64FilterWorkBuffer == NULL)
    g_d64FilterWorkBuffer = new double[i32Size];
}

void ACC_ALGO_DeleteBuffer(void)
{
  if (g_d64FilterProBuffer)
  {
    delete  []g_d64FilterProBuffer;
    g_d64FilterProBuffer = NULL;
  }

  if (g_d64FilterWorkBuffer)
  {
    delete [] g_d64FilterWorkBuffer;
    g_d64FilterWorkBuffer = NULL;
  }
}

void ACC_Algorithm_Initial(void)
{
  if (g_d64FilterProBuffer)
  {
  memset(g_d64FilterProBuffer, 0, sizeof(g_d64FilterProBuffer));
  }

  if (g_d64FilterWorkBuffer)
  {
  memset(g_d64FilterWorkBuffer, 0, sizeof(g_d64FilterWorkBuffer));
  }
}

void ACC_ALGO_NewBuffer_SlideWindow_AccXYZC(void)
{
  if (g_d64SlideWindowBuffer_AccX == NULL)
    g_d64SlideWindowBuffer_AccX = new double[com_SlideWindow];

  if (g_d64SlideWindowBuffer_AccY == NULL)
    g_d64SlideWindowBuffer_AccY = new double[com_SlideWindow];

  if (g_d64SlideWindowBuffer_AccZ == NULL)
    g_d64SlideWindowBuffer_AccZ = new double[com_SlideWindow];

  if (g_d64SlideWindowBuffer_AccC == NULL)
    g_d64SlideWindowBuffer_AccC = new double[com_SlideWindow];
}

void ACC_ALGO_DeleteBuffer_SlideWindow_AccXYZC(void)
{
  if (g_d64SlideWindowBuffer_AccX)
  {
    delete  []g_d64SlideWindowBuffer_AccX;
    g_d64SlideWindowBuffer_AccX = NULL;
  }

  if (g_d64SlideWindowBuffer_AccY)
  {
    delete  []g_d64SlideWindowBuffer_AccY;
    g_d64SlideWindowBuffer_AccY = NULL;
  }

  if (g_d64SlideWindowBuffer_AccZ)
  {
    delete  []g_d64SlideWindowBuffer_AccZ;
    g_d64SlideWindowBuffer_AccZ = NULL;
  }

  if (g_d64SlideWindowBuffer_AccC)
  {
    delete  []g_d64SlideWindowBuffer_AccC;
    g_d64SlideWindowBuffer_AccC = NULL;
  }
}

void ACC_ALGO_InitialBuffer_SlideWindow_AccXYZC(void)
{
  if (g_d64SlideWindowBuffer_AccX)
  {
  memset(g_d64SlideWindowBuffer_AccX, 0, sizeof(g_d64SlideWindowBuffer_AccX));
  }

  if (g_d64SlideWindowBuffer_AccY)
  {
  memset(g_d64SlideWindowBuffer_AccY, 0, sizeof(g_d64SlideWindowBuffer_AccY));
  }

  if (g_d64SlideWindowBuffer_AccZ)
  {
  memset(g_d64SlideWindowBuffer_AccZ, 0, sizeof(g_d64SlideWindowBuffer_AccZ));
  }

  if (g_d64SlideWindowBuffer_AccC)
  {
  memset(g_d64SlideWindowBuffer_AccC, 0, sizeof(g_d64SlideWindowBuffer_AccC));
  }
}

//---------------------------------------------------------------------------------------------------
// Parsing Function
double AccUnitConversion(double d64Input)
{
	double d64output;
	
	d64output = d64Input*com_AccSensitivity*0.001*9.81;

	return d64output;
}

double AccCompositionOfForces(double d64AccX, double d64AccY, double d64AccZ)
{
	double d64AccC;

	d64AccC = pow(d64AccX*d64AccX+d64AccY*d64AccY+d64AccZ*d64AccZ, 0.5);

	return d64AccC;
}

void AccFinalTypeOf4Axis(double* d64FinalTypeAccX, double* d64FinalTypeAccY, double* d64FinalTypeAccZ, double* d64FinalTypeAccC, double* d64RawAccX, double* d64RawAccY, double* d64RawAccZ, long i32SampleSize)
{
	long i32Count;

	for (i32Count = 0; i32Count < i32SampleSize; i32Count++)
	{
		d64FinalTypeAccX[i32Count] = AccUnitConversion(d64RawAccX[i32Count]);
		d64FinalTypeAccY[i32Count] = AccUnitConversion(d64RawAccY[i32Count]);
		d64FinalTypeAccZ[i32Count] = AccUnitConversion(d64RawAccZ[i32Count]);
		d64FinalTypeAccC[i32Count] = AccCompositionOfForces(d64FinalTypeAccX[i32Count], d64FinalTypeAccY[i32Count], d64FinalTypeAccZ[i32Count]);
	}
}

//---------------------------------------------------------------------------------------------------
// Run Function
double GT_StrideEquation_By_StepBasedPDR(double* d64Input, long i32Size)
{
	long i;
	double SWM_ChestPosition = 0.7;
	double d64output = 0;

	for (i=0; i<i32Size; i++)
		{
			if (d64Input[i]<0)
				{
					d64output += -1*d64Input[i];
				}
			else
				{
					d64output += 1*d64Input[i];
				}
		}
	d64output = d64output / i32Size;
	d64output = pow(d64output, 0.3333);
	d64output = SWM_ChestPosition * 0.98 * d64output;
	return d64output;
}

long GT_JudgeStepPeak(double* d64VerticalAxis, long i32SampleCount)
{
  long i = 0;
  long IsStep = 1;
  long StepTimeInterval = 0.12*com_SampleRate;
  long i32MeanPoint = i32SampleCount/2;
  double Threshold = -1;

  for (i = 0; i <= StepTimeInterval; i++)
  {
    if (i != 0)
    {
      if (*d64VerticalAxis >= *(d64VerticalAxis+i) ||
          *d64VerticalAxis >= *(d64VerticalAxis-i) ||
          *d64VerticalAxis > Threshold   )
      {
        IsStep = 0;
        break;
      }
    }
  }
  return IsStep;
}

void GT_SearchStep(double* d64StepRate, double* d64StepLength, double* d64VerticalAxis, double* d64RawAccX, double* d64RawAccY, double* d64RawAccZ, long i32SampleCount)
{
  long i = 0;
  long IsStep = 0;
  long i32stepSize = 0;
  long i32StepNumber = 0;
  long i32SearchSensetivity = 0.12*com_SampleRate*2+1;
  long i32SearchPoint_S = i32SearchSensetivity/2;
  long i32SearchPoint_E = i32SampleCount - i32SearchSensetivity/2-1;
  double dnnStepLength = 0;

  *d64StepRate = 0;
  *d64StepLength = 0;

  long* i32StepTimeAxis = NULL;
  i32StepTimeAxis = new long[i32SampleCount];

  AllocateDnnAllBufferSetting();

  for (i = 0; i < i32SampleCount; i++)
  {
    if (i>=i32SearchPoint_S &&  i<=i32SearchPoint_E)
    {
      IsStep = GT_JudgeStepPeak(&d64VerticalAxis[i], i32SearchSensetivity);
    }
    if (IsStep == 1)
    {
      // function A: recording step time
      i32StepTimeAxis[i32StepNumber] = i;

      // function B: step rate of range
      if (i32StepNumber >= 1)
      {
		  i32stepSize = i32StepTimeAxis[i32StepNumber] - i32StepTimeAxis[i32StepNumber-1];
		  *d64StepRate += i32stepSize;
		  //*d64StepLength += GT_StrideEquation_By_StepBasedPDR(&d64VerticalAxis[i32StepTimeAxis[i32StepNumber-1]], i32stepSize);
		  *d64StepLength += GT_ACC_DNN_StepVelocity(d64RawAccX, d64RawAccY, d64RawAccZ, i32stepSize);
      }
      i32StepNumber += IsStep;
      IsStep = 0;
      // function end
    }
  }
  if (i32StepNumber > 1)
  {
    *d64StepRate = *d64StepRate / (i32StepNumber-1) / com_SampleRate; // total diff-time(per) => aveage diff-time(per) => aveage diff-time(sec)
	*d64StepLength = *d64StepLength / (i32StepNumber-1);
  }
  delete  []i32StepTimeAxis;
  i32StepTimeAxis = NULL;
  DeleteDnnAllBufferSetting();

}

void GT_SearchStep_forStepBasedPDR(double* d64StepRate, double* d64StepLength, double* d64VerticalAxis, double* d64RawAccX, double* d64RawAccY, double* d64RawAccZ, long i32SampleCount)
{
  long i = 0;
  long IsStep = 0;
  long i32stepSize = 0;
  long i32StepNumber = 0;
  long i32SearchSensetivity = 0.12*com_SampleRate*2+1;
  long i32SearchPoint_S = i32SearchSensetivity/2;
  long i32SearchPoint_E = i32SampleCount - i32SearchSensetivity/2-1;
  double dnnStepLength = 0;

  *d64StepRate = 0;
  *d64StepLength = 0;

  long* i32StepTimeAxis = NULL;
  i32StepTimeAxis = new long[i32SampleCount];

  AllocateDnnAllBufferSetting();

  for (i = 0; i < i32SampleCount; i++)
  {
    if (i>=i32SearchPoint_S &&  i<=i32SearchPoint_E)
    {
      IsStep = GT_JudgeStepPeak(&d64VerticalAxis[i], i32SearchSensetivity);
    }
    if (IsStep == 1)
    {
      // function A: recording step time
      i32StepTimeAxis[i32StepNumber] = i;

      // function B: step rate of range
      if (i32StepNumber >= 1)
      {
		  i32stepSize = i32StepTimeAxis[i32StepNumber] - i32StepTimeAxis[i32StepNumber-1];
		  *d64StepRate += i32stepSize;
		  *d64StepLength += GT_StrideEquation_By_StepBasedPDR(&d64VerticalAxis[i32StepTimeAxis[i32StepNumber-1]], i32stepSize);
		  //*d64StepLength += GT_ACC_DNN_StepVelocity(d64RawAccX, d64RawAccY, d64RawAccZ, i32stepSize);
      }
      i32StepNumber += IsStep;
      IsStep = 0;
      // function end
    }
  }
  if (i32StepNumber > 1)
  {
    *d64StepRate = *d64StepRate / (i32StepNumber-1) / com_SampleRate; // total diff-time(per) => aveage diff-time(per) => aveage diff-time(sec)
	*d64StepLength = *d64StepLength / (i32StepNumber-1);
  }
  delete  []i32StepTimeAxis;
  i32StepTimeAxis = NULL;
  DeleteDnnAllBufferSetting();

}

long GT_AcivityDetection(double* d64Vertical, double* d64Composition, long i32Size, double d64StepRate)
{// Activity Detection : Static(Status=0) Walk(Status=1) Run(Status=2)
  long i;
  long numVer = 0;
  long Status;
  double d64VerFeature = 0;
  double d64ComFeature = 0;
  double i32ActivityJudgment;
	
  // Compute feature
  for (i = 0; i < i32Size; i++)
  {
    d64ComFeature +=  d64Composition[i] * d64Composition[i];

    if (d64Vertical[i] < 0)
    {
      d64VerFeature += d64Vertical[i];
      numVer ++;
    }
  }

  d64VerFeature = d64VerFeature / numVer;
  d64ComFeature = pow(d64ComFeature /i32Size, 0.5);
  i32ActivityJudgment = 2.896 * d64VerFeature - 0.9171 * d64ComFeature + 11.6411;

  if (i32ActivityJudgment>0)
  {
    // Static
    Status=0;
  }
  else
  {
    if (d64StepRate>0.447)
    {
      // Walk
      Status=1;
    }
    else
    {
      // Run
      Status=2;
    }
  }
  return Status;
}

//---------------------------------------------------------------------------------------------------
// Main Function
void GT_ACC_Motion_Run(double* d64Output, double* d64AccDataX, double* d64AccDataY, double* d64AccDataZ)
{
  double f_d64Status = 0;
  double f_d64StepNumber = 0;
  double f_ProY = 0;
  double f_d64StepRate = 0;
  double f_d64StepLength = 0;
  double f_d64StepSpeed = 0;

  ACC_ALGO_NewBuffer_SlideWindow_AccXYZC();
  ACC_ALGO_InitialBuffer_SlideWindow_AccXYZC();
  AccFinalTypeOf4Axis(
	  g_d64SlideWindowBuffer_AccX, 
	  g_d64SlideWindowBuffer_AccY, 
	  g_d64SlideWindowBuffer_AccZ, 
	  g_d64SlideWindowBuffer_AccC, 
	  d64AccDataX, 
	  d64AccDataY, 
	  d64AccDataZ, 
	  com_SlideWindow);

  ACC_ALGO_NewBuffer(com_SlideWindow);
  ACC_Algorithm_Initial();
    {
      // Signal process ------------
      GT_MeanFilter(g_d64FilterWorkBuffer, g_d64SlideWindowBuffer_AccY, com_SlideWindow, 10);
      GT_MeanFilter(g_d64FilterProBuffer, g_d64FilterWorkBuffer, com_SlideWindow, 3);
      GT_MedFilter(g_d64FilterWorkBuffer, g_d64FilterProBuffer, com_SlideWindow, 50);

      // Execute function ------------
        // Function A : Step Rate
		GT_SearchStep(&f_d64StepRate, &f_d64StepLength, g_d64FilterWorkBuffer, g_d64SlideWindowBuffer_AccX, g_d64SlideWindowBuffer_AccY, g_d64SlideWindowBuffer_AccZ, com_SlideWindow);
		if (f_d64StepLength == 0)
		{
			f_d64StepSpeed = 0;
		}
		else
		{
			f_d64StepSpeed = f_d64StepLength / f_d64StepRate;
		}
        // Function B : Step Number
        f_d64StepNumber = GT_JudgeStepPeak(&g_d64FilterWorkBuffer[com_SlideWindow/2], com_SlideWindow);
        // Function C : Motion Detection
        f_d64Status = GT_AcivityDetection(g_d64FilterWorkBuffer, g_d64SlideWindowBuffer_AccC, com_SlideWindow, f_d64StepRate);
        // Save wave value
        f_ProY=g_d64FilterWorkBuffer[com_SlideWindow/2];
    }
  ACC_ALGO_DeleteBuffer();
  ACC_ALGO_DeleteBuffer_SlideWindow_AccXYZC();
  d64Output[0] = f_ProY;
  d64Output[1] = f_d64StepNumber;
  d64Output[2] = f_d64StepRate;
  d64Output[3] = f_d64Status;
  d64Output[4] = f_d64StepLength;
  d64Output[5] = f_d64StepSpeed;
}

void GT_ACC_Motion_SuperRun(long* f_d64Status,double* f_d64StepSpeed, double* d64AccDataX, double* d64AccDataY, double* d64AccDataZ)
{
  double f_d64StepRate = 0;
  double f_d64StepLength = 0;

  ACC_ALGO_NewBuffer_SlideWindow_AccXYZC();
  ACC_ALGO_InitialBuffer_SlideWindow_AccXYZC();
  AccFinalTypeOf4Axis(
	  g_d64SlideWindowBuffer_AccX, 
	  g_d64SlideWindowBuffer_AccY, 
	  g_d64SlideWindowBuffer_AccZ, 
	  g_d64SlideWindowBuffer_AccC, 
	  d64AccDataX, 
	  d64AccDataY, 
	  d64AccDataZ, 
	  com_SlideWindow);

  ACC_ALGO_NewBuffer(com_SlideWindow);
  ACC_Algorithm_Initial();
    {
      // Signal process ------------
      GT_MeanFilter(g_d64FilterWorkBuffer, g_d64SlideWindowBuffer_AccY, com_SlideWindow, 10);
      GT_MeanFilter(g_d64FilterProBuffer, g_d64FilterWorkBuffer, com_SlideWindow, 3);
      GT_MedFilter(g_d64FilterWorkBuffer, g_d64FilterProBuffer, com_SlideWindow, 50);

      // Execute function ------------
        // Function A : Step Rate
		GT_SearchStep(&f_d64StepRate, &f_d64StepLength, g_d64FilterWorkBuffer, g_d64SlideWindowBuffer_AccX, g_d64SlideWindowBuffer_AccY, g_d64SlideWindowBuffer_AccZ, com_SlideWindow);
		if (f_d64StepLength == 0)
		{
			f_d64StepSpeed = 0;
		}
		else
		{
			*f_d64StepSpeed = f_d64StepLength / f_d64StepRate;
		}
        // Function C : Motion Detection
        *f_d64Status = GT_AcivityDetection(g_d64FilterWorkBuffer, g_d64SlideWindowBuffer_AccC, com_SlideWindow, f_d64StepRate);
    }
  ACC_ALGO_DeleteBuffer();
  ACC_ALGO_DeleteBuffer_SlideWindow_AccXYZC();
}

void GT_ACC_Motion_SuperRun(double* f_d64StepSpeed, double* d64AccDataX, double* d64AccDataY, double* d64AccDataZ)
{
  double f_d64StepRate = 0;
  double f_d64StepLength = 0;

  ACC_ALGO_NewBuffer_SlideWindow_AccXYZC();
  ACC_ALGO_InitialBuffer_SlideWindow_AccXYZC();
  AccFinalTypeOf4Axis(g_d64SlideWindowBuffer_AccX, g_d64SlideWindowBuffer_AccY, g_d64SlideWindowBuffer_AccZ, g_d64SlideWindowBuffer_AccC, d64AccDataX, d64AccDataY, d64AccDataZ, com_SlideWindow);

  ACC_ALGO_NewBuffer(com_SlideWindow);
  ACC_Algorithm_Initial();

  // Signal process ------------
  GT_MeanFilter(g_d64FilterWorkBuffer, g_d64SlideWindowBuffer_AccY, com_SlideWindow, 10);
  GT_MeanFilter(g_d64FilterProBuffer, g_d64FilterWorkBuffer, com_SlideWindow, 3);
  GT_MedFilter(g_d64FilterWorkBuffer, g_d64FilterProBuffer, com_SlideWindow, 50);

  // Execute function ------------
  GT_SearchStep(&f_d64StepRate, &f_d64StepLength, g_d64FilterWorkBuffer, g_d64SlideWindowBuffer_AccX, g_d64SlideWindowBuffer_AccY, g_d64SlideWindowBuffer_AccZ, com_SlideWindow);
  if (f_d64StepLength == 0)
  {
	  f_d64StepSpeed = 0;
  }
  else
  {
	  *f_d64StepSpeed = f_d64StepLength / f_d64StepRate;
  }
  ACC_ALGO_DeleteBuffer();
  ACC_ALGO_DeleteBuffer_SlideWindow_AccXYZC();
}

void GT_ACC_Motion_SuperRun_VelocityByPDR(double* d64Output, double* d64AccDataX, double* d64AccDataY, double* d64AccDataZ)
{
  double f_d64Status = 0;
  double f_d64StepNumber = 0;
  double f_ProY = 0;
  double f_d64StepRate = 0;
  double f_d64StepLength = 0;
  double f_d64StepSpeed = 0;

  ACC_ALGO_NewBuffer_SlideWindow_AccXYZC();
  ACC_ALGO_InitialBuffer_SlideWindow_AccXYZC();
  AccFinalTypeOf4Axis(
	  g_d64SlideWindowBuffer_AccX, 
	  g_d64SlideWindowBuffer_AccY, 
	  g_d64SlideWindowBuffer_AccZ, 
	  g_d64SlideWindowBuffer_AccC, 
	  d64AccDataX, 
	  d64AccDataY, 
	  d64AccDataZ, 
	  com_SlideWindow);

  ACC_ALGO_NewBuffer(com_SlideWindow);
  ACC_Algorithm_Initial();
    {
      // Signal process ------------
      GT_MeanFilter(g_d64FilterWorkBuffer, g_d64SlideWindowBuffer_AccY, com_SlideWindow, 10);
      GT_MeanFilter(g_d64FilterProBuffer, g_d64FilterWorkBuffer, com_SlideWindow, 3);
      GT_MedFilter(g_d64FilterWorkBuffer, g_d64FilterProBuffer, com_SlideWindow, 50);

      // Execute function ------------
        // Function A : Step Rate
		GT_SearchStep_forStepBasedPDR(&f_d64StepRate, &f_d64StepLength, g_d64FilterWorkBuffer, g_d64SlideWindowBuffer_AccX, g_d64SlideWindowBuffer_AccY, g_d64SlideWindowBuffer_AccZ, com_SlideWindow);
		if (f_d64StepLength == 0)
		{
			f_d64StepSpeed = 0;
		}
		else
		{
			f_d64StepSpeed = f_d64StepLength / f_d64StepRate;
		}
        // Function B : Step Number
        f_d64StepNumber = GT_JudgeStepPeak(&g_d64FilterWorkBuffer[com_SlideWindow/2], com_SlideWindow);
        // Function C : Motion Detection
        f_d64Status = GT_AcivityDetection(g_d64FilterWorkBuffer, g_d64SlideWindowBuffer_AccC, com_SlideWindow, f_d64StepRate);
        // Save wave value
        f_ProY=g_d64FilterWorkBuffer[com_SlideWindow/2];
    }
  ACC_ALGO_DeleteBuffer();
  ACC_ALGO_DeleteBuffer_SlideWindow_AccXYZC();
  d64Output[0] = f_ProY;
  d64Output[1] = f_d64StepNumber;
  d64Output[2] = f_d64StepRate;
  d64Output[3] = f_d64Status;
  d64Output[4] = f_d64StepLength;
  d64Output[5] = f_d64StepSpeed;
}