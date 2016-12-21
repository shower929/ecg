package com.swm.core;

/**
 * Created by yangzhenyu on 2016/10/6.
 */

class QRS_PARAM {
    int	u8RRI_ValidFlagIdx;
    int i32Q_LeftPoint_1D_X;
    int i32S_RightPoint_1D_X;
    int i32MIN_Point_1D_X;
    int	i32Q_Point_X;
    int	i32R_Point_X;
    int	i32S_Point_X;
    int	i32R_Point_Y;
    int	i32Q_Point_Y;
    int	i32S_Point_Y;
    int	i32R_Point_1D_Y;
    int	i32Q_Point_1D_Y;
    int	i32S_Point_1D_Y;
    int	i32MIN_Point_1D_Y;
    int	i32Q_LeftPoint_1D_Y;	///< 2016.05.07 Clark Add
    int	i32S_RightPoint_1D_Y;	///< 2016.05.07 Clark Add
    int i32R_QL_deta_X;
    int i32R_SR_deta_X;
    int	i32QR_deta_X;
    int	i32RS_deta_X;
    int	i32QR_deta_Y;
    int	i32RS_deta_Y;
    int	i32R_Anglel;
    int	i32RR_Interval;
    int 	i32RR_Search_Idx;	// "1" is need to search, and "0" is ignore.
}
