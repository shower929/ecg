//#include "SWM_GTFilter.h"
//#include "stdafx.h"
#include <stdio.h>

// Mean compute at local
double GT_LocalMean(double* d64Buffer, long i32bufferSize)
{
  long i = 0;
  double d64Sum = 0;

  for (i = 0; i < i32bufferSize; i++)
  {
    d64Sum += d64Buffer[i];
  }
  d64Sum = d64Sum/i32bufferSize;
  return d64Sum;
}

// MeanFilter by Clark
void GT_MeanFilter(
  double* d64OutputSample,
  double* d64InputSample,
  long  i32SampleCount,
  long  i32FilterDegree)
{
  long i = 0;
  long i32offset_S = 0;
  long i32offset_E = 0;
  double d64LocalMean = 0;

  i32offset_S = i32FilterDegree/2;
  i32offset_E = i32SampleCount - i32FilterDegree/2-1;

  for (i = 0; i<i32SampleCount; i++)
  {
     if((i >= i32offset_S) && (i <= i32offset_E))
     {
       d64LocalMean = GT_LocalMean(&d64InputSample[i-i32offset_S], i32FilterDegree);
       d64OutputSample[i] = d64LocalMean;
     }
     else
     {
       d64OutputSample[i] = d64InputSample[i];
     }
  }
  for (i = 0; i < i32offset_S; i++)
  {
    d64OutputSample[i] = d64OutputSample[i32offset_S];
    d64OutputSample[i+i32offset_E+1] = d64OutputSample[i32offset_E];
  }
}

// MedFilter form Clark
void GT_MedFilter(
  double* d64OutputSample,
  double* d64InputSample,
  long  i32SampleCount,
  long  i32FilterDegree)
{
  long i = 0;
  long i32offset_S = 0;
  long i32offset_E = 0;
  double d64LocalMean = 0;

  i32offset_S = i32FilterDegree/2;
  i32offset_E = i32SampleCount - i32FilterDegree/2-1;

  for (i = 0; i<i32SampleCount; i++)
  {
     if((i >= i32offset_S) && (i <= i32offset_E))
     {
       d64LocalMean = GT_LocalMean(&d64InputSample[i-i32offset_S], i32FilterDegree);
       d64OutputSample[i] = d64InputSample[i]-d64LocalMean;
     }
     else
     {
       d64OutputSample[i] = d64InputSample[i];
     }
  }
  for (i = 0; i < i32offset_S; i++)
  {
    d64OutputSample[i] = d64InputSample[i32offset_S];
    d64OutputSample[i+i32offset_E+1] = d64OutputSample[i32offset_E];
  }
}
