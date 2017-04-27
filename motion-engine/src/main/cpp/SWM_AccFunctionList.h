// For SuperRun API (C)
void GT_ACC_Motion_Run(double* d64output, double* d64AccDataX, double* d64AccDataY, double* d64AccDataZ);
// Condition : 1. There must be a 6 second lead time.
//             2. d64AccDataX, d64AccDataY and d64AccDataZ is point, it is array[300].
// ---------------------------------------------------------------------------------------------
// Output :    1. output => save 4 motion detection value [waveValue, stepNumber, stepRate, Status].
//                (1) waveValue : for draw curve.
//                (2) stepNumber : step number, output 1 is have step, 0 is not have step.
//                (3) stepRate : step rate, unit sec/step.
//                (4) Status : activity status, 0 is static, 1 is walk, 2 is run.
//                (5) Velocity : Step speed by DNN (m/s);
//
// Input :     2. d64AccDataX => ACC raw data of axis-X.
//             3. d64AccDataY => ACC raw data of axis-Y.
//             4. d64AccDataZ => ACC raw data of axis-Z.
//

// For SuperRun API (B)
void GT_ACC_Motion_SuperRun(long* f_d64Status,double* f_d64StepSpeed, double* d64AccDataX, double* d64AccDataY, double* d64AccDataZ);
// Condition : 1. There must be a 6 second lead time.
//             2. d64AccDataX, d64AccDataY and d64AccDataZ is point, it is array[300].
// ---------------------------------------------------------------------------------------------
// Output :    1. f_d64Status    : Activity status, 0 is static, 1 is walk, 2 is run.
//             2. f_d64StepSpeed : Step velocity by DNN (m/s).
//
// Input :     2. d64AccDataX => ACC raw data of axis-X.
//             3. d64AccDataY => ACC raw data of axis-Y.
//             4. d64AccDataZ => ACC raw data of axis-Z.
//

// For SuperRun API (A)
void GT_ACC_Motion_SuperRun(double* f_d64StepSpeed, double* d64AccDataX, double* d64AccDataY, double* d64AccDataZ);
// Condition : 1. There must be a 6 second lead time.
//             2. d64AccDataX, d64AccDataY and d64AccDataZ is point, it is array[300].
// ---------------------------------------------------------------------------------------------
// Output :    1. f_d64StepSpeed : Step velocity by DNN (m/s).
//
// Input :     1. d64AccDataX => ACC raw data of axis-X.
//             2. d64AccDataY => ACC raw data of axis-Y.
//             3. d64AccDataZ => ACC raw data of axis-Z.
//
// For SuperRun API (Total)
void GT_ACC_Motion_SuperRun_VelocityByPDR(double* d64Output, double* d64AccDataX, double* d64AccDataY, double* d64AccDataZ);
// Condition : 1. There must be a 6 second lead time.
//             2. d64AccDataX, d64AccDataY and d64AccDataZ is point, it is array[300].
// ---------------------------------------------------------------------------------------------
// Output :    1. output => save 4 motion detection value [waveValue, stepNumber, stepRate, Status].
//                (1) waveValue : for draw curve.
//                (2) stepNumber : step number, output 1 is have step, 0 is not have step.
//                (3) stepRate : step rate, unit sec/step.
//                (4) Status : activity status, 0 is static, 1 is walk, 2 is run.
//                (5) Length : Step length(m).
//                (6) Velocity : Step speed by PDR (m/s);
//
// Input :     2. d64AccDataX => ACC raw data of axis-X.
//             3. d64AccDataY => ACC raw data of axis-Y.
//             4. d64AccDataZ => ACC raw data of axis-Z.
//