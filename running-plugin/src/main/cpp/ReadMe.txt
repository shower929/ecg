1. API Entry form SWM_AccFunctionList.h

2. There have three API:
	(a) GT_ACC_Motion_SuperRun(double* f_d64StepSpeed, double* d64AccDataX, double* d64AccDataY, double* d64AccDataZ)
		This output only speed, it is fast of compute.

	(b) GT_ACC_Motion_SuperRun(long* f_d64Status,double* f_d64StepSpeed, double* d64AccDataX, double* d64AccDataY, double* d64AccDataZ)
		There have two outputs, status and speed, it is fast of compute.

	(c) GT_ACC_Motion_Run(double* d64output, double* d64AccDataX, double* d64AccDataY, double* d64AccDataZ)
		There have five output, wave, step number, rate, status and speed, it is slow of compute.

3. The input(d64AccDataX, d64AccDataY, d64AccDataZ) is array[300]
   If you use API(c), you need update each data for API.
   If you use API(a)(b), you only need to updata each sec for API.