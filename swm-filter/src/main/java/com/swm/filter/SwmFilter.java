package com.swm.filter;

import com.swm.sdk.EcgData;

import java.nio.ByteBuffer;
import java.util.Deque;
import java.util.List;

/**
 * Created by yangzhenyu on 2017/5/8.
 */

public interface SwmFilter {
    public void filter(EcgData ecgData);
    public void reset();
}
