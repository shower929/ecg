package com.swm.core;

import android.os.Handler;
import android.util.Log;

import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by yangzhenyu on 2016/9/27.
 */
class EcgService {
    private static final String LOG_TAG = "ECG";
    private LinkedBlockingDeque<EcgData> mListenerQueue;
    private Vector<EcgListener> mListeners;
    private Thread mCallbackWorker;
    private IIRFilter mIirFilter;
    private LinkedBlockingDeque<RawData> IIRQueue;
    private FIRFilter mFirFilter;
    private LinkedBlockingDeque<RawData> FIRQueue;
    private static final int FILTER_NUM = 31;
    private static final double IIR_COEFF = 0.992;
    private LinkedBlockingQueue<RawData> mOutputBuffer;
    private EcgDataWorker mEcgDataWorker;
    private static final Object LISTENER_LOCK = new Object();

    Dump mDump;

    private short gi16_accECGFIRCount = 0;
    private short gi16_ECG_250SPS_LPF_31Steps[] = {
        99,    306,    592,    848,    916,    659,     52,   -749,  -1421,
                -1569,   -887,    686,   2884,   5166,   6886,   7526,   6886,   5166,
                2884,    686,   -887,  -1569,  -1421,   -749,     52,    659,    916,
                848,    592,    306,     99
    };

    private class RawData extends DumpData{
        private final short processData;
        private int pFilteredOut;
        private final double fIIRFilterCoeff;

        private RawData(short processData, double fIIRFilterCoeff) {
            this.processData = processData;
            this.fIIRFilterCoeff = fIIRFilterCoeff;
        }

        @Override
        public byte[] dump() {
            return String.valueOf(pFilteredOut).getBytes();
        }

    }

    private class EcgDataWorker extends Thread {
        private static final int SAMPLE_RATE = 20;
        private static final int ECG_CLOCK = 1000 / SAMPLE_RATE;    //ms
        private EcgData ecgData;
        private final Object LOCK = new Object();
        private Handler mTimer = new Handler();

        private EcgDataWorker() {
        }

        private Runnable mFlush;

        @Override
        public void run() {
            super.run();
            for (;;) {
                if (!SwmCore.sRunning)
                    return;

                try {
                    RawData rawData = mOutputBuffer.take();
                    if (mFlush == null) {
                        ecgData = new EcgData();
                        mFlush = new Runnable() {
                            @Override
                            public void run() {
                                synchronized (LOCK) {
                                    mListenerQueue.offer(ecgData);
                                    ecgData = new EcgData();
                                    mTimer.postDelayed(this, ECG_CLOCK);
                                }
                           }
                        };
                        mTimer.postDelayed(mFlush, ECG_CLOCK);
                    }

                    synchronized (LOCK) {
                        ecgData.values.add(rawData.pFilteredOut);
                    }

                } catch (InterruptedException e) {
                    Log.d(LOG_TAG, e.getMessage(), e);
                }

            }
        }
    }

    private class FIRFilter extends Thread {
        private short i;
        private int acc = 0;
        private int gi16_FirstECGFIRFlag = 1;
        private short[] filterCoeff = gi16_ECG_250SPS_LPF_31Steps;
        private short[] gi16_ECGworkingBuf = new short[FILTER_NUM];

        private FIRFilter() {
            super("FIRFilter");
        }

