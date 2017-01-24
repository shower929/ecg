package com.swm.information;

import com.swm.core.InformationData;

/**
 * Created by yangzhenyu on 2017/1/24.
 */

public interface InformationListener {
    void onInformationDataAvailable(InformationData informationData);
}
