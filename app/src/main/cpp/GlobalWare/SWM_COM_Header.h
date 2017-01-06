#ifndef SWM_COM_HEADER_H
#define SWM_COM_HEADER_H

#define USE_MATLAB_SIMULATE
#if defined(USE_PC_SIMULATE)
#include <stdio.h>
#include <string.h>
#include <math.h>
#include <stdlib.h>
#endif
//#define USE_PC_SIMULATE	//For BCB platform must define the flag
#define USE_MATLAB_SIMULATE

#define max(A, B)  				(((A) > (B)) ? (A) : (B))
#define min(A, B)  				(((A) < (B)) ? (A) : (B))
#define SAFE_DIVIDE32(v) 		(v==0?(long)1:(long)v)
#define SAFE_DIVIDE16(v) 		(v==0?(short)1:(short)v)
#define ABS(A)    				(((A) < 0) ? -1 * (A) : (A))

#define MAX_HR_LIMITE			(220)
#define MIN_HR_LIMITE			(45)

#define ECG_MEDIAN_POINT		(125)
#define ECG_MEAN_POINT			(6)
#define FILTER_SIZE				(31)

#define OVER_FLOW_MAX			(60000)
#define OVER_FLOW_MIN			(-60000)
#define SHORT_MAX				(65535)
#define EMPTY					(0)

#define CAL_TIME_BUF			(6)		
#define KEEP_TIME_BUF			(3)		///< 2016.04.02 Clark Add
#define HISTORY_HRV_RRI_BUF		(700)	///< 2016.05.08 Clark Add for recording 5 min
#define HRV_RRI_LIMIT_BUF		(7200)	///< 2016.05.08 Clark Add for recording 2 hours
#define LOCAL_MAX_POINTS_NUM	(200) //(CAL_TIME_BUF * 600) //the possible QRS pairs is which the formula: { MAX_HR(BPM) X 250SPS x CAL_TIME_BUF / [(1000 X 60 / 4) X 2] X 2 } 5min
#define MAX_QRS_PAIR_NUM		(14400)
#define	TOTAL_TIME_NUM			(7200)
#endif

