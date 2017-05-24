package com.swm.sdk;

import android.support.annotation.Nullable;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Created by yangzhenyu on 2017/5/1.
 */

public class SwmDataStore {

    private Deque<Double> data;
    private static final int DOUBLE_SIZE = Double.SIZE / Byte.SIZE;

    synchronized void putData(double value) {
        if (data == null)
            data = new ArrayDeque<>();

        synchronized (data) {
            data.add(Double.valueOf(value));
            while(data.size() > 2048)
                data.remove();

        }
    }

    synchronized int read(ByteBuffer dst) {
        if(data == null)
            return 0;

        synchronized (data) {
            int count;
            int limit = data.size();

            int bufferCapacity = dst.capacity() / DOUBLE_SIZE;

            for(count = 0; count < limit && dst.position() / DOUBLE_SIZE < bufferCapacity; count++) {
                Double value = data.poll();

                dst.putDouble(value);
            }

            return count;
        }
    }
}
