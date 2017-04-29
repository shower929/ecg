package com.swm.running;

import com.swm.chart.RealtimeLineChart;
import com.swm.chart.RealtimeSingleLineChart;
import com.swm.chart.SignalGenerator;

/**
 * Created by yangzhenyu on 2017/4/7.
 */

public class RunningPresenter implements RunningPluginListener{
    private final RealtimeSingleLineChart lineChart;

    public RunningPresenter(RealtimeSingleLineChart lineChart) {
        this.lineChart = lineChart;
    }


    @Override
    public void onVdotChanged(double vdot) {
        lineChart.offerData((float) vdot);
    }
}
