package com.swm.core;

/**
 * Created by yangzhenyu on 2016/10/5.
 */

class SWM_ECG_ALGO_HEADER {
    final String ALGO_VERSION = "ALGO.001.000.T10";

    static long SAFE_DIVIDE32(long v) {return v==0 ? (long)1 : (long)v; };
    static long SAFE_DIVIDE16(long v) {return v==0?(long)1:(long)v;};
    static final int OVER_FLOW_MAX = 30000;
    static final int OVER_FLOW_MIN = -30000;
    static final int SHORT_MAX = 32767;
    static final int ECG_SAMPLE_RATE = 250;
    static final short ECG_SHIFT_SAMPLE = 250;
    static final short ECG_MEDIAN_POINT = 125;
    static final short ECG_MEAN_POINT = 6;//2015/05/17 modify
    static final short MAX_HR_LIMITE = 220;
    static final short MIN_HR_LIMITE = 45;
    static final short DYNAMIC_HR_RANGE = 15;
    static final short LOCAL_HR_RANGE = 15;
    static final short MAX_RRI_BUF = 20;
    static final short MAX_RRI_LIMITE = 340;                // {[(1000 msec X 60)/ MIN_HR(BPM)]/ 4 msec} + 5 BPM
    static final short MIN_RRI_LIMITE = 63;                // {[(1000 msec X 60)/ MAX_HR(BPM)]/ 4 msec} - 5 BPM
    static final int CAL_TIME_BUF = 6;
    static final short KEEP_TIME_BUF = 3;
    //public short MAX_QRS_PAIR_1SEC = 6;        //(MAX_HR_LIMITE * 250 / 15000)
    static final int LOCAL_MAX_POINTS_NUM = 200; //the possible QRS pairs is which the formula: { MAX_HR(BPM) X 250SPS x CAL_TIME_BUF / [(1000 X 60 / 4) X 2] X 2 } 5min
    static final short ECG_SEARCH_DURATION = 63;        //samples = (250/MAX_QRS_PAIR_1SEC) 63
    static final short QRS_SEARCH_DURATION = 20;
    static final short R_ANGLE_LIMIT = 0;
    static final short STD_RRI_THRESHOLD = 100;       //(5)
    static final float RS_DYNAMIC_FACTOR = 0.5F;

    //New Algorithm parameters
    static final short QRS_1D_MAX_POINT_Y = -10;                //must be bigger than 7
    static final short QRS_1D_MIN_POINT_Y = -7;            //must be less than 7
    static final short QRS_1D_QQ_DELTA_X_MIN = 3;
    static final short QRS_1D_QQ_DELTA_X_MAX = 10;
    static final short QRS_1D_QR_DELTA_X_MIN = 1;
    static final short QRS_1D_QR_DELTA_X_MAX = 7;
    static final short QRS_1D_RS_DELTA_X_MIN = 5;
    static final short QRS_1D_RS_DELTA_X_MAX = 12;
    static final short QRS_1D_QRS_DELTA_Y_MAX	=800;	//must be less than 7

    static final short QRS_1D_RQL_DELTA_X_MIN =0;		///< 2016.04.02 Clark Add
    static final short QRS_1D_RQL_DELTA_X_MAX	=100;	///< 2016.04.02 Clark Add
    static final short QRS_1D_RSR_DELTA_X_MIN =0;		///< 2016.04.02 Clark Add
    static final short QRS_1D_RSR_DELTA_X_MAX	=100;	///< 2016.04.02 Clark Add

    static final short HRV_FIRST_TIME_BUF	=30;	///< 2016.05.08 Clark Add
    static final short HRV_CAL_TIME_BUF = 300;	///< 2016.05.08 Clark Add
    static final short HRV_RRI_MAX_BUF = 700;	///< 2016.05.08 Clark Add for recording 5 min
    static final short HRV_RRI_LIMIT_BUF = 19600;   ///< 2016.05.08 Clark Add for recording 24 hours

}
