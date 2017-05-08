package com.swm.sdk;

import com.swm.chart.LineDataSource;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

/**
 * Created by yangzhenyu on 2017/5/3.
 */

public class BreathDataSource extends LineDataSource {
    @Override
    public int read(ByteBuffer buffer) {
        return 0;
    }
}
