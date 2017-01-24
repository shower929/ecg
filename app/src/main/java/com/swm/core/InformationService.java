package com.swm.core;

import com.swm.information.InformationListener;

/**
 * Created by yangzhenyu on 2017/1/24.
 */

class InformationService implements SwmDataListener{
    InformationListener mListener;

    @Override
    public synchronized void onSwmDataAvailable(SwmData swmData) {
        if (mListener == null)
            return;

        InformationData informationData = new InformationData(new String(swmData.value));
        mListener.onInformationDataAvailable(informationData);
    }

    synchronized void setListener(InformationListener listener) {
        mListener = listener;
    }

    synchronized void removeListener() {
        mListener = null;
    }
}
