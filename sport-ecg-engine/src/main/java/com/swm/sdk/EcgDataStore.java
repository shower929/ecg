package com.swm.sdk;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by yangzhenyu on 2017/5/1.
 */

class EcgDataStore extends SwmDataStore {

    private static EcgDataStore ECG_DATA_SOURCE;

    private String fileName;
    private File file;
    private FileOutputStream fileOutput;
    private FileChannel fileChannel;

    private Thread thread;
    private Runnable flush;
    private long clock;


    private EcgDataStore() {

        //@TODO Write disk dump
        //initDiskStorage();
        //initDataFlush();
    }

    synchronized static EcgDataStore getIns() {
        if(ECG_DATA_SOURCE == null)
            ECG_DATA_SOURCE = new EcgDataStore();

        return ECG_DATA_SOURCE;
    }

    void setClock(long clock) {
        this.clock = clock;
    }

    /**
    private void initDataFlush() {
        flush = new Runnable() {
            @Override
            public void run() {
                for(;;) {
                    if(!running)
                        return;

                    buffer.flip();

                    try {
                        fileChannel.write(buffer);
                        while(buffer.hasRemaining()) {
                            buffer.compact();
                            fileChannel.write(buffer);
                        }
                        buffer.clear();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(clock);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread = new Thread(flush);
        thread.start();
    }
    */
    private void initDiskStorage() {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String parsed = format.format(now);
        fileName = parsed.toString();

        File sdCard = Environment.getExternalStorageDirectory();
        try {
            File dir = new File(sdCard.getPath() + "/SWData/");
            if(!dir.exists())
                dir.mkdir();

            file = new File(dir,  fileName + ".raw");
            file.createNewFile();

            file.setReadable(true, false);
            fileOutput = new FileOutputStream(file);
            fileChannel = fileOutput.getChannel();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
