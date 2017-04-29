package com.swm.sdk;

import android.view.View;

import com.swm.chart.SwmLineChart;
import com.swm.engineering.app.ServiceListener;
import com.swm.view.HeartRateView;
import com.swm.view.HrvView;
import com.swm.view.RecordButton;
import com.swm.view.TimerView;

/**
 * Created by yangzhenyu on 2017/4/28.
 */

public class HeartPresenter implements ServiceListener
                                    , HeartEngineRawOutput
                                    , View.OnClickListener{
    private final SwmLineChart lineChart;
    private final HeartRateView heartRateView;
    private final HrvView hrvView;
    private final RecordButton recordButton;
    private final TimerView timerView;

    HeartPresenter(SwmLineChart lineChart, HeartRateView heartRateView, HrvView hrvView, RecordButton recordButton, TimerView timerView) {
        this.lineChart = lineChart;
        this.heartRateView = heartRateView;
        this.hrvView = hrvView;
        this.recordButton = recordButton;
        this.timerView = timerView;
    }

    @Override
    public void onRawDataAvailable(float value) {
        lineChart.line(value);
    }

    @Override
    public void onHeartRateAvailable(int heartRate) {
        heartRateView.setHeartRate(heartRate);
    }

    @Override
    public void onBreathRateAvailable(int breathRate) {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.swm_record:
            handleRecordEvent();
            break;
        }
        recordButton.setText(getString(R.string.swm_stop));
        mRecordBtn.setBackground(getResources().getDrawable(R.drawable.swm_stop_button));
    }


    private boolean isRecording() {
        return SwmCore.getIns().isRecording()
                && mLocationService.isRecording();
    }

    private void handleRecordEvent() {
        if (!isRecording()) {
            recordButton.setText(getString(R.string.swm_stop));
            recordButton.setBackground(getResources().getDrawable(R.drawable.swm_stop_button));
            SwmCore.getIns().setTimerListener(this);
            SwmCore.getIns().startRecord();

            mElapseView.setText("00:00:00");
            mElapseView.setVisibility(View.VISIBLE);
        } else {
            mRecordBtn.setText(getString(R.string.swm_record));
            mRecordBtn.setBackground(getResources().getDrawable(R.drawable.swm_cta_button));
            SwmCore.getIns().removeTimerListener(this);
            SwmCore.getIns().stopRecord();
            mLocationService.stopRecording();

            mElapseView.setVisibility(View.INVISIBLE);
        }
    }
}
