package com.swm.hrv;

import com.swm.core.HrvData;

/**
 * Created by yangzhenyu on 2016/12/13.
 */

public interface HrvListener {
    void onHrvDataAvailable(HrvData hrvData);
}
