package com.swm.sdk;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by yangzhenyu on 2016/10/25.
 */

public class Dump<T extends DumpData> {
    private File mFile;
    private FileOutputStream mFileOutput;
    private String mFileName;
    private ByteBuffer mWriteBuffer;
    private FileChannel mFileChannel;
    public BlockingQueue<T> mBuffer;
    private boolean mRecording = false;
    private DumpWorker mDumpWorker;
    private boolean mWithoutComma = false;

    private class DumpWorker extends Thread {

        DumpWorker() {
            Date now = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String parsed = format.format(now);
            mFileName = mFileName + "_" + parsed.toString();
        }

        void init() {
            File sdCard = Environment.getExternalStorageDirectory();
            try {
                File dir = new File(sdCard.getPath() + "/SWData/");
                if(!dir.exists())
                    dir.mkdir();

                mFile = new File(dir,  mFileName + ".txt");
                mFile.createNewFile();

                mFile.setReadable(true, false);
                mFileOutput = new FileOutputStream(mFile);
                mFileChannel = mFileOutput.getChannel();

                mWriteBuffer = ByteBuffer.allocate(128);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void run() {
            super.run();
            for (;;) {

                if (!mRecording)
                    break;

                try {
                    T t = mBuffer.take();
                    mWriteBuffer.put(t.dump());
                    if (!mWithoutComma)
                        mWriteBuffer.put(",".getBytes());
                    mWriteBuffer.flip();
                    mFileChannel.write(mWriteBuffer);

                    while(mWriteBuffer.hasRemaining()) {
                        mWriteBuffer.compact();
                        mFileChannel.write(mWriteBuffer);
                    }
                    mWriteBuffer.clear();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                mFileChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    Dump(String fileName) {
        mFileName = fileName;
        mBuffer = new LinkedBlockingQueue<>();
    }

    void setWithoutComma(boolean withoutComma) {
        mWithoutComma = withoutComma;
    }

    synchronized void start() {
        if (mRecording)
            return;

        mRecording = true;
        mBuffer = new LinkedBlockingQueue<T>();
        mDumpWorker = new DumpWorker();
        mDumpWorker.init();
        mDumpWorker.start();
    }

    synchronized void stop() {
        mRecording = false;
        mDumpWorker.interrupt();
        mBuffer = null;
    }

    public void putData(T data) {
        mBuffer.offer(data);
    }
}
