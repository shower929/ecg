package com.swm.engineering;

import android.app.PendingIntent;
import android.app.Service;
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
import com.google.android.gms.location.LocationServices;
import com.swm.sdk.Dump;

/**
 * Created by yangzhenyu on 2016/11/3.
 */

public class MyLocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final String ACTION_LAST_LOCATION = "action_last_location";
    public static final String EXTRA_LOCATION = "extra_location";

    private GoogleApiClient mGoogleApiClient;
    private PendingIntent mPendingIntent;
    private boolean mTracking = false;

    private LocationBinder mBinder = new LocationBinder();

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
        broadcast(location);
        startTracking();
    }

    private void broadcast(Location location) {
        Intent intent = new Intent(ACTION_LAST_LOCATION);
        intent.setPackage(getPackageName());
        intent.putExtra(EXTRA_LOCATION, location);

        sendBroadcast(intent);
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
        Intent intent = new Intent(this, MotionPresenter.LocationBroadcastReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

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


    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
