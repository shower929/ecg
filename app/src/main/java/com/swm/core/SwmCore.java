package com.swm.core;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.swm.heart.BuildConfig;
import com.swm.hrv.HrvListener;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by yangzhenyu on 2016/9/27.
 */

public class SwmCore {
    private static final String LOG_TAG = "SWM";
    private static SwmCore SWM_CORE;
    private static final float CLOCK = 50f; //Hz
    private static final float EXPECTED_TIME = 1f/CLOCK * 1000; //ms

    private static MotionService mMotionService;
    private static LinkedBlockingDeque<SwmData> mMotionDataQueue;
    private static Thread mMotionWorker;

    private EcgService mEcgService;
    private LinkedBlockingDeque<SwmData> mEcgDataQueue;
    private Thread mEcgWorker;

    private static AcceleratorService mAcceleratorService;
    private static LinkedBlockingQueue<SwmData> mAccDataQueue;
    private static Thread mAccWorker;

    private static BreathService mBreathService;
    private static LinkedBlockingQueue<SwmData> mBreathDataQueue;
    private static Thread mBreathWorker;

    private static HeartRateService mHeartRateService;
    private static LinkedBlockingQueue<SwmData> mHeartBeatServiceQueue;
    private static final long TIME_FRAME = 1000;

    static boolean sRunning = true;

    private static Handler mProfilingHandler = new Handler();
    private int mRxSize;

    private boolean mFirstMotionPacket = true;
    private int mPreMotionIndex = -1;
    private float mReceivedMotionPacketCount = 0;
    private int mTotalMotionPacketCount;

    private boolean mFirstEcgPacket = true;
    private int mPreEcgIndex = -1;
    private int mEcgPackCount = -1;
    private float mReceivedEcgPacketCount = 0;
    private int mTotalEcgPacketCount;

    private double mTotalPacketLoss;

    private long mLastReceiveTime;
    private float mEcgLatency;

    private boolean mInit = true;

    private Context mContext;
    private SuperRunCloudService mSuperRunCloudService;
    private EmergencyCloudService mEmergencyCloudService;
    private boolean mRecording;
    private Runnable mTimer;
    private Handler mHandler;
    private int mElapse;
    private TimerListener mTimerListener;
    private SportService mSportService;
    private ProfilingListener mProfilingListener;
    private static final Object PROFILING_LOCK = new Object();
    private double mErrorByte;
    private Dump mDump;
    private HrvService mHrvService;
    private EcgMetaData mEcgMetaData;

    static {
        System.loadLibrary("swm_ecg_algo");
    }

    static native void APPSEcgInitialForModeChange();
    static native int CalculateEcgMetaData(EcgMetaData ecgMetaData, int[] i32ECGRawBuffer);

