package com.swm.core;

import android.location.Location;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.swm.heartbeat.HeartRateListener;
import com.swm.motion.MotionListener;
import com.swm.sdk.EcgData;
import com.swm.sdk.HeartRateData;
import com.swm.sdk.MotionData;

import java.util.concurrent.BlockingQueue;

/**
 * Created by yangzhenyu on 2016/10/29.
 */

class SuperRunCloudService implements HeartRateListener, EcgListener, MotionListener{
    private int mHeartRate;
    private DatabaseReference mHeartRateRef;
    private DatabaseReference mEcgRef;
    private DatabaseReference mMotionRef;
    private DatabaseReference mLocationRef;
    private MotionData mMotionData;
    private Location mLocation;
    private BlockingQueue<EcgData> mEcgQueue;
    private Thread mEcgWorker;
    private boolean mMonitoring = false;

    SuperRunCloudService() {

    }

    @Override
    public void onHeartRateDataAvailable(HeartRateData heartRateData) {
        synchronized (mHeartRateRef) {
            if (mHeartRate == heartRateData.heartRate)
                return;

            mHeartRate = heartRateData.heartRate;

            String key = mHeartRateRef.push().getKey();
            mHeartRateRef.child(key).child("heart_rate").setValue(heartRateData.heartRate);
            mHeartRateRef.child(key).child("timestamp").setValue(System.currentTimeMillis());
        }
    }

    synchronized void startMonitor(FirebaseUser user) {
        mMonitoring = true;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.setLogLevel(Logger.Level.DEBUG);
        DatabaseReference usersRef = database.getReference("users");
        usersRef.child("users").child(user.getUid()).child("name").setValue(user.getDisplayName());
        usersRef.child("users").child(user.getUid()).child("email").setValue(user.getEmail());

        mHeartRateRef = database.getReference("heart_rate").child(user.getUid());


        //mMotionRef = database.getReference("motion").child(user.getUid());
        mLocationRef = database.getReference("location").child(user.getUid());

        try {
            SwmCore.getIns().getHeartRateService().addListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //SwmCore.getIns().getMotionService().setListener(this);
    }

    @Override
    public void onEcgDataAvailable(EcgData ecgData) {
        String key = mEcgRef.push().getKey();
        mEcgRef.child(key).child("ecg").setValue(ecgData.values);
        mEcgRef.child(key).child("timestamp").setValue(System.currentTimeMillis());
    }

    @Override
    public void onMotionDataAvailable(MotionData motionData) {
        if(mMotionData != null && mMotionData.equals(motionData))
            return;

        mMotionData = motionData;
        String key = mMotionRef.push().getKey();
        mMotionRef.child(key).child("accelerator").child("x").setValue(motionData.accelerator.x);
        mMotionRef.child(key).child("accelerator").child("y").setValue(motionData.accelerator.y);
        mMotionRef.child(key).child("accelerator").child("z").setValue(motionData.accelerator.z);

        mMotionRef.child(key).child("gyro").child("x").setValue(motionData.accelerator.x);
        mMotionRef.child(key).child("gyro").child("y").setValue(motionData.accelerator.y);
        mMotionRef.child(key).child("gyro").child("z").setValue(motionData.accelerator.z);

        mMotionRef.child(key).child("magnetic").child("x").setValue(motionData.magnetic.x);
        mMotionRef.child(key).child("magnetic").child("y").setValue(motionData.magnetic.y);
        mMotionRef.child(key).child("magnetic").child("z").setValue(motionData.magnetic.z);

        mMotionRef.child(key).child("timestamp").setValue(System.currentTimeMillis());
    }

    void logLocation(Location location) {
        synchronized (mLocationRef) {
            if (mLocation != null && mLocation.distanceTo(location) == 0)
                return;

            mLocation = location;

            String key = mLocationRef.push().getKey();
            mLocationRef.child(key).child("latitude").setValue(location.getLatitude());
            mLocationRef.child(key).child("longitude").setValue(location.getLongitude());
            mLocationRef.child(key).child("timestamp").setValue(System.currentTimeMillis());
        }
    }

    boolean isMonitoring() {
        return mMonitoring;
    }
    
    void stopMonitor() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.goOffline();

    }

    void logEvent(UserEvent event) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference eventRef = firebaseDatabase.getReference("event");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userEventRef = eventRef.child(user.getUid());
        String eventKey = userEventRef.push().getKey();
        userEventRef.child(eventKey).child("event").setValue(event.event);
        userEventRef.child(eventKey).child("timestamp").setValue(System.currentTimeMillis());
    }

    void logIntensity(int intensity) {

    }
}
