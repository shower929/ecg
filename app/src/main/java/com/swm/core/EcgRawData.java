package com.swm.core;

import java.util.List;

/**
 * Created by yangzhenyu on 2017/1/19.
 */

class EcgRawData {
    final List<Short> ecgData;

    EcgRawData(List<Short> ecgData) {
        this.ecgData = ecgData;
    }

}