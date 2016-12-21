package com.swm.power;

import android.content.Context;
import android.database.Cursor;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.swm.power.PowerModel.Columns;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangzhenyu on 2016/10/18.
 */

public class PowerModule {
    private static PowerModule mPowerModule;
    private PowerDatabaseHelper mPowerDatabaseHelper;

    private PowerModule(Context context) {
        mPowerDatabaseHelper = new PowerDatabaseHelper(context);
    }
    public static synchronized void init(Context context) {
        if (mPowerModule == null) {
            mPowerModule = new PowerModule(context);
        }
    }

    public static synchronized PowerModule getIns() throws Exception {
        if (mPowerModule == null)
            throw new Exception("Should init first");

        return mPowerModule;
    }

    public void queryPower(final PowerQueryCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference powerRef = database.getReference().child("power").child(user.getUid());
        Query query = powerRef.orderByChild("power").limitToFirst(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null)
                    return;
                String key = dataSnapshot.getKey();
                PowerModel model = new PowerModel((Integer)dataSnapshot.child(key).child("power").getValue()
                                                , (Integer)dataSnapshot.child(key).child("distance").getValue()
                                                , (Integer)dataSnapshot.child(key).child("elapse").getValue()
                                                , (Long)dataSnapshot.child(key).child("timestamp").getValue());

                callback.onQueryDone(model);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
