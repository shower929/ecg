package com.swm.body;

import android.content.Context;
import android.content.SharedPreferences;

import com.swm.heart.pref.SwmPref;

/**
 * Created by yangzhenyu on 2016/11/11.
 */

public class PersonalModule {
    private static PersonalModule mPersonalModule;
    private Context mContext;
    public static final int GENDER_MALE = 1;
    public static final int GENDER_FEMALE = 2;

    private PersonalModule(Context context) {
        mContext = context;
    }

    public static synchronized void init (Context context) {
        if (mPersonalModule == null)
            mPersonalModule = new PersonalModule(context);
    }
    public static synchronized PersonalModule getIns() {
        return mPersonalModule;
    }

    public int getMaxHeartRate() {
        SharedPreferences pref = mContext.getSharedPreferences(SwmPref.PREF_NAME, Context.MODE_PRIVATE);
        int gender = pref.getInt(SwmPref.SWM_GENDER, GENDER_MALE);
        int age = pref.getInt(SwmPref.SWM_AGE, 0);
        return 220 - age;
    }

    public void setGender(int gender) {
        SharedPreferences pref = mContext.getSharedPreferences(SwmPref.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(SwmPref.SWM_GENDER, gender);
        editor.commit();
    }

    public void setAge(int age) {
        SharedPreferences pref = mContext.getSharedPreferences(SwmPref.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(SwmPref.SWM_AGE, age);
        editor.commit();
    }

}