        @Override
        public void run() {
            super.run();
            for(;;) {
                if (!SwmCore.sRunning)
                    return;
                try {
                    RawData rawData = FIRQueue.take();
                    if (gi16_FirstECGFIRFlag == 1) {
                        for (int i = 0; i < FILTER_NUM; i++) {
                            gi16_ECGworkingBuf[i] = 0;
                        }
                        gi16_FirstECGFIRFlag = 0;
                        gi16_accECGFIRCount = 0;

                    }

                    if (gi16_accECGFIRCount < FILTER_NUM) {
                        gi16_ECGworkingBuf[gi16_accECGFIRCount] = rawData.processData;
                        rawData.pFilteredOut = rawData.processData;
                        gi16_accECGFIRCount++;
                        mOutputBuffer.offer(rawData);
                    } else {
                        acc = 0;
                        for (int i = 0; i < FILTER_NUM; i++) {
                            acc += gi16_ECGworkingBuf[i] * filterCoeff[i];
                            if (i < FILTER_NUM - 1) {
                                gi16_ECGworkingBuf[i] = gi16_ECGworkingBuf[i+1];
                            } else {
                                gi16_ECGworkingBuf[i] = rawData.processData;
                            }
                        }
                        acc = acc > 0x3fffffff ? 0x3fffffff : acc;
                        acc = acc < -0x40000000 ? -0x40000000 : acc;
                        acc = acc >> 16;
                        rawData.pFilteredOut = (short) acc;
                        mOutputBuffer.offer(rawData);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class IIRFilter extends Thread {
        private int i32PrevSample = 0;
        private int i32PrevDCSample = 0;
        private int gi16_FirstECGIIRFlag = 1;

        private IIRFilter () {
            super("IIRFilter");
        }

        @Override
        public void run() {
            for (;;) {
                if (!SwmCore.sRunning)
                    return;

                try {
                    RawData rawData = IIRQueue.take();
                    if (gi16_FirstECGIIRFlag == 1) {
                        i32PrevDCSample = 0;
                        gi16_FirstECGIIRFlag = 0;
                        i32PrevSample = rawData.processData;
                        rawData.pFilteredOut = (short) i32PrevDCSample;
                    }

                    i32PrevDCSample = (int) (rawData.processData - i32PrevSample + rawData.fIIRFilterCoeff * i32PrevDCSample);
                    i32PrevSample = rawData.processData;
                    rawData.pFilteredOut = (short) i32PrevDCSample;
                    //FIRQueue.offer(rawData);
                    mOutputBuffer.offer(rawData);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    EcgService() {
        IIRQueue = new LinkedBlockingDeque<>();
        mOutputBuffer = new LinkedBlockingQueue<>();
        FIRQueue = new LinkedBlockingDeque<>();
        mListenerQueue = new LinkedBlockingDeque<>();

        mIirFilter = new IIRFilter();
        mIirFilter.start();

        mFirFilter = new FIRFilter();
        mFirFilter.start();

        mCallbackWorker = new Thread(new Runnable() {
            @Override
            public void run() {
                for (;;) {
                    if (!SwmCore.sRunning)
                        return;

                    try {
                        EcgData ecgData = mListenerQueue.take();
                        if (mListeners == null) {
                            continue;
                        }
                        synchronized (LISTENER_LOCK) {
                            for(EcgListener listener : mListeners) {
                                listener.onEcgDataAvailable(ecgData);
                            }
                        }
                    } catch (InterruptedException e) {
                        Log.d(LOG_TAG, e.getMessage(), e);
                    }

                }
            }
        });
        mCallbackWorker.start();
        mEcgDataWorker = new EcgDataWorker();
        mEcgDataWorker.start();
    }

    void onSwmDataAvailable(SwmData swmData) {

        int len = swmData.value.length;
        for(int i = 10; i < len - 1; i = i + 2) {

            short ecg = ((short) (((swmData.value[i+1] & 0xFF) << 8) | (swmData.value[i] & 0xFF)));
            RawData rawData = new RawData(ecg, IIR_COEFF);

            if(mListeners != null && mListeners.size() > 0)
                IIRQueue.offer(rawData);

        }
        if(mDump != null) {
            mDump.putData(swmData);
        }
    }

    void registerListener(EcgListener listener) throws Exception {
        synchronized (LISTENER_LOCK) {
            if (mListeners == null) {
                mListeners = new Vector<>();
            }

            if(mListeners.contains(listener))
                throw new Exception("Duplicated register listener");

            mListeners.add(listener);
        }
    }

    void removeListener(EcgListener listener) {
        mListeners.remove(listener);
    }

    void startRecord() {
        mDump = new Dump("raw_Ecg");
        mDump.setWithoutComma(true);
        mDump.start();
    }

    void stopRecord() {
        mDump.stop();
        mDump = null;
    }

    boolean isRecording() {
        return mDump != null;
    }

    void stop() {
        mCallbackWorker.interrupt();

        if (mDump != null)
            mDump.stop();

        mEcgDataWorker.interrupt();

    }
}