    private SwmCore(Context context) {
        mContext = context;
        initMotionService();
        initEcgService();
        initHeartBeatService();
        initAcceleratorService();
        initBreathService();
        initHrvService();
        SwmDeviceController.init(context);
        initEmergencyCloudService();

        mProfilingHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Log.v("Profiling", "ECG service queue: " + mEcgDataQueue.size());
                Log.v("Profiling", "Accelerator service queue: " + mAccDataQueue.size());
                Log.v("Profiling", "Breath service queue: " + mBreathDataQueue.size());
                Log.v("Profiling", "Heart beat service queue: " + mHeartBeatServiceQueue.size());
                if (mEcgService.mDump != null) {
                    Log.v("Profiling", "ECG dump queue: " + mEcgService.mDump.mBuffer.size());
                }

                if (mHeartRateService.mDump != null) {
                    Log.v("Profiling", "Heart beat dump queue: " + mHeartRateService.mDump.mBuffer.size());
                }

                synchronized (PROFILING_LOCK) {
                    Log.v("Profiling", "bps: " + mRxSize);

                    double byteErrorRate = 0;

                    if (mProfilingListener != null)
                        mProfilingListener.onThroughput(mRxSize);

                    double lossRate = 0.0;
                    if(mTotalMotionPacketCount > 0) {
                        double motionLossRate = (mTotalMotionPacketCount - mReceivedMotionPacketCount) / mTotalMotionPacketCount * 100f;
                        Log.w("Profiling", "Motion packet loss: " + motionLossRate);
                        lossRate += motionLossRate;
                    }

                    if (mTotalEcgPacketCount > 0) {
                        double ecgLossRate = (mTotalEcgPacketCount - mReceivedEcgPacketCount) / mTotalEcgPacketCount * 100f;
                        Log.w("Profiling", "Ecg packet loss: " + ecgLossRate);
                        lossRate += ecgLossRate;
                    }

                    mTotalPacketLoss+=lossRate;

                    if(mProfilingListener != null)
                        mProfilingListener.onPacketLoss(mTotalPacketLoss);

                    if (mErrorByte > 0) {
                        byteErrorRate = mErrorByte / TIME_FRAME * 100f;

                        if (mProfilingListener != null)
                            mProfilingListener.onByteError(byteErrorRate);
                        mErrorByte = 0;
                    }

                    if (mEcgPackCount > 0) {
                        float avgLatency = mEcgLatency / mEcgPackCount;

                        if (mRecording) {
                            mDump.putData(new BleDumpData(mRxSize, lossRate, byteErrorRate, avgLatency));
                        }
                    }

                    mRxSize = 0;

                    mReceivedEcgPacketCount = 0;
                    mTotalEcgPacketCount = 0;

                    mReceivedMotionPacketCount = 0;
                    mTotalMotionPacketCount = 0;

                    mEcgPackCount = 0;
                    mEcgLatency = 0;
                }

                if (sRunning) {
                    mProfilingHandler.postDelayed(this, TIME_FRAME);
                }
            }
        }, TIME_FRAME);
    }

    Context getContext() {
        return mContext;
    }

    static synchronized SwmCore getIns() {
        return SWM_CORE;
    }

    void initSportService() {
        mSportService = new SportService();
    }

    private void checkBitError(byte[] data) {
        byte correctBitValue = data[18];
        int len = data.length;
        for (int i = 0; i < len; i++) {
            if (data[i] != correctBitValue)
                mErrorByte++;
        }
    }

    void onBleDataAvailable(BleData data) {

        if (data.uuid.equals(MotionBleProfile.DATA)) {

            onMotionBleDataAvailable(data);

            if(BuildConfig.ENGINEERING)
                synchronized (PROFILING_LOCK) {
                    mRxSize += data.rawData.length;

                    if (mFirstMotionPacket) {
                        mFirstMotionPacket = false;
                        mTotalMotionPacketCount++;
                    } else {
                        int diff = Math.abs(data.rawData[18] - mPreMotionIndex);
                        if (diff == 255)
                            mTotalMotionPacketCount++;
                        else
                            mTotalMotionPacketCount+=diff;
                    }

                    mReceivedMotionPacketCount++;

                    mPreMotionIndex = data.rawData[18];
            }

        }

        if (data.uuid.equals(EcgBleProfile.DATA)) {

            onEcgBleDataAvailable(data);
            onAccDataAvailable(data);
            onBreathDataAvailable(data);

            if (BuildConfig.ENGINEERING)
                synchronized (PROFILING_LOCK) {
                    long now = System.currentTimeMillis();
                    long latency = now - mLastReceiveTime;
                    mLastReceiveTime = now;
                    Log.d("Profiling", "Latency: " + latency);

                    mEcgLatency+=latency;

                    mRxSize += data.rawData.length;

                    if (mFirstEcgPacket) {
                        mFirstEcgPacket = false;
                        mTotalEcgPacketCount++;
                    } else {
                        int diff = Math.abs(data.rawData[6] - mPreEcgIndex);
                        if (diff == 255)
                            mTotalEcgPacketCount++;
                        else
                            mTotalEcgPacketCount+=diff;
                    }

                    mReceivedEcgPacketCount++;
                    mPreEcgIndex = data.rawData[6];
                }
        }
    }

    private void onEcgBleDataAvailable(BleData bleData) {
        SwmData rawData = SwmData.ecgDataFrom(bleData);
        mEcgDataQueue.offer(rawData);
        SwmData ecgData = new SwmData(SwmData.ECG, Arrays.copyOfRange(bleData.rawData, 10, 20));
        mHeartBeatServiceQueue.offer(ecgData);
    }

    void onMotionBleDataAvailable(BleData bleData){
        mMotionDataQueue.offer(SwmData.motionDataFrom(bleData));
    }

    void onAccDataAvailable(BleData bleData) {
        mAccDataQueue.offer(SwmData.accDataFrom(bleData));
    }

    void onBreathDataAvailable(BleData bleData) {
        mBreathDataQueue.offer(SwmData.breathDataFrom(bleData));
    }

    private void initEcgService() {
        mEcgService = new EcgService();
        mEcgDataQueue = new LinkedBlockingDeque<SwmData>();
        mEcgWorker = new Thread(new Runnable() {
            @Override
            public void run() {
                for (;;) {
                    if (sRunning) {
                        try {
                            SwmData swmData = mEcgDataQueue.take();
                            mEcgService.onSwmDataAvailable(swmData);
                        } catch (InterruptedException e) {
                            Log.e(LOG_TAG, e.getMessage(), e);
                        }
                    }
                }
            }
        });
        mEcgWorker.start();
    }

    private void initHeartBeatService() {
        if (mHeartRateService == null) {
            mHeartRateService = new HeartRateService();
            mHeartBeatServiceQueue = new LinkedBlockingQueue<>();
            Thread heartBeatQueueWorker = new Thread(new Runnable() {
                @Override
                public void run() {
                    for(;;) {
                        if (!sRunning)
                            return;

                        try {
                            SwmData swmData = mHeartBeatServiceQueue.take();
                            mHeartRateService.onSwmDataAvailable(swmData);
                        } catch (InterruptedException e) {
                            Log.e(LOG_TAG, e.getMessage(), e);
                        }
                    }
                }
            });
            heartBeatQueueWorker.start();
        }
    }

    synchronized SuperRunCloudService getSuperRunCloudService() {
        if (mSuperRunCloudService == null)
            mSuperRunCloudService = new SuperRunCloudService();
        return mSuperRunCloudService;
    }

    void initEmergencyCloudService() {
        mEmergencyCloudService = new EmergencyCloudService();
    }

    private void initAcceleratorService() {
        if (mAcceleratorService == null) {
            mAcceleratorService = new AcceleratorService();
            mAccDataQueue = new LinkedBlockingQueue<>();
            mAccWorker = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (;;) {
                        if (sRunning) {
                            try {
                                SwmData swmData = mAccDataQueue.take();
                                mAcceleratorService.onSwmDataAvailable(swmData);
                            } catch (InterruptedException e) {
                                Log.e(LOG_TAG, e.getMessage(), e);
                            }
                        }
                    }
                }
            });
            mAccWorker.start();
        }
    }

    private void initBreathService() {
        if (mBreathService == null) {
            mBreathService = new BreathService();
            mBreathDataQueue = new LinkedBlockingQueue<>();
            mBreathWorker = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (;;) {
                        if (sRunning) {
                            try {
                                SwmData swmData = mBreathDataQueue.take();
                                mBreathService.onSwmDataAvailable(swmData);
                            } catch (InterruptedException e) {
                                Log.e(LOG_TAG, e.getMessage(), e);
                            }
                        }
                    }
                }
            });
            mBreathWorker.start();
        }
    }

    private void initMotionService() {
        if(mMotionService == null) {
            mMotionService = new MotionService();
            mMotionDataQueue = new LinkedBlockingDeque<SwmData>();
            mMotionWorker = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (;;) {
                        if (sRunning) {
                            try {
                                SwmData swmData = mMotionDataQueue.take();
                                mMotionService.onSwmDataAvailable(swmData);
                            } catch (InterruptedException e) {
                                Log.e(LOG_TAG, e.getMessage(), e);
                            }
                        }
                    }
                }
            });
            mMotionWorker.start();
        }
    }

    EcgService getEcgService() {
        return mEcgService;
    }

    MotionService getMotionService() {
        return mMotionService;
    }

    AcceleratorService getAcceleratorService() {
        return mAcceleratorService;
    }

    BreathService getBreathService() {
        return mBreathService;
    }

    HeartRateService getHeartBeatService() {
        return mHeartRateService;
    }

    EmergencyCloudService getEmergencyCloudService() {
        return mEmergencyCloudService;
    }

    HrvService getHrvService() {
        return mHrvService;
    }

    static synchronized void init(Context context) {
        if (SWM_CORE == null) {
            SWM_CORE = new SwmCore(context);
        }
    }

    void stop() {
        sRunning = false;
        getEcgService().stop();
        getHeartBeatService().stop();
        getMotionService().stop();
        getBreathService().stop();
    }

    synchronized void startRecord() {
        if (mRecording)
            return;

        mRecording = true;
        if (mDump == null)
            mDump = new Dump("BLE");

        mDump.start();
        getEcgService().startRecord();
        getHeartBeatService().startRecord();
        getMotionService().startRecord();

        if (mTimer == null)
            mTimer = new Runnable() {
                @Override
                public void run() {
                    mElapse++;
                    int second = mElapse % 60;
                    int min = mElapse / 60 % 60;
                    int hour = mElapse / 60 / 60 % 24;
                    String time = String.format("%02d:%02d:%02d", hour, min, second);
                    if (mTimerListener != null) {
                        mTimerListener.onTick(time);
                        mHandler.postDelayed(this, 1000);
                    }
                }
            };

        if (mHandler == null)
            mHandler = new Handler();

        mElapse = 0;
        mHandler.postDelayed(mTimer, 1000);
    }

    void stopRecord() {
        if (!mRecording)
            return;

        mRecording = false;
        mHandler.removeCallbacks(mTimer);
        mElapse = 0;
        getEcgService().stopRecord();
        getHeartBeatService().stopRecord();
        getMotionService().stopRecord();
        mDump.stop();
    }

    void setTimerListener(TimerListener listener) {
       mTimerListener = listener;
    }

    void removeTimerListener(TimerListener listener) {
        if (mTimerListener == listener)
            mTimerListener = null;
    }

    boolean isRecording() {
        return mRecording;
    }

    synchronized SportService getSportService() {
        if (mSportService == null)
            initSportService();

        return mSportService;
    }

    void setProfilingListener(ProfilingListener listener) {
        mProfilingListener = listener;
    }

    void removeProfilingListener() {
        mProfilingListener = null;
    }

    private void initHrvService() {
        mHrvService = new HrvService();
    }

    void setHrvListener(HrvListener listener) {
        try {
            getHrvService().addListener(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void removeHrvListener(HrvListener listener) {
        getHrvService().removeListener(listener);
    }

    void setEcgMetaData(EcgMetaData ecgMetaData) {
        mEcgMetaData = ecgMetaData;
    }

    EcgMetaData getEcgMetaData() {
        return mEcgMetaData;
    }
}
