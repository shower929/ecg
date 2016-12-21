package com.swm.core;

import android.os.Environment;
import android.util.Log;

import com.swm.motion.MotionListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by yangzhenyu on 2016/9/26.
 */

 class MotionService {
    private static final String LOG_TAG = "MotionService";
    private LinkedBlockingDeque<MotionData> mMotionDataQueue;
    private Thread mMotionWorker;
    private MotionListener mListener;

    private BlockingQueue<SwmData> mDumpQueue;
    private DumpWorker mDumpWorker;
    private boolean mRecording = false;

    private class DumpWorker extends Thread {
        private File mFile;
        private FileOutputStream mFileOutput;
        private String mFileName;
        private ByteBuffer mWriteBuffer;
        private FileChannel mFileChannel;

        DumpWorker() {
            Date now = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String parsed = format.format(now);
            mFileName = "Raw_Mov_"+ parsed.toString();
        }

        @Override
        public void run() {
            super.run();
            for (;;) {
                if (!SwmCore.sRunning)
                    return;

                if (!mRecording)
                    return;

                try {
                    SwmData swmData = mDumpQueue.take();
                    mWriteBuffer.put(swmData.value);
                    mWriteBuffer.put(",".getBytes());
                    mWriteBuffer.flip();
                    while(mWriteBuffer.remaining() > 0)
                        mFileChannel.write(mWriteBuffer);
                    mWriteBuffer.clear();
                } catch (InterruptedException e) {
                    Log.d(LOG_TAG, e.getMessage(), e);
                } catch (IOException e) {
                    Log.d(LOG_TAG, e.getMessage(), e);
                }
            }
        }

        void init() {

            File sdCard = Environment.getExternalStorageDirectory();
            try {
                mFile = new File(sdCard.getPath() + "/SWData"  +"/",  mFileName + ".txt");
                mFile.createNewFile();
                mFile.setReadable(true, false);
                mFileOutput = new FileOutputStream(mFile);
                mFileChannel = mFileOutput.getChannel();
                mWriteBuffer = ByteBuffer.allocate(48);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public class MotionWorker implements Runnable {

        @Override
        public void run() {
            for (;;) {
                try {
                    MotionData motioData = mMotionDataQueue.take();
                    if (mListener != null) {
                        mListener.onMotionDataAvailable(motioData);
                    }
                } catch (InterruptedException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                }
            }
        }
    }


    MotionService() {
        mMotionDataQueue = new LinkedBlockingDeque<>();
        mMotionWorker = new Thread(new MotionWorker());
        mMotionWorker.start();
    }

    void onSwmDataAvailable(SwmData swmData) {
        MotionData motionData = new MotionData(
                swmData.value[1] & 0xFF << 8 | swmData.value[0] & 0xFF
                , swmData.value[3] & 0xFF << 8 | swmData.value[2] & 0xFF
                , swmData.value[5] & 0xFF << 8 | swmData.value[4] & 0xFF
                , swmData.value[7] & 0xFF << 8 | swmData.value[6] & 0xFF
                , swmData.value[9] & 0xFF << 8 | swmData.value[8] & 0xFF
                , swmData.value[11] & 0xFF << 8 | swmData.value[10] & 0xFF
                , swmData.value[13] & 0xFF << 8 | swmData.value[12] & 0xFF
                , swmData.value[15] & 0xFF << 8 | swmData.value[14] & 0xFF
                , swmData.value[17] & 0xFF << 8 | swmData.value[16] & 0xFF
                , swmData.value[18]
        );
        mMotionDataQueue.offer(motionData);
        if (mRecording) {
            mDumpQueue.offer(swmData);
        }
    }

    void setListener(MotionListener motionListener) {
        mListener = motionListener;
    }

    void removeListener() {
        mListener = null;
    }

    void startRecord() {
        mDumpQueue = new LinkedBlockingQueue<>();
        mRecording = true;
        mDumpWorker = new DumpWorker();
        mDumpWorker.init();
        mDumpWorker.start();
    }

    void stopRecord() {
        //mDumpWorker.stop();
        mRecording = false;
    }

    boolean isRecording() {
        return mRecording;
    }

    void stop() {

        if (mDumpWorker != null)
            mDumpWorker.interrupt();

        mMotionWorker.interrupt();
    }
}
