package com.swm.ble;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.swm.sdk.BleListener;
import com.swm.sdk.BleEngine;
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
    private static final UUID PRESSURE = UUID.fromString("F000AA41-0451-4000-B000-000000000000");

    private BleEngine service;
    private SwmDevice device;

    private DeviceStatus deviceStatus;

    private EcgPacketLossModel ecgPacketLossModel;
    private MotionPacketLossModel motionPacketLossModel;
    private PressurePacketLossModel pressurePacketLossModel;

    private Presenter ecgPresenter;
    private Presenter motionPresenter;
    private Presenter pressurePresenter;

    private SwmToggle swmServiceToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ecgPacketLossModel = new EcgPacketLossModel();
        motionPacketLossModel = new MotionPacketLossModel();
        pressurePacketLossModel = new PressurePacketLossModel();

        device = SwmDeviceModule.getIns().getDevice();

        service = SwmServiceProvider.getIns().internal();
        service.setBleListener(this);

        setContentView(R.layout.activity_main);

        swmServiceToggle = (SwmToggle) findViewById(R.id.ble_swm_service_toggle);
        swmServiceToggle.setOnClickListener(this);

        TextView ecgPacketCount = (TextView) findViewById(R.id.ble_ecg_packet_count);
        TextView ecgPacketLossView = (TextView) findViewById(R.id.ble_ecg_packet_loss);
        SwmToggle ecgToggle = (SwmToggle) findViewById(R.id.ble_ecg_toggle);
        ecgPresenter = new Presenter(ecgPacketCount, ecgPacketLossView, ecgToggle);
        ecgToggle.setOnClickListener(this);

        TextView motionCount = (TextView) findViewById(R.id.ble_motion_count);
        TextView motionPacketLossView = (TextView) findViewById(R.id.ble_motion_packet_loss);
        SwmToggle motionToggle = (SwmToggle) findViewById(R.id.ble_motion_toggle);
        motionPresenter = new Presenter(motionCount, motionPacketLossView, motionToggle);
        motionToggle.setOnClickListener(this);

        TextView pressureCount = (TextView) findViewById(R.id.ble_pressure_count);
        TextView pressurePacketLoss = (TextView) findViewById(R.id.ble_pressure_packet_loss);
        SwmToggle pressureToggle = (SwmToggle) findViewById(R.id.ble_pressure_toggle);
        pressurePresenter = new Presenter(pressureCount, pressurePacketLoss, pressureToggle);
        pressureToggle.setOnClickListener(this);

        deviceStatus = (DeviceStatus) findViewById(R.id.ble_device_status);
    }

    @Override
    protected void onStart() {
        super.onStart();
        device.setListener(this);
        ecgPacketLossModel.setListener(ecgPresenter);
        motionPacketLossModel.setListener(motionPresenter);
        pressurePacketLossModel.setListener(pressurePresenter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        device.setListener(null);
        ecgPacketLossModel.setListener(null);
        motionPacketLossModel.setListener(null);
        pressurePacketLossModel.setListener(null);
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
        } else if (service == Service.PRESSURE) {
            pressurePresenter.onServiceStateChange(service, enable);
            pressurePacketLossModel.reset();
        }

        if (device.isEcgServiceEnable() || device.isMotionServiceEnable() || device.isPressureServiceEnable())
            swmServiceToggle.on();
        else
            swmServiceToggle.off();

    }

    @Override
    public void onRawDataAvailable(UUID uuid, byte[] data) {
        if (uuid.equals(ECG)) {
            ecgPacketLossModel.offer(data);
        } else if (uuid.equals(MOTION)) {
            motionPacketLossModel.offer(data);
        } else if (uuid.equals(PRESSURE)) {
            pressurePacketLossModel.offer(data);
        }
    }

    private void toggleEcg() {
        boolean enable = !device.isEcgServiceEnable();
        try {
            device.enableEcgService(enable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toggleMotion() {
        boolean enable = !device.isMotionServiceEnable();
        try {
            device.enableMotionService(enable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void togglePressure() {
        boolean enable = !device.isPressureServiceEnable();
        try {
            device.enablePressureService(enable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toggleSwmService() {
        boolean enable = device.isEcgServiceEnable() || device.isMotionServiceEnable() || device.isPressureServiceEnable();

        try {
            device.enableEcgService(!enable);
            device.enableMotionService(!enable);
            device.enablePressureService(!enable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ble_ecg_toggle) {
            toggleEcg();
        } else if (v.getId() == R.id.ble_motion_toggle) {
            toggleMotion();
        } else if (v.getId() == R.id.ble_pressure_toggle) {
            togglePressure();
        } else if (v.getId() == R.id.ble_swm_service_toggle) {
            toggleSwmService();
        }
    }
}
