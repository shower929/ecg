package com.swm.sdk;

import com.swm.chart.LineDataSource;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

/**
 * Created by yangzhenyu on 2017/5/1.
 */

public class EcgDataSource extends LineDataSource {
    private Thread worker;
    private Runnable generator;

    public EcgDataSource() {}
    @Override
    public int read(ByteBuffer buffer) {
        return EcgDataStore.getIns().read(buffer);
    }
}
