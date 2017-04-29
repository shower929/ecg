package com.swm.sdk;

import android.view.View;

import com.swm.chart.SwmLineChart;
import com.swm.core.view.LocationView;

/**
 * Created by yangzhenyu on 2017/4/28.
 */

class MotionPresenter implements MotionEngineRawOutput
                                , View.OnClickListener{
    private final SwmLineChart acceleratorView;
    private final SwmLineChart gyroView;
    private final SwmLineChart magneticView;
    private final LocationView locationView;

    MotionPresenter(SwmLineChart acceleratorView, SwmLineChart gyroView, SwmLineChart magneticView, LocationView locationView) {
        this.acceleratorView = acceleratorView;
        this.gyroView = gyroView;
        this.magneticView = magneticView;
        this.locationView = locationView;
    }

    @Override
    public void onMotionDataAvailable(MotionData motionData) {

    }


    @Override
    public void onClick(View v) {
        mLocationService.startRecording();
    }
}
