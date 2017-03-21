package com.swm.ble;

import android.view.View;
import android.widget.TextView;

import com.swm.sdk.SwmData;
import com.swm.sdk.SwmListener;

/**
 * Created by yangzhenyu on 2017/3/16.
 */

public class Presenter implements PacketLossModelListener, View.OnClickListener, SwmListener{
    private final TextView packetCount;
    private final TextView packetLossView;
    private final SwmToggle toggle;

    public Presenter(TextView packetCount, TextView packetLossView, SwmToggle toggle) {
        this.packetCount = packetCount;
        this.packetLossView = packetLossView;
        this.toggle = toggle;
    }

    @Override
    public void onArrival(final int count) {
        packetCount.post(new Runnable() {
            @Override
            public void run() {
                packetCount.setText(String.valueOf(count));
            }
        });
    }

    @Override
    public void onPacketLoss(final int loss) {
        packetLossView.post(new Runnable() {
            @Override
            public void run() {
                packetLossView.setText(String.valueOf(loss));
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnectStateChanged(int state) {

    }

    @Override
    public void onDataAvailable(SwmData data) {

    }

    @Override
    public void onServiceStateChange(Service service, boolean enable) {
        if (enable)
            toggle.on();
        else {
            toggle.off();
        }
    }
}
