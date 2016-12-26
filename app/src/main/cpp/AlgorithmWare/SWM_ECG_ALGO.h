#ifndef SWM_ECG_ALGO
#define SWM_ECG_ALGO

#define ALGO_VERSION	"ALGO.001.000.T10"

#define RAW_BUFF_TEST_SIZE      (250 * 30)
#define ECG_SAMPLE_RATE			(250)
#define ECG_SHIFT_SAMPLE		(250)

#define DYNAMIC_HR_RANGE		(15)	///< 2016.04.02 Clark Add
#define LOCAL_HR_RANGE			(15)	///< 2016.04.02 Clark Add
#define MAX_RRI_LIMITE			(340)	// {[(1000 msec X 60)/ MIN_HR(BPM)]/ 4 msec} + 5 BPM
#define MIN_RRI_LIMITE			(63)	// {[(1000 msec X 60)/ MAX_HR(BPM)]/ 4 msec} - 5 BPM
#define MAX_RRI_BUF				(20)	

//#define MAX_QRS_PAIR_1SEC		(6)		//(MAX_HR_LIMITE * 250 / 15000)
#define ECG_SEARCH_DURATION		(63)	//samples = (250/MAX_QRS_PAIR_1SEC) 63
#define QRS_SEARCH_DURATION		(20)
#define R_ANGLE_LIMIT			(0)
//#define STD_RRI_THRESHOLD       (100)			//(32)//(8)//(5)
#define RS_DYNAMIC_FACTOR		(0.5)			//(0.35)

//New Algorithm parameters
#define QRS_1D_MAX_POINT_Y		(-10)//(4)				//must be bigger than 7
#define QRS_1D_MIN_POINT_Y		(-7)			//must be less than 7
//#define QRS_1D_QQ_DELTA_X_MIN	(3)
//#define QRS_1D_QQ_DELTA_X_MAX	(10)
#define QRS_1D_QR_DELTA_X_MIN	(1)//(3)
#define QRS_1D_QR_DELTA_X_MAX	(7)
#define QRS_1D_RS_DELTA_X_MIN	(5)
#define QRS_1D_RS_DELTA_X_MAX	(12)
#define QRS_1D_QRS_DELTA_Y_MAX	(800)			//must be less than 7

#define QRS_1D_RQL_DELTA_X_MIN  (0)//(6)		///< 2016.04.02 Clark Add
#define QRS_1D_RQL_DELTA_X_MAX	(100)//(14)		///< 2016.04.02 Clark Add
#define QRS_1D_RSR_DELTA_X_MIN  (0)//(6)		///< 2016.04.02 Clark Add
#define QRS_1D_RSR_DELTA_X_MAX	(100)//(14)		///< 2016.04.02 Clark Add

#define HRV_FIRST_TIME_BUF		(30)	///< 2016.05.08 Clark Add
#define HRV_CAL_TIME_BUF		(300)	///< 2016.05.08 Clark Add

typedef struct
{
	char	u8RRI_ValidFlagIdx;
	long	i32Q_LeftPoint_1D_X;	///< 2016.04.02 Clark Add
	long	i32S_RightPoint_1D_X;	///< 2016.04.02 Clark Add
	long	i32MIN_Point_1D_X;		///< 2016.04.02 Clark Add
	long	i32Q_Point_X;
	long	i32R_Point_X;
	long	i32S_Point_X;
	long	i32R_Point_Y;
	long	i32Q_Point_Y;
	long	i32S_Point_Y;
	long	i32R_Point_1D_Y;
	long	i32Q_Point_1D_Y;
	long	i32S_Point_1D_Y;
	long	i32MIN_Point_1D_Y;
	long	i32Q_LeftPoint_1D_Y;	///< 2016.05.07 Clark Add
	long	i32S_RightPoint_1D_Y;	///< 2016.05.07 Clark Add
	long    i32R_QL_deta_X;			///< 2016.04.02 Clark Add
	long	i32R_SR_deta_X;			///< 2016.04.02 Clark Add
	long	i32QR_deta_X;
	long	i32RS_deta_X;
	long	i32QR_deta_Y;
	long	i32RS_deta_Y;
	long	i32R_Anglel;
	long	i32RR_Interval;
	long 	i32RR_Search_Idx;	// "1" is need to search, and "0" is ignore.

}QRS_PARAM;

typedef struct
{
	short	i16HRVFirstFlag;
	short	i16HRVTimer;
	short	i16UpdateHRIdx;
	short	i16HistoryHRIdx;
	short	i16HistoryRRIIdx;
	short	i16RecordRRIIdx;
	long	i32TimeFrameIdx;
	long	i32RecordHRVFrameIdx;
	long	i32QRSPairIdx;
	short	i16UpdateHRBuf[CAL_TIME_BUF];
	short	i16HistoryHRBuf[KEEP_TIME_BUF];
	short	i16HistoryRRIBuf[HISTORY_HRV_RRI_BUF];
	short	i16RecordHRVRRIBuf[HRV_RRI_LIMIT_BUF];
	long	i32RecordHRVTimeBuf[HRV_RRI_LIMIT_BUF];
	long	i32FinalHRBuf[TOTAL_TIME_NUM];
	long	i32FinalSDNNBuf[TOTAL_TIME_NUM];
	long	i32FinalRMSSDBuf[TOTAL_TIME_NUM];
	long	i32QPointX[MAX_QRS_PAIR_NUM];
	long	i32RPointX[MAX_QRS_PAIR_NUM];
	long	i32SPointX[MAX_QRS_PAIR_NUM];
	short	i16IsEquationForMaxHR;
	short 	i16UserAge;
	short 	i16UserGender;
	short 	i16UserStaticHR;
	short 	i16UserMaximumHR;
	float 	fUserWeight;
	float 	fUserHeight;
	float 	fMHR[TOTAL_TIME_NUM];
	float 	fVO2Max[TOTAL_TIME_NUM];
	float 	fSumCalorie[TOTAL_TIME_NUM];
	short 	i16AnsAge[TOTAL_TIME_NUM];
	short 	i16StressStatus[TOTAL_TIME_NUM];
}HISTORY_INFO;

extern HISTORY_INFO g_sHistoryInfo;

//External API Prototype
short APPS_ECG_RPeakDetection(long *i32ProcessingBuf);
short APPS_HRV_GetMonitorStatusFlag(void);
short APPS_HRV_GetValueSDNN(void);
short APPS_HRV_GetValueRMSSD(void);
short APPS_ECG_GetValueHR(void);
void APPS_ECG_InitialForModeChange(short isHRVOn);
short APPS_ECG_SimulationRTOS(long *i32ECGRawBuffer);
long MY_SearchLocalQRSComplex(long 	i32StartX, long i32EndX, long i32Torrance,long i32DynamicRSSlopeTH, long* pi32QRSIdx, long* pi32Buffer, long* pi32WorkBuffer, QRS_PARAM* psQRSComplex);
void MY_RemoveFalseQRSComplex(short	i16TotalCount, short i16SigmaThreshold, short i16CountThreshold, short* pi16Mean, short* pi16Sigma, short* pi16Count, QRS_PARAM* psQRSComplex);
void MY_FindECGWave(long i32StartPoint, long  i32EndPoint, long* pi32DynamicRSSlopeThreshold, long* pi32QRSIndex, short* pi16Mean, short* pi16Sigma, short* pi16Count, long* pi32SrcBuf, long* pi32WorkBuf, QRS_PARAM* psQRSArray);
void APPS_ECG_Main(long *i32ProcessingBuf, long i32DataLength);
void APPS_RealTime_OS(long *i32ProcessingBuf);
void APPS_RRI();
#endif

