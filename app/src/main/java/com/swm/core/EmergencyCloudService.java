package com.swm.core;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.swm.emergency.UserQueryCallback;

/**
 * Created by yangzhenyu on 2016/11/1.
 */

class EmergencyCloudService {
    private SwmCloudHeartRateListener mClientHeartRateListener;
    private ChildEventListener mCloudHeartRateListener;
    private DatabaseReference mHeartRateRef;
    private SwmUser mSwmUser;
    private DatabaseReference mUserRef;
    private ValueEventListener mUserValueListener;
    private Query mHeartRateQuery;

    void setHeartRateListener(SwmCloudHeartRateListener listener, SwmUser user) {
        mSwmUser  = user;
        mClientHeartRateListener = listener;
        mHeartRateRef = FirebaseDatabase.getInstance().getReference("heart_rate");

        mCloudHeartRateListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Integer heartRate = ((Long)dataSnapshot.child("heart_rate").getValue()).intValue();
                if (mClientHeartRateListener != null) {
                    mClientHeartRateListener.onDataAvailable(new HeartRateData(heartRate));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        //mHeartRateQuery = mHeartRateRef.child(user.uid).startAt(System.currentTimeMillis());
        //mHeartRateQuery.addChildEventListener(mCloudHeartRateListener);
    }

    void removeHeartRateListener() {
        mClientHeartRateListener = null;
        mHeartRateRef.child(mSwmUser.uid).removeEventListener(mCloudHeartRateListener);
    }

    synchronized void querySwmUser(final String uid, final UserQueryCallback callback) {
        if (mUserRef == null)
            mUserRef = FirebaseDatabase.getInstance().getReference("users");

        mUserValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SwmUser user = new SwmUser((String)dataSnapshot.child("name").getValue()
                        , (String) dataSnapshot.child("email").getValue()
                        , (String) dataSnapshot.child("tel").getValue()
                        , uid);
                callback.onQueryDone(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mUserRef.child("users").child(uid).addListenerForSingleValueEvent(mUserValueListener);
    }
}
