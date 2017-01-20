package com.swm.core;

import java.util.List;

/**
 * Created by yangzhenyu on 2016/10/5.
 */

class WindowedEcgData {
    final List<Short> g_i32ECGInBuffer;

    WindowedEcgData(List<Short> g_i32ECGInBuffer) {
        this.g_i32ECGInBuffer = g_i32ECGInBuffer;
    }
}
