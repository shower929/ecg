package com.swm.sdk;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;

/**
 * Created by yangzhenyu on 2017/3/29.
 */

public class GpsEngine implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener
        , MotionEngine {

    private GoogleApiClient mGoogleApiClient;
    private PendingIntent mPendingIntent;
    private boolean mTracking = false;
    private static LocationListener mListener;
    private Context context;
    private static MotionEngineOutput output;

    public GpsEngine(Context context) {
        this.context = context;
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


    }

    @Override
    public void setOutput(MotionEngineOutput output) {
        this.output = output;
    }

    @Override
    public void start() {
        startTracking();
        mGoogleApiClient.connect();
    }

    @Override
    public void stop() {
        stopTracking();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onFuel(BleData data) {

    }

    public interface LocationListener {
        void onLocationUpdate(Location location);
    }

    public static class LocationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!LocationResult.hasResult(intent))
                return;

            LocationResult locationResult = LocationResult.extractResult(intent);
            List<Location> locations = locationResult.getLocations();
            Location previousLocation = null;
            long previous = 0;
            long now = 0;

            for (Location location : locations) {

                if (mListener != null)
                    mListener.onLocationUpdate(location);

                if (output != null)
                    now = System.currentTimeMillis();
                    double speed = calculateSpeed(previousLocation, location, (now - previous)/1000);
                    previousLocation = location;
                    previous = now;
                    output.onSpeed(speed);
            }
        }
    }

    private static double calculateSpeed(Location previous, Location current, long time) {
        if (current.getSpeed() > 0.0)
            return current.getSpeed();
        else
            return current.distanceTo(previous) / time;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mListener != null && location != null)
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
        Intent intent = new Intent(context, LocationBroadcastReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, mPendingIntent);
    }

    public void stopTracking() {
        if (!mTracking)
            return;

        if (!mGoogleApiClient.isConnected())
            return;

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mPendingIntent);
        mGoogleApiClient.disconnect();
    }

    public boolean isTracking() {
        return mTracking;
    }

    public void setLocationListener(LocationListener listener) {
        mListener = listener;
    }

    void removeLocationListener() {
        mListener = null;
    }
}
