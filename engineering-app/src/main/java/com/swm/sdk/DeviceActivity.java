package com.swm.sdk;

import android.os.Bundle;
import android.widget.TextView;

import com.swm.engineering.app.SwmBaseActivity;
import com.swm.heart.*;

/**
 * Created by yangzhenyu on 2017/4/28.
 */

class DeviceActivity extends SwmBaseActivity implements ProfilingListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        packetLossView = (TextView) findViewById(com.swm.heart.R.id.swm_packetloss_view);
        throughPutView = (TextView) findViewById(com.swm.heart.R.id.swm_throughput_view);
    }

    @Override
    public void onPacketLoss(final double rate) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPacketLossView.setText(String.valueOf(rate) + "%");
            }
        });
    }

    @Override
    public void onThroughput(final double bps) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mThroughputView.setText(String.valueOf(bps) + " bps");
            }
        });
    }

    @Override
    public void onLatency(double latency) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        SwmCore.getIns().removeProfilingListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SwmCore.getIns().setProfilingListener(this);
    }
}
