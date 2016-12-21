package com.swm.core;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;

/**
 * Created by yangzhenyu on 2016/11/3.
 */

public class MyLocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClient;
    private PendingIntent mPendingIntent;
    private boolean mTracking = false;
    private static LocationListener mListener;
    private LocationBinder mBinder = new LocationBinder();
    private static Dump mDump;
    private static boolean mRecording = false;

    public static class LocationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!LocationResult.hasResult(intent))
                return;

            LocationResult locationResult = LocationResult.extractResult(intent);
            List<Location> locations = locationResult.getLocations();
            for (Location location : locations) {
                if (mListener != null)
                    mListener.onLocationUpdate(location);

                if (mRecording)
                    mDump.putData(new LocationData(location.getLatitude(), location.getLongitude(), location.getAccuracy(), location.getTime()));
            }
        }
    }

    public interface LocationListener {
        void onLocationUpdate(Location location);
    }

    public class LocationBinder extends Binder {

        public MyLocationService getService() {
            return MyLocationService.this;
        }
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting())
            mGoogleApiClient.connect();

        return mBinder;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mListener != null && location!= null)
            mListener.onLocationUpdate(location);

        startTracking();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void startTracking() {
        if (mTracking)
            return;

        if (!mGoogleApiClient.isConnected())
            return;

        mTracking = true;
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        Intent intent = new Intent(this, LocationBroadcastReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, mPendingIntent);
    }

    public void stopTracking() {
        if (!mTracking)
            return;

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mPendingIntent);
        mGoogleApiClient.disconnect();
    }

    public boolean isTracking() {
        return mTracking;
    }

    void startRecording() {
        mRecording = true;
        mDump = new Dump<LocationData>("Raw_Gps");
        mDump.start();

    }

    void stopRecording() {
        mRecording = false;
        mDump.stop();
    }

    static boolean isRecording() {
        return mRecording;
    }

    public void setLocationListener(LocationListener listener) {
        mListener = listener;
    }

    void removeLocationListener() {
        mListener = null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
