package com.swm.ble;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.swm.sdk.BleListener;
import com.swm.sdk.BleService;
import com.swm.sdk.SwmData;
import com.swm.sdk.SwmDevice;
import com.swm.sdk.SwmDeviceModule;
import com.swm.sdk.SwmListener;
import com.swm.sdk.SwmServiceProvider;

import java.util.UUID;

public class MainActivity extends AppCompatActivity implements SwmListener
                                                                , BleListener
                                                                , View.OnClickListener{
    private static final UUID ECG = UUID.fromString("F000AA71-0451-4000-B000-000000000000");
    private static final UUID MOTION = UUID.fromString("F000AA81-0451-4000-B000-000000000000");

    private BleService service;
    private SwmDevice device;

    private DeviceStatus deviceStatus;

    private EcgPacketLossModel ecgPacketLossModel;
    private MotionPacketLossModel motionPacketLossModel;

    private Presenter ecgPresenter;
    private Presenter motionPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ecgPacketLossModel = new EcgPacketLossModel();
        motionPacketLossModel = new MotionPacketLossModel();
        device = SwmDeviceModule.getIns().getDevice();

        service = SwmServiceProvider.getIns().internal();
        service.setBleListener(this);

        setContentView(R.layout.activity_main);

        TextView ecgIndexView = (TextView) findViewById(R.id.ble_ecg_index);
        TextView ecgPacketLossView = (TextView) findViewById(R.id.ble_ecg_packet_loss);
        SwmToggle ecgToggle = (SwmToggle) findViewById(R.id.ble_ecg_toggle);
        ecgPresenter = new Presenter(ecgIndexView, ecgPacketLossView, ecgToggle);
        ecgToggle.setOnClickListener(this);

        TextView motionIndexView = (TextView) findViewById(R.id.ble_motion_index);
        TextView motionPacketLossView = (TextView) findViewById(R.id.ble_motion_packet_loss);
        SwmToggle motionToggle = (SwmToggle) findViewById(R.id.ble_motion_toggle);
        motionPresenter = new Presenter(motionIndexView, motionPacketLossView, motionToggle);
        motionToggle.setOnClickListener(this);

        deviceStatus = (DeviceStatus) findViewById(R.id.ble_device_status);
    }

    @Override
    protected void onStart() {
        super.onStart();
        device.setListener(this);
        ecgPacketLossModel.setListener(ecgPresenter);
        motionPacketLossModel.setListener(motionPresenter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        device.setListener(null);
        ecgPacketLossModel.setListener(null);
        motionPacketLossModel.setListener(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SwmDeviceModule.getIns().disconnect();
    }

    @Override
    public void onConnectStateChanged(final int state) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (state == SwmListener.CONNECTED)
                    deviceStatus.active();
                else if (state == SwmListener.DISCONNECTED)
                    deviceStatus.inactive();
            }
        });

    }

    @Override
    public void onDataAvailable(SwmData data) {

    }

    @Override
    public void onServiceStateChange(SwmListener.Service service, boolean enable) {
        if (service == Service.ECG) {
            ecgPresenter.onServiceStateChange(service, enable);
            ecgPacketLossModel.reset();
        } else if(service == Service.MOTION) {
            motionPresenter.onServiceStateChange(service, enable);
            motionPacketLossModel.reset();
        }

    }

    @Override
    public void onRawDataAvailable(UUID uuid, byte[] data) {
        if (uuid.equals(ECG)) {
            ecgPacketLossModel.offer(data);
        }

        if (uuid.equals(MOTION)) {
            motionPacketLossModel.offer(data);
        }
    }

    private void toggleEcg() {
        boolean enable = !device.isEcgEnable();
        device.enableEcg(enable);
    }

    private void toggleMotion() {
        boolean enable = !device.isMotionEnable();
        device.enableMotion(enable);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ble_ecg_toggle) {
            toggleEcg();
        } else if (v.getId() == R.id.ble_motion_toggle) {
            toggleMotion();
        }
    }
}
